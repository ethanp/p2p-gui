package client.peer;

import Exceptions.FailedToFindServerException;
import Exceptions.ServersIOException;
import client.download.FileDownload;
import javafx.beans.property.MapProperty;
import javafx.beans.property.SimpleMapProperty;
import p2p.file.meta.MetaP2P;
import p2p.peer.ChunksForService;
import p2p.peer.PeerAddr;
import p2p.protocol.fileTransfer.PeerTalk;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;
import util.Common;
import util.ServersCommon;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.RandomAccessFile;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Ethan Petuchowski 1/18/15
 *
 * This is the Client's understanding of peers who
 * trackers have claimed contain files.
 *
 * It includes what chunks we think the Peer has of which files.
 *
 * We don't check whether the Client is online until
 * we actually HAVE to interact with them.
 */
public class Peer extends PeerAddr implements Runnable {

    private static final long DOWNLOAD_TIMEOUT = Common.secToNano(3);
    private static final int ERRORS_THRESHOLD = 3;

    Socket socket;
    PrintWriter writerToPeer;
    BufferedInputStream streamFromPeer;

    MapProperty<MetaP2P, ChunksForService> chunksOfFiles;
    List<FileDownload> downloadsImPartOf;

    FileDownload fileCurrentlyDownloading;

    long nanosecsDownloading;
    long bytesDownloaded;

    int countDownloadDataErrors;
    int countTimeouts;

    ExecutorService jobQueue = Executors.newSingleThreadExecutor();

    public Peer(InetSocketAddress peerListenAddr) {
        super(peerListenAddr);
    }

    @Override public void run() {}

    public void connect() throws FailedToFindServerException {
        chunksOfFiles = new SimpleMapProperty<>();
        try {
            socket = ServersCommon.connectToInetSocketAddr(getServingAddr());
            writerToPeer = ServersCommon.printWriter(socket);
            streamFromPeer = ServersCommon.buffIStream(socket);
        }
        catch (ServersIOException e) {
            e.printStackTrace();
            // if this happens in normal operation, there should be a retry count
            // and if that gets hit, it gives up on creating a PeerDownload instance at all
        }
        catch (FailedToFindServerException e) {
            System.err.println("Couldn't connect to peer at "+getServingAddr());
            throw e;
        }
    }

    public boolean hasChunk(int chunkIdx, MetaP2P mFile) {
        return chunksOfFiles.containsKey(mFile)
            && chunksOfFiles.get(mFile).hasIdx(chunkIdx);
    }

    double secondsSpentDownloading() { return nanosecsDownloading / 1E3; }
    double getAverageDownloadSpeed() { return bytesDownloaded / secondsSpentDownloading(); }
    void   addFileDownload(FileDownload fileDownload) { downloadsImPartOf.add(fileDownload); }


    /* ACCORDING TO PROTOCOL */
    void requestChunk(MetaP2P mFile, int chunkIdx) {

        /* SEND REQUEST */
        writerToPeer.println(PeerTalk.ToPeer.GET_CHUNK);
        writerToPeer.println(mFile.serializeToString());
        writerToPeer.println(chunkIdx);
        writerToPeer.flush();

        /* READ RESPONSE */
        @SuppressWarnings("UnusedAssignment") // the IDE is confused
        int responseSize = PeerTalk.FromPeer.DEFAULT_VALUE;
        try {
            responseSize = Common.readIntLineFromStream(streamFromPeer);
        }
        catch (IOException e) {
            e.printStackTrace();
            throw new NotImplementedException();
        }
        /* check for errors */
        switch (responseSize) {
            case PeerTalk.FromPeer.DEFAULT_VALUE:
                System.err.println("received PeerTalk.FromPeer.DEFAULT_VALUE");
                /* deal with this when it happens */
                break;
            case PeerTalk.FromPeer.FILE_NOT_AVAILABLE:
                System.err.println("received PeerTalk.FromPeer.FILE_NOT_AVAILABLE");
                removeCurrentFileFromDownloads();
                break;
            case PeerTalk.FromPeer.CHUNK_NOT_AVAILABLE:
                System.err.println("received PeerTalk.FromPeer.CHUNK_NOT_AVAILABLE");
                /* TODO update BitMap */
                break;
            case PeerTalk.FromPeer.OUT_OF_BOUNDS:
                System.err.println("received PeerTalk.FromPeer.OUT_OF_BOUNDS");
                /* deal with this when it happens */
                break;
        }
        downloadChunk(chunkIdx, responseSize);
    }

    private void removeCurrentFileFromDownloads() {
        downloadsImPartOf.remove(fileCurrentlyDownloading);
        fileCurrentlyDownloading = null;
    }

    void downloadChunk(int chunkIdx, int responseSize) {
        long startTime = System.nanoTime();
        byte[] response = new byte[responseSize];
        int bytesRcvd = 0;
        while (bytesRcvd < responseSize) {
            int rcv;
            try {
                rcv = streamFromPeer.read(response, bytesRcvd, responseSize-bytesRcvd);
            }
            catch (IOException e) {
                e.printStackTrace();
                System.out.println("Retrying");
                downloadError();
                return;
            }
            if (rcv >= 0) {
                bytesRcvd += rcv;
                bytesDownloaded += rcv;
            } else {
                downloadError();
                return;
            }
            if (didTimeout(startTime)) {
                downloadTimeout();
                return;
            }
        }
        long endTime = System.nanoTime();
        nanosecsDownloading += endTime - startTime;
        try {
            RandomAccessFile fileOutput = new RandomAccessFile(fileCurrentlyDownloading.getPFile().getLocalFile(), "rw");
            fileOutput.seek(fileCurrentlyDownloading.getPFile().getBytesPerChunk()*chunkIdx);
            fileOutput.write(response);
            fileOutput.close();
        }
        catch (IOException e) {
            // this would most-likely mean a bug
            // it is not part of the logic for this to ever occur
            e.printStackTrace();
        }
        fileCurrentlyDownloading.markChunkAsAvbl(chunkIdx);
        fileCurrentlyDownloading = null;
    }

    private boolean didTimeout(long time) {
        return (System.nanoTime() - time) > DOWNLOAD_TIMEOUT;
    }

    private void downloadTimeout() {
        if (++countTimeouts > ERRORS_THRESHOLD) {
            System.err.println("Passed timeout threshold");
            // disconnect from peer and don't reconnect for a little while?
        }
        fileCurrentlyDownloading = null;
    }

    private void downloadError() {
        if (++countDownloadDataErrors > ERRORS_THRESHOLD) {
            System.err.println("Passed download errors threshold");
            // disconnect from peer and don't reconnect for a little while?
        }
        fileCurrentlyDownloading = null;
    }

    public boolean addFile(MetaP2P metaP2P) {
        if (!chunksOfFiles.containsKey(metaP2P)) {
            chunksOfFiles.put(metaP2P, new ChunksForService(metaP2P.getNumChunks()));
            return true;
        }
        return false;
    }

    public Set<MetaP2P> getFiles() {
        return chunksOfFiles.keySet();
    }

    public void addFiles(Set<MetaP2P> files) {
        for (MetaP2P file : files)
            addFile(file);
    }

    public void queueUpdateChunkAvailability(MetaP2P metaP2P) {
        jobQueue.submit(new UpdateChunkAvblJob(metaP2P));
    }

    private class UpdateChunkAvblJob extends PeerWork {
        MetaP2P metaP2P;

        UpdateChunkAvblJob(MetaP2P meta) {
            metaP2P = meta;
            priority = 1;
        }

        @Override public void run() {}
    }

    private class DownloadChunkJob extends PeerWork {
        MetaP2P metaP2P;
        int chunkIdx;

        public DownloadChunkJob(MetaP2P meta, int chunkIndex) {
            metaP2P = meta;
            chunkIdx = chunkIndex;
        }

        @Override public void run() {}
    }

    private abstract class PeerWork implements Comparable<PeerWork>, Runnable {
        /** HIGHER priority means MORE urgent */
        protected int priority = 0;

        /** @return a negative integer iff this object is less than the specified object. */
        @Override public int compareTo(PeerWork o) { return o.priority - priority; }
    }
}

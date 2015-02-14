package client.peer;

import Exceptions.FailedToFindServerException;
import Exceptions.ServersIOException;
import client.download.FileDownload;
import javafx.beans.property.MapProperty;
import javafx.beans.property.SimpleMapProperty;
import p2p.exceptions.ConnectToPeerException;
import p2p.file.meta.MetaP2P;
import p2p.peer.ChunksForService;
import p2p.peer.PeerAddr;
import p2p.protocol.fileTransfer.PeerTalk;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;
import util.Common;
import util.Connection;
import util.StringsOutBytesIn;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.InetSocketAddress;
import java.util.Collection;
import java.util.HashSet;
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

    StringsOutBytesIn chunkConn = new StringsOutBytesIn(getServingAddr());

    MapProperty<MetaP2P, ChunksForService> chunksOfFiles;

    Collection<FileDownload> ongoingFileDownloads = new HashSet<>();

    FileDownload fileCurrentlyDownloading;

    Thread chunkDownloadThread;

    long nanosecsDownloading;
    long bytesDownloaded;

    int countDownloadDataErrors;
    int countTimeouts;

    ExecutorService jobQueue = Executors.newSingleThreadExecutor();
    private boolean running = false;

    public Peer(InetSocketAddress peerListenAddr) {
        super(peerListenAddr);
    }

    @Override public void run() {
        running = true;
        Collection<Integer> chunkIdxs = fileCurrentlyDownloading.decideChunksToDownload(this);
        for (int idx : chunkIdxs) {
            jobQueue.submit(new DownloadChunkJob(fileCurrentlyDownloading.getMFile(), idx, this));
        }
    }

    public void connect() throws FailedToFindServerException, ConnectToPeerException, ServersIOException {
        if (chunkConn.socket.isConnected()) {
            throw new ConnectToPeerException("Already connected");
        }
        connect(chunkConn);
        chunksOfFiles = new SimpleMapProperty<>();
    }

    public void connect(Connection connection) throws ConnectToPeerException, FailedToFindServerException, ServersIOException {
        connection.connect();
    }

    public boolean hasChunk(int chunkIdx, MetaP2P mFile) {
        return chunksOfFiles.containsKey(mFile)
            && chunksOfFiles.get(mFile).hasIdx(chunkIdx);
    }

    double secondsSpentDownloading() { return nanosecsDownloading / 1E3; }
    double getAverageDownloadSpeed() { return bytesDownloaded / secondsSpentDownloading(); }


    // TODO move this
    /* ACCORDING TO PROTOCOL */
    void requestChunk(MetaP2P mFile, int chunkIdx) {

        /* SEND REQUEST */
        chunkConn.writer.println(PeerTalk.ToPeer.GET_CHUNK);
        chunkConn.writer.println(mFile.serializeToString());
        chunkConn.writer.println(chunkIdx);
        chunkConn.writer.flush();

        /* READ RESPONSE */
        @SuppressWarnings("UnusedAssignment") // the IDE is confused
        int responseSize = PeerTalk.FromPeer.DEFAULT_VALUE;
        try {
            responseSize = Common.readIntLineFromStream(chunkConn.in);
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
                /* update BitMap */
                chunksOfFiles.get(fileCurrentlyDownloading.getMFile())
                             .setChunkAvailable(chunkIdx, false);
                break;
            case PeerTalk.FromPeer.OUT_OF_BOUNDS:
                System.err.println("received PeerTalk.FromPeer.OUT_OF_BOUNDS");
                /* deal with this when it happens */
                break;
        }
        downloadChunk(chunkIdx, responseSize);
    }

    private void removeCurrentFileFromDownloads() {
        ongoingFileDownloads.remove(fileCurrentlyDownloading);
        fileCurrentlyDownloading = ongoingFileDownloads.iterator().next();
    }

    // TODO move this
    void downloadChunk(int chunkIdx, int responseSize) {
        long startTime = System.nanoTime();
        byte[] response = new byte[responseSize];
        int bytesRcvd = 0;
        while (bytesRcvd < responseSize) {
            int rcv;
            try {
                rcv = chunkConn.in.read(response, bytesRcvd, responseSize-bytesRcvd);
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

    public void updateChunkAvbl(MetaP2P metaP2P) throws ServersIOException, FailedToFindServerException {
        jobQueue.submit(new UpdateChunkAvblJob(metaP2P, this));
    }

    public void addDownload(FileDownload fileDownload) {
        ongoingFileDownloads.add(fileDownload);
        if (!running) {
            chunkDownloadThread = new Thread(this);
            chunkDownloadThread.start();
            fileCurrentlyDownloading = fileDownload;
        }
    }

}


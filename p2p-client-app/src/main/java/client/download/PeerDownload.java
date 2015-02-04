package client.download;

import Exceptions.ServersIOException;
import Exceptions.SocketCouldntConnectException;
import p2p.file.meta.MetaP2PFile;
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

/**
 * Ethan Petuchowski 2/3/15
 */
public class PeerDownload implements Runnable {

    private static final long DOWNLOAD_TIMEOUT = Common.secondsToNanoseconds(3);
    private static final int ERRORS_THRESHOLD = 3;

    Socket socket;
    long nanosecsDownloading;
    long bytesDownloaded;
    PrintWriter writerToPeer;
    BufferedInputStream streamFromPeer;
    List<FileDownload> downloadsImPartOf;
    FileDownload fileCurrentlyDownloading;
    int countDownloadDataErrors;
    int countTimeouts;

    double secondsSpentDownloading() { return nanosecsDownloading / 1E3; }
    double getAverageDownloadSpeed() { return bytesDownloaded / secondsSpentDownloading(); }
    void   addFileDownload(FileDownload fileDownload) { downloadsImPartOf.add(fileDownload); }

    PeerDownload(InetSocketAddress peerListenAddr) {
        try {
            socket = ServersCommon.socketAtAddr(peerListenAddr);
            writerToPeer = ServersCommon.printWriter(socket);
            streamFromPeer = ServersCommon.buffIStream(socket);
        }
        catch (SocketCouldntConnectException | ServersIOException e) {
            e.printStackTrace();
            // if this happens in normal operation, there should be a retry count
            // and if that gets hit, it gives up on creating a PeerDownload instance at all
        }
    }

    @Override public void run() {
        requestChunk();
    }

    private void requestChunk() {
        fileCurrentlyDownloading = figureOutFileToDownloadFor();
        int chunkIdx = fileCurrentlyDownloading.getChunkIdxToDownload();
        MetaP2PFile mFile = fileCurrentlyDownloading.getMFile();
        requestChunk(mFile, chunkIdx);
    }

    private FileDownload figureOutFileToDownloadFor() {
        // TODO implement PeerDownload figureOutFileToDownloadFor
        throw new NotImplementedException();
    }

    /* ACCORDING TO PROTOCOL */
    void requestChunk(MetaP2PFile mFile, int chunkIdx) {

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
            // TODO implement PeerDownload IOException
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
        return System.nanoTime()-time > DOWNLOAD_TIMEOUT;
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
}

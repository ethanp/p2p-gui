package client.peer;

import client.p2pFile.FileDownload;
import p2p.exceptions.InvalidDataException;
import p2p.file.MetaP2P;
import p2p.protocol.fileTransfer.PeerTalk;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;
import util.Common;

import java.io.IOException;
import java.util.concurrent.Callable;

import static p2p.protocol.fileTransfer.PeerTalk.FromPeer.CHUNK_NOT_AVAILABLE;
import static p2p.protocol.fileTransfer.PeerTalk.FromPeer.DEFAULT_VALUE;
import static p2p.protocol.fileTransfer.PeerTalk.FromPeer.FILE_NOT_AVAILABLE;
import static p2p.protocol.fileTransfer.PeerTalk.FromPeer.OUT_OF_BOUNDS;

public class DownloadChunkJob implements Callable<Boolean> {
    MetaP2P metaP2P;
    int chunkIdx;
    Peer peer;
    FileDownload fileDownload;

    public DownloadChunkJob(FileDownload fileDownload, int chunkIndex, Peer peer) {
        this.fileDownload = fileDownload;
        chunkIdx = chunkIndex;
        this.peer = peer;
        metaP2P = fileDownload.getMFile();
    }

    /**
     * @return whether the chunk was downloaded and written.
     * @throws InvalidDataException if the received data didn't match the Meta-specified digest.
     */
    @Override public Boolean call() throws InvalidDataException {
        return requestChunk();
    }

    /* ACCORDING TO PROTOCOL */
    boolean requestChunk() throws InvalidDataException {

        /* SEND REQUEST */
        peer.chunkConn.writer.println(PeerTalk.ToPeer.GET_CHUNK);
        peer.chunkConn.writer.println(metaP2P.serializeToString());
        peer.chunkConn.writer.println(chunkIdx);
        peer.chunkConn.writer.flush();

        /* READ RESPONSE */
        @SuppressWarnings("UnusedAssignment") // the IDE is confused
        int responseSize = DEFAULT_VALUE;
        try {
            responseSize = Common.readIntLineFromStream(peer.chunkConn.in);
        }
        catch (IOException e) {
            e.printStackTrace();
            throw new NotImplementedException();
        }
        /* check for errors */
        switch (responseSize) {

            case DEFAULT_VALUE:
                System.err.println("received DEFAULT_VALUE");
                return false;

            case FILE_NOT_AVAILABLE:
                System.err.println("received FILE_NOT_AVAILABLE");
                peer.stopDownloading(metaP2P);
                return false;

            case CHUNK_NOT_AVAILABLE:
                System.err.println("received CHUNK_NOT_AVAILABLE");
                peer.markAbsent(metaP2P, chunkIdx);
                return false;

            case OUT_OF_BOUNDS:
                System.err.println("received OUT_OF_BOUNDS");
                /* deal with this when it happens */
                return false;

            default:
                return downloadChunk(responseSize);
        }
    }

    boolean downloadChunk(int responseSize) throws InvalidDataException {
        long startTime = System.nanoTime();
        byte[] response = new byte[responseSize];
        int bytesRcvd = 0;
        while (bytesRcvd < responseSize) {
            int rcv;
            try {
                rcv = peer.chunkConn.in.read(response, bytesRcvd, responseSize-bytesRcvd);
            }
            catch (IOException e) {
                e.printStackTrace();
                System.out.println("Retrying");
                peer.downloadError();
                return false;
            }
            if (rcv >= 0) {
                bytesRcvd += rcv;
                peer.bytesDownloaded += rcv;
            } else {
                peer.downloadError();
                return false;
            }
        }
        long endTime = System.nanoTime();
        peer.nanosecsDownloading += endTime - startTime;
        fileDownload.writeToDisk(response, chunkIdx);
        return true;
    }

    @Override public String toString() {
        return "DownloadChunkJob{"+
               "metaP2P="+metaP2P+
               ", chunkIdx="+chunkIdx+
               '}';
    }
}

package p2p.transfer;

import p2p.file.meta.P2PFile;

import java.io.File;
import java.net.Socket;

/**
 * Ethan Petuchowski 1/7/15
 */
public class ChunkDownload extends Thread {
    private final int chunkIdx;
    private final Socket peerConn; // connected to a @ClientPeerServer
    private final P2PFile pFile;
    private final File localFile;

    public ChunkDownload(int chunkIdx, Socket peerConn, P2PFile pFile) {
        this.chunkIdx = chunkIdx;
        this.peerConn = peerConn;
        this.pFile = pFile;
        this.localFile = pFile.getLocalFile();
    }

    public
}

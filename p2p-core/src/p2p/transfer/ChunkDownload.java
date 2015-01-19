package p2p.transfer;

import p2p.file.p2pFile.P2PFile;
import p2p.protocol.chunkTransfer.ClientSideChunkProtocol;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;
import util.Common;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Ethan Petuchowski 1/7/15
 */
public class ChunkDownload extends Thread implements ClientSideChunkProtocol {
    protected final int chunkIdx;
    protected final Socket peerConn; // connected to a @ClientPeerServer
    protected final P2PFile pFile;
    protected final File localFile;
    protected final PrintWriter peerOut;
    protected final BufferedReader peerIn;

    public ChunkDownload(int chunkIdx, Socket peerConn, P2PFile pFile) {
        this.chunkIdx = chunkIdx;
        this.peerConn = peerConn;
        this.pFile = pFile;

        localFile = pFile.getLocalFile();
        peerOut = Common.printWriter(peerConn);
        peerIn = Common.bufferedReader(peerConn);
    }

    // TODO implement ChunkDownload.run()
    @Override public void run() {
        /* TODO the file should have been already created closer to when
         *      the user clicked that she wanted to download it
         */
        assert localFile.exists() && !localFile.isDirectory();

        int initialByteOffset = chunkIdx * pFile.getBytesPerChunk();

        /* It appears that Thread's run() cannot throw an Exception.
         * I guess where would it GO, so that makes sense.
         * The upshot is we must catch the exception here.
         * But we already asserted the file exists in the beginning so we should be good-to-go.
         */
        try {
            BufferedOutputStream fileOutStream = new BufferedOutputStream(new FileOutputStream(localFile));
            /* TODO use the ClientSideChunkProtocol (as yet unimplemented) */
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        throw new NotImplementedException();
    }
}

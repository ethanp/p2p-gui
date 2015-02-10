package client.download;

import Exceptions.ServersIOException;
import client.p2pFile.P2PFile;
import client.protocol.ClientSideChunkProtocol;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;
import util.ServersCommon;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Ethan Petuchowski 1/7/15
 */
public class ChunkDownload implements Runnable, ClientSideChunkProtocol {
    protected final int chunkIdx;
    protected final Socket peerConn; // connected to a @ClientPeerServer
    protected final P2PFile pFile;
    protected final File localFile;
    protected final PrintWriter peerOut;
    protected final BufferedReader peerIn;

    public ChunkDownload(int chunkIdx, Socket peerConn, P2PFile pFile) throws IOException, ServersIOException {
        this.chunkIdx = chunkIdx;
        this.peerConn = peerConn;
        this.pFile = pFile;

        localFile = pFile.getLocalFile();
        peerOut = ServersCommon.printWriter(peerConn);
        peerIn = ServersCommon.bufferedReader(peerConn);
    }

    @Override public void run() {
        /* NOTE: the file should have been already created closer to when
         *       the user clicked that she wanted to download it
         */
        assert localFile.exists() && !localFile.isDirectory();

        int initialByteOffset = chunkIdx * pFile.getBytesPerChunk();

        /* It appears that Thread's run() cannot throw an Exception.
         * I guess where would it GO, so that makes sense.
         * The upshot is we must catch the exception here.
         * But we already asserted the file exists in the beginning so we should be good-to-go.
         */
        try (BufferedOutputStream fileOutStream =
                     new BufferedOutputStream(
                             new FileOutputStream(localFile)))
        {
            /* perhaps use the ClientSideChunkProtocol (i.e. requestChunk() below) */
        }
        catch (IOException e) { e.printStackTrace(); }
        throw new NotImplementedException();
    }

    @Override public void requestChunk() {

    }
}

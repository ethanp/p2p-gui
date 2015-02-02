package p2p.protocol.fileTransfer;

import p2p.exceptions.CreateP2PFileException;

import java.io.IOException;

/**
 * Ethan Petuchowski 1/20/15
 */
public interface ServerSideChunkProtocol {
    public void serveAvailabilities();
    public void serveChunk() throws IOException, CreateP2PFileException;
}

package p2p.protocol.fileTransfer;

/**
 * Ethan Petuchowski 1/20/15
 */
public interface ServerSideChunkProtocol {
    public void serveAvailabilities();
    public void serveChunk();
}

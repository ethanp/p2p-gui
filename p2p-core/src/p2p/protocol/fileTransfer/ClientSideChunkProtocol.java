package p2p.protocol.fileTransfer;

/**
 * Ethan Petuchowski 1/18/15
 *
 * Currently, this is `implemented` by `ChunkDownload`
 */
public interface ClientSideChunkProtocol {
    public void requestChunk();
}

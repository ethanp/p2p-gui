package p2p.file.chunk;

import util.Common;

/**
 * Ethan Petuchowski 1/8/15
 */
public abstract class Chunk {

    protected final int chunkSize;

    /* Should this be a "property"?  Would that add a ton of space overhead? */
    protected final byte[] data;

    public Chunk(byte[] data) {
        this.chunkSize = Common.DEFAULT_CHUNK_SIZE;
        this.data = data;
    }
}

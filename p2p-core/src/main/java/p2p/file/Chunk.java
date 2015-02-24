package p2p.file;

import util.Common;

import java.security.InvalidParameterException;

/**
 * Ethan Petuchowski 1/8/15
 */
public class Chunk {


    /* Should this be a "property"?  Would that add a ton of space overhead?
        Probably not because this thing is getting read->written->garbage collected
         so I don't think the space per chunk is really a concern
    */
    protected final byte[] data;

    public Chunk(byte[] data) {
        if (data.length > Common.NUM_CHUNK_BYTES) {
            throw new InvalidParameterException("that byte[] is too big");
        }
        this.data = data;
    }

    public int size()       { return data.length; }
    public byte[] getData() { return data; }
}

package p2p.peer;

import java.util.BitSet;

/**
 * Ethan Petuchowski 1/18/15
 *
 * this wraps a BitSet denoting which chunks of a particular file are
 * available for upload to interested peers
 */
public class ChunksForService {
    protected final int numChunks; // bitSet doesn't seem to save the size it was created with?
    protected final BitSet bitSet;

    public ChunksForService(int numChunks) {
        this.numChunks = numChunks;
        bitSet = new BitSet(numChunks);
        setAllAsAvailable();
    }

    public byte[] serializeToBytes() {
        int numBytes = (int) Math.ceil(numChunks/8.0);
        byte[] bytes = new byte[numBytes];

        /**
         * NB: This produces a serialization of the BitSet in LITTLE-ENDIAN order,
         *     which means the LEAST significant digits are in the FIRST byte, and
         *     you move MORE significant as you move to HIGHER indexes.
         *
         * Side note: This only iterates through to the last bit that's set to TRUE.
         * It doesn't make a difference though bc the array gets initialized to zeros. */
        for (int i = 0; i < bitSet.length(); i++)
            if (bitSet.get(i))
                bytes[i/8] |= 1 << (i%8); // that's some shit right there.
        return bytes;
    }

    public static ChunksForService createFromBytes(int numChunks, byte[] bytes) {
        ChunksForService toRet = new ChunksForService(numChunks);
        for (int i = 0; i < numChunks; i++)
            if ((bytes[i/8] & (1 << (i%8))) == 1)
                toRet.setChunkAvailable(i, true);
        return toRet;
    }

    public boolean hasIdx(int chunkIdx)     { return bitSet.get(chunkIdx); }
    public int     numChunks()              { return numChunks; }
    public void    setAllAsAvailable()      { bitSet.set(0, numChunks, true); }
    public double  getProportionAvailable() { return ((double) bitSet.cardinality()) / numChunks; }
    public void    setChunkAvailable(int index, boolean available) { bitSet.set(index, available); }

    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ChunksForService)) return false;
        ChunksForService that = (ChunksForService) o;
        if (numChunks != that.numChunks) return false;
        if (!bitSet.equals(that.bitSet)) return false;
        return true;
    }
    @Override public int hashCode() {
        int result = numChunks;
        result = 31*result+bitSet.hashCode();
        return result;
    }
}

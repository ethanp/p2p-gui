package p2p.peer;

import Exceptions.ServersIOException;
import util.Common;

import java.io.BufferedInputStream;
import java.io.IOException;
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

    public int firstUnavailableChunk() {
        return bitSet.nextClearBit(0);
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
        for (int i = 0; i < numChunks; i++) {
            int bitVal = bytes[i/8] & (1 << (i%8));
            toRet.updateIdx(i, bitVal == 1);
        }
        return toRet;
    }

    public boolean hasIdx(int chunkIdx)     { return bitSet.get(chunkIdx); }
    public int     numChunks()              { return numChunks; }
    public void    setAllAsAvailable()      { bitSet.set(0, numChunks, true); }
    public double  getProportionAvailable() { return ((double) bitSet.cardinality()) / numChunks; }
    public void updateIdx(int index, boolean availability) { bitSet.set(index, availability); }

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

    public static ChunksForService deserialize(BufferedInputStream in) throws IOException, ServersIOException {
        int numChunks = Common.readIntLineFromStream(in);
        int numBytes = (int) Math.ceil(numChunks/8.0);
        byte[] bytes = new byte[numBytes];
        int numRcvd = in.read(bytes);
        if (numRcvd < numBytes) {
            throw new ServersIOException("ChunksForService was not all received in one go");
        }
        return createFromBytes(numChunks, bytes);
    }
}

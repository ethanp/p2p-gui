package p2p.peer;

import java.util.BitSet;

/**
 * Ethan Petuchowski 1/18/15
 *
 * this wraps a BitSet denoting which chunks of a particular file are
 * available for upload to interested peers
 */
public class ChunksForService {
    protected final BitSet bitSet;

    public ChunksForService(int numChunks) {
        bitSet = new BitSet(numChunks);
    }

    public boolean hasIdx(int chunkIdx) {
        return bitSet.get(chunkIdx); // Returns value of bit with specified index
    }

    public int numChunks() {
        return bitSet.size();
    }
    public void setAllAsAvailable() {
        bitSet.set(0, numChunks(), true);
    }
    public double getProportionAvailable() {
        return ((double) bitSet.cardinality()) / numChunks();
    }
}

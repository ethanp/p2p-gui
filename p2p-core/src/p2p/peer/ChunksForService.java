package p2p.peer;

import java.util.BitSet;

/**
 * Ethan Petuchowski 1/18/15
 *
 * this wraps a BitSet denoting which chunks of a particular file are
 * available for upload to interested peers
 */
public class ChunksForService {
    protected BitSet bitSet;
}

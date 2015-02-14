package client.peer;

/**
* Ethan Petuchowski 2/13/15
*/
public abstract class PeerWork implements Comparable<PeerWork>, Runnable {
    PeerWork(Peer peer) { this.peer = peer; }

    /** HIGHER priority means MORE urgent */
    protected int priority = 0;

    protected Peer peer;


    /** @return a negative integer iff this object is less than the specified object. */
    @Override public int compareTo(PeerWork o) { return o.priority - priority; }
}

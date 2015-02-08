package tracker;

import p2p.peer.PeerAddr;

import java.net.InetSocketAddress;

/**
 * Ethan Petuchowski 1/18/15
 *
 * This contains ONLY the information about a Peer that a Tracker needs
 * to keep track of, namely the ServerSocket address at which it is
 * servicing requests for Chunks.
 */
public class TrackerPeer extends PeerAddr {
    protected TrackerPeer(InetSocketAddress socketAddr) {
        super(socketAddr);
    }
}

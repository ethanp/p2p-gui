package p2p.peer;

import util.Common;

import java.net.InetSocketAddress;

/**
 * Ethan Petuchowski 1/8/15
 */
public class FakePeer extends Peer {
    public FakePeer(String fakeAddr) {
        super(new InetSocketAddress(fakeAddr, 2));
    }

    public static FakePeer createWithUnresolvedIP() {
        return new FakePeer(Common.randomIPPortString());
    }
}

package client.peer;

import util.ServersCommon;

import java.net.InetSocketAddress;

/**
 * Ethan Petuchowski 1/8/15
 */
public class FakePeer extends Peer {
    public FakePeer(String fakeAddr) {
        super(new InetSocketAddress(fakeAddr, 666));
    }

    public static FakePeer createWithUnresolvedIP() {
        return new FakePeer(ServersCommon.randomIPPortString());
    }
}

package client.peer;

import util.ServersCommon;

import java.net.InetSocketAddress;

/**
 * Ethan Petuchowski 1/8/15
 */
public class FakeRemotePeer extends RemotePeer {
    public FakeRemotePeer(String fakeAddr) { super(new InetSocketAddress(fakeAddr, 666)); }

    public static FakeRemotePeer createWithUnresolvedIP() {
        return new FakeRemotePeer(ServersCommon.randomIPPortString());
    }
}

package p2p.tracker;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import p2p.file.FakeP2PFile;
import p2p.file.P2PFile;
import p2p.tracker.swarm.RemoteSwarm;
import util.Common;

/**
 * Ethan Petuchowski 1/8/15
 */
public class FakeRemoteTracker extends AbstractRemoteTracker {
    String ipPortString;

    static FakeRemoteTracker defaultFakeRemoteTracker
            = new FakeRemoteTracker("123.123.123.123:3300");

    static {
        defaultFakeRemoteTracker.requestInfo();
    }

    public static FakeRemoteTracker getDefaultFakeRemoteTracker() {
        return defaultFakeRemoteTracker;
    }

    public static FakeRemoteTracker makeFakeTracker() {
        String ip = Common.randomIPPortString();
        FakeRemoteTracker t = new FakeRemoteTracker(ip);
        ObservableList<RemoteSwarm> swarms = FXCollections.observableArrayList();
        int N = Common.randInt(6);
        for (int i = 0; i < N; i++)
            swarms.add(new RemoteSwarm(FakeP2PFile.genFakeFile(), t));
        t.setSwarms(swarms);
        return t;
    }

    public FakeRemoteTracker(String fakeIPAddrAndPort) {
        super(null);
        ipPortString = fakeIPAddrAndPort;
    }

    @Override public String getIpPortString() { return ipPortString; }

    @Override public void requestInfo() {
        final RemoteSwarm swarm1 = new RemoteSwarm(FakeP2PFile.genFakeFile(), defaultFakeRemoteTracker);
        final RemoteSwarm swarm2 = new RemoteSwarm(FakeP2PFile.genFakeFile(), defaultFakeRemoteTracker);
        swarm1.addRandomPeers();
        swarm2.addRandomPeers();
        setSwarms(FXCollections.observableArrayList(swarm1,swarm2));
    }

    @Override public void updateSwarmAddrs(P2PFile pFile) {
        getSwarmForFile(pFile).addRandomPeers();
    }
}

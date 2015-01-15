package p2p.tracker;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import p2p.file.FakeP2PFile;
import p2p.file.P2PFile;
import p2p.peer.FakePeer;
import p2p.tracker.swarm.RemoteSwarm;
import p2p.tracker.swarm.Swarm;
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
        setSwarms(
                FXCollections.observableArrayList(
                        new RemoteSwarm(FakeP2PFile.genFakeFile(), defaultFakeRemoteTracker),
                        new RemoteSwarm(FakeP2PFile.genFakeFile(), defaultFakeRemoteTracker)));
        updateSwarmAddrs(swarms.get(0).getP2pFile());
        updateSwarmAddrs(swarms.get(1).getP2pFile());
    }

    @Override public void updateSwarmAddrs(P2PFile pFile) {
        Swarm swarm = getSwarmForFile(pFile);
        int nSeeders = Common.randInt(10);
        int nLeechers = Common.randInt(10);
        for (int i = 0; i < nSeeders; i++)
            swarm.getSeeders().add(FakePeer.create());
        for (int i = 0; i < nLeechers; i++)
            swarm.getLeechers().add(FakePeer.create());

    }
}

package p2p.tracker;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import p2p.file.FakeP2PFile;
import p2p.file.P2PFile;

import java.util.Random;

/**
 * Ethan Petuchowski 1/8/15
 */
public class FakeRemoteTracker extends RemoteTracker {
    static Random r = new Random();
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
        String ip = r.nextInt(255)+"."+r.nextInt(255)+"."+
                    r.nextInt(255)+"."+r.nextInt(255)+":"+r.nextInt(5000);
        FakeRemoteTracker t = new FakeRemoteTracker(ip);
        ObservableList<Swarm> swarms = FXCollections.observableArrayList();
        int N = r.nextInt(6);
        for (int i = 0; i < N; i++)
            swarms.add(new Swarm(FakeP2PFile.genFakeFile(), t));
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
                        new Swarm(FakeP2PFile.genFakeFile(), defaultFakeRemoteTracker),
                        new Swarm(FakeP2PFile.genFakeFile(), defaultFakeRemoteTracker)));
    }

    @Override public void updateSwarmAddrs(P2PFile pFile) {/* nothing to do */}
}

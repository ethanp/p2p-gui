package p2p.tracker;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import p2p.file.meta.LocalFakeFile;
import p2p.tracker.swarm.RemoteSwarm;
import util.Common;

import java.io.IOException;

/**
 * Ethan Petuchowski 1/8/15
 */
public class FakeRemoteTracker extends AbstractRemoteTracker {
    String ipPortString;

    static FakeRemoteTracker defaultFakeRemoteTracker;

    static {
        try { defaultFakeRemoteTracker = new FakeRemoteTracker("123.123.123.123:3300"); }
        catch (IOException e) { e.printStackTrace(); }
        defaultFakeRemoteTracker.listFiles();
    }

    public static FakeRemoteTracker getDefaultFakeRemoteTracker() {
        return defaultFakeRemoteTracker;
    }

    public static FakeRemoteTracker makeFakeTracker() {
        String ip = Common.randomIPPortString();
        FakeRemoteTracker t = null;
        try { t = new FakeRemoteTracker(ip); }
        catch (IOException e) { e.printStackTrace(); }
        ObservableList<RemoteSwarm> swarms = FXCollections.observableArrayList();
        int N = Common.randInt(6);
        for (int i = 0; i < N; i++)
            swarms.add(new RemoteSwarm(LocalFakeFile.genFakeFile(), t));
        assert t != null;
        t.setSwarms(swarms);
        return t;
    }

    public FakeRemoteTracker(String fakeIPAddrAndPort) throws IOException {
        super(null);
        ipPortString = fakeIPAddrAndPort;
    }

    @Override public String getIpPortString() { return ipPortString; }

    /**
     * Tracker receives P2PFile from Peer looks for it among its LocalSwarms If it exists, add Peer
     * to Swarm Otherwise create a new Swarm for it
     */
    @Override public void addFileRequest() {

    }

    /**
     * Tracker tells a Peer who wants to download a P2PFile about the specific IP Addresses of Peers
     * in an existing Swarm so that the Peer can update its internal view of the Swarm
     * @param remoteSwarm
     */
    @Override public void updateSwarmInfo(RemoteSwarm remoteSwarm) {
        getSwarmForFile(remoteSwarm.getP2pFile()).addRandomPeers();
    }

    /**
     * Tracker sends Peer its full list of Swarms INCLUDING specific IP Addresses of Swarm members
     */
    @Override public void listFiles() {
        final RemoteSwarm swarm1 = new RemoteSwarm(LocalFakeFile.genFakeFile(), defaultFakeRemoteTracker);
        final RemoteSwarm swarm2 = new RemoteSwarm(LocalFakeFile.genFakeFile(), defaultFakeRemoteTracker);
        swarm1.addRandomPeers();
        swarm2.addRandomPeers();
        setSwarms(FXCollections.observableArrayList(swarm1,swarm2));
    }
}

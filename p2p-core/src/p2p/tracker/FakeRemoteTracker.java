package p2p.tracker;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import p2p.exceptions.ConnectToTrackerException;
import p2p.file.meta.MetaP2PFile;
import p2p.tracker.swarm.ClientSwarm;
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
        catch (ConnectToTrackerException | IOException e) { e.printStackTrace(); }
        defaultFakeRemoteTracker.listFiles();
    }

    public static FakeRemoteTracker getDefaultFakeRemoteTracker() {
        return defaultFakeRemoteTracker;
    }

    public static FakeRemoteTracker makeFakeTracker() {
        String ip = Common.randomIPPortString();
        FakeRemoteTracker t = null;
        try { t = new FakeRemoteTracker(ip); }
        catch (ConnectToTrackerException | IOException e) { e.printStackTrace(); }
        ObservableList<ClientSwarm> swarms = FXCollections.observableArrayList();
        int N = Common.randInt(6);
        for (int i = 0; i < N; i++)
            swarms.add(new ClientSwarm(MetaP2PFile.genFake(), t));
        assert t != null;
        t.setSwarms(swarms);
        return t;
    }

    public FakeRemoteTracker(String fakeIPAddrAndPort)
            throws IOException, ConnectToTrackerException
    {
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
     * @param clientSwarm
     */
    @Override public void updateSwarmInfo(ClientSwarm clientSwarm) {
        getSwarmForFile(clientSwarm.getMetaP2P()).addFakePeers();
    }

    /**
     * Create metadatas for 2 non-existent files
     * Give them random peer addresses
     * Stick them in the fake remote tracker's swarms list
     */
    @Override public void listFiles() {
        final ClientSwarm swarm1 = new ClientSwarm(MetaP2PFile.genFake(), defaultFakeRemoteTracker);
        final ClientSwarm swarm2 = new ClientSwarm(MetaP2PFile.genFake(), defaultFakeRemoteTracker);
        swarm1.addFakePeers();
        swarm2.addFakePeers();
        setSwarms(FXCollections.observableArrayList(swarm1,swarm2));
    }
}

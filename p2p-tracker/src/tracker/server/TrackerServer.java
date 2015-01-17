package tracker.server;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import p2p.protocol.tracker.TrackerSideTrackerProtocol;
import p2p.tracker.LocalTracker;
import util.SimpleServer;

import java.io.IOException;

/**
 * Ethan Petuchowski 1/16/15
 */
public class TrackerServer extends SimpleServer implements TrackerSideTrackerProtocol {

    protected final ObjectProperty<LocalTracker> tracker;
    protected final IntegerProperty rcvReqCt = new SimpleIntegerProperty(0);

    public TrackerServer() {
        super();
        tracker = new SimpleObjectProperty<>(new LocalTracker(getAddr()));
    }

    @Override protected void runLoopCode() throws IOException {
        rcvReqCt.add(1);
        String command = bufferedReader.readLine();
        if (command == null) throw new RuntimeException("null command");
        System.out.println("tracker received command: "+command);
    }

    public int getRcvReqCt() { return rcvReqCt.get(); }
    public IntegerProperty rcvReqCtProperty() { return rcvReqCt; }
    public void setRcvReqCt(int newRcvReqCt) { rcvReqCt.set(newRcvReqCt); }
    public LocalTracker getTracker() { return tracker.get(); }
    public ObjectProperty<LocalTracker> trackerProperty() { return tracker; }
    public void setTracker(LocalTracker tracker) { this.tracker.set(tracker); }

    /**
     * Tracker receives P2PFile from Peer looks for it among its LocalSwarms
     * If it exists, add Peer to Swarm
     * Otherwise create a new Swarm for it
     */
    @Override public void addFileRequest() {

    }

    /**
     * Tracker tells a Peer who wants to download a P2PFile about the
     * specific IP Addresses of Peers in an existing Swarm
     * so that the Peer can update its internal view of the Swarm
     */
    @Override public void sendSwarmInfo() {

    }

    /**
     * Tracker sends Peer its full list of Swarms
     * INCLUDING specific IP Addresses of Swarm members
     */
    @Override public void listFiles() {

    }
}

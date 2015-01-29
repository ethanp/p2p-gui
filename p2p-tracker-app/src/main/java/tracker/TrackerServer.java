package tracker;

import Exceptions.ListenerCouldntConnectException;
import Exceptions.NotConnectedException;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import p2p.protocol.tracker.TrackerSideTrackerProtocol;
import servers.SingleThreadedServer;
import util.ServersCommon;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.Socket;

/**
 * Ethan Petuchowski 1/16/15
 */
public class TrackerServer extends SingleThreadedServer implements TrackerSideTrackerProtocol {

    protected final ObjectProperty<LocalTracker> tracker;
    protected final IntegerProperty rcvReqCt = new SimpleIntegerProperty(0);
    protected BufferedReader bufferedReader;

    public TrackerServer() throws ListenerCouldntConnectException, NotConnectedException {
        super();
        tracker = new SimpleObjectProperty<>(new LocalTracker(getExternalSocketAddr()));
    }

    /**
     * TODO  Tracker receives P2PFile from Peer looks for it among its LocalSwarms
     * If it exists, add Peer to Swarm
     * Otherwise create a new Swarm for it
     */
    @Override public void addFileRequest() {}

    /**
     * TODO  Tracker tells a Peer who wants to download a P2PFile about the
     * specific IP Addresses of Peers in an existing Swarm
     * so that the Peer can update its internal view of the Swarm
     */
    @Override public void sendSwarmInfo() {}

    /**
     * TODO  Tracker sends Peer its full list of Swarms
     * INCLUDING specific IP Addresses of Swarm members
     */
    @Override public void listFiles() {}

    @Override protected void dealWithSocket(Socket socket) {
        rcvReqCt.add(1);
        String command = null;
        try {
            bufferedReader = ServersCommon.bufferedReader(socket);
            command = bufferedReader.readLine();
        }
        catch (IOException e) {
            /* If this happens and I figure out why,
               I will do something specific with this exception */
            e.printStackTrace();
        }
        if (command == null) throw new RuntimeException("null command");
        System.out.println("tracker received command: "+command);
        System.out.println("that's as far as I got though (nothing happens now.)");
    }


    /* Getters and Setters */
    public int getRcvReqCt() { return rcvReqCt.get(); }
    public IntegerProperty rcvReqCtProperty() { return rcvReqCt; }
    public void setRcvReqCt(int newRcvReqCt) { rcvReqCt.set(newRcvReqCt); }
    public LocalTracker getTracker() { return tracker.get(); }
    public ObjectProperty<LocalTracker> trackerProperty() { return tracker; }
    public void setTracker(LocalTracker tracker) { this.tracker.set(tracker); }
}

package p2p.peer;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import p2p.protocol.ClientSideTrackerProtocol;
import p2p.tracker.AbstractRemoteTracker;
import util.Common;
import util.SimpleServer;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Ethan Petuchowski 1/16/15
 *
 * The plan is that this will be the "server" code running in the "p2p-client" to service upload
 * requests from external Peers.
 */
public class ClientPeer extends Peer implements ClientSideTrackerProtocol {

    protected final ObjectProperty<ClientPeerServer> server
            = new SimpleObjectProperty<>(new ClientPeerServer());
    protected final BooleanProperty isServing = new SimpleBooleanProperty(false);

    public ClientPeer() {
        super(null);
        server.getValue().start();
    }

    public static void sendEphemeralRequest(AbstractRemoteTracker tracker) {
        ClientPeer clientPeer = new ClientPeer();
        try {
            clientPeer.addFileRequest(tracker);
        }
        catch (IOException e) {
            System.out.println("Couldn't send message from ephemeral client to given tracker");
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Tracker receives P2PFile from Peer looks for it among its LocalSwarms
     * If it exists, add Peer to Swarm
     * Otherwise create a new Swarm for it
     */
    @Override public void addFileRequest(AbstractRemoteTracker tracker) throws IOException {
        Socket trackerSocket = tracker.connect();
        System.out.println("connected to tracker");
        PrintWriter printWriter = Common.printWriter(trackerSocket);
        printWriter.println("sending message to tracker");
    }

    /**
     * Tracker tells a Peer who wants to download a P2PFile about the
     * specific IP Addresses of Peers in an existing Swarm
     * so that the Peer can update its internal view of the Swarm
     */
    @Override public void sendSwarmInfo(AbstractRemoteTracker tracker) throws IOException {
        Socket trackerSocket = tracker.connect();
        System.out.println("connected to tracker");
    }

    /**
     * Tracker sends Peer its full list of Swarms
     * INCLUDING specific IP Addresses of Swarm members
     */
    @Override public void listFiles(AbstractRemoteTracker tracker) throws IOException {
        Socket trackerSocket = tracker.connect();
        System.out.println("connected to tracker");
    }

    class ClientPeerServer extends SimpleServer {
        @Override protected void beforeRunLoop() {
            setServingAddr(getAddr());
            setIsServing(true);
        }

        @Override protected void runLoopCode() throws IOException {
            String command = bufferedReader.readLine();
            if (command == null) throw new RuntimeException("null command");
            System.out.println("client peer received command: "+command);
        }
    }

    public boolean getIsServing() { return isServing.get(); }
    public BooleanProperty isServingProperty() { return isServing; }
    public void setIsServing(boolean isServing) { this.isServing.set(isServing); }
}

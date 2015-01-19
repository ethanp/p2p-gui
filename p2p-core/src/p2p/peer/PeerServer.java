package p2p.peer;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import p2p.exceptions.ConnectToTrackerException;
import p2p.tracker.AbstractRemoteTracker;
import util.SimpleServer;

import java.io.IOException;

/**
 * Ethan Petuchowski 1/16/15
 *
 * This is the "server" code running in the "p2p-client" to service upload
 * requests from external Peers.
 */
public class PeerServer extends Peer {

    protected final ObjectProperty<ClientPeerServer> server
            = new SimpleObjectProperty<>(new ClientPeerServer());
    protected final BooleanProperty isServing = new SimpleBooleanProperty(false);

    public PeerServer() {
        super(null);
        server.getValue().start();
    }

    public static void sendEphemeralRequest(AbstractRemoteTracker tracker) {
        try {
            tracker.addFileRequest();
        }
        catch (ConnectToTrackerException | IOException e) {
            System.out.println("Couldn't send message from ephemeral client to given tracker");
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
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

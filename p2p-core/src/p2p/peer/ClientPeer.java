package p2p.peer;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import util.SimpleServer;

import java.io.IOException;

/**
 * Ethan Petuchowski 1/16/15
 *
 * The plan is that this will be the "server" code running in the "p2p-client" to service upload
 * requests from external Peers.
 */
public class ClientPeer extends Peer {

    protected final ObjectProperty<ClientPeerServer> server = new SimpleObjectProperty<>(null);

    public ClientPeer() {
        super(null);
        server.getValue().start();
    }

    public boolean isServing() { return getServingAddr() != null; }

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

    class ClientPeerServer extends SimpleServer {
        @Override protected void beforeRunLoop() {
            setServingAddr(getAddr());
        }

        @Override protected void runLoopCode() throws IOException {
            String command = bufferedReader.readLine();
            if (command == null) throw new RuntimeException("null command");
            System.out.println("client peer received command: "+command);
        }
    }
}

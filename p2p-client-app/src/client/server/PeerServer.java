package client.server;

import Exceptions.ListenerCouldntConnectException;
import Exceptions.NotConnectedException;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import p2p.exceptions.ConnectToTrackerException;
import p2p.peer.Peer;
import p2p.protocol.fileTransfer.ServerSideChunkProtocol;
import p2p.tracker.AbstractRemoteTracker;
import servers.MultiThreadedServer;
import servers.ServerThread;
import util.Common;

import java.io.IOException;

/**
 * Ethan Petuchowski 1/16/15
 *
 * This is the "server" code running in the "p2p-client" to service upload
 * requests from external Peers.
 */
public class PeerServer extends Peer implements ServerSideChunkProtocol {

    protected final ObjectProperty<ClientPeerServer> server;
    protected final BooleanProperty isServing = new SimpleBooleanProperty(false);

    public PeerServer() throws ListenerCouldntConnectException, NotConnectedException {
        super(null);
        server = new SimpleObjectProperty<>(new ClientPeerServer(Common.CHUNK_SERVE_POOL_SIZE));
        new Thread(server.getValue()).start();
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

    @Override public void serveChunk() {}

    class ClientPeerServer extends MultiThreadedServer<ServerThread> {

        public ClientPeerServer(int poolSize)
                throws ListenerCouldntConnectException, NotConnectedException
        { super(poolSize); }

//        @Override protected void beforeRunLoop() {
//            setServingAddr(getExternalSocketAddr());
//            setIsServing(true);
//        }
//
//        @Override protected void runLoopCode() throws IOException {
//            String command = bufferedReader.readLine();
//            if (command == null) throw new RuntimeException("null command");
//            System.out.println("client peer received command: "+command);
//            switch (command) {
//                case PeerTalk.GET_AVAILABILITIES:
//                    serveAvailabilities();
//                    break;
//            }
//        }

        private void serveAvailabilities() throws IOException {
            System.out.println("serving availabilities");
//            try (ObjectOutputStream objOut = Common.objectOStream(conn);
//                 ObjectInputStream  objIn  = Common.objectIStream(conn))
//            {
//                MetaP2PFile mFile = (MetaP2PFile) objIn.readObject();
//                for (P2PFile pFile : Main.getLocalFiles()) {
//                    if (pFile.getMetaP2PFile().equals(mFile)) {
//                        objOut.writeObject(pFile.getAvailableChunks());
//                    }
//                }
//            }
//            catch (ClassNotFoundException e) { e.printStackTrace(); }

    }

    }


    public boolean getIsServing() { return isServing.get(); }
    public BooleanProperty isServingProperty() { return isServing; }
    public void setIsServing(boolean isServing) { this.isServing.set(isServing); }
}

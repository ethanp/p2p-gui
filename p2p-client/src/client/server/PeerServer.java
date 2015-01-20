package client.server;

import client.Main;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import p2p.exceptions.ConnectToTrackerException;
import p2p.file.meta.MetaP2PFile;
import p2p.file.p2pFile.P2PFile;
import p2p.peer.Peer;
import p2p.protocol.fileTransfer.PeerTalk;
import p2p.protocol.fileTransfer.ServerSideChunkProtocol;
import p2p.tracker.AbstractRemoteTracker;
import util.Common;
import util.SimpleServer;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * Ethan Petuchowski 1/16/15
 *
 * This is the "server" code running in the "p2p-client" to service upload
 * requests from external Peers.
 */
public class PeerServer extends Peer implements ServerSideChunkProtocol {

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

    @Override public void serveChunk() {

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
            switch (command) {
                case PeerTalk.GET_AVAILABILITIES:
                    serveAvailabilities();
                    break;
            }
        }

        private void serveAvailabilities() throws IOException {
            System.out.println("serving availabilities");
            try (ObjectOutputStream objOut = Common.objectOStream(conn);
                 ObjectInputStream  objIn  = Common.objectIStream(conn))
            {
                MetaP2PFile mFile = (MetaP2PFile) objIn.readObject();
                for (P2PFile pFile : Main.getLocalFiles()) {
                    if (pFile.getMetaP2PFile().equals(mFile)) {
                        objOut.writeObject(pFile.getAvailableChunks());
                    }
                }
            }
            catch (ClassNotFoundException e) { e.printStackTrace(); }

    }

    }


    public boolean getIsServing() { return isServing.get(); }
    public BooleanProperty isServingProperty() { return isServing; }
    public void setIsServing(boolean isServing) { this.isServing.set(isServing); }
}

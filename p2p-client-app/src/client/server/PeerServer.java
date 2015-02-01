package client.server;

import Exceptions.ListenerCouldntConnectException;
import Exceptions.NotConnectedException;
import Exceptions.ServersIOException;
import client.p2pFile.P2PFile;
import client.state.ClientState;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import p2p.file.meta.MetaP2PFile;
import p2p.protocol.fileTransfer.ServerSideChunkProtocol;
import servers.MultiThreadedServer;
import servers.ServerThread;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;
import util.Common;
import util.ServersCommon;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 * Ethan Petuchowski 1/16/15
 *
 * This is the "server" code running in the "p2p-client" to service upload
 * requests from external Peers.
 */
public class PeerServer extends MultiThreadedServer<ServerThread> {

    protected final BooleanProperty isServing = new SimpleBooleanProperty(false);

    public PeerServer() throws ListenerCouldntConnectException, NotConnectedException {

        // TODO I HAVE TO PASS A SERVER_SOCKET OR SOMETHING IN HERE TOO,...IT CAN'T JUST BE NULL!@!
        super(null, Common.CHUNK_SERVE_POOL_SIZE);
    }


    class PeerServerThread extends ServerThread implements ServerSideChunkProtocol {

        public PeerServerThread(Socket socket) {
            super(socket);
        }

        private void serveAvailabilities() throws IOException, ServersIOException {
            System.out.println("serving availabilities");
            try (ObjectOutputStream objOut = ServersCommon.objectOStream(socket);
                 ObjectInputStream objIn = ServersCommon.objectIStream(socket)) {
                MetaP2PFile mFile = (MetaP2PFile) objIn.readObject();
                for (P2PFile pFile : ClientState.getLocalFiles()) {
                    if (pFile.getMetaP2PFile().equals(mFile)) {
                        objOut.writeObject(pFile.getAvailableChunks());
                    }
                }
            }
            catch (ClassNotFoundException e) { e.printStackTrace(); }

        }

        /* from ServerSideChunkProtocol */
        @Override public void serveChunk() {
            // TODO implement PeerServerThread serveChunk
            throw new NotImplementedException();
        }

        @Override public void run() {
           // TODO implement PeerServerThread run
           throw new NotImplementedException();
        }
    }



    public boolean getIsServing() { return isServing.get(); }
    public BooleanProperty isServingProperty() { return isServing; }
    public void setIsServing(boolean isServing) { this.isServing.set(isServing); }
}

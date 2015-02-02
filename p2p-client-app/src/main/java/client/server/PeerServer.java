package client.server;

import Exceptions.ListenerCouldntConnectException;
import Exceptions.NoInternetConnectionException;
import Exceptions.ServersIOException;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import p2p.protocol.fileTransfer.ServerSideChunkProtocol;
import servers.MultiThreadedServer;
import servers.Server;
import servers.ServerThread;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;
import util.Common;

import java.net.Socket;

/**
 * Ethan Petuchowski 1/16/15
 *
 * This is the "server" code running in the "p2p-client" to service upload
 * requests from external Peers.
 */
public class PeerServer extends MultiThreadedServer<PeerServer.ConnectionHandler> {

    protected final BooleanProperty isServing = new SimpleBooleanProperty(false);

    public PeerServer() throws ListenerCouldntConnectException, NoInternetConnectionException, ServersIOException {
        super(Server.LOWEST_PORT_FORWARDED,
              Server.HIGHEST_PORT_FORWARDED,
              Common.CHUNK_SERVE_POOL_SIZE);
    }


    class ConnectionHandler extends ServerThread implements ServerSideChunkProtocol {

        public ConnectionHandler(Socket socket) {
            super(socket);
        }

        @Override public void serveAvailabilities() {
            System.out.println("serving availabilities");
//                MetaP2PFile mFile = (MetaP2PFile) objIn.readObject();
//                for (P2PFile pFile : ClientState.getLocalFiles()) {
//                    if (pFile.getMetaP2PFile().equals(mFile)) {
//                        objOut.writeObject(pFile.getAvailableChunks());
//                    }
//                }
//            }
        }

        /* from ServerSideChunkProtocol */
        @Override public void serveChunk() {
            // TODO implement ConnectionHandler serveChunk
            throw new NotImplementedException();
        }

        @Override public void run() {
           // TODO implement ConnectionHandler run
           throw new NotImplementedException();
        }
    }



    public boolean getIsServing() { return isServing.get(); }
    public BooleanProperty isServingProperty() { return isServing; }
    public void setIsServing(boolean isServing) { this.isServing.set(isServing); }
}

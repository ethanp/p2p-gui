package client.server;

import Exceptions.ListenerCouldntConnectException;
import Exceptions.NoInternetConnectionException;
import Exceptions.ServersIOException;
import client.p2pFile.P2PFile;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import p2p.exceptions.CreateP2PFileException;
import p2p.file.chunk.Chunk;
import p2p.file.meta.MetaP2P;
import p2p.protocol.fileTransfer.PeerTalk;
import p2p.protocol.fileTransfer.ServerSideChunkProtocol;
import servers.MultiThreadedServer;
import servers.Server;
import servers.ServerTaskRunner;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;
import util.Common;
import util.ServersCommon;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownServiceException;

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

    @Override protected ConnectionHandler createTask(Socket socket) throws ServersIOException {
        return new ConnectionHandler(socket);
    }


    class ConnectionHandler extends ServerTaskRunner implements ServerSideChunkProtocol {

        BufferedOutputStream outputStream;
        BufferedReader reader;
        public ConnectionHandler(Socket socket) throws ServersIOException {
            super(socket);
            outputStream = ServersCommon.buffOStream(socket);
            reader = ServersCommon.bufferedReader(socket);
        }

        @Override public void serveAvbl() {
            System.out.println("serving availabilities");

            /* listen for which file we're talking about */
            MetaP2P mFile = null;
            try {
                mFile = MetaP2P.deserializeFromReader(reader);
            }
            catch (IOException e) {
                // just burying my head, hoping this doesn't happen
                e.printStackTrace();
            }

            /* from the mFile, they already know the size of the BitSet */
            /* so we just need to send the receiver the BitSet itself */
//            P2PFile pFile = ClientState.getLocalP2PFile(mFile);
//            ChunksForService c = pFile.getAvailableChunks();

        }

        /* from ServerSideChunkProtocol */
        @Override public void serveChunk() throws IOException, CreateP2PFileException {
            /* determine which file we're talking about */
            MetaP2P meta = MetaP2P.deserializeFromReader(reader);

            /* make sure I have that file */
            P2PFile pFile = null;
//            for (P2PFile localFile : ClientState.getLocalFiles()) {
//                if (meta.equals(localFile.getMetaPFile())) {
//                    pFile = localFile;
//                    break;
//                }
//            }

            /* determine which chunk we're talking about */
            String chkIdxStr = reader.readLine();
            int chunkIdx = Integer.parseInt(chkIdxStr);

            /* report errors if something is the matter */
            if (pFile == null)
                ServersCommon.streamIntLine(outputStream, PeerTalk.FromPeer.FILE_NOT_AVAILABLE);
            else if (chunkIdx < 0 || chunkIdx >= pFile.getNumChunks())
                ServersCommon.streamIntLine(outputStream, PeerTalk.FromPeer.OUT_OF_BOUNDS);
            else if (!pFile.hasChunk(chunkIdx))
                ServersCommon.streamIntLine(outputStream, PeerTalk.FromPeer.CHUNK_NOT_AVAILABLE);

            /* send the data to the requester */
            else {
                Chunk chunk = pFile.getChunk(chunkIdx);
                ServersCommon.streamIntLine(outputStream, chunk.size()); // inform downloader of size
                outputStream.write(chunk.getData());                     // serve
            }
            outputStream.flush();
        }



        @Override public void run() {
            try {
                String command = reader.readLine();
                if (command == null) {
                    throw new NotImplementedException();
                }
                System.out.println("PeerServer received command: "+command);
                switch (command) {
                    case PeerTalk.ToPeer.GET_CHUNK:
                        try {
                            serveChunk();
                        }
                        catch (CreateP2PFileException e) {
                            e.printStackTrace();
                        }
                        break;
                    case PeerTalk.ToPeer.GET_AVBL:
                        serveAvbl();
                        break;
                    default:
                        throw new UnknownServiceException("PeerServer can't handle a "+command);
                }
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public boolean getIsServing() { return isServing.get(); }
    public BooleanProperty isServingProperty() { return isServing; }
    public void setIsServing(boolean isServing) { this.isServing.set(isServing); }
}

package client.server;

import Exceptions.ListenerCouldntConnectException;
import Exceptions.NoInternetConnectionException;
import Exceptions.ServersIOException;
import client.p2pFile.P2PFile;
import client.state.ClientState;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import p2p.exceptions.CreateP2PFileException;
import p2p.file.chunk.Chunk;
import p2p.file.meta.MetaP2PFile;
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
        @Override public void serveChunk() throws IOException, CreateP2PFileException {
            /* determine which file we're talking about */
            MetaP2PFile meta = MetaP2PFile.deserializeFromReader(reader);

            /* make sure I have that file */
            P2PFile pFile = null;
            for (P2PFile localFile : ClientState.getLocalFiles()) {
                if (meta.equals(localFile.getMetaP2PFile())) {
                    pFile = localFile;
                    break;
                }
            }
            /* determine which chunk we're talking about */
            String chkIdxStr = reader.readLine();
            int chunkIdx = Integer.parseInt(chkIdxStr);

            /* if I either don't have the file or the chunk, respond with -1:DOES_NOT_EXIST */
            if (pFile == null || !pFile.hasChunk(chunkIdx)) {
                String minusOne = String.valueOf(PeerTalk.ToPeer.DOES_NOT_EXIST);
                outputStream.write(minusOne.getBytes());
            }

            /* otherwise send the data to the requester */
            else {
                Chunk chunk = pFile.getChunk(chunkIdx);

                /* first inform downloader of chunk size */
                String sizeString = chunk.size()+"\n";
                outputStream.write(sizeString.getBytes());

                /* then serve the chunk */
                outputStream.write(chunk.getData());
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
                    case PeerTalk.ToPeer.GET_AVAILABILITIES:
                        serveAvailabilities();
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

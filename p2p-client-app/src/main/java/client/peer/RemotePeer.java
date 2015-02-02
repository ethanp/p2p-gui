package client.peer;

import Exceptions.FailedToFindServerException;
import Exceptions.ServersIOException;
import client.download.ChunkDownload;
import client.p2pFile.P2PFile;
import javafx.beans.property.MapProperty;
import javafx.beans.property.SimpleMapProperty;
import p2p.exceptions.ConnectToPeerException;
import p2p.file.meta.MetaP2PFile;
import p2p.peer.ChunksForService;
import p2p.peer.Peer;
import p2p.protocol.fileTransfer.PeerTalk;
import util.ServersCommon;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * Ethan Petuchowski 1/18/15
 *
 * This is the Client's understanding of live peers
 * including what chunks they have of which files
 */
public class RemotePeer extends Peer {

    protected final MapProperty<MetaP2PFile, ChunksForService> chunksOfFiles;

    protected RemotePeer(InetSocketAddress socketAddr) { super(socketAddr);
        chunksOfFiles = new SimpleMapProperty<>();
    }

    public boolean hasChunk(int chunkIdx, MetaP2PFile mFile) {
        return chunksOfFiles.containsKey(mFile)
            && chunksOfFiles.get(mFile).hasIdx(chunkIdx);
    }

    public class ChunkAvblUpdater implements Runnable {
        MetaP2PFile metaFile;

        public ChunkAvblUpdater(MetaP2PFile mFile) { metaFile = mFile; }

        @Override public void run() {
            updateAvailabilities();
        }

        protected void updateAvailabilities() {
            try (Socket             peerConn = connectToPeer();
                 ObjectOutputStream objOut   = ServersCommon.objectOStream(peerConn);
                 ObjectInputStream  objIn    = ServersCommon.objectIStream(peerConn);
                 PrintWriter        cmdPrt   = ServersCommon.printWriter(peerConn))
            {
                cmdPrt.println(PeerTalk.ToTracker.GET_AVAILABILITIES);
                cmdPrt.flush();
                objOut.writeObject(metaFile);
                try {
                    ChunksForService cfs = (ChunksForService) objIn.readObject();
                    synchronized (chunksOfFiles) { chunksOfFiles.put(metaFile, cfs); }
                }
                catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
            catch (IOException | ConnectToPeerException | ServersIOException e) {
                e.printStackTrace();
            }
        }
    }

    public ChunkAvblUpdater avblUpdater(MetaP2PFile mFile) {
        return new ChunkAvblUpdater(mFile);
    }

    protected Socket connectToPeer() throws ConnectToPeerException {
        try {
            return ServersCommon.connectToInetSocketAddr(getServingAddr());
        }
        catch (FailedToFindServerException e) {
            e.printStackTrace();
            throw new ConnectToPeerException("Couldn't connect to peer at "+
                                             ServersCommon.ipPortToString(getServingAddr()));
        }
    }

    /* TODO this will be called by a FileDownload object (it is not called at all currently) */
    public void requestChunk(P2PFile pFile, int chunkIdx) throws ConnectToPeerException, IOException, ServersIOException {

        /* just a logic-check to make sure that at least this client THINKS the peer
         * has the desired Chunk available for download
         */
        assert chunksOfFiles.get(pFile.getMetaP2PFile())
                            .hasIdx(chunkIdx);

        // TODO create the appropriate ChunkDownload and x.start() it
        Socket peerConn = connectToPeer();
        ChunkDownload chunkDownloadObject = new ChunkDownload(chunkIdx, peerConn, pFile);

        /* TODO probably instead of just start()ing it in here, it should be added
         * to a field somewhere on the Client (i.e. Current User) containing a
         * "ThreadPool<ChunkDownloads>
         */
        new Thread(chunkDownloadObject).start();
    }
}

package p2p.peer;

import javafx.beans.property.MapProperty;
import javafx.beans.property.SimpleMapProperty;
import p2p.exceptions.ConnectToPeerException;
import p2p.file.meta.MetaP2PFile;
import p2p.file.p2pFile.P2PFile;
import p2p.protocol.fileTransfer.PeerTalk;
import p2p.transfer.ChunkDownload;
import util.Common;

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

    public class ChunkAvailabilityUpdater implements Runnable {
        MetaP2PFile metaFile;

        public ChunkAvailabilityUpdater(MetaP2PFile mFile) { metaFile = mFile; }

        @Override public void run() {
            try (Socket             peerConn = connectToPeer();
                 ObjectOutputStream objOut   = Common.objectOStream(peerConn);
                 ObjectInputStream  objIn    = Common.objectIStream(peerConn);
                 PrintWriter        cmdPrt   = Common.printWriter(peerConn))
            {
                cmdPrt.println(PeerTalk.GET_AVAILABILITIES);
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
            catch (IOException | ConnectToPeerException e) {
                e.printStackTrace();
            }
        }
    }

    public ChunkAvailabilityUpdater createAvailabilityUpdater(MetaP2PFile mFile) {
        return new ChunkAvailabilityUpdater(mFile);
    }

    private Socket connectToPeer() throws ConnectToPeerException {
        try {
            return Common.connectToInetSocketAddr(getServingAddr());
        }
        catch (IOException e) {
            e.printStackTrace();
            throw new ConnectToPeerException("Couldn't connect to peer at "+
                                             Common.ipPortToString(getServingAddr()));
        }
    }

    /* TODO this will be called by a FileDownload object (it is not called at all currently) */
    public void downloadChunk(P2PFile pFile, int chunkIdx) throws ConnectToPeerException {

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

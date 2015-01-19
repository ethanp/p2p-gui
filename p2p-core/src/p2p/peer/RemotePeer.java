package p2p.peer;

import javafx.beans.property.MapProperty;
import javafx.beans.property.SimpleMapProperty;
import p2p.exceptions.ConnectToPeerException;
import p2p.file.meta.MetaP2PFile;
import p2p.file.p2pFile.P2PFile;
import p2p.transfer.ChunkDownload;
import util.Common;

import java.io.IOException;
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

    /* TODO this will be called by a FileDownload object (it is not called at all currently) */
    public void downloadChunk(P2PFile pFile, int chunkIdx) throws ConnectToPeerException {

        /* just a logic-check to make sure that at least this client THINKS the peer
         * has the desired Chunk available for download
         */
        assert chunksOfFiles.get(pFile.getMetaP2PFile())
                            .hasIdx(chunkIdx);

        // TODO create the appropriate ChunkDownload and x.start() it
        try (Socket peerConn = Common.connectToInetSocketAddr(getServingAddr())) {
            ChunkDownload chunkDownloadObject = new ChunkDownload(chunkIdx, peerConn, pFile);

            /* TODO probably instead of just start()ing it in here, it should be added
             * to a field somewhere on the Client (i.e. Current User) containing a
             * "ThreadPool<ChunkDownloads>
             */
            chunkDownloadObject.start();
        }
        catch (IOException e) {
            throw new ConnectToPeerException("Couldn't connect to peer at "+
                                             Common.ipPortToString(getServingAddr()));
        }
    }
}

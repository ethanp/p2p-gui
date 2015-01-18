package p2p.peer;

import javafx.beans.property.MapProperty;
import p2p.file.meta.MetaP2PFile;
import p2p.file.meta.P2PFile;

import java.net.InetSocketAddress;

/**
 * Ethan Petuchowski 1/18/15
 *
 * This is the Client's understanding of live peers
 * including what chunks they have of which files
 */
public class RemotePeer extends Peer {

    protected final MapProperty<MetaP2PFile, ChunksForService> chunksOfFiles;

    protected RemotePeer(InetSocketAddress socketAddr) { super(socketAddr);
        chunksOfFiles = null;
    }

    /* TODO this will be called by a FileDownload object */
    public void downloadChunk(P2PFile pFile, int chunkIdx) {

    }
}

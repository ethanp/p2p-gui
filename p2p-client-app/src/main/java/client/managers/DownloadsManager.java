package client.managers;

import client.download.FileDownload;
import client.peer.Peer;
import client.state.ClientState;
import p2p.file.meta.MetaP2P;

import java.net.InetSocketAddress;
import java.util.Collection;

/**
 * Ethan Petuchowski 2/8/15
 */
public class DownloadsManager {

    Collection<FileDownload> fileDownloads;
    Collection<Peer> connectedPeers;
    ClientState state;

    public DownloadsManager(ClientState state) {
        this.state = state;
    }

    /**
     * This is called by the UI layer (GUI *or* CLI) when the User chooses to download a file.
     * It creates a `FileDownload` object which in turn uses the existing
     * `connectedPeers` creates new ones, from whom `Chunk`s are solicited.
     */
    public void downloadFile(MetaP2P mFile) {
        fileDownloads.add(new FileDownload(state, mFile));
    }

    /**
     * This is for use in the FileDownload.
     * When figuring out which peers are associated with that download
     * it uses this to create the appropriate connections to the peers.
     */
    public boolean getPeer(InetSocketAddress address) {
        /* If the peer is not already connected */
        /* try to connect to the peer */
        /* if that doesn't work */
        return false;
        /* otherwise */
        // return true;
    }
}

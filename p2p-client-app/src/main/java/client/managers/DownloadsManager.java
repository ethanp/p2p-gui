package client.managers;

import client.download.FileDownload;
import client.peer.Peer;
import javafx.collections.ObservableList;
import p2p.file.meta.MetaP2P;

import java.net.InetSocketAddress;

/**
 * Ethan Petuchowski 2/8/15
 */
public class DownloadsManager {

    ObservableList<FileDownload> fileDownloads;
    ObservableList<Peer> connectedPeers;

    public void downloadFile(MetaP2P mFile) {
        /* if we already have this file, don't download it */
        /* get trackers who know about this file */
        /* update our knowledge of the swarms they have for this file */
        /* create the appropriate FileDownload object */
        /* the FileDownload will initiate whatever peer connections it
           needs that we don't already have open */
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

package client.state;

import Exceptions.FailedToFindServerException;
import Exceptions.ServersIOException;
import client.p2pFile.FileDownload;
import client.peer.Peer;
import p2p.exceptions.ConnectToPeerException;
import p2p.exceptions.FileUnavailableException;
import p2p.file.MetaP2P;
import util.Common;

import java.nio.file.FileAlreadyExistsException;
import java.util.Collection;
import java.util.HashSet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Ethan Petuchowski 2/8/15
 */
public class DownloadsManager {

    Collection<FileDownload> fileDownloads;
    Collection<Peer> connectedPeers;
    ClientState clientState;
    ExecutorService downloadPool;

    public DownloadsManager(ClientState state) {
        clientState = state;
        connectedPeers = new HashSet<>();
        downloadPool = Executors.newFixedThreadPool(Common.FILE_DOWNLOADS_POOL_SIZE);
    }

    /**
     * This is called by the UI layer (GUI *or* CLI; via `clientState`) when the User
     * chooses to download a file.
     *
     * It creates a `FileDownload` object which connects to as many Peers as possible
     * (some may already be connected), from whom `Chunk`s are solicited.
     */
    public FileDownload downloadMeta(MetaP2P mFile) throws FileAlreadyExistsException, FileUnavailableException {
        FileDownload fileDownload = new FileDownload(clientState, mFile);
        fileDownloads.add(fileDownload);
        downloadPool.submit(fileDownload);
        return fileDownload;
    }

    /**
     * This is for use in the FileDownload.
     * When figuring out which peers are associated with that download
     * it uses this to create the appropriate connections to the peers.
     */
    public boolean connectToPeer(Peer peer) {
        /* If the peer is not already connected */
        if (!connectedPeers.contains(peer)) {
            /* try to connect to the peer */
            try {
                peer.connect();
                connectedPeers.add(peer);
            }
            /* if that doesn't work return false */
            catch (FailedToFindServerException | ConnectToPeerException | ServersIOException e) {
                System.err.println("Couldn't connect to peer: "+peer.addrStr());
                return false;
            }
        }
        /* we are connected */
        return true;
    }
}

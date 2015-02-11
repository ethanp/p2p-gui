package client.download;

import Exceptions.FailedToFindServerException;
import client.p2pFile.P2PFile;
import client.peer.Peer;
import client.state.ClientState;
import p2p.exceptions.FileUnavailableException;
import p2p.file.meta.MetaP2P;
import util.Common;

import java.io.File;
import java.nio.channels.AlreadyConnectedException;
import java.nio.file.FileAlreadyExistsException;
import java.util.Collection;
import java.util.HashSet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Ethan Petuchowski 1/18/15
 */
public class FileDownload implements Runnable {

    public Collection<Peer> getConnectedPeersWFile() { return connectedPeersWFile; }

    private final Collection<Peer> connectedPeersWFile = new HashSet<>();
    private Collection<Peer> peersWFile;
    private final P2PFile pFile;
    private final File localFile;
    private final ClientState state;

    private static final ExecutorService threadPool
            = Executors.newFixedThreadPool(Common.CHUNK_AVAILABILITY_POOL_SIZE);

    public FileDownload(ClientState clientState, MetaP2P metaP2P) throws FileAlreadyExistsException, FileUnavailableException {
        state = clientState;

        localFile = new File(state.getDownloadsDir(), metaP2P.getFilename());
        pFile = new P2PFile(localFile, metaP2P);

        /* if we already have this file, don't download it */
        if (state.hasLocalFile(metaP2P))
            throw new FileAlreadyExistsException(
                    state.getLocalP2PFile(metaP2P).getFilename());

        /* get all relevant peers from the trackers who know about this file */
        peersWFile = state.collectPeersServing(metaP2P);
        if (peersWFile.isEmpty())
            throw new FileUnavailableException(
                    "no peers with this file were found. Try updating trackers?");
    }

    @Override public void run() {
        /* figure out which peers we're already connected to and connect to whomever possible */
        for (Peer peer : peersWFile) {
            if (state.isConnectedTo(peer)) {
                connectedPeersWFile.add(peer);
            }
            else {
                try {
                    peer.connect();
                    /* I never tell state.justConnectedTo(peer);
                     * I'll do that IF NECESSARY */
                }
                catch (FailedToFindServerException e) {
                    System.err.println("Couldn't connect to peer at "+peer.addrStr());
                }
                catch (AlreadyConnectedException e) {
                    System.err.println("We already were connected to peer at"+peer.addrStr());
                }
            }
        }

        /* update Client's knowledge of which
         * Chunks each Peer has for this File */
        for (Peer peer : connectedPeersWFile)
            peer.updateChunkAvbl(pFile.getMetaPFile());
    }

    public void markChunkAsAvbl(int idx) { pFile.markChunkAsAvbl(idx); }
    public P2PFile getPFile() { return pFile;}
    public MetaP2P getMFile() { return pFile.getMetaPFile(); }
}

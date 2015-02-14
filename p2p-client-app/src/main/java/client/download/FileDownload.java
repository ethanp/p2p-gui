package client.download;

import client.p2pFile.P2PFile;
import client.peer.Peer;
import client.state.ClientState;
import p2p.exceptions.FileUnavailableException;
import p2p.exceptions.InvalidDataException;
import p2p.file.meta.MetaP2P;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.FileAlreadyExistsException;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

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

    /**
     * Figure out which Peers we're already connected to,
     * connect to whomever possible,
     * and start downloading the File from them.
     */
    @Override public void run() {
        for (Peer peer : peersWFile) {
            if (state.connectToPeer(peer)) {
                connectedPeersWFile.add(peer);
                peer.addDownload(this);
            }
        }
    }

    public void markChunkAsAvbl(int idx) { pFile.markChunkAsAvbl(idx); }
    public P2PFile getPFile() { return pFile;}
    public MetaP2P getMFile() { return pFile.getMetaPFile(); }

    public List<Integer> decideChunksToDownload(Peer peer) {

        // real creative.
        int idx = pFile.getAvailableChunks().firstUnavailableChunk();

        // TODO the plan is that I will also keep track of
        //      List<Set<Peer>> whoIsDownloadingWhichChunks ??
        return Arrays.asList(idx);
    }

    /**
     * This could be called in multiple threads at the same time.
     * It obtains the lock on P2PFile.ChunksForService checks if its already written,
     * and marks it as written, before and writing it to disk.
     */
    public void writeToDisk(byte[] response, int chunkIdx) throws InvalidDataException {

        if (!getMFile().verifyChunk(response, chunkIdx)) {
            throw new InvalidDataException("chunk data didn't match hash");
        }

        /* I wonder if this could use some sort of readAndUpdate atomic operation
         * or whatever it's called
         */
        synchronized (pFile.getAvailableChunks()) {
            if (pFile.hasChunk(chunkIdx))
                return;
            markChunkAsAvbl(chunkIdx);
        }

        try {
            RandomAccessFile fileOutput = new RandomAccessFile(getPFile().getLocalFile(), "rw");
            fileOutput.seek(getPFile().getBytesPerChunk()*chunkIdx);
            fileOutput.write(response);
            fileOutput.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
    }
}

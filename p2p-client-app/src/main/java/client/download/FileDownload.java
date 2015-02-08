package client.download;

import client.p2pFile.P2PFile;
import client.peer.Peer;
import client.tracker.swarm.ClientSwarm;
import javafx.collections.FXCollections;
import javafx.collections.ObservableSet;
import p2p.exceptions.ConnectToPeerException;
import p2p.file.meta.MetaP2P;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;
import util.Common;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Ethan Petuchowski 1/18/15
 */
public class FileDownload implements Runnable {

    private final ObservableSet<ClientSwarm> clientSwarms;
    private final P2PFile     pFile;
    private final File        localFile;

    private static final ExecutorService threadPool
            = Executors.newFixedThreadPool(Common.CHUNK_AVAILABILITY_POOL_SIZE);

    /**
     * creates the required OS-File object and P2PFile
     *
     * @param downloadDirectory the parent directory for the file to be downloaded
     * @param swarm the (first) swarm of the file were downloading.
     *              More swarms belonging to other trackers can be added after the
     *              fact to (potentially) make the download faster.
     */
    public FileDownload(File downloadDirectory, ClientSwarm swarm) {
        clientSwarms = FXCollections.observableSet(swarm);
        localFile = new File(downloadDirectory, swarm.getMetaP2P().getFilename());
        pFile = new P2PFile(localFile, swarm.getMetaP2P());
    }

    private Set<Peer> allKnownPeers() {
        Set<Peer> peers = new HashSet<>();
        for (ClientSwarm clientSwarm : clientSwarms)
            for (Peer peer : clientSwarm.getAllPeers())
                if (!peers.contains(peer))
                    peers.add(peer);
        return peers;
    }

    @Override public void run() {
        try {
            updateAllChunkAvailabilities();
        }
        catch (ConnectToPeerException | IOException e) {
            System.err.println(e.getMessage());
            e.printStackTrace();
        }
    }

    public void updateAllChunkAvailabilities() throws ConnectToPeerException, IOException {

        /* TODO this should be using the thread pool
         * but I would like it to still be possible to run it synchronously
         */
        for (Peer peer : allKnownPeers()) {
            peer.updateAvblForSync(pFile.getMetaPFile());
        }

        try {
            /* only allow up to 5 seconds for a Peer to respond with its ChunkAvailability's */
            threadPool.awaitTermination(5, TimeUnit.SECONDS);
        }
        catch (InterruptedException e) {
            // Maybe there were TOO MANY Peers...(happy reason for an Exception to be thrown)
            System.err.println("updating chunk availabilities timed-out");
            System.err.println(e.getMessage());
            e.printStackTrace();
        }
        synchronized (clientSwarms) { clientSwarms.notifyAll(); }
    }

    private int[] getChunkAvailabilityCounts() {
        int[] avb = new int[pFile.getNumChunks()];
        for (int i = 0; i < pFile.getNumChunks(); i++) {
            for (Peer peer : allKnownPeers()) {
                if (peer.hasChunk(i, pFile.getMetaPFile())) {
                    avb[i]++;
                }
            }
        }
        return avb;
    }

    public int getChunkIdxToDownload() {
        // TODO implement FileDownload getChunkIdxToDownload
        throw new NotImplementedException();
    }

    public void markChunkAsAvbl(int idx) { pFile.markChunkAsAvbl(idx); }
    public P2PFile getPFile() { return pFile;}
    public MetaP2P getMFile() { return pFile.getMetaPFile(); }
    public ObservableSet<ClientSwarm> getClientSwarms() { return clientSwarms; }
}

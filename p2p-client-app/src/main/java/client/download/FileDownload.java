package client.download;

import client.p2pFile.P2PFile;
import client.peer.RemotePeer;
import client.tracker.swarm.ClientSwarm;
import javafx.collections.FXCollections;
import javafx.collections.ObservableSet;
import p2p.exceptions.ConnectToPeerException;
import p2p.file.meta.MetaP2PFile;
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

    public FileDownload(File parentDir, ClientSwarm swarm) {
        clientSwarms = FXCollections.observableSet(swarm);
        // TODO implement FileDownload FileDownload
        throw new NotImplementedException();
//        pFile = P2PFile.newP2PFileInDir(parentDir, swarm.getMetaP2P());
//        localFiles.add(pFile);
//        localFile = pFile.getLocalFile();
    }

    private Set<RemotePeer> getPeersFromSwarms() {
        Set<RemotePeer> peers = new HashSet<>();
        for (ClientSwarm clientSwarm : clientSwarms)
            for (RemotePeer peer : clientSwarm.getAllPeers())
                if (!peers.contains(peer))
                    peers.add(peer);
        return peers;
    }

    @Override public void run() {
        /* TODO use `RemotePeer`'s `requestChunk()` which will create `ChunkDownloads`
         * and deposit `Chunk`s of the file directly into the `localFile` */
        try {
            updateAllChunkAvailabilities();
        }
        catch (ConnectToPeerException | IOException e) {
            System.err.println(e.getMessage());
            e.printStackTrace();
        }

        int[] availabilityCounts = getChunkAvailabilityCounts();

    }

    private void updateAllChunkAvailabilities() throws ConnectToPeerException, IOException {

        for (RemotePeer peer : getPeersFromSwarms()) {
            threadPool.submit(peer.avblUpdater(pFile.getMetaPFile()));
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
    }

    private int[] getChunkAvailabilityCounts() {
        int[] avb = new int[pFile.getNumChunks()];
        for (int i = 0; i < pFile.getNumChunks(); i++) {
            for (RemotePeer peer : getPeersFromSwarms()) {
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
    public MetaP2PFile getMFile() { return pFile.getMetaPFile(); }
}

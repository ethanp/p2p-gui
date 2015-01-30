package client.download;

import client.tracker.swarm.ClientSwarm;
import p2p.exceptions.ConnectToPeerException;
import client.p2pFile.P2PFile;
import client.peer.RemotePeer;
import util.Common;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Ethan Petuchowski 1/18/15
 */
public class FileDownload implements Runnable {

    protected final ClientSwarm clientSwarm;
    protected final P2PFile     pFile;
    protected final File        localFile;

    protected static final ExecutorService threadPool
            = Executors.newFixedThreadPool(Common.CHUNK_AVAILABILITY_POOL_SIZE);

    public FileDownload(File parentDir, ClientSwarm swarm, Collection<P2PFile> localFiles) {
        clientSwarm = swarm;
        pFile = P2PFile.newP2PFileInDir(parentDir, swarm.getMetaP2P());
        localFiles.add(pFile);
        localFile = pFile.getLocalFile();
    }

    @Override public void run() {
        /* TODO use `RemotePeer`'s `downloadChunk()` which will create `ChunkDownloads`
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
        for (RemotePeer peer : clientSwarm.getAllPeers()) {
            RemotePeer.ChunkAvailabilityUpdater updateAvailabilityTask =
                    peer.createAvailabilityUpdater(pFile.getMetaP2PFile());
            threadPool.submit(updateAvailabilityTask);
        }
        try {
            threadPool.awaitTermination(5, TimeUnit.SECONDS);
        }
        catch (InterruptedException e) {
            System.err.println("updating chunk availabilities timed-out");
            e.printStackTrace();
        }
    }

    private int[] getChunkAvailabilityCounts() {
        int[] avb = new int[pFile.getNumChunks()];
        for (int i = 0; i < pFile.getNumChunks(); i++) {
            for (RemotePeer peer : clientSwarm.getAllPeers()) {
                if (peer.hasChunk(i, pFile.getMetaP2PFile())) {
                    avb[i]++;
                }
            }
        }
        return avb;
    }

}

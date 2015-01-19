package p2p.transfer;

import p2p.exceptions.ConnectToPeerException;
import p2p.file.p2pFile.P2PFile;
import p2p.peer.RemotePeer;
import p2p.tracker.swarm.ClientSwarm;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Ethan Petuchowski 1/18/15
 */
public class FileDownload extends Thread {
    protected final P2PFile pFile;
    protected final File localFile;
    protected final ClientSwarm clientSwarm;

    ExecutorService threadPool = Executors.newFixedThreadPool(10);

    public FileDownload(P2PFile pFile, File localFile, ClientSwarm swarm) {
        this.pFile = pFile;
        this.localFile = localFile;
        clientSwarm = swarm;
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
}

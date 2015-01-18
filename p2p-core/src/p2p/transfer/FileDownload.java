package p2p.transfer;

import p2p.file.meta.P2PFile;
import p2p.tracker.swarm.ClientSwarm;

import java.io.File;

/**
 * Ethan Petuchowski 1/18/15
 */
public class FileDownload extends Thread {
    protected final P2PFile pFile;
    protected final File localFile;

    /* TODO seeders and leechers in here should be `RemotePeer`s, not just `Peer`s */
    protected final ClientSwarm clientSwarm;

    public FileDownload(P2PFile pFile, File localFile, ClientSwarm swarm) {
        this.pFile = pFile;
        this.localFile = localFile;
        clientSwarm = swarm;
    }

    @Override public void run() {
        /* TODO use `RemotePeer`'s `downloadChunk()` which will create `ChunkDownloads`
         * and deposit `Chunk`s of the file directly into the `localFile` */

     }
}

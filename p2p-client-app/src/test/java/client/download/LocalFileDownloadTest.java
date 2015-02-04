package client.download;

import client.peer.RemotePeer;
import client.state.ClientState;
import client.tracker.RemoteTracker;
import client.tracker.swarm.ClientSwarm;
import org.junit.Test;
import p2p.exceptions.CreateP2PFileException;
import p2p.file.meta.MetaP2PFile;

import java.io.File;

public class LocalFileDownloadTest {
    @Test public void testCreateFileDownload() throws Exception {
        FileDownload fileDownload = makeFileDownload();

    }

    // TODO maybe I should use this in the PeerDownload tests
    public FileDownload makeFileDownload() throws CreateP2PFileException {
        RemoteTracker stubTracker = null;
        MetaP2PFile mFile = new MetaP2PFile("filename", 9, "file hash");
        ClientSwarm clientSwarm = new ClientSwarm(mFile, stubTracker);
        RemotePeer myselfAsPeer = new RemotePeer(ClientState.getExternalSocketAddr());
        clientSwarm.getSeeders().add(myselfAsPeer);
        FileDownload fileDownload = new FileDownload(new File("parentDir"), clientSwarm);
        return fileDownload;
    }
}

package client.download;

import client.LocalDownloadTest;
import org.junit.Test;
import p2p.exceptions.ConnectToPeerException;
import p2p.exceptions.CreateP2PFileException;

import java.io.IOException;

public class LocalFileDownloadTest extends LocalDownloadTest {

    @Test public void testGetChunkAvailability() throws CreateP2PFileException, ConnectToPeerException, IOException, InterruptedException {
//        FileDownload fileDownload = makeSequenceFileDownload(sequenceMeta);
//        ObservableSet<ClientSwarm> clientSwarms = fileDownload.getClientSwarms();
//        ClientSwarm relevantSwarm = (ClientSwarm) clientSwarms.toArray()[0];
//        Peer myselfAsPeer = relevantSwarm.getSeeders().get(0);
//
//        assertFalse(myselfAsPeer.hasChunk(0, sequenceMeta));
//
//        fileDownload.updateAllChunkAvailabilities();
//        synchronized (clientSwarms) { clientSwarms.wait(); }
//
//        assertTrue(myselfAsPeer.hasChunk(0, sequenceMeta));
    }

    @Test public void testCreateFileDownload() throws Exception {
        FileDownload fileDownload = makeSequenceFileDownload(sequenceMeta);

    }
}

package client;

import client.p2pFile.P2PFile;
import client.peer.Peer;
import client.state.ClientState;
import client.tracker.FakeRemoteTracker;
import client.tracker.RemoteTracker;
import client.tracker.swarm.ClientSwarm;
import javafx.collections.FXCollections;
import org.junit.Before;
import org.junit.Test;
import p2p.exceptions.CreateP2PFileException;
import p2p.file.meta.MetaP2P;
import tracker.TrackerPeer;
import tracker.TrackerState;
import tracker.TrackerSwarm;
import util.Digester;
import util.ServersCommon;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.regex.Pattern;

import static org.junit.Assert.assertTrue;

public class ClientCLITest {

    ClientCLI cli;

    @Before public void setUp() throws Exception { cli = new ClientCLI(); }

    void assertContains(String pattern, String text) {

        /* DOTALL means the dot applies to newline chars too
         * so that we can search for the pattern
         * anywhere inside a multiline text
         */
        Pattern p = Pattern.compile(".*"+pattern+".*", Pattern.DOTALL);
        try {
            assertTrue(p.matcher(text).matches());
        } catch (AssertionError e) {
            throw new AssertionError(
                    "\n\n\tcouldn't find pattern:\n\n"+pattern+"\n\n\tin text:\n\n"+text+"\n");
        }
    }

    @Test public void testNonExistentTracker() throws Exception {
        String fakeTrackerAddr = "123.123.123.123:1234";
        String output = cli.trackerCommand(("tracker "+fakeTrackerAddr).split(" "));
        assertContains("Tracker not found", output);
    }

    @Test public void testListEmptyTracker() throws Exception {
        TrackerState trackerState = TrackerState.create();
        String tAddrStr = ServersCommon.ipPortToString(trackerState.getExternalAddr());
        String output = cli.trackerCommand(("tracker "+tAddrStr).split(" "));
        assertContains("Tracker has no files", output);
    }

    @Test public void testListTrackerFilesOutput() throws Exception {
        TrackerState trackerState = TrackerState.create();
        TrackerSwarm trackerSwarm1 = new TrackerSwarm(MetaP2P.genFake(), trackerState);
        trackerSwarm1.addSeeders(
                TrackerPeer.genFake(),
                TrackerPeer.genFake(),
                TrackerPeer.genFake());
        TrackerSwarm trackerSwarm2 = new TrackerSwarm(MetaP2P.genFake(), trackerState);
        trackerSwarm2.addLeechers(
                TrackerPeer.genFake(),
                TrackerPeer.genFake());
        trackerState.getSwarms().addAll(trackerSwarm1, trackerSwarm2);
        InetSocketAddress trackerAddr = trackerState.getExternalAddr();
        String tAddrStr = ServersCommon.ipPortToString(trackerAddr);
        String output = cli.trackerCommand(("tracker "+tAddrStr).split(" "));
        String[] outputLines = output.split("\n");

        /* verify */
        assertContains("File list", output);
        assertContains("1\\) file-.*3 seeders  0 leechers.*B", outputLines[1]);
        assertContains("2\\) file-.*0 seeders  2 leechers.*B", outputLines[2]);
    }

    @Test public void testDlFileOnePeer() throws CreateP2PFileException, IOException {

        /* set local downloads dir */
        final File downloadsDir = new File("/Users/Ethan/Desktop/yaya/forDl");
        cli.state.setDownloadsDir(downloadsDir);

        /* add a file-to-serve to local files for service for a different peer */
        ClientState remoteState = new ClientState();
        Peer peer = new Peer(remoteState.externalListenAddr());
        File fileToServe = new File("/Users/Ethan/Desktop/yaya/forServe/Baller.jpg");
        P2PFile pFile = remoteState.addLocalFile(fileToServe);

        /* make it seem like we found this peer via a tracker w/o actually involving a tracker */
        ClientSwarm clientSwarm = new ClientSwarm(pFile.getMetaPFile(), null);
        clientSwarm.addSeeder(peer);

        RemoteTracker remoteTracker = new FakeRemoteTracker("0.0.0.0:0");
        remoteTracker.setSwarms(FXCollections.observableArrayList(clientSwarm));

        /* downloadCommand( the file ) */
        cli.downloadCommand("download 1".split(" "));

        /* verify that it arrived */
        Digester.checkFileAgainstDigest(
                new File(downloadsDir, "Baller.jpg"),
                pFile.getMetaPFile().getFileDigest());
    }
}

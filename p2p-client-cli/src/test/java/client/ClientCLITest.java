package client;

import base.BaseCLITest;
import org.junit.Test;
import tracker.TrackerState;
import util.ServersCommon;

import java.io.IOException;
import java.net.InetSocketAddress;

public class ClientCLITest extends BaseCLITest<ClientCLI> {
    @Override protected ClientCLI create() { return new ClientCLI(); }

    @Test public void testPrintsServeAddrFirst() throws IOException {
        String output = captureOutputOfInput("exit\n");
        assertContains("Srv: "+ServersCommon.IP4_wPORT_REG, output);
    }

    @Test public void testNonExistentTracker() throws Exception {
        String fakeTrackerAddr = "123.123.123.123:1234";
        String output = captureOutputOfInput("tracker "+fakeTrackerAddr+END_IN);
        assertContains("Tracker not found", output);
    }

    /**
     * requires the existence of a running tracker
     * which is why I added the tracker-app to the client-cli's test-dependencies
     */
    @Test public void testConnectToTracker() throws Exception {
        TrackerState trackerState = TrackerState.create();
        InetSocketAddress trackerAddr = trackerState.getExternalSocketAddr();
        String tAddrStr = ServersCommon.ipPortToString(trackerAddr);
        String output = captureOutputOfInput("tracker "+tAddrStr+END_IN);
        assertContains("connected", output);
    }

    @Test public void testListTrackerFiles() throws Exception {
        TrackerState trackerState = TrackerState.create();
        InetSocketAddress trackerAddr = trackerState.getExternalSocketAddr();
        String tAddrStr = ServersCommon.ipPortToString(trackerAddr);
        String output = captureOutputOfInput("tracker "+tAddrStr+END_IN);
        assertContains("File list", output);

    }
}

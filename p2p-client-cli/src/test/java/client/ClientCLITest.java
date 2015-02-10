package client;

import org.junit.Test;
import tracker.TrackerState;
import util.ServersCommon;

import java.net.InetSocketAddress;
import java.util.regex.Pattern;

import static org.junit.Assert.assertTrue;

public class ClientCLITest {

    ClientCLI cli;

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
        cli = new ClientCLI();
        String fakeTrackerAddr = "123.123.123.123:1234";
        String output = cli.trackerCommand(("tracker "+fakeTrackerAddr).split(" "));
        assertContains("Tracker not found", output);
    }

    /**
     * requires the existence of a running tracker
     * which is why I added the tracker-app to the client-cli's test-dependencies
     */
    @Test public void testConnectToTracker() throws Exception {
        cli = new ClientCLI();
        TrackerState trackerState = TrackerState.create();
        String tAddrStr = ServersCommon.ipPortToString(trackerState.getExternalAddr());
        String output = cli.trackerCommand(("tracker "+tAddrStr).split(" "));
        assertContains("connected", output);
    }

    @Test public void testListTrackerFiles() throws Exception {
        TrackerState trackerState = TrackerState.create();
        InetSocketAddress trackerAddr = trackerState.getExternalAddr();
        String tAddrStr = ServersCommon.ipPortToString(trackerAddr);
        String output = cli.trackerCommand(("tracker "+tAddrStr).split(" "));
        assertContains("File list", output);
    }
}

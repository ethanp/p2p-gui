package client;

import org.junit.Test;
import util.ServersCommon;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.regex.Pattern;

import static org.junit.Assert.assertTrue;

public class ClientCLITest {
    ClientCLI cli;
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    InputStream in;
    static final String STAR = ".*";
    static final String END_IN = "\nexit\n";

    private void assertContains(String pattern, String text) {

        /* DOTALL means the dot applies to newline chars too
         * so that we can search for the pattern
         * anywhere inside a multiline text
         */
        Pattern p = Pattern.compile(STAR+pattern+STAR, Pattern.DOTALL);
        assertTrue(p.matcher(text).matches());
    }

    /**
     * @param input  will be passed to the ClientCLI
     * @return the (multiple lines of) output printed by the CLI
     */
    private String captureOutputOfInput(String input) throws IOException {
        System.setOut(new PrintStream(out));
        in = new ByteArrayInputStream(input.getBytes());
        System.setIn(in);
        cli = new ClientCLI();
        out.flush();
        return new String(out.toByteArray());
    }

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
     * TODO requires the existence of a running tracker...
     * which is why I added the tracker-app to the client-cli's test-dependencies
     */
    @Test public void testConnectToTracker() throws Exception {
        
    }

}

package client;

import org.junit.Test;
import util.Common;
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

    private void assertMatches(String pattern, String text) {
        assertTrue(Pattern.matches(pattern, text));
    }

    private String[] captureOutputLines(String input) throws IOException {
        System.setOut(new PrintStream(out));
        in = new ByteArrayInputStream(input.getBytes());
        System.setIn(in);
        cli = new ClientCLI();
        out.flush();
        return new String(out.toByteArray()).split(Common.SYS_LINE_SEP);
    }

    @Test public void testPrintsServeAddrFirst() throws IOException {
        String[] lines = captureOutputLines("exit\n");
        assertMatches("^Srv: "+ServersCommon.IP4_wPORT_REG+"$", lines[0]);
    }

    @Test public void testTrackerCommand() throws Exception {
        // TODO this is where I'm at with this.

    }
}

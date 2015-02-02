package servers.simple;

import org.junit.Test;
import util.ServersCommon;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertFalse;

public class EchoSingleThreadedServerTest {

    String simpleEchoMsg = "Hello\n" +
                           "Server\n" +
                           "Pingdya how dosit feel?\n";

    @Test public void testEchoFromLoopbackAddress() throws Exception {
        EchoSingleThreadedServer server = new EchoSingleThreadedServer();
        new Thread(server).start();
        Socket socket = server.connectToLoopbackAddr();
        BufferedReader reader = ServersCommon.bufferedReader(socket);
        PrintWriter writer = ServersCommon.printWriter(socket);
        writer.println(simpleEchoMsg);
        String[] msgParts = simpleEchoMsg.split("\n");
        String[] response = new String[3];
        response[0] = reader.readLine();
        response[1] = reader.readLine();
        response[2] = reader.readLine();
        assertArrayEquals(msgParts, response);
        assertFalse(reader.ready()); // inStream must be DONE being read
    }

    @Test public void testEchoFromExternalIPAddress() throws Exception {
        EchoSingleThreadedServer server = new EchoSingleThreadedServer(3000, 3500);
        new Thread(server).start();
        Socket socket = server.connectToExternalAddr();
        BufferedReader reader = ServersCommon.bufferedReader(socket);
        PrintWriter writer = ServersCommon.printWriter(socket);
        writer.println(simpleEchoMsg);
        String[] msgParts = simpleEchoMsg.split("\n");
        String[] response = new String[3];
        response[0] = reader.readLine();
        response[1] = reader.readLine();
        response[2] = reader.readLine();
        assertArrayEquals(msgParts, response);
        assertFalse(reader.ready()); // inStream must be DONE being read
    }

    @Test public void testPingLocalServerFromSubProcess() throws Exception {
        EchoSingleThreadedServer server = new EchoSingleThreadedServer(3000, 3500);
        new Thread(server).start();
        pingAtIPPort("0.0.0.0:"+server.getPort());
    }

    @Test public void testPingExternalServerFromSubProcess() throws Exception {
        EchoSingleThreadedServer server = new EchoSingleThreadedServer(3000, 3500);
        new Thread(server).start();
        String ipPort = ServersCommon.ipPortToString(server.getExternalSocketAddr());
        pingAtIPPort(ipPort);
    }

    /* Maybe not super cross-compatible with other IDEs, I don't know */
    private void pingAtIPPort(String ipPort) throws IOException, InterruptedException {
        String[] ip = ipPort.split(":");
        ProcessBuilder procBldr = new ProcessBuilder(
                "java", "-cp", "servers/target/classes", "util.Pinger", ip[0], ip[1]
        );
        procBldr.redirectErrorStream(true);
        procBldr.redirectOutput(ProcessBuilder.Redirect.INHERIT);
        Process p = procBldr.start();
        p.waitFor();
        System.out.println("Process exited with code = "+p.exitValue());
    }
}

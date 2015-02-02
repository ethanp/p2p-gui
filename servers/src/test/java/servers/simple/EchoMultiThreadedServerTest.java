package servers.simple;

import org.junit.Test;
import servers.Server;
import util.ServersCommon;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.net.Socket;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertFalse;

public class EchoMultiThreadedServerTest {
    String simpleEchoMsg = "Hello\n" +
                           "Server\n" +
                           "Pingdya how dosit feel?\n";

    @Test public void testEchoFromLoopbackAddress() throws Exception {

        EchoMultiThreadedServer server = new EchoMultiThreadedServer(
                Server.LOWEST_PORT_FORWARDED,
                Server.HIGHEST_PORT_FORWARDED,
                Server.TYPICAL_THREADPOOL_SIZE
        );
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
}

package tracker;

import Exceptions.FailedToFindServerException;
import Exceptions.ListenerCouldntConnectException;
import Exceptions.NotConnectedException;
import Exceptions.ServersIOException;
import org.junit.Before;
import org.junit.Test;
import util.ServersCommon;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;

import static org.junit.Assert.assertEquals;


public class TrackerServerTest {

    TrackerServer trackerServer;

    @Before
    public void setUp() throws Exception {
        trackerServer = new TrackerServer();
        new Thread(trackerServer).start();
    }

    @Test public void testServerConnectToRouter()
            throws ListenerCouldntConnectException, NotConnectedException, InterruptedException
    {
        final InetSocketAddress addr = trackerServer.getExternalSocketAddr();
        System.out.println("connected at: "+ServersCommon.ipPortToString(addr));
    }


    @Test public void testEchoCommand() throws ServersIOException, FailedToFindServerException, IOException {

        Socket socket = ServersCommon.connectLocallyToInetAddr(trackerServer.getExternalSocketAddr());
        PrintWriter printWriter = ServersCommon.printWriter(socket);
        BufferedReader bufferedReader = ServersCommon.bufferedReader(socket);

        final String sentString = "tell me this\n"+
                                  "I got somethin' to tell ya\n";

        final String command = "ECHO\n"+sentString;
        printWriter.println(command);
        printWriter.flush();
        StringBuilder response = new StringBuilder();
        String line;
        while (true) {
            line = bufferedReader.readLine();
            if (line == null || line.length() == 0)
                break;
            response.append(line+"\n");
        }
        assertEquals(sentString, response.toString());
    }
}

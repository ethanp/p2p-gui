package tracker;

import Exceptions.ListenerCouldntConnectException;
import Exceptions.NotConnectedException;
import org.junit.Before;
import org.junit.Test;
import util.ServersCommon;

import java.net.InetSocketAddress;


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
}

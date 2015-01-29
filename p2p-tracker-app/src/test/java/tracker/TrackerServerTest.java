package tracker;

import Exceptions.FailedToFindServerException;
import Exceptions.ListenerCouldntConnectException;
import Exceptions.NotConnectedException;
import Exceptions.ServersIOException;
import org.junit.Before;
import org.junit.Test;
import p2p.exceptions.ConnectToTrackerException;
import p2p.file.meta.MetaP2PFile;
import p2p.protocol.tracker.ClientSideTrackerProtocol;
import p2p.tracker.swarm.ClientSwarm;
import util.ServersCommon;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;

import static org.junit.Assert.assertEquals;


public class TrackerServerTest implements ClientSideTrackerProtocol {

    TrackerServer trackerServer;
    PrintWriter printWriter;
    BufferedReader bufferedReader;
    Socket socket;
    ObjectOutputStream oos;
    ObjectInputStream ois;
    @Before
    public void setUp() throws Exception {
        trackerServer = new TrackerServer();
        new Thread(trackerServer).start();
        socket = ServersCommon.connectLocallyToInetAddr(trackerServer.getExternalSocketAddr());
        printWriter = ServersCommon.printWriter(socket);
        bufferedReader = ServersCommon.bufferedReader(socket);
    }

    @Test public void testServerConnectToRouter()
            throws ListenerCouldntConnectException, NotConnectedException, InterruptedException
    {
        final InetSocketAddress addr = trackerServer.getExternalSocketAddr();
        System.out.println("connected at: "+ServersCommon.ipPortToString(addr));
    }


    @Test public void testEchoCommand()
            throws ServersIOException, FailedToFindServerException, IOException
    {
        final String sentString = "tell me this\n"+
                                  "I got somethin' to tell ya\n";

        printWriter.println("ECHO");
        printWriter.println(sentString);
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

    @Test public void testAddNewFileRequest() throws IOException, ServersIOException {

        oos = ServersCommon.objectOStream(socket);
        ois = ServersCommon.objectIStream(socket);

        printWriter.println("ADD");
        printWriter.flush();

        MetaP2PFile meta = MetaP2PFile.genFake();
        oos.writeObject(meta);
        assertEquals(1, trackerServer.getTracker().getSwarms().size());
    }

    /**
     * Tracker receives P2PFile from Peer looks for it among its LocalSwarms If it exists, add Peer
     * to Swarm Otherwise create a new Swarm for it
     */
    @Override public void addFileRequest() throws IOException, ConnectToTrackerException, ServersIOException {

    }

    /**
     * Tracker tells a Peer who wants to download a P2PFile about the specific IP Addresses of Peers
     * in an existing Swarm so that the Peer can update its internal view of the Swarm
     */
    @Override public void updateSwarmInfo(ClientSwarm clientSwarm) throws IOException, ConnectToTrackerException, ServersIOException {

    }

    /**
     * Tracker sends Peer its full list of Swarms INCLUDING specific IP Addresses of Swarm members
     */
    @Override public void listFiles() throws IOException, ConnectToTrackerException, ServersIOException {

    }
}

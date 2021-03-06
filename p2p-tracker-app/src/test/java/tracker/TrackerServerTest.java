package tracker;

import Exceptions.FailedToFindServerException;
import Exceptions.ListenerCouldntConnectException;
import Exceptions.NoInternetConnectionException;
import Exceptions.ServersIOException;
import org.junit.Before;
import org.junit.Test;
import p2p.exceptions.ConnectToTrackerException;
import p2p.file.MetaP2P;
import p2p.protocol.fileTransfer.PeerTalk;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;
import util.ServersCommon;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.List;

import static org.junit.Assert.assertEquals;


public class TrackerServerTest {

    TrackerServer   trackerServer;
    PrintWriter     printWriter;
    BufferedReader  bufferedReader;
    Socket          socket;
    TrackerState trackerState;

    @Before public void setUp() throws Exception {
        trackerState = TrackerState.create();
        trackerServer = trackerState.getTrackerServer();
        socket = trackerServer.connectToExternalAddr();
        printWriter = ServersCommon.printWriter(socket);
        bufferedReader = ServersCommon.bufferedReader(socket);
    }

    @Test public void testServerConnectToRouter()
            throws ListenerCouldntConnectException, NoInternetConnectionException, InterruptedException
    {
        final InetSocketAddress addr = trackerServer.getExternalSocketAddr();
        System.out.println("connected at: "+ServersCommon.ipPortToString(addr));
    }


    @Test public void testEchoCommand()
            throws ServersIOException, FailedToFindServerException, IOException
    {
        final String sentString = "tell me this\n"+
                                  "I got somethin' to tell ya\n";

        printWriter.println(PeerTalk.ToTracker.ECHO);
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

    @Test public void testAddNewFileRequest() throws IOException, ServersIOException, InterruptedException, ConnectToTrackerException {

        MetaP2P meta = MetaP2P.genFake();
        InetSocketAddress addr = ServersCommon.randomSocketAddr();
        addFileRequest(meta, addr);
        assertEquals(1, trackerServer.getState().getSwarms().size());

        TrackerSwarm trackerSwarm = trackerServer.getState().getSwarms().get(0);

        assertEquals(meta, trackerSwarm.getMetaP2P());
        assertEquals(meta.getFilename(), trackerSwarm.getMetaP2P().getFilename());
        assertEquals(meta.getFileDigest(), trackerSwarm.getMetaP2P().getFileDigest());
        assertEquals(meta.getFilesize(), trackerSwarm.getMetaP2P().getFilesize());

        assertEquals(1, trackerSwarm.getSeeders().size());
        assertEquals(0, trackerSwarm.getLeechers().size());
    }

    @Test public void testNewPeerForExistingFileRequest() throws FailedToFindServerException, IOException, ServersIOException, ConnectToTrackerException {
        MetaP2P meta = MetaP2P.genFake();
        InetSocketAddress addr1 = ServersCommon.randomSocketAddr();
        InetSocketAddress addr2 = ServersCommon.randomSocketAddr();

        addFileRequest(meta, addr1);
        reconnectToTrackerServer();
        addFileRequest(meta, addr2);

        final TrackerSwarm trackerSwarm = trackerServer.getState().getSwarms().get(0);

        assertEquals(1, trackerServer.getState().getSwarms().size());
        assertEquals(2, trackerSwarm.getSeeders().size());
        assertEquals(0, trackerSwarm.getLeechers().size());
    }

    @Test public void testSecondFileRequest() throws ServersIOException, ConnectToTrackerException, IOException, FailedToFindServerException {
        MetaP2P meta1 = MetaP2P.genFake();
        MetaP2P meta2 = MetaP2P.genFake();
        InetSocketAddress addr = ServersCommon.randomSocketAddr();
        addFileRequest(meta1, addr);
        reconnectToTrackerServer();
        addFileRequest(meta2, addr);
        assertEquals(2, trackerServer.getState().getSwarms().size());

        TrackerSwarm trackerSwarm1 = trackerServer.getState().getSwarms().get(0);
        TrackerSwarm trackerSwarm2 = trackerServer.getState().getSwarms().get(1);

        assertEquals(meta1, trackerSwarm1.getMetaP2P());
        assertEquals(meta2, trackerSwarm2.getMetaP2P());
        assertEquals(1, trackerSwarm1.getSeeders().size());
        assertEquals(1, trackerSwarm2.getSeeders().size());
    }

    @Test public void testSecondFileGetsSecondSeeder() throws ServersIOException, ConnectToTrackerException, IOException, FailedToFindServerException {
        uploadTwoFiles();

        List<TrackerSwarm> swarms = trackerServer.getState().getSwarms();
        List seeders1 = swarms.get(0).getSeeders();
        List seeders2 = swarms.get(1).getSeeders();

        assertEquals(2, swarms.size());
        assertEquals(1, seeders1.size());
        assertEquals(2, seeders2.size());
    }

    private void uploadTwoFiles() throws IOException, ConnectToTrackerException, ServersIOException, FailedToFindServerException {
        MetaP2P meta1 = MetaP2P.genFake();
        MetaP2P meta2 = MetaP2P.genFake();
        InetSocketAddress addr1 = ServersCommon.randomSocketAddr();
        InetSocketAddress addr2 = ServersCommon.randomSocketAddr();

        addFileRequest(meta1, addr1);
        reconnectToTrackerServer();
        addFileRequest(meta2, addr1);
        reconnectToTrackerServer();
        addFileRequest(meta2, addr2);
    }

    @Test public void testListFiles() throws ServersIOException, ConnectToTrackerException, FailedToFindServerException, IOException {
        uploadTwoFiles();

        List<TrackerSwarm> swarms = trackerServer.getState().getSwarms();
        List seeders1 = swarms.get(0).getSeeders();
        List seeders2 = swarms.get(1).getSeeders();

//        Set<ClientSwarm> rcvdSwarms = listFiles();
//        assertEquals(2, rcvdSwarms.size());
    }

    private void reconnectToTrackerServer() throws IOException, FailedToFindServerException, ServersIOException {
        socket.close();
        socket = trackerServer.connectToLoopbackAddr();
        printWriter = ServersCommon.printWriter(socket);
        bufferedReader = ServersCommon.bufferedReader(socket);
    }

    /** Based on ClientSideTrackerProtocol which isn't accessible in this module
     *
     * Tracker receives P2PFile from Peer looks for it among its LocalSwarms If it exists, add Peer
     * to Swarm Otherwise create a new Swarm for it
     */
    public void addFileRequest(MetaP2P meta, InetSocketAddress addr) throws IOException, ConnectToTrackerException, ServersIOException {

        /* Command */
        printWriter.println(PeerTalk.ToTracker.ADD_FILE_REQUEST);
        printWriter.flush();

        /* Peer server's listening port */
        printWriter.println(ServersCommon.ipPortToString(addr));

        /* Upload MetaFile */
        printWriter.println(meta.serializeToString());
        printWriter.flush();

        /* wait for tracker to create the swarm and add peer */
        synchronized (trackerServer) {
            try { trackerServer.wait(); }
            catch (InterruptedException e) { e.printStackTrace(); }
        }
    }

    /** Based on ClientSideTrackerProtocol which isn't accessible in this module
     *
     * Tracker tells a Peer who wants to download a P2PFile
     * about the specific IP Addresses of Peers in an existing Swarm
     * so that the Peer can update its internal view of the Swarm
     */
    public void updateSwarmInfo(MetaP2P meta) throws IOException, ConnectToTrackerException, ServersIOException {
        throw new NotImplementedException();
    }

    /** Based on ClientSideTrackerProtocol which isn't accessible in this module
     *
     * Tracker sends Peer its full list of Swarms INCLUDING specific IP Addresses of Swarm members
     */
}

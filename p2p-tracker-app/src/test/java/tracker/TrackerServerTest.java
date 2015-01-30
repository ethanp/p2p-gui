package tracker;

import Exceptions.FailedToFindServerException;
import Exceptions.ListenerCouldntConnectException;
import Exceptions.NotConnectedException;
import Exceptions.ServersIOException;
import org.junit.Before;
import org.junit.Test;
import p2p.exceptions.ConnectToTrackerException;
import p2p.file.meta.MetaP2PFile;
import p2p.protocol.fileTransfer.PeerTalk;
import p2p.protocol.tracker.ClientSideTrackerProtocol;
import p2p.tracker.swarm.ClientSwarm;
import util.ServersCommon;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;

import static org.junit.Assert.assertEquals;


public class TrackerServerTest implements ClientSideTrackerProtocol {

    TrackerServer trackerServer;
    PrintWriter printWriter;
    BufferedReader bufferedReader;
    Socket socket;
    LocalTracker localTracker;
    @Before
    public void setUp() throws Exception {
        localTracker = LocalTracker.create();
        trackerServer = localTracker.getTrackerServer();
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

        printWriter.println(PeerTalk.ECHO);
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

        MetaP2PFile meta = MetaP2PFile.genFake();
        InetSocketAddress addr = ServersCommon.randomSocketAddr();

        addFileRequest(meta, addr);

        assertEquals(1, trackerServer.getTracker().getSwarms().size());


        final TrackerSwarm trackerSwarm = trackerServer.getTracker().getSwarms().get(0);
        final MetaP2PFile trackerMeta = trackerSwarm.getMetaP2P();

        assertEquals(meta.getFilename(), trackerMeta.getFilename());
        assertEquals(meta.getDigest(), trackerMeta.getDigest());
        assertEquals(meta.getFilesizeBytes(), trackerMeta.getFilesizeBytes());

        assertEquals(1, trackerSwarm.getSeeders().size());
        assertEquals(0, trackerSwarm.getLeechers().size());
    }

    @Test public void testNewPeerForExistingFileRequest() throws FailedToFindServerException, IOException, ServersIOException, ConnectToTrackerException {
        MetaP2PFile meta = MetaP2PFile.genFake();
        InetSocketAddress addr1 = ServersCommon.randomSocketAddr();
        InetSocketAddress addr2 = ServersCommon.randomSocketAddr();
        addFileRequest(meta, addr1);
        socket.close();
        socket = ServersCommon.connectLocallyToInetAddr(trackerServer.getExternalSocketAddr());
        printWriter = ServersCommon.printWriter(socket);
        addFileRequest(meta, addr2);
        assertEquals(1, trackerServer.getTracker().getSwarms().size());
        assertEquals(2, trackerServer.getTracker().getSwarms().get(0).getSeeders().size());
        assertEquals(0, trackerServer.getTracker().getSwarms().get(0).getLeechers().size());
    }

    /**
     * Tracker receives P2PFile from Peer looks for it among its LocalSwarms If it exists, add Peer
     * to Swarm Otherwise create a new Swarm for it
     */
    @Override public void addFileRequest(MetaP2PFile meta, InetSocketAddress addr) throws IOException, ConnectToTrackerException, ServersIOException {

        /* Command */
        printWriter.println(PeerTalk.ADD_FILE_REQUEST);
        printWriter.flush();

        /* Peer server's listening port */
        printWriter.println(ServersCommon.randomIPPortString());

        /* Upload MetaFile */
        printWriter.println(meta.serializeToString());
        printWriter.flush();

        /* wait for tracker to do whatever it has to do */
        synchronized (trackerServer) {
            try {
                trackerServer.wait();
            }
            catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
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

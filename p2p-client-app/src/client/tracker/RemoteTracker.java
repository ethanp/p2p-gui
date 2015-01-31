package client.tracker;

import Exceptions.FailedToFindServerException;
import Exceptions.ServersIOException;
import client.p2pFile.P2PFile;
import client.protocol.ClientSideTrackerProtocol;
import client.tracker.swarm.ClientSwarm;
import p2p.exceptions.ConnectToTrackerException;
import p2p.file.meta.MetaP2PFile;
import p2p.protocol.fileTransfer.PeerTalk;
import p2p.protocol.tracker.TrackerTalk;
import p2p.tracker.Tracker;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;
import util.ServersCommon;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * Ethan Petuchowski 1/15/15
 *
 * This contains the info that a Peer knows about a Tracker.
 * This is also what a Peer sends messages to in order to interact with that Tracker.
 */
public class RemoteTracker extends Tracker<ClientSwarm> implements ClientSideTrackerProtocol {

    protected Socket connToTracker;
    protected PrintWriter out;
    protected BufferedReader in;

    public RemoteTracker(InetSocketAddress addr)
            throws IOException, ConnectToTrackerException, ServersIOException {
        super(addr);
        listFiles();
    }

    public void createSwarmForMetaFile(MetaP2PFile mFile) {
        ClientSwarm s = new ClientSwarm(mFile, this);
        getSwarms().add(s);
    }

    public void createSwarmForFile(P2PFile pFile) {
        createSwarmForMetaFile(pFile.getMetaP2PFile());
    }

    public void connect() throws ConnectToTrackerException, IOException, ServersIOException {
        try {
            connToTracker = ServersCommon.connectToInetSocketAddr(getTrkrListenAddr());
        }
        catch (FailedToFindServerException e) {
            e.printStackTrace();
            System.err.println(e.getMessage());
            throw new ConnectToTrackerException(
                    "tried to connect to " + ServersCommon.ipPortToString(getTrkrListenAddr())+
                    " but received "       + e.getMessage());
        }
        out = ServersCommon.printWriter(connToTracker);
        in = ServersCommon.bufferedReader(connToTracker);
    }

    public void disconnect() throws IOException {
        if (connToTracker.isConnected() && !connToTracker.isClosed()) {
            out.close();
            in.close();
            connToTracker.close();
        }
    }

    @Override public void addAddrToSwarmFor(InetSocketAddress addr, MetaP2PFile meta) {
        // TODO implement RemoteTracker addAddrToSwarmFor
        throw new NotImplementedException();
    }

    /**
     * Tracker receives P2PFile from Peer looks for it among its LocalSwarms If it exists, add Peer
     * to Swarm Otherwise create a new Swarm for it
     */
    @Override public void addFileRequest(MetaP2PFile meta, InetSocketAddress peerListenAddr) throws IOException, ConnectToTrackerException, ServersIOException {
        connect();
        out.println(PeerTalk.ADD_FILE_REQUEST);
        out.println(ServersCommon.ipPortToString(peerListenAddr));
        out.println(meta.serializeToString());
        out.flush();
        disconnect();
    }

    /**
     * Tracker tells a Peer who wants to download a P2PFile
     * about the specific IP Addresses of Peers in an existing Swarm
     * so that the Peer can update its internal view of the Swarm
     */
    @Override public ClientSwarm updateSwarmInfo(MetaP2PFile meta)
            throws IOException, ConnectToTrackerException, ServersIOException
    {
        ClientSwarm clientSwarm = new ClientSwarm(meta, this);
        connect();

        /* Send Command */
        out.println(TrackerTalk.SWARM_UPDATE);

        /* Tell Tracker which File we're talking about */
        out.println(meta.serializeToString());

        /* get number of seeders and leechers */
        String sNumSdrs = in.readLine();
        String sNumLchrs = in.readLine();
        int numSdrs = Integer.parseInt(sNumSdrs);
        int numLchrs = Integer.parseInt(sNumLchrs);

        /* Rcv Seeders and add them to Swarm */
        for (int i = 0; i < numSdrs; i++) {
            String sAddr = in.readLine();
            InetSocketAddress addr = ServersCommon.addrFromString(sAddr);
//            TODO new RemotePeer(addr);
        }

        /* Rcv Leechers and add them to Swarm */
        for (int i = 0; i < numLchrs; i++) {

        }

        disconnect();
        // TODO implement RemoteTracker updateSwarmInfo
        throw new NotImplementedException();
    }

    /**
     * Tracker sends Peer its full list of Swarms
     * INCLUDING specific IP Addresses of Swarm members
     */
    @Override public void listFiles() throws IOException, ConnectToTrackerException, ServersIOException {
        connect();
        out.println(TrackerTalk.LIST_FILES);
        int numFiles = Integer.parseInt(in.readLine());
        for (int i = 0; i < numFiles; i++) {
            // TODO implement RemoteTracker listFiles
            throw new NotImplementedException();
        }
        disconnect();
    }
}
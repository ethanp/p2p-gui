package p2p.tracker;

import p2p.exceptions.ConnectToTrackerException;
import p2p.file.meta.MetaP2PFile;
import p2p.file.p2pFile.P2PFile;
import p2p.protocol.tracker.ClientSideTrackerProtocol;
import p2p.tracker.swarm.ClientSwarm;
import util.Common;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * Ethan Petuchowski 1/15/15
 *
 * This contains the info that a Peer knows about a Tracker.
 */
public abstract class AbstractRemoteTracker extends Tracker<ClientSwarm> implements ClientSideTrackerProtocol {

    protected Socket connToTracker;
    protected PrintWriter out;
    protected BufferedReader in;

    public AbstractRemoteTracker(InetSocketAddress addr)
            throws IOException, ConnectToTrackerException
    {
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

    public void connect() throws ConnectToTrackerException {
        try {
            connToTracker = Common.connectToInetSocketAddr(getListeningSockAddr());
        }
        catch (IOException e) {
            e.printStackTrace();
            System.err.println(e.getMessage());
            throw new ConnectToTrackerException(
                    "tried to connect to " + Common.ipPortToString(getListeningSockAddr())+
                    " but received "       + e.getMessage());
        }
        out = Common.printWriter(connToTracker);
        in = Common.bufferedReader(connToTracker);
    }

    public void disconnect() throws IOException {
        if (connToTracker.isConnected() && !connToTracker.isClosed()) {
            out.close();
            in.close();
            connToTracker.close();
        }
    }
}

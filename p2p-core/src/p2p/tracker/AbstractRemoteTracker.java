package p2p.tracker;

import p2p.file.meta.MetaP2PFile;
import p2p.protocol.tracker.ClientSideTrackerProtocol;
import p2p.tracker.swarm.RemoteSwarm;
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
public abstract class AbstractRemoteTracker extends Tracker<RemoteSwarm> implements ClientSideTrackerProtocol {

    protected Socket connToTracker;
    protected PrintWriter out;
    protected BufferedReader in;

    public AbstractRemoteTracker(InetSocketAddress addr) throws IOException {
        super(addr);
        listFiles();
    }

    public void createSwarmForFile(MetaP2PFile pFile) {
        RemoteSwarm s = new RemoteSwarm(pFile, this);
        getSwarms().add(s);
    }

    public void connect() throws IOException {
        connToTracker = Common.connectToInetSocketAddr(getListeningSockAddr());
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

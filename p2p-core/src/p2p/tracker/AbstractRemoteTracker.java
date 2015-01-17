package p2p.tracker;

import p2p.file.P2PFile;
import p2p.protocol.tracker.ClientSideTrackerProtocol;
import p2p.tracker.swarm.RemoteSwarm;
import util.Common;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * Ethan Petuchowski 1/15/15
 *
 * This contains the info that a Peer knows about a Tracker.
 */
public abstract class AbstractRemoteTracker extends Tracker<RemoteSwarm> implements ClientSideTrackerProtocol {

    public AbstractRemoteTracker(InetSocketAddress addr) throws IOException {
        super(addr);
        listFiles();
    }

    public void createSwarmForFile(P2PFile pFile) {
        RemoteSwarm s = new RemoteSwarm(pFile, this);
        getSwarms().add(s);
    }

    public Socket connect() throws IOException {
        return Common.connectToInetSocketAddr(getListeningSockAddr());
    }
}

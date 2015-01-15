package p2p.tracker;

import p2p.file.P2PFile;
import p2p.tracker.swarm.RemoteSwarm;

import java.net.InetSocketAddress;

/**
 * Ethan Petuchowski 1/15/15
 *
 * This contains the info that a Peer knows about a Tracker.
 */
public abstract class AbstractRemoteTracker extends Tracker<RemoteSwarm> {

    public abstract void requestInfo();
    public abstract void updateSwarmAddrs(P2PFile pFile);
    public AbstractRemoteTracker(InetSocketAddress addr) {
        super(addr);
        requestInfo();
    }

    public void createSwarmFor(P2PFile pFile) {
        RemoteSwarm s = new RemoteSwarm(pFile, this);
        getSwarms().add(s);
    }
}

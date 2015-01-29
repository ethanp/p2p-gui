package tracker;

import Exceptions.ListenerCouldntConnectException;
import Exceptions.NotConnectedException;
import p2p.file.meta.MetaP2PFile;
import p2p.tracker.Tracker;

import java.net.InetSocketAddress;

/**
 * Ethan Petuchowski 1/15/15
 *
 * This is the thing you instantiate to locally BECOME a live Tracker.
 * The tracker-GUI and tracker-CLI are both just FRONTENDS for THIS.
 *
 * It has a list of swarms and a running server.
 */
public class LocalTracker extends Tracker<TrackerSwarm> implements Runnable {
    TrackerServer trackerServer;

    public LocalTracker(InetSocketAddress addr)
            throws ListenerCouldntConnectException, NotConnectedException
    {
        super(addr);
    }

    @Override public void addAddrToSwarmFor(InetSocketAddress addr, MetaP2PFile meta) {
        TrackerSwarm swarm = getSwarmForFile(meta);
        if (swarm == null) {
            swarm = new TrackerSwarm(meta, this);
        }
        swarm.getSeeders().add(new TrackerPeer(addr));
    }

    public static LocalTracker create()
            throws ListenerCouldntConnectException, NotConnectedException {
        TrackerServer trkSrv = new TrackerServer();
        LocalTracker localTracker = new LocalTracker(trkSrv.getExternalSocketAddr());
        localTracker.setTrackerServer(trkSrv);
        return localTracker;
    }

    @Override public void run() {}

    public void setTrackerServer(TrackerServer trackerServer) {
        this.trackerServer = trackerServer;
    }

    public InetSocketAddress getExternalSocketAddr() {
        return trackerServer.getExternalSocketAddr();
    }
}

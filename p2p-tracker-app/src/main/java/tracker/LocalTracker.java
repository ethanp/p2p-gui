package tracker;

import Exceptions.ListenerCouldntConnectException;
import Exceptions.NoInternetConnectionException;
import p2p.file.meta.MetaP2P;
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
            throws ListenerCouldntConnectException, NoInternetConnectionException
    {
        super(addr);
    }

    @Override public void addAddrToSwarmFor(InetSocketAddress addr, MetaP2P meta) {
        TrackerSwarm swarm = getSwarmForFile(meta);
        if (swarm == null) {
            swarm = new TrackerSwarm(meta, this);
            getSwarms().add(swarm);
        }
        swarm.getSeeders().add(new TrackerPeer(addr));
    }

    public static LocalTracker create()
            throws ListenerCouldntConnectException, NoInternetConnectionException
    {
        TrackerServer trkSrv = new TrackerServer();
        LocalTracker localTracker = new LocalTracker(trkSrv.getExternalSocketAddr());
        trkSrv.setTracker(localTracker);
        localTracker.setTrackerServer(trkSrv);
        return localTracker;
    }

    @Override public void run() {}

    public TrackerServer getTrackerServer() {
        return trackerServer;
    }

    public void setTrackerServer(TrackerServer trackerServer) {
        this.trackerServer = trackerServer;
    }

    public InetSocketAddress getExternalSocketAddr() {
        return trackerServer.getExternalSocketAddr();
    }
}

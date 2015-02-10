package tracker;

import Exceptions.ListenerCouldntConnectException;
import Exceptions.NoInternetConnectionException;
import Exceptions.ServersIOException;
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
public class TrackerState extends Tracker<TrackerSwarm> implements Runnable {
    TrackerServer trackerServer;

    public TrackerState(InetSocketAddress addr)
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

    public static TrackerState create()
            throws ListenerCouldntConnectException, NoInternetConnectionException, ServersIOException
    {
        TrackerServer trkSrv = new TrackerServer(3000, 3500);
        new Thread(trkSrv).start();
        TrackerState trackerState = new TrackerState(trkSrv.getExternalSocketAddr());
        trkSrv.setTracker(trackerState);
        trackerState.setTrackerServer(trkSrv);

        return trackerState;
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

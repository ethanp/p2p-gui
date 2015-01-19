package p2p.tracker;

import p2p.exceptions.ConnectToTrackerException;
import p2p.tracker.swarm.TrackerSwarm;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.List;

/**
 * Ethan Petuchowski 1/15/15
 *
 * This contains the info that a Tracker knows about itself.
 */
public class LocalTracker extends Tracker {

    public LocalTracker(InetSocketAddress addr) {
        super(addr);
    }

    public LocalTracker(List<TrackerSwarm> swarms, InetSocketAddress addr) {
        super(addr, swarms);
    }

    public AbstractRemoteTracker asRemote() {
        try {
            return new RealRemoteTracker(getListeningSockAddr());
        }
        catch (ConnectToTrackerException | IOException e) {
            // this should never happen.
            // a tracker should have no issue connecting to itself...
            e.printStackTrace();
        }
        return null;
    }
}

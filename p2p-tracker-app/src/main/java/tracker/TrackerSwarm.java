package tracker;

import p2p.file.MetaP2P;
import p2p.peer.PeerAddr;
import p2p.tracker.Swarm;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

/**
 * Ethan Petuchowski 1/15/15
 *
 * the type of Swarm held by a Tracker to help a Client
 *
 *      a) find files to download
 *      b) find peers from whom to download a desired file
 */
public class TrackerSwarm extends Swarm<TrackerState, PeerAddr> {
    public TrackerSwarm(MetaP2P baseMetaP2P, TrackerState trkr) {
        super(baseMetaP2P, trkr);
    }

    @Override public Swarm<TrackerState, PeerAddr> addFakePeers() {
        throw new NotImplementedException();
    }

    public static TrackerSwarm createLoadedSwarm(TrackerState trkr) {
        TrackerSwarm swarm = new TrackerSwarm(MetaP2P.genFake(), trkr);
        swarm.addFakePeers();
        return swarm;
    }
}

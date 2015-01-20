package p2p.tracker.swarm;

import p2p.file.meta.MetaP2PFile;
import p2p.peer.TrackerPeer;
import p2p.tracker.LocalTracker;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

/**
 * Ethan Petuchowski 1/15/15
 *
 * the type of Swarm held by a Tracker to help a Client
 *
 *      a) find files to download
 *      b) find peers from whom to download a desired file
 */
public class TrackerSwarm extends Swarm<LocalTracker, TrackerPeer> {
    public TrackerSwarm(MetaP2PFile baseMetaP2PFile, LocalTracker trkr) {
        super(baseMetaP2PFile, trkr);
    }

    @Override public Swarm<LocalTracker, TrackerPeer> addFakePeers() {
        throw new NotImplementedException();
    }

    public static TrackerSwarm createLoadedSwarm(LocalTracker trkr) {
        TrackerSwarm swarm = new TrackerSwarm(MetaP2PFile.genFake(), trkr);
        swarm.addFakePeers();
        return swarm;
    }
}

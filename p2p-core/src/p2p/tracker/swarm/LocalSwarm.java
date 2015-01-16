package p2p.tracker.swarm;

import p2p.file.FakeP2PFile;
import p2p.file.P2PFile;
import p2p.tracker.LocalTracker;

/**
 * Ethan Petuchowski 1/15/15
 */
public class LocalSwarm extends Swarm<LocalTracker> {
    public LocalSwarm(P2PFile baseP2PFile, LocalTracker trkr) {
        super(baseP2PFile, trkr);
    }
    public static LocalSwarm loadedSwarm(LocalTracker trkr) {
        LocalSwarm swarm = new LocalSwarm(FakeP2PFile.genFakeFile(), trkr);
        swarm.addRandomPeers();
        return swarm;
    }
}

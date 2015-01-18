package p2p.tracker.swarm;

import p2p.file.meta.LocalFakeFile;
import p2p.file.meta.MetaP2PFile;
import p2p.tracker.LocalTracker;

/**
 * Ethan Petuchowski 1/15/15
 */
public class LocalSwarm extends Swarm<LocalTracker> {
    public LocalSwarm(MetaP2PFile baseMetaP2PFile, LocalTracker trkr) {
        super(baseMetaP2PFile, trkr);
    }
    public static LocalSwarm createLoadedSwarm(LocalTracker trkr) {
        LocalSwarm swarm = new LocalSwarm(LocalFakeFile.genFakeFile(), trkr);
        swarm.addRandomPeers();
        return swarm;
    }
}

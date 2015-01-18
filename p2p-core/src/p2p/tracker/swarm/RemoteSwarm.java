package p2p.tracker.swarm;

import p2p.file.meta.MetaP2PFile;
import p2p.tracker.AbstractRemoteTracker;

/**
 * Ethan Petuchowski 1/15/15
 */
public class RemoteSwarm extends Swarm<AbstractRemoteTracker> {
    public RemoteSwarm(MetaP2PFile baseMetaP2PFile, AbstractRemoteTracker trkr) {
        super(baseMetaP2PFile, trkr);
    }
}

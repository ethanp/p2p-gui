package p2p.tracker.swarm;

import p2p.file.P2PFile;
import p2p.tracker.AbstractRemoteTracker;

/**
 * Ethan Petuchowski 1/15/15
 */
public class RemoteSwarm extends Swarm<AbstractRemoteTracker> {
    public RemoteSwarm(P2PFile baseP2PFile, AbstractRemoteTracker trkr) {
        super(baseP2PFile, trkr);
    }
}

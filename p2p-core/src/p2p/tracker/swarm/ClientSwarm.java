package p2p.tracker.swarm;

import p2p.file.meta.MetaP2PFile;
import p2p.peer.FakeRemotePeer;
import p2p.peer.RemotePeer;
import p2p.tracker.AbstractRemoteTracker;
import util.Common;

/**
 * Ethan Petuchowski 1/15/15
 *
 * The type of Swarm held by a Client allowing it to decide
 * from which Peer to download which Chunk
 */
public class ClientSwarm extends Swarm<AbstractRemoteTracker, RemotePeer> {
    public ClientSwarm(MetaP2PFile baseMetaP2PFile, AbstractRemoteTracker trkr) {
        super(baseMetaP2PFile, trkr);
    }

    @Override public ClientSwarm addRandomPeers() {
        int nSeeders = Common.randInt(10);
        int nLeechers = Common.randInt(10);
        for (int i = 0; i < nSeeders; i++)
            getSeeders().add(FakeRemotePeer.createWithUnresolvedIP());
        for (int i = 0; i < nLeechers; i++)
            getLeechers().add(FakeRemotePeer.createWithUnresolvedIP());
        return this;
    }
}

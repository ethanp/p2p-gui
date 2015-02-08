package client.tracker.swarm;

import client.peer.FakePeer;
import client.tracker.RemoteTracker;
import p2p.file.meta.MetaP2P;
import client.peer.Peer;
import p2p.tracker.swarm.Swarm;
import util.Common;

/**
 * Ethan Petuchowski 1/15/15
 *
 * The type of Swarm held by a Client allowing it to decide
 * from which Peer to download which Chunk
 */
public class ClientSwarm extends Swarm<RemoteTracker, Peer> {
    public ClientSwarm(MetaP2P baseMetaP2P, RemoteTracker trkr) {
        super(baseMetaP2P, trkr);
    }

    @Override public ClientSwarm addFakePeers() {
        int nSeeders = Common.randInt(10);
        int nLeechers = Common.randInt(10);
        for (int i = 0; i < nSeeders; i++)
            addSeeder(FakePeer.createWithUnresolvedIP());
        for (int i = 0; i < nLeechers; i++)
            addLeecher(FakePeer.createWithUnresolvedIP());
        return this;
    }
}

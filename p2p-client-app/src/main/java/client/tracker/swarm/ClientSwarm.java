package client.tracker.swarm;

import client.tracker.RemoteTracker;
import p2p.file.meta.MetaP2PFile;
import client.peer.FakeRemotePeer;
import client.peer.RemotePeer;
import p2p.tracker.swarm.Swarm;
import util.Common;

/**
 * Ethan Petuchowski 1/15/15
 *
 * The type of Swarm held by a Client allowing it to decide
 * from which Peer to download which Chunk
 */
public class ClientSwarm extends Swarm<RemoteTracker, RemotePeer> {
    public ClientSwarm(MetaP2PFile baseMetaP2PFile, RemoteTracker trkr) {
        super(baseMetaP2PFile, trkr);
    }

    @Override public ClientSwarm addFakePeers() {
        int nSeeders = Common.randInt(10);
        int nLeechers = Common.randInt(10);
        for (int i = 0; i < nSeeders; i++)
            addSeeder(FakeRemotePeer.createWithUnresolvedIP());
        for (int i = 0; i < nLeechers; i++)
            addLeecher(FakeRemotePeer.createWithUnresolvedIP());
        return this;
    }
}

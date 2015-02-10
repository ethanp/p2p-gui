package client.tracker.swarm;

import client.peer.FakePeer;
import client.tracker.RemoteTracker;
import p2p.exceptions.ConnectToTrackerException;
import p2p.file.meta.MetaP2P;
import client.peer.Peer;
import p2p.tracker.swarm.Swarm;
import util.Common;
import util.ServersCommon;

import java.io.BufferedReader;
import java.io.IOException;

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

    public static ClientSwarm deserializeFromTracker(BufferedReader reader, RemoteTracker tracker) throws ConnectToTrackerException {
        try {
            MetaP2P m = MetaP2P.deserializeFromReader(reader);
            ClientSwarm swarm = new ClientSwarm(m, tracker);
            int numSeeders = Integer.parseInt(reader.readLine());
            for (int i = 0; i < numSeeders; i++)
                swarm.addSeeder(new Peer(ServersCommon.addrFromString(reader.readLine())));
            int numLeechers = Integer.parseInt(reader.readLine());
            for (int i = 0; i < numLeechers; i++)
                swarm.addLeecher(new Peer(ServersCommon.addrFromString(reader.readLine())));
            return swarm;
        }
        catch (IOException e) {
            throw new ConnectToTrackerException("couldn't get swarm from tracker");
        }
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

package client.state;

import Exceptions.ServersIOException;
import client.peer.Peer;
import client.tracker.RemoteTracker;
import client.tracker.ClientSwarm;
import javafx.collections.FXCollections;
import javafx.collections.ObservableSet;
import p2p.exceptions.ConnectToTrackerException;
import p2p.file.MetaP2P;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Ethan Petuchowski 2/9/15
 */
public class TrackersManager {
    private ObservableSet<RemoteTracker> knownTrackers = FXCollections.observableSet();
    public ObservableSet<RemoteTracker> getKnownTrackers() { return knownTrackers; }

    public RemoteTracker addTracker(String addrStr) throws IOException, ServersIOException, ConnectToTrackerException {
        RemoteTracker tracker = new RemoteTracker(addrStr);
        getKnownTrackers().add(tracker);
        return tracker;
    }

    public Collection<ClientSwarm> listTracker(RemoteTracker tracker) throws ServersIOException, ConnectToTrackerException, IOException {
        try {
            return tracker.listFiles();
        }
        catch (IOException | ConnectToTrackerException | ServersIOException e) {
            getKnownTrackers().remove(tracker);
            throw e;
        }
    }

    public Collection<Peer> collectPeersServing(MetaP2P metaP2P) {
        Collection<Peer> peers = new ArrayList<>();
        for (RemoteTracker tracker : getKnownTrackers()) {
            ClientSwarm swarm = tracker.getSwarmForFile(metaP2P);
            if (swarm != null)
                peers.addAll(swarm.getAllPeers());
        }
        return peers;
    }
}

package client.managers;

import Exceptions.ServersIOException;
import client.tracker.RemoteTracker;
import client.tracker.swarm.ClientSwarm;
import javafx.collections.FXCollections;
import javafx.collections.ObservableSet;
import p2p.exceptions.ConnectToTrackerException;

import java.io.IOException;
import java.util.Collection;

/**
 * Ethan Petuchowski 2/9/15
 */
public class TrackersManager {
    private ObservableSet<RemoteTracker> knownTrackers = FXCollections.observableSet();

    public ObservableSet<RemoteTracker> getKnownTrackers() { return knownTrackers; }

    public RemoteTracker addTracker(String addrStr) throws IOException, ServersIOException, ConnectToTrackerException {
        RemoteTracker tracker = new RemoteTracker(addrStr);
        knownTrackers.add(tracker);
        return tracker;
    }

    public Collection<ClientSwarm> listTracker(RemoteTracker tracker) throws ServersIOException, ConnectToTrackerException, IOException {
        try {
            return tracker.listFiles();
        }
        catch (IOException | ConnectToTrackerException | ServersIOException e) {
            knownTrackers.remove(tracker);
            throw e;
        }
    }
}

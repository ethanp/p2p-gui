package client.managers;

import Exceptions.ServersIOException;
import client.tracker.RemoteTracker;
import javafx.collections.FXCollections;
import javafx.collections.ObservableSet;
import p2p.exceptions.ConnectToTrackerException;

import java.io.IOException;

/**
 * Ethan Petuchowski 2/9/15
 */
public class TrackersManager {
    private ObservableSet<RemoteTracker> knownTrackers = FXCollections.emptyObservableSet();

    public ObservableSet<RemoteTracker> getKnownTrackers() { return knownTrackers; }

    public void addTrackerByAddrStr(String addrStr) throws IOException, ServersIOException, ConnectToTrackerException {
        RemoteTracker tracker = new RemoteTracker(addrStr);
        tracker.connect();
        tracker.disconnect();
    }
}

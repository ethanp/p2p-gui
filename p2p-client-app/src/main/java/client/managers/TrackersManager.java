package client.managers;

import client.tracker.RemoteTracker;
import javafx.collections.FXCollections;
import javafx.collections.ObservableSet;

import java.net.UnknownHostException;

/**
 * Ethan Petuchowski 2/9/15
 */
public class TrackersManager {
    private ObservableSet<RemoteTracker> knownTrackers = FXCollections.emptyObservableSet();

    public ObservableSet<RemoteTracker> getKnownTrackers() { return knownTrackers; }

    public void addTrackerByAddrStr(String addrStr) throws UnknownHostException {
        RemoteTracker tracker = new RemoteTracker(addrStr);
    }
}

package client.managers;

import Exceptions.ServersIOException;
import client.tracker.RemoteTracker;
import client.tracker.swarm.ClientSwarm;
import javafx.collections.FXCollections;
import javafx.collections.ObservableSet;
import p2p.exceptions.ConnectToTrackerException;

import java.io.IOException;

/**
 * Ethan Petuchowski 2/9/15
 */
public class TrackersManager {
    private ObservableSet<RemoteTracker> knownTrackers = FXCollections.observableSet();

    public ObservableSet<RemoteTracker> getKnownTrackers() { return knownTrackers; }

    public RemoteTracker addTrackerByAddrStr(String addrStr) throws IOException, ServersIOException, ConnectToTrackerException {
        RemoteTracker tracker = new RemoteTracker(addrStr);
        tracker.connect();
        tracker.disconnect();
        knownTrackers.add(tracker);
        return tracker;
    }

    public String listTracker(RemoteTracker tracker) throws ServersIOException, ConnectToTrackerException, IOException {
        if (!knownTrackers.contains(tracker)) {
            return "Tracker unknown";
        }
        tracker.listFiles();
        StringBuilder s = new StringBuilder("File list:\n");
        int i = 1;
        for (ClientSwarm swarm : tracker.getSwarms()) {
            s.append(i++ + ".)");
            s.append("   ");
            s.append(swarm.getFilename()+',');
            s.append("   ");
            s.append(swarm.numSeeders()+" seeders,");
            s.append("   ");
            s.append(swarm.numLeechers()+" leechers\n");
        }
        if (i == 1)
            s.append("Tracker has no files\n");
        return s.toString();
    }
}

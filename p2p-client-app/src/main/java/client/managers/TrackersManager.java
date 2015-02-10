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

    public String addTrackerByAddrStr(String addrStr) throws IOException, ServersIOException, ConnectToTrackerException {
        RemoteTracker tracker = new RemoteTracker(addrStr);
        knownTrackers.add(tracker);
        return listTracker(tracker);
    }

    public String listTracker(RemoteTracker tracker) throws ServersIOException, ConnectToTrackerException, IOException {
        try {
            Collection<ClientSwarm> clientSwarms = tracker.listFiles();
        }
        catch (IOException | ConnectToTrackerException | ServersIOException e) {
            knownTrackers.remove(tracker);
            throw e;
        }

        /* TODO this should be happening in the ClientCLI,
         * building a String to show to the user
         * has nothing to do with the role of the TrackersManager
         */
        StringBuilder s = new StringBuilder("File list:\n");
        int i = 1;
        for (ClientSwarm swarm : tracker.getSwarms()) {
            s.append(i++ + ".) ");
            s.append(swarm.toCLIString()+"\n");
        }
        if (i == 1)
            s.append("Tracker has no files\n");
        return s.toString();
    }
}

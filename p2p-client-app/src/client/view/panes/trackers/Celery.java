package client.view.panes.trackers;

import Exceptions.ServersIOException;
import client.Main;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import p2p.exceptions.ConnectToTrackerException;
import client.tracker.RemoteTracker;
import client.tracker.swarm.ClientSwarm;

import java.io.IOException;

/**
 * Ethan Petuchowski 1/13/15
 *
 * This is based on the mock-up I created in
 * ProgrammingGit/Educational/Java/JavaFX/MyTreeTrial
 */
public class Celery {
    private final ClientSwarm swarm;
    private final RemoteTracker tracker;

    /* this was part of a table-refresh hack that I'm no longer using
       in case I need it again, it involves going observeMe.set(null); observeMe.set(this); */
    public ObjectProperty<Celery> observeMe = new SimpleObjectProperty<>(this);

    public RemoteTracker getTracker() { return tracker; }
    public ClientSwarm getSwarm() { return swarm; }

    public void updateThisSwarm() throws ConnectToTrackerException, ServersIOException, IOException {
        if (!isSwarm())
            throw new RuntimeException("can only update a swarm");

        getSwarm().getTracker().updateSwarmInfo(getSwarm().getMetaP2P());
    }

    /* subtype checkers */
    public boolean isTracker()  { return tracker != null; }
    public boolean isSwarm()    { return swarm != null; }
    public boolean isRoot()     { return !isTracker() && !isSwarm(); }

    /* wrapping constructors */
    public Celery(ClientSwarm swarm) {
        this.swarm = swarm;
        this.tracker = null;
    }

    public Celery(RemoteTracker tracker) {
        this.swarm = null;
        this.tracker = tracker;
    }

    /** create a "base" SwarmTreeItem used to list
     *  the known Trackers as its children */
    public Celery(TreeTableRoot x) {
        this.swarm = null;
        this.tracker = null;
    }

    public String getName() {
        if (isRoot()) return Main.getKnownTrackers().size() + " trackers";
        if (isTracker()) return tracker.getIpPortString();
        else return swarm.getMetaP2P().getFilename();
    }

    public String getSize() {
        if (isSwarm()) return swarm.getMetaP2P().formattedFilesizeStr();
        if (isTracker()) return tracker.getSwarms().size() + " files";
        else return "";
    }

    public String getNumSeeders() {
        if (isSwarm()) return swarm.getSeeders().size()+"";
        else return "";
    }

    public String getNumLeechers() {
        if (isSwarm()) return swarm.getLeechers().size()+"";
        else return "";
    }

    public boolean equalsTracker(RemoteTracker tracker) {
        return isTracker() && this.tracker.equals(tracker);
    }

    public boolean equalsSwarm(ClientSwarm swarm) {
        return isSwarm() && this.swarm.equals(swarm);
    }
}

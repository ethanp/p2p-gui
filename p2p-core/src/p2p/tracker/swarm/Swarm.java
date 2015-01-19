package p2p.tracker.swarm;

import javafx.beans.property.ListProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import p2p.file.meta.MetaP2PFile;
import p2p.peer.Peer;
import p2p.tracker.Tracker;

import java.util.ArrayList;
import java.util.List;

/**
 * Ethan Petuchowski 1/10/15
 *
 * There are currently 2 subclassers:
 *
 *      1.) TrackerSwarm: the type of Swarm held by a Tracker to help a Client
 *
 *          a) find files to download
 *          b) find peers from whom to download a desired file
 *
 *
 *      2.) ClientSwarm: the type of Swarm held by a Client allowing it to
 *                       decide from whom to download which chunk
 */
public abstract class Swarm<T extends Tracker, P extends Peer> {
    /* CODE */
    protected final ListProperty<P> leechers;
    protected final ListProperty<P> seeders;
    protected final ObjectProperty<MetaP2PFile> p2pFile;
    protected final ObjectProperty<T> tracker;

    public Swarm(MetaP2PFile baseMetaP2PFile, T trkr) {
        p2pFile = new SimpleObjectProperty<>(baseMetaP2PFile);
        seeders = new SimpleListProperty<>(FXCollections.observableArrayList());
        leechers = new SimpleListProperty<>(FXCollections.observableArrayList());
        tracker = new SimpleObjectProperty<>(trkr);
    }

    public abstract Swarm<T, P> addRandomPeers();

    public List<P> getAllPeers() {
        List<P> peers = new ArrayList<>(getLeechers());
        peers.addAll(getSeeders());
        return peers;
    }

    /* GARBAGE */
    public ObservableList<P> getSeeders() { return seeders.get(); }
    public ListProperty<P> seedersProperty() { return seeders; }
    public void setSeeders(ObservableList<P> seeders) { this.seeders.set(seeders); }

    public ObservableList<P> getLeechers() { return leechers.get(); }
    public ListProperty<P> leechersProperty() { return leechers; }
    public void setLeechers(ObservableList<P> leechers) { this.leechers.set(leechers); }

    public MetaP2PFile getP2pFile() { return p2pFile.get(); }
    public ObjectProperty<MetaP2PFile> p2pFileProperty() { return p2pFile; }
    public void setP2pFile(MetaP2PFile metaP2PFile) { this.p2pFile.set(metaP2PFile); }

    public T getTracker() { return tracker.get(); }
    public ObjectProperty<T> trackerProperty() { return tracker; }
    public void setTracker(T tracker) { this.tracker.set(tracker); }

    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Swarm)) return false;
        Swarm swarm = (Swarm) o;
        if (!leechers.equals(swarm.leechers)) return false;
        if (!p2pFile.equals(swarm.p2pFile)) return false;
        if (!seeders.equals(swarm.seeders)) return false;
        if (!tracker.equals(swarm.tracker)) return false;
        return true;
    }

    @Override public int hashCode() {
        int result = leechers.hashCode();
        result = 31*result+seeders.hashCode();
        result = 31*result+p2pFile.hashCode();
        result = 31*result+tracker.hashCode();
        return result;
    }
}

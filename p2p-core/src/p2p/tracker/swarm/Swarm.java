package p2p.tracker.swarm;

import javafx.beans.property.ListProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import p2p.file.meta.MetaP2P;
import p2p.peer.PeerAddr;
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
public abstract class Swarm<T extends Tracker, P extends PeerAddr> {
    /* CODE */
    protected final ListProperty<P> seeders;
    protected final ListProperty<P> leechers;
    protected final ObjectProperty<MetaP2P> metaP2P;
    protected final ObjectProperty<T> tracker;

    public Swarm(MetaP2P baseMetaP2P, T trkr) {
        metaP2P = new SimpleObjectProperty<>(baseMetaP2P);
        seeders = new SimpleListProperty<>(FXCollections.observableArrayList());
        leechers = new SimpleListProperty<>(FXCollections.observableArrayList());
        tracker = new SimpleObjectProperty<>(trkr);
    }

    public abstract Swarm<T, P> addFakePeers();

    public List<P> getAllPeers() {
        List<P> peers = new ArrayList<>(getLeechers());
        peers.addAll(getSeeders());
        return peers;
    }

    /* GARBAGE */
    public ObservableList<P> getSeeders() { return seeders.get(); }
    public ListProperty<P> seedersProperty() { return seeders; }
    public void setSeeders(ObservableList<P> seeders) { this.seeders.set(seeders); }
    public void addSeeder(P seeder) { seeders.get().add(seeder); }

    public ObservableList<P> getLeechers() { return leechers.get(); }
    public ListProperty<P> leechersProperty() { return leechers; }
    public void setLeechers(ObservableList<P> leechers) { this.leechers.set(leechers); }
    public void addLeecher(P leecher) { seeders.get().add(leecher); }

    public MetaP2P getMetaP2P() { return metaP2P.get(); }
    public ObjectProperty<MetaP2P> metaP2PProperty() { return metaP2P; }
    public void setMetaP2P(MetaP2P metaP2P) { this.metaP2P.set(metaP2P); }

    public T getTracker() { return tracker.get(); }
    public ObjectProperty<T> trackerProperty() { return tracker; }
    public void setTracker(T tracker) { this.tracker.set(tracker); }

    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Swarm)) return false;
        Swarm swarm = (Swarm) o;
        if (!leechers.equals(swarm.leechers)) return false;
        if (!metaP2P.equals(swarm.metaP2P)) return false;
        if (!seeders.equals(swarm.seeders)) return false;
        if (!tracker.equals(swarm.tracker)) return false;
        return true;
    }

    @Override public int hashCode() {
        int result = leechers.hashCode();
        result = 31*result+seeders.hashCode();
        result = 31*result+metaP2P.hashCode();
        result = 31*result+tracker.hashCode();
        return result;
    }
}

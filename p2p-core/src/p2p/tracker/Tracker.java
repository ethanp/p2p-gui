package p2p.tracker;

import javafx.beans.property.ListProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import p2p.file.meta.MetaP2PFile;
import p2p.tracker.swarm.Swarm;
import util.ServersCommon;

import java.net.InetSocketAddress;

/**
 * Ethan Petuchowski 1/7/15
 */
public abstract class Tracker<S extends Swarm> {

    protected final ListProperty<S> swarms;
    protected final ObjectProperty<InetSocketAddress> trkrListenAddr;

    public Tracker(InetSocketAddress addr) {
        trkrListenAddr = new SimpleObjectProperty<>(addr);
        swarms = new SimpleListProperty<>(FXCollections.observableArrayList());
    }

    protected S getSwarmForFile(MetaP2PFile file) {
        for (S s : getSwarms())
            if (s.getMetaP2P().equals(file))
                return s;
        return null;
    }

    public ObservableList<S> getSwarms() { return swarms.get(); }
    public ListProperty<S> swarmsProperty() { return swarms; }
    public void setSwarms(ObservableList<S> swarms) { this.swarms.set(swarms); }
    public InetSocketAddress getTrkrListenAddr() { return trkrListenAddr.get(); }
    public ObjectProperty<InetSocketAddress> trkrListenAddrProperty(){return trkrListenAddr;}
    public void setTrkrListenAddr(InetSocketAddress addr) { this.trkrListenAddr.set(addr); }
    public String getIpPortString() { return ServersCommon.ipPortToString(getTrkrListenAddr()); }

    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Tracker)) return false;
        Tracker tracker = (Tracker) o;
        if (!trkrListenAddr.equals(tracker.trkrListenAddr)) return false;
        if (!swarms.equals(tracker.swarms)) return false;
        return true;
    }
    @Override public int hashCode() {
        int result = swarms.hashCode();
        result = 31*result+trkrListenAddr.hashCode();
        return result;
    }

    public abstract void addAddrToSwarmFor(InetSocketAddress addr, MetaP2PFile meta);
}

package p2p.tracker;

import javafx.beans.property.ListProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import p2p.file.MetaP2P;
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

    public S getSwarmForFile(MetaP2P file) {
        for (S s : getSwarms())
            if (s.getMetaP2P().equals(file))
                return s;
        return null;
    }

    public ObservableList<S> getSwarms() { return swarms.get(); }
    public int               numSwarms() { return getSwarms().size(); }
    public void              setSwarms(ObservableList<S> swarms) { this.swarms.set(swarms); }
    public InetSocketAddress getTrkrListenAddr() { return trkrListenAddr.get(); }
    public String            getIpPortString() { return ServersCommon.ipPortToString(getTrkrListenAddr()); }

    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Tracker)) return false;
        Tracker tracker = (Tracker) o;
        String thisAddr = ServersCommon.ipPortToString(trkrListenAddr.get());
        String thatAddr = ServersCommon.ipPortToString((InetSocketAddress) tracker.trkrListenAddr.get());

        if (!thisAddr.equals(thatAddr)) return false;
        if (!swarms.equals(tracker.swarms)) return false;
        return true;
    }
    @Override public int hashCode() {
        int result = swarms.hashCode();
        result = 31*result+trkrListenAddr.hashCode();
        return result;
    }

    public abstract void addAddrToSwarmFor(InetSocketAddress addr, MetaP2P meta);
}

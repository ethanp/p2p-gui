package p2p.peer;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import util.Common;

import java.net.InetSocketAddress;

/**
 * Ethan Petuchowski 1/7/15
 */
public abstract class Peer {

    @Override public String toString() {
        return Common.ipPortToString(servingAddr.get());
    }

    protected final ObjectProperty<InetSocketAddress> servingAddr;

    protected Peer(InetSocketAddress socketAddr) {
        servingAddr = new SimpleObjectProperty<>(socketAddr);
    }

    public InetSocketAddress getServingAddr() { return servingAddr.get(); }
    public ObjectProperty<InetSocketAddress> servingAddrProperty() { return servingAddr; }
    public void setServingAddr(InetSocketAddress servingAddr) { this.servingAddr.set(servingAddr); }
}

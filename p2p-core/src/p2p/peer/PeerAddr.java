package p2p.peer;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import util.ServersCommon;

import java.net.InetSocketAddress;

/**
 * Ethan Petuchowski 1/7/15
 *
 * Represents a "user" who can upload and download P2PFiles.
 *
 * All it has in this base format is an ServerSocket (IPAddress + Port)
 * through which it is prepared to service requests from other Peers for Chunks.
 *
 * 3 Subclassers:
 *
 *  1.) TrackerPeer:
 *          info Tracker maintains about Peer:
 *                  (nothing additional)
 *  2.) PeerServer:
 *          You The Client's live running server waiting on requests from
 *          other Peers for Chunks
 *
 *  3.) RemotePeer:
 *          info You The Client maintain about a Peer:
 *                  namely which Chunks it has of which files
 *                          via (roughly) a Map<Meta, BitMap>
 */
public abstract class PeerAddr {

    @Override public String toString() {
        return ServersCommon.ipPortToString(servingAddr.get());
    }

    protected final ObjectProperty<InetSocketAddress> servingAddr;

    protected PeerAddr(InetSocketAddress socketAddr) {
        servingAddr = new SimpleObjectProperty<>(socketAddr);
    }

    public InetSocketAddress getServingAddr() { return servingAddr.get(); }
    public ObjectProperty<InetSocketAddress> servingAddrProperty() { return servingAddr; }
    public void setServingAddr(InetSocketAddress servingAddr) { this.servingAddr.set(servingAddr); }
}

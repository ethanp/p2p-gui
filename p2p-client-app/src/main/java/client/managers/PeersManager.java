package client.managers;

import client.peer.Peer;

import java.net.InetSocketAddress;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Ethan Petuchowski 2/10/15
 */
public class PeersManager {
    Map<InetSocketAddress, Peer> knownPeers = new HashMap<>();
    Map<InetSocketAddress, Peer> connectedPeers = new HashMap<>();

    public boolean knowPeer(Peer peer) {
        return knownPeers.containsKey(peer.getServingAddr());
    }

    public boolean isConnectedTo(Peer peer) {
        return connectedPeers.containsKey(peer.getServingAddr());
    }

    public Collection<Peer> getKnownPeers() {
        return knownPeers.values();
    }

    public void mergePeerIn(Peer peer) {
        Peer existing = knownPeers.get(peer.getServingAddr());
        if (existing != null)
            existing.addFiles(peer.getFiles());
        else
            knownPeers.put(peer.getServingAddr(), peer);
    }
}

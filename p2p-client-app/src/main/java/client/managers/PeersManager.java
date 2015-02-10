package client.managers;

import client.peer.Peer;
import p2p.file.meta.MetaP2P;

import java.net.InetSocketAddress;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Ethan Petuchowski 2/10/15
 */
public class PeersManager {
    Map<InetSocketAddress, Peer> knownPeers = new HashMap<>();
    public boolean knowPeer(Peer peer) {
        return knownPeers.containsKey(peer.getServingAddr());
    }
    public Collection<Peer> getKnownPeers() {
        return knownPeers.values();
    }

    /* this is for adding a peer that we know nothing about except that they have a particular file */
    public void addMetaToPeer(InetSocketAddress sAddr, MetaP2P metaP2P) {
        Peer existing = knownPeers.get(sAddr);
        if (existing == null) {
            Peer newPeer = new Peer(sAddr);
            newPeer.addFile(metaP2P);
        }
    }
}

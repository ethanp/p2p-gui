package p2p.protocol.tracker;

import Exceptions.ServersIOException;

/**
 * Ethan Petuchowski 1/16/15
 */
public interface TrackerSideTrackerProtocol {
    /**
     * TrackerServer receives MetaP2PFile from Peer.
     * If no corresponding TrackerSwarm exists, create one.
     * Add TrackerPeer to the TrackerSwarm.
     */
    public void addFileRequest() throws ServersIOException;

    /**
     * Tracker tells a Peer who wants to download a P2PFile
     * about the specific IP Addresses of Peers in an existing Swarm
     * so that the Peer can update its internal view of the Swarm
     */
    public void sendSwarmInfo();

    /**
     * Tracker sends Peer its full list of Swarms
     * INCLUDING specific IP Addresses of Swarm members
     */
    public void listFiles();
}

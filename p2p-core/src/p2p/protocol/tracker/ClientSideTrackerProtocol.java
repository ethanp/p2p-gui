package p2p.protocol.tracker;

import p2p.tracker.swarm.ClientSwarm;

import java.io.IOException;

/**
 * Ethan Petuchowski 1/16/15
 */
public interface ClientSideTrackerProtocol {
    /**
     * Tracker receives P2PFile from Peer looks for it among its LocalSwarms
     * If it exists, add Peer to Swarm
     * Otherwise create a new Swarm for it
     */
    public void addFileRequest() throws IOException;

    /**
     * Tracker tells a Peer who wants to download a P2PFile
     * about the specific IP Addresses of Peers in an existing Swarm
     * so that the Peer can update its internal view of the Swarm
     * @param clientSwarm
     */
    public void updateSwarmInfo(ClientSwarm clientSwarm) throws IOException;

    /**
     * Tracker sends Peer its full list of Swarms
     * INCLUDING specific IP Addresses of Swarm members
     */
    public void listFiles() throws IOException;
}

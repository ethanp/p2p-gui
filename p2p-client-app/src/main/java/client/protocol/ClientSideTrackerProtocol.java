package client.protocol;

import Exceptions.ServersIOException;
import client.tracker.swarm.ClientSwarm;
import p2p.exceptions.ConnectToTrackerException;
import p2p.file.meta.MetaP2P;

import java.io.IOException;
import java.net.InetSocketAddress;

/**
 * Ethan Petuchowski 1/16/15
 */
public interface ClientSideTrackerProtocol {
    /**
     * Tracker receives P2PFile from Peer looks for it among its LocalSwarms
     * If it exists, add Peer to Swarm
     * Otherwise create a new Swarm for it
     */
    public void addFileRequest(MetaP2P meta, InetSocketAddress addr) throws IOException, ConnectToTrackerException, ServersIOException;

    /**
     * Tracker tells a Peer who wants to download a P2PFile
     * about the specific IP Addresses of Peers in an existing Swarm
     * so that the Peer can update its internal view of the Swarm
     * @param meta
     */
    public ClientSwarm updateSwarmInfo(MetaP2P meta) throws IOException, ConnectToTrackerException, ServersIOException;

    /**
     * Tracker sends Peer its full list of Swarms
     * INCLUDING specific IP Addresses of Swarm members
     */
    public void listFiles() throws IOException, ConnectToTrackerException, ServersIOException;
}

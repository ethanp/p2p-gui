package p2p.tracker;

import p2p.tracker.swarm.RemoteSwarm;
import util.Common;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * Ethan Petuchowski 1/15/15
 */
public class RealRemoteTracker extends AbstractRemoteTracker {
    public RealRemoteTracker(InetSocketAddress addr) throws IOException {
        super(addr);
    }

    private Socket connectToRemote() throws IOException {
        return Common.connectToInetSocketAddr(getListeningSockAddr());
    }

    /**
     * Tracker receives P2PFile from Peer looks for it among its LocalSwarms
     * If it exists, add Peer to Swarm
     * Otherwise create a new Swarm for it
     */
    @Override public void addFileRequest() throws IOException {
        Socket socket = connectToRemote();
    }

    /**
     * Tracker tells a Peer who wants to download a P2PFile
     * about the specific IP Addresses of Peers in an existing Swarm
     * so that the Peer can update its internal view of the Swarm
     */
    @Override public void updateSwarmInfo(RemoteSwarm remoteSwarm) throws IOException {
        Socket socket = connectToRemote();
    }

    /**
     * Tracker sends Peer its full list of Swarms
     * INCLUDING specific IP Addresses of Swarm members
     */
    @Override public void listFiles() throws IOException {
        Socket socket = connectToRemote();
    }
}

package p2p.tracker;

import p2p.file.P2PFile;

import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * Ethan Petuchowski 1/15/15
 */
public class RealRemoteTracker extends AbstractRemoteTracker {
    public RealRemoteTracker(InetSocketAddress addr) {
        super(addr);
    }

    @Override public void requestInfo() {
        Socket socket = connectToRemote();
    }

    @Override public void updateSwarmAddrs(P2PFile pFile) {
        Socket socket = connectToRemote();
    }

    private Socket connectToRemote() {
        return null;
    }
}

package p2p.tracker;

import p2p.file.P2PFile;

import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * Ethan Petuchowski 1/15/15
 *
 * This contains the info that a Peer knows about a Tracker.
 */
public class RemoteTracker extends Tracker {

    public RemoteTracker(InetSocketAddress addr) {
        super(addr);
        System.out.println("requesting swarms from "+getIpPortString());
        System.out.println("(unimplemented)");
//        requestInfo();
    }

    private Socket connectToRemote() {
        return null;
    }

    public void requestInfo() {
        /* connect to remote Tracker via known addr */
        Socket socket = connectToRemote();

        /* ask it for its List<Swarm> */
        /* set those to be THIS instance's List<Swarm> */

        /* close the connection */
    }

    public void updateSwarmAddrs(P2PFile pFile) {
        Socket socket = connectToRemote();

        /* close the connection */
    }
}

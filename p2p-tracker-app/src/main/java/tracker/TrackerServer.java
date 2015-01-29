package tracker;

import Exceptions.ListenerCouldntConnectException;
import Exceptions.NotConnectedException;
import Exceptions.ServersIOException;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import p2p.file.meta.MetaP2PFile;
import p2p.protocol.tracker.TrackerSideTrackerProtocol;
import servers.SingleThreadedServer;
import util.ServersCommon;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * Ethan Petuchowski 1/16/15
 */
public class TrackerServer extends SingleThreadedServer implements TrackerSideTrackerProtocol {

    protected final ObjectProperty<LocalTracker> tracker = new SimpleObjectProperty<>(null);
    protected final IntegerProperty rcvReqCt = new SimpleIntegerProperty(0);
    protected BufferedReader bufferedReader;
    protected String command;

    public TrackerServer() throws ListenerCouldntConnectException, NotConnectedException {
        super();
    }

    /**
     * TrackerServer receives MetaP2PFile from Peer.
     * If no corresponding TrackerSwarm exists, create one.
     * Add TrackerPeer to the TrackerSwarm.
     */
    @Override public void addFileRequest() throws ServersIOException {
        InetAddress otherEndAddress = socket.getInetAddress();
        InetSocketAddress addr = new InetSocketAddress(otherEndAddress, socket.getPort());
        ObjectInputStream ois = ServersCommon.objectIStream(socket);
        ObjectOutputStream oos = ServersCommon.objectOStream(socket);
        MetaP2PFile meta = null; // TODO receive from OOS
        getTracker().addAddrToSwarmFor(addr, meta);
    }

    /**
     * TODO  Tracker tells a Peer who wants to download a P2PFile about the
     * specific IP Addresses of Peers in an existing Swarm
     * so that the Peer can update its internal view of the Swarm
     */
    @Override public void sendSwarmInfo() {}

    /**
     * TODO  Tracker sends Peer its full list of Swarms
     * INCLUDING specific IP Addresses of Swarm members
     */
    @Override public void listFiles() {}

    @Override protected void dealWithSocket(Socket socket) throws ServersIOException {
        this.socket = socket;
        rcvReqCt.add(1);
        try {
            bufferedReader = ServersCommon.bufferedReader(socket);
            command = bufferedReader.readLine();
        }
        catch (IOException e) {
            /* If this ever actually happens I will deal with it then */
            e.printStackTrace();
        }
        if (command == null) throw new RuntimeException("null command");
        System.out.println("tracker received command: "+command);
        switch (command) {
            case "ECHO": echo();           return;
            case "ADD":  addFileRequest(); return;
            default: System.err.println("ERROR: There is no case to handle that command.");
        }
    }

    protected void echo() {
        try (PrintWriter printWriter = ServersCommon.printWriter(socket)) {
            while (true) {
                String inLine = bufferedReader.readLine();
                if (inLine == null || inLine.length() == 0)
                    break;
                printWriter.println(inLine);
            }
            printWriter.flush();
        }
        catch (ServersIOException | IOException e) { e.printStackTrace(); }
    }

    /* Getters and Setters */
    public int getRcvReqCt() { return rcvReqCt.get(); }
    public IntegerProperty rcvReqCtProperty() { return rcvReqCt; }
    public void setRcvReqCt(int newRcvReqCt) { rcvReqCt.set(newRcvReqCt); }
    public LocalTracker getTracker() { return tracker.get(); }
    public ObjectProperty<LocalTracker> trackerProperty() { return tracker; }
    public void setTracker(LocalTracker tracker) { this.tracker.set(tracker); }
}

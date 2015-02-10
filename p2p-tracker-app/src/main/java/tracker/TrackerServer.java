package tracker;

import Exceptions.ListenerCouldntConnectException;
import Exceptions.NoInternetConnectionException;
import Exceptions.ServersIOException;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import p2p.exceptions.CreateP2PFileException;
import p2p.file.meta.MetaP2P;
import p2p.protocol.fileTransfer.PeerTalk;
import p2p.protocol.tracker.TrackerSideTrackerProtocol;
import servers.SingleThreadedServer;
import util.ServersCommon;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * Ethan Petuchowski 1/16/15
 */
public class TrackerServer extends SingleThreadedServer implements TrackerSideTrackerProtocol {

    protected final ObjectProperty<TrackerState> tracker = new SimpleObjectProperty<>(null);
    protected final IntegerProperty rcvReqCt = new SimpleIntegerProperty(0);
    protected BufferedReader bufferedReader;
    protected String command;

    public TrackerServer(int i, int i1) throws ListenerCouldntConnectException, NoInternetConnectionException, ServersIOException {
        super(i, i1);
    }

    /**
     * TrackerServer receives MetaP2P from Peer.
     * If no corresponding TrackerSwarm exists, create one.
     * Add TrackerPeer to the TrackerSwarm.
     */
    @Override public void addFileRequest() throws ServersIOException, CreateP2PFileException {
        MetaP2P meta;
        InetSocketAddress addr;
        try {
            String[] components = bufferedReader.readLine().split(":");
            addr = new InetSocketAddress(components[0], Integer.parseInt(components[1]));
            meta = MetaP2P.deserializeFromReader(bufferedReader);
        }
        catch (IOException e) {
            throw new ServersIOException(e);
        }
        getTracker().addAddrToSwarmFor(addr, meta);

        synchronized (this) { this.notifyAll(); } // for unit test
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
    @Override public void listFiles() {
        System.out.println("List files is not implemented in the Tracker");
    }

    @Override protected void beforeAllListenLoops() {
        super.beforeAllListenLoops();
        System.out.println("tracker server starting run loop");
    }

    @Override protected void dealWithSocket(Socket socket) throws ServersIOException {
        this.socket = socket;

        rcvReqCt.add(1);

        try {
            bufferedReader = ServersCommon.bufferedReader(socket);
            command = bufferedReader.readLine();
        }
        catch (IOException e) {
            throw new ServersIOException(e);
        }

        if (command == null)
            throw new RuntimeException("null command");

        System.out.println("tracker received command: "+command);

        switch (command) {
            case PeerTalk.ToTracker.ECHO:
                echo();
                break;
            case PeerTalk.ToTracker.ADD_FILE_REQUEST:
                try {
                    addFileRequest();
                }
                catch (CreateP2PFileException e) {
                    e.printStackTrace();
                }
                break;
            case PeerTalk.ToTracker.LIST_FILES:
                listFiles();
                break;
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
    public TrackerState getTracker() { return tracker.get(); }
    public ObjectProperty<TrackerState> trackerProperty() { return tracker; }
    public void setTracker(TrackerState tracker) { this.tracker.set(tracker); }
}

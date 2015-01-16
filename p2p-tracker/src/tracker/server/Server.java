package tracker.server;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import p2p.tracker.LocalTracker;
import util.Common;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Ethan Petuchowski 1/14/15
 *
 * normal server.
 * not necessarily abstract, I'm just scoping it out.
 */
public class Server extends Thread {

    protected final ObjectProperty<LocalTracker> tracker;
    protected final ObjectProperty<InetAddress> localIPAddr
            = new SimpleObjectProperty<>(Common.findMyIP());
    protected ServerSocket listener;
    protected final static IntegerProperty rcvReqCt = new SimpleIntegerProperty(0);
    Socket conn;
    BufferedReader in;
    PrintWriter out;

    /* not to be confused with getattr */
    public InetSocketAddress getAddr() {
        return new InetSocketAddress(localIPAddr.get(), listener.getLocalPort());
    }

    public String getAddrString() {
        return Common.ipPortToString(getAddr());
    }

    public Server() {
        try {
            listener = Common.socketPortInRange(Common.PORT_MIN, Common.PORT_MAX);
        }
        catch (IOException e) {
            System.err.println(Common.ExitCodes.SERVER_FAILURE);
            System.err.println(e.getMessage());
            System.exit(Common.ExitCodes.SERVER_FAILURE.ordinal());
        }
        tracker = new SimpleObjectProperty<>(new LocalTracker(getAddr()));
    }

    @Override public void run() {
        //noinspection InfiniteLoopStatement
        while (true) {
            try {
                conn = listener.accept();
                in = Common.bufferedReader(conn);
                out = Common.printWriter(conn);
                rcvReqCt.add(1);
                atYourService();
            }
            catch (IOException e) {
                System.err.println("Exception in tracker server main listen-loop");
                System.err.println(e.getMessage());
            }
        }
    }

    private void atYourService() throws IOException {
        String command = in.readLine();
        if (command == null) throw new RuntimeException("null command");
        System.out.println("received command: "+command);
    }

    /**
     * receive file from peer look for it among swarms
     * if it exists, add peer to swarm
     * otherwise create a new swarm for it
     */
    public void addFileRequest(){}

    /**
     * we tell a Peer who wants to download a File
     * about the specific IP Addresses of Peers in an existing Swarm
     * so it can update its internal view of the Swarm
     */
    public void sendSwarmInfo(){}

    /**
     * send full list of Swarms
     * INCLUDING specific IP Addresses of Swarm members
     */
    public void listFiles(){}

    public static int getRcvReqCt() { return rcvReqCt.get(); }
    public static IntegerProperty rcvReqCtProperty() { return rcvReqCt; }
    public static void setRcvReqCt(int rcvReqCt) {
        Server.rcvReqCt.set(rcvReqCt);
    }
    public LocalTracker getTracker() { return tracker.get(); }
    public ObjectProperty<LocalTracker> trackerProperty() { return tracker; }
    public void setTracker(LocalTracker tracker) { this.tracker.set(tracker); }

}

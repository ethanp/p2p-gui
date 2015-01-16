package util;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

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
 * 1. normal configurable single-threaded server
 * 2. override
 *      ```
 *          protected void runLoopCode() throws IOException
 *      ```
 *    to make the thing do something when someone connects to it
 */
public abstract class SimpleServer extends Thread {

    protected final ObjectProperty<InetAddress> localIPAddr
            = new SimpleObjectProperty<>(Common.findMyIP());
    protected ServerSocket listener;
    protected Socket conn;
    protected BufferedReader bufferedReader;
    protected PrintWriter printWriter;

    /* not to be confused with getattr */
    public InetSocketAddress getAddr() {
        return new InetSocketAddress(localIPAddr.get(), listener.getLocalPort());
    }

    public String getAddrString() {
        return Common.ipPortToString(getAddr());
    }

    public SimpleServer() {
        try {
            listener = Common.socketPortInRange(Common.PORT_MIN, Common.PORT_MAX);
        }
        catch (IOException e) {
            System.err.println(Common.ExitCodes.SERVER_FAILURE);
            System.err.println(e.getMessage());
            System.exit(Common.ExitCodes.SERVER_FAILURE.ordinal());
        }
    }


    @Override public void run() {
        beforeRunLoop();
        //noinspection InfiniteLoopStatement
        while (true) {
            try {
                conn = listener.accept();
                bufferedReader = Common.bufferedReader(conn);
                printWriter = Common.printWriter(conn);
                runLoopCode();
            }
            catch (IOException e) {
                System.err.println("Exception in tracker server main listen-loop");
                System.err.println(e.getMessage());
            }
        }
    }

    protected abstract void runLoopCode() throws IOException;
    protected void beforeRunLoop(){}
}

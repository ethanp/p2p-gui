package servers;

import Exceptions.FailedToFindServerException;
import Exceptions.ListenerCouldntConnectException;
import Exceptions.NoInternetConnectionException;
import Exceptions.ServersIOException;
import util.ServersCommon;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Ethan Petuchowski 1/28/15
 */
public abstract class Server implements Runnable {

    public static final boolean LOOPBACK_ADDR = true;
    public static final boolean EXTERNAL_ADDR = false;

    public static final int LOWEST_PORT_FORWARDED = 3000;
    public static final int HIGHEST_PORT_FORWARDED = 3500;

    public static final int TYPICAL_THREADPOOL_SIZE = 20; // pulled outta nowheres

    protected ServerSocket listener;
    protected InetAddress ipAddr;

    public Server() throws ListenerCouldntConnectException, NoInternetConnectionException {
        try {
            listener = new ServerSocket(0);
        }
        catch (IOException e) {
            throw new ListenerCouldntConnectException(e);
        }
        setAndPrintIp();
    }

    public Server(int port) throws ListenerCouldntConnectException, NoInternetConnectionException {
        try {
            listener = new ServerSocket(port);
        }
        catch (IOException e) {
            throw new ListenerCouldntConnectException(e);
        }
        setAndPrintIp();
    }

    public Server(ServerSocket listener) throws ListenerCouldntConnectException, NoInternetConnectionException {
        if (listener.isBound()) {
            throw new ListenerCouldntConnectException("the given listener was not bound");
        }
        this.listener = listener;
        setAndPrintIp();
    }


    public Server(int fromPortNo, int toPortNo) throws ListenerCouldntConnectException, NoInternetConnectionException, ServersIOException {
        listener = ServersCommon.socketPortInRange(fromPortNo, toPortNo);
        setAndPrintIp();
    }

    @Override public void run() {
        beforeAllListenLoops();
        try {
            listenLoop();
        }
        catch (ServersIOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void setAndPrintIp() throws NoInternetConnectionException {
        ipAddr = ServersCommon.findMyIP();
        System.out.println("Srv: "+ServersCommon.ipPortToString(getExternalSocketAddr()));
    }

    protected void listenLoop() throws ServersIOException, InterruptedException {
        while (true) {
            if (Thread.currentThread().isInterrupted()) {
                throw new InterruptedException();
            }
            beginningOfListenLoop();
            Socket socket;
            try {
                socket = listener.accept();
                dealWithSocket(socket);
            }
            catch (IOException e) {
                e.printStackTrace();
            }
            endOfListenLoop();
        }
    }


    /* Implementation required */
    protected abstract void dealWithSocket(Socket socket) throws ServersIOException;


    /* Implementation optional */
    protected void beforeAllListenLoops(){/*nothing*/}
    protected void endOfListenLoop(){/*nothing*/}
    protected void beginningOfListenLoop(){/*nothing*/}

    public Socket connectToLoopbackAddr() { return connect(LOOPBACK_ADDR); }
    public Socket connectToExternalAddr() { return connect(EXTERNAL_ADDR); }

    protected Socket connect(boolean locally) {
        try {
            if (locally)
                return ServersCommon.connectLocallyToInetAddr(getExternalSocketAddr());
            else
                return ServersCommon.connectToInetSocketAddr(getExternalSocketAddr());
        }
        catch (FailedToFindServerException e) {
            /* this would be odd...it can't find itself? */
            e.printStackTrace();
        }
        return null;
    }

    /* Getters and Setters */
    public InetSocketAddress getExternalSocketAddr() {
        return new InetSocketAddress(ipAddr, listener.getLocalPort());
    }

    public InetAddress getIpAddr() { return ipAddr; }
    public int getPort() { return listener.getLocalPort(); }
    public void setIpAddr(InetAddress ipAddr) { this.ipAddr = ipAddr; }
}

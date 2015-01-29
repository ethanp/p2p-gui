package servers;

import Exceptions.ListenerCouldntConnectException;
import Exceptions.NotConnectedException;
import util.ServersCommon;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Ethan Petuchowski 1/28/15
 */
public abstract class Server extends Thread {
    protected ServerSocket listener;

    protected InetAddress localIPAddr;

    public Server() throws ListenerCouldntConnectException, NotConnectedException {
        try {
            listener = new ServerSocket(0);
        }
        catch (IOException e) {
            throw new ListenerCouldntConnectException(e);
        }
        localIPAddr = ServersCommon.findMyIP();
    }

    public Server(ServerSocket listener) throws ListenerCouldntConnectException, NotConnectedException {
        if (listener.isBound()) {
            throw new ListenerCouldntConnectException("the given listener was not bound");
        }
        this.listener = listener;
        localIPAddr = ServersCommon.findMyIP();
    }


    public Server(int fromPortNo, int toPortNo) throws ListenerCouldntConnectException, NotConnectedException {
        try {
            listener = ServersCommon.socketPortInRange(fromPortNo, toPortNo);
        }
        catch (IOException e) {
            throw new ListenerCouldntConnectException(e);
        }
        localIPAddr = ServersCommon.findMyIP();
    }

    @Override public void run() {
        beforeListenLoops();
        listenLoop();
    }

    protected abstract void beforeListenLoops();

    protected void listenLoop() {
        while (true) {
            beginningOfListenLoop();
            try (Socket socket = listener.accept()) {
                dealWithSocket(socket);
            }
            catch (IOException e) {
                e.printStackTrace();
            }
            endOfListenLoop();
        }
    }

    protected abstract void endOfListenLoop();

    protected abstract void beginningOfListenLoop();

    protected abstract void dealWithSocket(Socket socket);

    public InetSocketAddress getExternalSocketAddr() {
        return new InetSocketAddress(localIPAddr, listener.getLocalPort());
    }

    public InetAddress getLocalIPAddr() {
        return localIPAddr;
    }

    public void setLocalIPAddr(InetAddress localIPAddr) {
        this.localIPAddr = localIPAddr;
    }
}

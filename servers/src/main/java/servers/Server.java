package servers;

import Exceptions.ListenerCouldntConnectException;
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

    protected InetAddress localIPAddr = ServersCommon.findMyIP();

    public Server() throws ListenerCouldntConnectException {
        try {
            listener = new ServerSocket(0);
        }
        catch (IOException e) {
            throw new ListenerCouldntConnectException(e);
        } finally {
            try {
                listener.close();
            }
            catch (IOException e) {
                /* ignore */
            }
        }
    }

    public Server(ServerSocket listener) throws ListenerCouldntConnectException {
        if (listener.isBound()) {
            throw new ListenerCouldntConnectException("the given listener was not bound");
        }
        this.listener = listener;
    }


    public Server(int fromPortNo, int toPortNo) throws ListenerCouldntConnectException {
        try {
            listener = ServersCommon.socketPortInRange(fromPortNo, toPortNo);
        }
        catch (IOException e) {
            throw new ListenerCouldntConnectException(e);
        } finally {
            try {
                listener.close();
            }
            catch (IOException e) {
                /* ignore */
            }
        }
    }

    @Override public void run() {
        listenLoop();
    }

    protected void listenLoop() {
        while (true) {
            try (Socket socket = listener.accept()) {
                dealWithSocket(socket);
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

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

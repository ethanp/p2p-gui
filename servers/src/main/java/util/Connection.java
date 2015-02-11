package util;

import Exceptions.FailedToFindServerException;
import Exceptions.ServersIOException;

import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * Ethan Petuchowski 2/11/15
 */
public abstract class Connection {

    public InetSocketAddress socketAddress;
    public Socket socket;

    public Connection(InetSocketAddress addr) {
        socketAddress = addr;
    }

    public void connect() throws FailedToFindServerException, ServersIOException {
        socket = ServersCommon.connectToInetSocketAddr(socketAddress);
    }
}

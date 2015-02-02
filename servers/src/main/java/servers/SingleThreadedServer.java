package servers;

import Exceptions.ListenerCouldntConnectException;
import Exceptions.NoInternetConnectionException;
import Exceptions.ServersIOException;

import java.net.ServerSocket;
import java.net.Socket;

/**
 * Ethan Petuchowski 1/28/15
 */
public abstract class SingleThreadedServer extends Server {
    protected Socket socket;
    public SingleThreadedServer() throws ListenerCouldntConnectException, NoInternetConnectionException {
        super();
    }

    public SingleThreadedServer(ServerSocket listener) throws ListenerCouldntConnectException, NoInternetConnectionException {
        super(listener);
    }

    public SingleThreadedServer(int fromPortNo, int toPortNo) throws ListenerCouldntConnectException, NoInternetConnectionException, ServersIOException {
        super(fromPortNo, toPortNo);
    }

    public SingleThreadedServer(int port) throws ListenerCouldntConnectException, NoInternetConnectionException {
        super(port);
    }
}

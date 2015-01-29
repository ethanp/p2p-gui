package servers;

import Exceptions.ListenerCouldntConnectException;
import Exceptions.NotConnectedException;

import java.net.ServerSocket;
import java.net.Socket;

/**
 * Ethan Petuchowski 1/28/15
 */
public abstract class SingleThreadedServer extends Server {
    protected Socket socket;
    public SingleThreadedServer() throws ListenerCouldntConnectException, NotConnectedException {
        super();
    }

    public SingleThreadedServer(ServerSocket listener) throws ListenerCouldntConnectException, NotConnectedException {
        super(listener);
    }

    public SingleThreadedServer(int fromPortNo, int toPortNo) throws ListenerCouldntConnectException, NotConnectedException {
        super(fromPortNo, toPortNo);
    }

}

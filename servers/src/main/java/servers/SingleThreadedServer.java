package servers;

import Exceptions.ListenerCouldntConnectException;

import java.net.ServerSocket;

/**
 * Ethan Petuchowski 1/28/15
 */
public abstract class SingleThreadedServer extends Server {
    public SingleThreadedServer() throws ListenerCouldntConnectException {
    }

    public SingleThreadedServer(ServerSocket listener) throws ListenerCouldntConnectException {
        super(listener);
    }

    public SingleThreadedServer(int fromPortNo, int toPortNo) throws ListenerCouldntConnectException {
        super(fromPortNo, toPortNo);
    }
}

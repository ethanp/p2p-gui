package servers;

import Exceptions.ListenerCouldntConnectException;
import Exceptions.NotConnectedException;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Ethan Petuchowski 1/28/15
 */
public abstract class MultiThreadedServer<ThreadType extends ServerThread> extends Server {

    protected ExecutorService threadPool;

    public MultiThreadedServer(int poolSize) throws ListenerCouldntConnectException, NotConnectedException {
        super();
        threadPool = Executors.newFixedThreadPool(poolSize);
    }

    public MultiThreadedServer(ServerSocket listener, int poolSize) throws ListenerCouldntConnectException, NotConnectedException {
        super(listener);
        threadPool = Executors.newFixedThreadPool(poolSize);
    }

    public MultiThreadedServer(int fromPortNo, int toPortNo, int poolSize) throws ListenerCouldntConnectException, NotConnectedException {
        super(fromPortNo, toPortNo);
        threadPool = Executors.newFixedThreadPool(poolSize);
    }

    @Override protected void dealWithSocket(Socket socket) {
        ServerThread serverThread = ThreadType.create(socket);
        threadPool.submit(serverThread);
    }
}

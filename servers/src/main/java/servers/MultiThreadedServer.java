package servers;

import Exceptions.ListenerCouldntConnectException;
import Exceptions.NoInternetConnectionException;
import Exceptions.ServersIOException;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Ethan Petuchowski 1/28/15
 */
public abstract class MultiThreadedServer<TaskType extends ServerTaskRunner> extends Server {

    protected ExecutorService threadPool;

    public MultiThreadedServer(int poolSize) throws ListenerCouldntConnectException, NoInternetConnectionException {
        super();
        threadPool = Executors.newFixedThreadPool(poolSize);
    }

    public MultiThreadedServer(int port, int poolSize) throws ListenerCouldntConnectException, NoInternetConnectionException {
        super(port);
        threadPool = Executors.newFixedThreadPool(poolSize);
    }

    public MultiThreadedServer(ServerSocket listener, int poolSize) throws ListenerCouldntConnectException, NoInternetConnectionException {
        super(listener);
        threadPool = Executors.newFixedThreadPool(poolSize);
    }

    public MultiThreadedServer(int fromPortNo, int toPortNo, int poolSize) throws ListenerCouldntConnectException, NoInternetConnectionException, ServersIOException {
        super(fromPortNo, toPortNo);
        threadPool = Executors.newFixedThreadPool(poolSize);
    }

    protected abstract TaskType createTask(Socket socket) throws ServersIOException;

    /* implements abstract method */
    @Override protected void dealWithSocket(Socket socket) throws ServersIOException {
        TaskType serverTask = createTask(socket);
        threadPool.submit(new Thread(serverTask));
    }
}

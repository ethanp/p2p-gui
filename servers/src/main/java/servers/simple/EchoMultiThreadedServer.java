package servers.simple;

import Exceptions.ListenerCouldntConnectException;
import Exceptions.NoInternetConnectionException;
import Exceptions.ServersIOException;
import servers.MultiThreadedServer;
import servers.ServerTaskRunner;
import util.ServersCommon;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/**
 * Ethan Petuchowski 2/2/15
 */
public class EchoMultiThreadedServer extends MultiThreadedServer<EchoMultiThreadedServer.EchoTaskRunner> {
    public class EchoTaskRunner extends ServerTaskRunner {
        @Override public void run() {
            try (
                    BufferedReader reader = ServersCommon.bufferedReader(socket);
                    PrintWriter writer = ServersCommon.printWriter(socket))
            {
                List<String> lines = new ArrayList<>();
                String rcvLine = null;
                while (true) {
                    try {
                        rcvLine = reader.readLine();
                    }
                    catch (IOException e) {
                        e.printStackTrace();
                    }
                    if (rcvLine == null || rcvLine.trim().isEmpty()) {
                        break;
                    }
                    System.out.println("Server rcvd: "+rcvLine);
                    lines.add(rcvLine);
                }
                for (String line : lines) {
                    writer.println(line);
                }
            }
            catch (ServersIOException | IOException e) {
                e.printStackTrace();
            }
            try {
                socket.close();
            }
            catch (IOException ignored) {}
        }

        public EchoTaskRunner(Socket socket) {
            super(socket);
        }
    }
    public EchoMultiThreadedServer(int poolSize) throws ListenerCouldntConnectException, NoInternetConnectionException {
        super(poolSize);
    }
    public EchoMultiThreadedServer(int port, int poolSize) throws ListenerCouldntConnectException, NoInternetConnectionException {
        super(port, poolSize);
    }
    public EchoMultiThreadedServer(ServerSocket listener, int poolSize) throws ListenerCouldntConnectException, NoInternetConnectionException {
        super(listener, poolSize);
    }
    public EchoMultiThreadedServer(int fromPortNo, int toPortNo, int poolSize) throws ListenerCouldntConnectException, NoInternetConnectionException, ServersIOException {
        super(fromPortNo, toPortNo, poolSize);
    }

    @Override protected EchoTaskRunner createTask(Socket socket) {
        return new EchoTaskRunner(socket);
    }
}

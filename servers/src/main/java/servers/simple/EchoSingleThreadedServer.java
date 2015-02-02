package servers.simple;

import Exceptions.ListenerCouldntConnectException;
import Exceptions.NoInternetConnectionException;
import Exceptions.ServersIOException;
import servers.SingleThreadedServer;
import util.ServersCommon;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/**
 * Ethan Petuchowski 2/1/15
 *
 * After a connection is established, it reads string lines from the socket until it finds
 * an empty line, then it sends back all the strings it received and closes the connection.
 */
public class EchoSingleThreadedServer extends SingleThreadedServer {

    public EchoSingleThreadedServer() throws ListenerCouldntConnectException, NoInternetConnectionException {
    }

    public EchoSingleThreadedServer(ServerSocket listener) throws ListenerCouldntConnectException, NoInternetConnectionException {
        super(listener);
    }

    public EchoSingleThreadedServer(int fromPortNo, int toPortNo) throws ListenerCouldntConnectException, NoInternetConnectionException, ServersIOException {
        super(fromPortNo, toPortNo);
    }

    public EchoSingleThreadedServer(int port) throws ListenerCouldntConnectException, NoInternetConnectionException {
        super(port);
    }

    @Override protected void dealWithSocket(Socket socket) throws ServersIOException {
        BufferedReader reader = ServersCommon.bufferedReader(socket);
        PrintWriter writer = ServersCommon.printWriter(socket);
        List<String> lines = new ArrayList<>();
        String rcvLine;
        while (true) {
            try {
                rcvLine = reader.readLine();
            }
            catch (IOException e) {
                throw new ServersIOException(e);
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
        writer.flush();
        writer.close();
        try { socket.close(); }
        catch (IOException ignored) {}
    }
}

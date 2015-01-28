package util.server;

import util.Common;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Ethan Petuchowski 1/28/15
 */
public abstract class SimpleSingleServer extends SimpleServer {
    protected abstract void runLoopCode() throws IOException;
    protected Socket singleSocket;
    protected BufferedReader bufferedReader;
    protected PrintWriter printWriter;

    @Override protected void useConnection(Socket connection) throws IOException {
            singleSocket = connection;
            bufferedReader = Common.bufferedReader(singleSocket);
            printWriter = Common.printWriter(singleSocket);
            runLoopCode();
    }
}

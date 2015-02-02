package servers;

import Exceptions.ServersIOException;
import util.ServersCommon;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Ethan Petuchowski 1/28/15
 */
public abstract class PrintWriterServerTaskRunner extends ServerTaskRunner {
    BufferedReader bufferedReader;
    PrintWriter printWriter;

    public PrintWriterServerTaskRunner(Socket socket) throws ServersIOException {
        super(socket);
        bufferedReader = ServersCommon.bufferedReader(socket);
        printWriter = ServersCommon.printWriter(socket);
    }
}

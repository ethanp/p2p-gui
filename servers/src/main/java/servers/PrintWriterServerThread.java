package servers;

import Exceptions.ServersIOException;
import util.ServersCommon;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Ethan Petuchowski 1/28/15
 */
public abstract class PrintWriterServerThread extends ServerThread {
    BufferedReader bufferedReader;
    PrintWriter printWriter;

    public PrintWriterServerThread(Socket socket) throws ServersIOException {
        super(socket);
        try {
            bufferedReader = ServersCommon.bufferedReader(socket);
            printWriter = ServersCommon.printWriter(socket);
        }
        catch (IOException e) {
            throw new ServersIOException(e);
        }
    }
}
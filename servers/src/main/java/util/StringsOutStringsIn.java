package util;

import Exceptions.FailedToFindServerException;
import Exceptions.ServersIOException;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.net.InetSocketAddress;

/**
 * Ethan Petuchowski 2/11/15
 */
public class StringsOutStringsIn extends Connection {

    public PrintWriter writer;
    public BufferedReader reader;

    public StringsOutStringsIn(InetSocketAddress addr) {
        super(addr);
    }

    @Override public void connect() throws FailedToFindServerException, ServersIOException {
        super.connect();
        writer = ServersCommon.printWriter(socket);
        reader = ServersCommon.bufferedReader(socket);
    }
}

package util;

import Exceptions.FailedToFindServerException;
import Exceptions.ServersIOException;

import java.io.BufferedInputStream;
import java.io.PrintWriter;
import java.net.InetSocketAddress;

/**
 * Ethan Petuchowski 2/11/15
 */
public class StringsOutBytesIn extends Connection {

    public BufferedInputStream in;
    public PrintWriter writer;

    public StringsOutBytesIn(InetSocketAddress addr) {
        super(addr);
    }

    @Override public void connect() throws FailedToFindServerException, ServersIOException {
        super.connect();
        in = ServersCommon.buffIStream(socket);
        writer = ServersCommon.printWriter(socket);
    }
}

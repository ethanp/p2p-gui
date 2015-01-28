package util.server;

import util.Common;

import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Ethan Petuchowski 1/28/15
 */
public abstract class SimpleMultiServer<InnerServer extends SimpleInnerServer> extends SimpleServer {

    ExecutorService threadPool = Executors.newFixedThreadPool(Common.CHUNK_SERVE_POOL_SIZE);

    @Override protected void useConnection(Socket connection) throws IOException {
        SimpleInnerServer innerServer = InnerServer.create(connection);
    }
}

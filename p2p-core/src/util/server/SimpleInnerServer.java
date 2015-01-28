package util.server;

import java.net.Socket;

/**
 * Ethan Petuchowski 1/28/15
 */
public abstract class SimpleInnerServer extends Thread {

    protected final Socket innerSocket;

    SimpleInnerServer(Socket connection) {
        innerSocket = connection;
    }

    @Override public void run() {
        innerLoopCode();
    }

    protected abstract void innerLoopCode();

    public static SimpleInnerServer create(Socket connection) {
        throw new RuntimeException("Can't instantiate SimpleInnerServer directly");
    }
}

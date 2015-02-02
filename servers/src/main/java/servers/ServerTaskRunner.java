package servers;

import java.net.Socket;

/**
 * Ethan Petuchowski 1/28/15
 */
public abstract class ServerTaskRunner implements Runnable {
    protected Socket socket;

    public ServerTaskRunner(Socket socket) {
        this.socket = socket;
    }
}

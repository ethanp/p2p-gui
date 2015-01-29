package servers;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.net.Socket;

/**
 * Ethan Petuchowski 1/28/15
 */
public abstract class ServerThread extends Thread {
    protected Socket socket;

    public ServerThread(Socket socket) {
        this.socket = socket;
    }

    public static ServerThread create(Socket socket) {
        throw new NotImplementedException();
    }

}

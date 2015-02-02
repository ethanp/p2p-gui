package Exceptions;

/**
 * Ethan Petuchowski 2/1/15
 */
public class SocketCouldntConnectException extends ServersException {
    public SocketCouldntConnectException() {
        super();
    }
    public SocketCouldntConnectException(String message) {
        super(message);
    }
    public SocketCouldntConnectException(Throwable cause) {
        super(cause);
    }
    public SocketCouldntConnectException(String message, Throwable cause) {
        super(message, cause);
    }
}

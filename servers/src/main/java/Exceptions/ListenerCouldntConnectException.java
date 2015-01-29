package Exceptions;

/**
 * Ethan Petuchowski 1/28/15
 */
public class ListenerCouldntConnectException extends ServersException {
    public ListenerCouldntConnectException() {
        super();
    }
    public ListenerCouldntConnectException(String message) {
        super(message);
    }
    public ListenerCouldntConnectException(Throwable cause) {
        super(cause);
    }
    public ListenerCouldntConnectException(String message, Throwable cause) {
        super(message, cause);
    }
}

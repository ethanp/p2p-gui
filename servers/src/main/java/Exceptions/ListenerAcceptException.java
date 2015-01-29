package Exceptions;

/**
 * Ethan Petuchowski 1/28/15
 */
public class ListenerAcceptException extends ServersException {
    public ListenerAcceptException() {
        super();
    }
    public ListenerAcceptException(String message) {
        super(message);
    }
    public ListenerAcceptException(Throwable cause) {
        super(cause);
    }
    public ListenerAcceptException(String message, Throwable cause) {
        super(message, cause);
    }
}

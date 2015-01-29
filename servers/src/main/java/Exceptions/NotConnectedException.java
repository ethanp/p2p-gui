package Exceptions;

/**
 * Ethan Petuchowski 1/28/15
 */
public class NotConnectedException extends ServersException {
    public NotConnectedException() {
        super();
    }
    public NotConnectedException(String message) {
        super(message);
    }
    public NotConnectedException(Throwable cause) {
        super(cause);
    }
    public NotConnectedException(String message, Throwable cause) {
        super(message, cause);
    }
}

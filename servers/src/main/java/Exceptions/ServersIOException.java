package Exceptions;

/**
 * Ethan Petuchowski 1/28/15
 */
public class ServersIOException extends ServersException {
    public ServersIOException() {
        super();
    }
    public ServersIOException(String message) {
        super(message);
    }
    public ServersIOException(Throwable cause) {
        super(cause);
    }
    public ServersIOException(String message, Throwable cause) {
        super(message, cause);
    }
}

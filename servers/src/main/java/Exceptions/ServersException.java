package Exceptions;

/**
 * Ethan Petuchowski 1/28/15
 */
public class ServersException extends Exception {
    public ServersException() {
        super();
    }
    public ServersException(String message) {
        super(message);
    }
    public ServersException(Throwable cause) {
        super(cause);
    }
    public ServersException(String message, Throwable cause) {
        super(message, cause);
    }
}

package Exceptions;

/**
 * Ethan Petuchowski 1/28/15
 */
public class NoInternetConnectionException extends ServersException {
    public NoInternetConnectionException() {
        super();
    }
    public NoInternetConnectionException(String message) {
        super(message);
    }
    public NoInternetConnectionException(Throwable cause) {
        super(cause);
    }
    public NoInternetConnectionException(String message, Throwable cause) {
        super(message, cause);
    }
}

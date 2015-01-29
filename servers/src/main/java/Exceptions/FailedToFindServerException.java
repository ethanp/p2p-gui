package Exceptions;

/**
 * Ethan Petuchowski 1/29/15
 */
public class FailedToFindServerException extends ServersException {
    public FailedToFindServerException() {
        super();
    }
    public FailedToFindServerException(String message) {
        super(message);
    }
    public FailedToFindServerException(Throwable cause) {
        super(cause);
    }
    public FailedToFindServerException(String message, Throwable cause) {
        super(message, cause);
    }
}

package p2p.exceptions;

/**
 * Ethan Petuchowski 2/14/15
 */
public class InvalidDataException extends P2PException {
    public InvalidDataException() { super(); }
    public InvalidDataException(String message) { super(message); }
    public InvalidDataException(Throwable cause) { super(cause); }
    public InvalidDataException(String message, Throwable cause) { super(message, cause); }
}

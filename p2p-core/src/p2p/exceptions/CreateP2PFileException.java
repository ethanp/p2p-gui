package p2p.exceptions;

/**
 * Ethan Petuchowski 1/18/15
 */
public class CreateP2PFileException extends P2PException {
    public CreateP2PFileException() { super(); }
    public CreateP2PFileException(String message) { super(message); }
    public CreateP2PFileException(Throwable cause) { super(cause); }
    public CreateP2PFileException(String message, Throwable cause) { super(message, cause); }
}

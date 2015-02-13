package p2p.exceptions;

/**
 * Ethan Petuchowski 2/10/15
 */
public class FileUnavailableException extends p2p.exceptions.P2PException {
    public FileUnavailableException() { super(); }
    public FileUnavailableException(String message) { super(message); }
    public FileUnavailableException(Throwable cause) { super(cause); }
    public FileUnavailableException(String message, Throwable cause) { super(message, cause); }
}

package p2p.exceptions;

/**
 * Ethan Petuchowski 1/18/15
 */
public class P2PConnectionException extends p2p.exceptions.P2PException {
    public P2PConnectionException() { super(); }
    public P2PConnectionException(String message) { super(message); }
    public P2PConnectionException(Throwable cause) { super(cause); }
    public P2PConnectionException(String message, Throwable cause) { super(message, cause); }
}

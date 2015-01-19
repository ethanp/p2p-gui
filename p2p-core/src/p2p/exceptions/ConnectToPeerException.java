package p2p.exceptions;

/**
 * Ethan Petuchowski 1/18/15
 */
public class ConnectToPeerException extends P2PConnectionException {
    public ConnectToPeerException() { super(); }
    public ConnectToPeerException(String message) { super(message); }
    public ConnectToPeerException(Throwable cause) { super(cause); }
    public ConnectToPeerException(String message, Throwable cause) { super(message, cause); }
}

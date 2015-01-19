package p2p.exceptions;

/**
 * Ethan Petuchowski 1/18/15
 */
public class ConnectToTrackerException extends P2PConnectionException {
    public ConnectToTrackerException() { super(); }
    public ConnectToTrackerException(String message) { super(message); }
    public ConnectToTrackerException(Throwable cause) { super(cause); }
    public ConnectToTrackerException(String message, Throwable cause) { super(message, cause); }
}

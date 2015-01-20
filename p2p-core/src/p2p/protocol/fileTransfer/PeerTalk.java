package p2p.protocol.fileTransfer;

/**
 * Ethan Petuchowski 1/19/15
 *
 * I'm using Strings rather than an Enum because it means that the string
 * value is a compile time constant and can be sent across with a PrintWriter,
 * received, and used in a switch statement.
 */
public abstract class PeerTalk {
    public static final String GET_AVAILABILITIES = "GET AVAILABILITIES";
}

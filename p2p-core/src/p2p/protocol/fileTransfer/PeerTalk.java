package p2p.protocol.fileTransfer;

/**
 * Ethan Petuchowski 1/19/15
 *
 * I'm using Strings rather than an Enum because it means that the string
 * value is a compile time constant and can be sent across with a PrintWriter,
 * received, and used in a switch statement.
 */
public abstract class PeerTalk {
    public abstract class ToTracker {
        public static final String ADD_FILE_REQUEST = "ADD";
        public static final String ECHO = "ECHO";
    }
    public abstract class ToPeer {
        public static final String GET_CHUNK = "CHUNK";
        public static final String GET_AVAILABILITIES = "GET";
        public static final int CHUNK_NOT_AVAILABLE = -1;
        public static final int FILE_NOT_AVAILABLE = -2;
        public static final int OUT_OF_BOUNDS = -3;
    }
}

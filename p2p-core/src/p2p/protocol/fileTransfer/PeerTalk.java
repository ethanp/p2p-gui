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
        public static final String LIST_FILES = "LIST";
        public static final String SWARM_UPDATE = "SWARMS";
    }
    public abstract class ToPeer {
        public static final String GET_CHUNK = "CHUNK";
        public static final String GET_AVBL = "GET";
    }
    public abstract class FromPeer {
        public static final int CHUNK_NOT_AVAILABLE = -1;
        public static final int FILE_NOT_AVAILABLE = -2;
        public static final int OUT_OF_BOUNDS = -3;
        public static final int DEFAULT_VALUE = -4;
    }
}

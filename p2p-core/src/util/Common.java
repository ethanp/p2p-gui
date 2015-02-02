package util;

import java.util.Random;

/**
 * Ethan Petuchowski 1/13/15
 */
public class Common {

    public enum ExitCodes {
        SERVER_FAILURE
    }

    public static Random r = new Random();
    public static int randInt(int bound) { return r.nextInt(bound); }

    /* these are the ports that the router is configured to forward to me */
    public static final int PORT_MIN = 3000;
    public static final int PORT_MAX = 3500;

    public static final int NUM_CHUNK_BYTES = 1 << 13; // 8KB

    /* chars have 2 bytes
     * http://docs.oracle.com/javase/tutorial/java/nutsandbolts/datatypes.html */
    public static final int NUM_CHUNK_CHARS = NUM_CHUNK_BYTES / 2;

    public static final int MAX_FILESIZE = Integer.MAX_VALUE;

    public static final int CHUNK_AVAILABILITY_POOL_SIZE = 10;
    public static final int CHUNK_REQUEST_POOL_SIZE = 20;
    public static final int CHUNK_SERVE_POOL_SIZE = 20;
    public static final int FILE_DOWNLOADS_POOL_SIZE = 4; // uTorrent's is user-configurable

    public static String formatByteCountToString(long numBytes) {
        assert numBytes >= 0 : "can't have negative number of bytes: "+numBytes;
        if (numBytes < 1E3) return String.format("%d B", numBytes);
        if (numBytes < 1E6) return String.format("%.2f KB", numBytes/1E3);
        if (numBytes < 1E9) return String.format("%.2f MB", numBytes/1E6);
        else return String.format("%.2f GB", numBytes/1E9);
    }
}

package util;

import java.io.IOException;
import java.io.InputStream;
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

    /* 2/2/15 no idea if this has any good or bad properties
     *        all I know is that it has worked for me so far
     *
     * Source: nowhere.
     */
    public static int readIntLineFromStream(InputStream stream) throws IOException {
        StringBuilder sb = new StringBuilder();
        byte[] forChar;
        for (;;) {
            forChar = new byte[1];
            stream.read(forChar); // for a buffered stream, these are probably all from the buffer

            // for speed one could use a switch on the values of 0-9 & \n and throw errors otw
            // but this is fine for now.
            String newChar = new String(forChar);

            if (newChar.equals("\n"))
                break;

            sb.append(newChar);
        }
        return Integer.parseInt(sb.toString());
    }
}

package util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Random;

/**
 * Ethan Petuchowski 1/13/15
 */
public class Common {

    public static final String SYS_LINE_SEP = System.getProperty("line.separator");

    public static long secToNano(int i) {
        return i * 1_000_000_000; // en.wikipedia.org/wiki/Nano- says 1E-9,
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

    public static String formatByteCount(long numBytes) {
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
        byte[] forChar = new byte[1];
        for (;;) {
            int rdVal = stream.read(forChar); // for a buffered stream, these are probably all from the buffer

            if (rdVal == -1 || rdVal > 1)
                throw new RuntimeException("Stream read value: "+rdVal);

            if (rdVal == 1) {
                String newChar = new String(forChar);

                if (newChar.equals("\n"))
                    return Integer.parseInt(sb.toString());

                if (sb.length() == 0 && !newChar.matches("[-0-9]")
                 || sb.length() >  0 && !newChar.matches("[0-9]"))
                    throw new RuntimeException("non-digit found in stream");

                sb.append(newChar);
            }

            if (rdVal == 0)
                System.out.println("No data read from stream");
        }
    }
}

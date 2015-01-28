package util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.util.Random;

/**
 * Ethan Petuchowski 1/13/15
 */
public class Common {

    public enum ExitCodes {
        SERVER_FAILURE
    }

    public static Random r = new Random();

    /* these are the ports that the router is configured to forward to me */
    public static final int PORT_MIN = 3000;
    public static final int PORT_MAX = 3500;

    public static final int DEFAULT_CHUNK_SIZE = 1 << 12; // 4KB

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

    public static BufferedReader bufferedReader(Socket s) {
        try { return new BufferedReader(new InputStreamReader(s.getInputStream())); }
        catch (IOException e) { e.printStackTrace(); }
        return null;
    }

    public static BufferedWriter bufferedWriter(Socket s) {
        try { return new BufferedWriter(new OutputStreamWriter(s.getOutputStream())); }
        catch (IOException e) { e.printStackTrace(); }
        return null;
    }

    public static PrintWriter printWriter(Socket s) {
        try { return new PrintWriter(s.getOutputStream(), true); }
        catch (IOException e) { e.printStackTrace(); }
        return null;
    }

    public static ObjectOutputStream objectOStream(Socket s) throws IOException {
        ObjectOutputStream oos = new ObjectOutputStream(s.getOutputStream());
        oos.flush();
        return oos;
    }

    public static ObjectInputStream objectIStream(Socket s) throws IOException {
        return new ObjectInputStream(s.getInputStream());
    }

    public static InetAddress findMyIP() {
        URL aws = null;
        InetAddress toRet = null;
        try { aws = new URL("http://checkip.amazonaws.com"); }
        catch (MalformedURLException e) { e.printStackTrace(); }
        BufferedReader in = null;
        try {
            if (aws != null) {
                in = new BufferedReader(new InputStreamReader(aws.openStream()));
                String ip = in.readLine();
                toRet = InetAddress.getByName(ip);
            }
        }
        catch (IOException e) {
            System.err.println("Peer not connected to Internet: can't find its IP");
        }
        finally {
            if (in != null) {
                try { in.close(); }
                catch (IOException e) { e.printStackTrace(); }
            }
        }
        return toRet;
    }

    /**
     * the range is CLOSED on BOTH ends
     */
    public static ServerSocket socketPortInRange(int start, int end) throws IOException {
        int[] ports = new int[end-start+1];
        for (int i = 0; i <= end-start; i++)
            ports[i] = i+start;
        return socketPortFromOptions(ports);
    }

    public static ServerSocket socketPortFromOptions(int[] ports) throws IOException {
        for (int port : ports) {
            try {
                return new ServerSocket(port);
            }
            catch (IOException ex) {
                /* try next port */
            }
        }
        throw new IOException("no free port found");
    }

    public static String ipPortToString(InetSocketAddress addr) {
        if (addr.isUnresolved()) {
            return addr.getHostName()+":"+addr.getPort()+" (unresolved)";
        } else {
            return addr.getAddress().toString().substring(1)+":"+addr.getPort();
        }
    }

    public static InetSocketAddress stringToIPPort(String str) {
        String[] strs = str.split(":");
        String ipAddr = strs[0];
        int port = Integer.parseInt(strs[1]);
        return new InetSocketAddress(ipAddr, port);
    }

    public static int randInt(int bound) { return r.nextInt(bound); }

    public static String randomIPPortString() {
        return r.nextInt(255)+"."+r.nextInt(255)+"."+
               r.nextInt(255)+"."+r.nextInt(255)+":"+r.nextInt(5000);
    }

    public static Socket connectToInetSocketAddr(InetSocketAddress inetSocketAddr)
            throws IOException
    {
        InetAddress ipAddr = inetSocketAddr.getAddress();
        int port = inetSocketAddr.getPort();
        return new Socket(ipAddr, port);
    }
}

package util;

import Exceptions.FailedToFindServerException;
import Exceptions.NoInternetConnectionException;
import Exceptions.ServersIOException;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.Random;

/**
 * Ethan Petuchowski 1/28/15
 */
public class ServersCommon {

    static final String IP4_SEG = "\\d{1,3}";
    static final String IP4_PRE = IP4_SEG+"\\.";
    public static final String IP_PORT_REG = "\\d{1,5}";
    public static final String IP4_REG = IP4_PRE+IP4_PRE+IP4_PRE+IP4_SEG;
    public static final String IP4_wPORT_REG = IP4_REG+":"+IP_PORT_REG;
    public static final int asMilli = 1000;

    public static Random r = new Random();
    public static int randInt(int bound) { return r.nextInt(bound); }

    public static BufferedReader bufferedReader(Socket s) throws ServersIOException {
        try {
            return new BufferedReader(new InputStreamReader(s.getInputStream()));
        }
        catch (IOException e) {
            throw new ServersIOException(e);
        }
    }

    public static InputStreamReader inReader(Socket s) throws ServersIOException {
        try {
            return new InputStreamReader(s.getInputStream());
        }
        catch (IOException e) {
            throw new ServersIOException(e);
        }
    }

    public static BufferedWriter bufferedWriter(Socket s) throws ServersIOException {
        try {
            return new BufferedWriter(new OutputStreamWriter(s.getOutputStream()));
        }
        catch (IOException e) {
            throw new ServersIOException(e);
        }
    }

    public static BufferedInputStream buffIStream(Socket s) throws ServersIOException {
        try {
            return new BufferedInputStream(s.getInputStream());
        }
        catch (IOException e) {
            throw new ServersIOException(e);
        }
    }

        public static BufferedOutputStream buffOStream(Socket s) throws ServersIOException {
        try {
            return new BufferedOutputStream(s.getOutputStream());
        }
        catch (IOException e) {
            throw new ServersIOException(e);
        }
    }

    public static PrintWriter printWriter(Socket s) throws ServersIOException {
        try {
            return new PrintWriter(s.getOutputStream(), true);
        }
        catch (IOException e) {
            throw new ServersIOException(e);
        }
    }

    public static ObjectOutputStream objectOStream(Socket s) throws ServersIOException {
        ObjectOutputStream oos = null;
        try {
            oos = new ObjectOutputStream(s.getOutputStream());
            oos.flush();
        }
        catch (IOException e) {
            throw new ServersIOException(e);
        }
        return oos;
    }

    public static ObjectInputStream objectIStream(Socket s) throws ServersIOException {
        try {
            return new ObjectInputStream(s.getInputStream());
        }
        catch (IOException e) {
            throw new ServersIOException(e);
        }
    }

    public static InetAddress findMyIP() throws NoInternetConnectionException {
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
            throw new NoInternetConnectionException(e);
        }
        finally {
            if (in != null) {
                try { in.close(); }
                catch (IOException e) { /* ignore */ }
            }
        }
        return toRet;
    }

    public static InetSocketAddress addrFromString(String addrStr) throws UnknownHostException {
        String[] pieces = addrStr.split(":");
        InetAddress ipAddr = InetAddress.getByName(pieces[0]);
        int portNo = Integer.parseInt(pieces[1]);
        return new InetSocketAddress(ipAddr, portNo);
    }

    /**
     * the range is CLOSED on BOTH ends
     */
    public static ServerSocket socketPortInRange(int start, int end) throws ServersIOException {
        int[] ports = new int[end-start+1];
        for (int i = 0; i <= end-start; i++)
            ports[i] = i+start;
        return socketPortFromOptions(ports);
    }

    static ServerSocket socketPortFromOptions(int[] ports) throws ServersIOException {
        for (int port : ports) {
            try {
                return new ServerSocket(port);
            }
            catch (IOException ex) {
                continue; /* try next port */
            }
        }
        throw new ServersIOException("no free port found");
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

    public static Socket connectToInetSocketAddr(InetSocketAddress inetSocketAddr) throws FailedToFindServerException
    {
        try {
            Socket s = new Socket();
            s.connect(inetSocketAddr, 2*asMilli);
            return s;
        }
        catch (IOException e) {
            throw new FailedToFindServerException(e);
        }
    }

    public static Socket connectLocallyToInetAddr(InetSocketAddress inetSocketAddr) throws FailedToFindServerException
    {
        try {
            return new Socket("0.0.0.0", inetSocketAddr.getPort());
        }
        catch (IOException e) {
            throw new FailedToFindServerException(e);
        }
    }

    public static String randomIPPortString() {
        return r.nextInt(255)+"."+r.nextInt(255)+"."+
               r.nextInt(255)+"."+r.nextInt(255)+":"+r.nextInt(5000);
    }

    public static InetSocketAddress randomSocketAddr() {
        return stringToIPPort(randomIPPortString());
    }

    /* TODO move this into regular Common class */
    public static void streamIntLine(OutputStream outStream, int intToSend) throws IOException {
        String intStr = intToSend+"\n";
        outStream.write(intStr.getBytes());
    }
}

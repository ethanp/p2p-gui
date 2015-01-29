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
import java.net.UnknownHostException;

/**
 * Ethan Petuchowski 1/28/15
 */
public class ServersCommon {
    public static BufferedReader bufferedReader(Socket s) throws IOException {
        return new BufferedReader(new InputStreamReader(s.getInputStream()));
    }

    public static BufferedWriter bufferedWriter(Socket s) throws IOException {
        return new BufferedWriter(new OutputStreamWriter(s.getOutputStream()));
    }

    public static PrintWriter printWriter(Socket s) throws IOException {
        return new PrintWriter(s.getOutputStream(), true);
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
            System.err.println("Not connected to Internet: can't find my IP");
        }
        finally {
            if (in != null) {
                try { in.close(); }
                catch (IOException e) { /* ignore */ }
            }
        }
        return toRet;
    }

    public static Socket socketAtAddr(InetSocketAddress addr) throws IOException {
        return new Socket(addr.getAddress(), addr.getPort());
    }

    public static InetSocketAddress addrFromString(String addrStr)
            throws UnknownHostException
    {
        String[] pieces = addrStr.split(":");
        InetAddress ipAddr = InetAddress.getByName(pieces[0]);
        int portNo = Integer.parseInt(pieces[1]);
        return new InetSocketAddress(ipAddr, portNo);
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

    static ServerSocket socketPortFromOptions(int[] ports) throws IOException {
        for (int port : ports) {
            try {
                return new ServerSocket(port);
            }
            catch (IOException ex) {
                continue; /* try next port */
            }
        }
        throw new IOException("no free port found");
    }
}

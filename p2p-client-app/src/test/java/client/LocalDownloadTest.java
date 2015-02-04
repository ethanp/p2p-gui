package client;

import Exceptions.FailedToFindServerException;
import Exceptions.ServersIOException;
import client.download.FileDownload;
import client.p2pFile.P2PFile;
import client.peer.RemotePeer;
import client.state.ClientState;
import client.tracker.RemoteTracker;
import client.tracker.swarm.ClientSwarm;
import org.junit.Before;
import p2p.exceptions.CreateP2PFileException;
import p2p.file.meta.MetaP2PFile;
import p2p.protocol.fileTransfer.PeerTalk;
import util.Common;
import util.ServersCommon;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Random;

/**
 * Ethan Petuchowski 2/4/15
 */
public class LocalDownloadTest {
    static Random r = new Random();

    protected File serveDir;
    protected File downloadDir;

    protected File   sequenceServedFile;
    protected byte[] sequenceFileBytes;

    protected Socket socket;
    protected PrintWriter writer;
    protected BufferedInputStream inputStream;
    protected MetaP2PFile sequenceMeta;

    @Before
    public void setUp() throws Exception {
        createCleanServeAndDownloadDirectories();
        sequenceServedFile = new File(serveDir, "local-1");
        sequenceFileBytes = LocalDownloadTest.fillWithNumberSequence(sequenceServedFile);
        LocalDownloadTest.serveFile(sequenceServedFile);
        sequenceMeta = ClientState.getLocalP2PFile(sequenceServedFile).getMetaPFile();
        connectToMyOwnPeerServer();
    }

    private void connectToMyOwnPeerServer() throws FailedToFindServerException, ServersIOException {
        socket = ServersCommon.connectLocallyToInetAddr(myAddress());
        writer = ServersCommon.printWriter(socket);
        inputStream = ServersCommon.buffIStream(socket);
    }

    public FileDownload makeSequenceFileDownload(MetaP2PFile meta) throws CreateP2PFileException {
        RemoteTracker stubTracker = null;
        ClientSwarm clientSwarm = new ClientSwarm(meta, stubTracker);
        RemotePeer myselfAsPeer = new RemotePeer(myAddress());
        clientSwarm.addSeeder(myselfAsPeer);
        FileDownload fileDownload = new FileDownload(downloadDir, clientSwarm);
        return fileDownload;
    }

    public void createCleanServeAndDownloadDirectories() throws FileNotFoundException {
        serveDir = LocalDownloadTest.makeCleanDir("p2p-gui serve");
        downloadDir = LocalDownloadTest.makeCleanDir("p2p-gui DL");
        ClientState.setUserDownloadDirectory(downloadDir);
    }

    public static P2PFile serveFile(File file) throws CreateP2PFileException, IOException {
        ClientState.addLocalFile(file);
        return ClientState.getLocalP2PFile(file);
    }

    public static byte[] fillWithNumberSequence(File localFIle) throws IOException, CreateP2PFileException {
        BufferedOutputStream outputStream = new BufferedOutputStream(new FileOutputStream(localFIle));
        byte[] bytes = new byte[Common.NUM_CHUNK_BYTES];
        for (int i = 0; i < bytes.length; i++)
            bytes[i] = (byte) (i % (int) Byte.MAX_VALUE);
        outputStream.write(bytes);
        outputStream.close();
        return bytes;
    }

    public static void fillWithRandomData(File toFill, int numBytes) {
        try (BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(toFill))) {
            byte[] bytes = new byte[Common.NUM_CHUNK_BYTES];
            while (numBytes > 0) {
                if (numBytes >= Common.NUM_CHUNK_BYTES) {
                    r.nextBytes(bytes);
                    numBytes -= Common.NUM_CHUNK_BYTES;
                    out.write(bytes);
                }
                else {
                    byte[] smallBytes = new byte[numBytes];
                    r.nextBytes(smallBytes);
                    out.write(smallBytes);
                    break;
                }
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void deleteR(File file) throws FileNotFoundException {
        if (file.exists()) {
            File[] children = file.listFiles();
            if (children != null)
                for (File child : children)
                    deleteR(child);
            if (!file.delete())
                throw new FileNotFoundException("??");
        }
    }

    public static File makeCleanDir(String name) throws FileNotFoundException {
        File serveDir = new File(name);
        deleteR(serveDir);
        serveDir.mkdir();
        return serveDir;
    }

    protected void requestChunk(MetaP2PFile meta, int chunkIdx) {
        writer.println(PeerTalk.ToPeer.GET_CHUNK);
        writer.println(meta.serializeToString());
        writer.println(chunkIdx);
        writer.flush();
    }

    private InetSocketAddress myAddress() {
        return ClientState.getExternalSocketAddr();
    }
}

package client.server;

import Exceptions.FailedToFindServerException;
import Exceptions.ServersIOException;
import client.p2pFile.P2PFile;
import client.state.ClientState;
import org.junit.Before;
import org.junit.Test;
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
import java.net.Socket;
import java.util.Random;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

public class LocalPeerServerTest {

    File serveDir;
    File downloadDir;
    File simpleFile;

    MetaP2PFile simpleMeta;
    byte[]      simpleFileBytes;

    Socket socket;
    PrintWriter writer;
    BufferedInputStream inputStream;

    static Random r = new Random();

    @Before
    public void setUp() throws Exception {
        createDirectories();
        simpleFile = new File(serveDir, "local-1");
        simpleFileBytes = fillWithNumberSequence(simpleFile);
        serveFile(simpleFile);
        simpleMeta = ClientState.getLocalP2PFile(simpleFile).getMetaP2PFile();
        connectToMyOwnPeerServer();
    }

    private void serveFile(File file) throws CreateP2PFileException, IOException {
        ClientState.addLocalFile(file);
    }

    private void createDirectories() throws FileNotFoundException {
        serveDir = makeCleanDir("p2p-gui serve");
        downloadDir = makeCleanDir("p2p-gui DL");
        ClientState.setUserDownloadDirectory(downloadDir);
    }

    private File makeCleanDir(String name) throws FileNotFoundException {
        File serveDir = new File(name);
        deleteR(serveDir);
        serveDir.mkdir();
        return serveDir;
    }

    private void deleteR(File file) throws FileNotFoundException {
        if (file.exists()) {
            File[] children = file.listFiles();
            if (children != null)
                for (File child : children)
                    deleteR(child);
            if (!file.delete())
                throw new FileNotFoundException("??");
        }
    }

    private void fillFileWithRandomData(File toFill, int numBytes) {
        try (BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(toFill))) {
            byte[] bytes = new byte[Common.NUM_CHUNK_BYTES];
            while (numBytes > 0) {
                if (numBytes >= Common.NUM_CHUNK_BYTES) {
                    r.nextBytes(bytes);
                    numBytes -= Common.NUM_CHUNK_BYTES;
                }
                else {
                    byte[] smallBytes = new byte[numBytes];
                    r.nextBytes(smallBytes);
                    numBytes = 0;
                }
                out.write(bytes);
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    private byte[] fillWithNumberSequence(File localFIle) throws IOException, CreateP2PFileException {
        BufferedOutputStream outputStream = new BufferedOutputStream(new FileOutputStream(localFIle));
        byte[] bytes = new byte[Common.NUM_CHUNK_BYTES];
        for (int i = 0; i < bytes.length; i++)
            bytes[i] = (byte) (i % (int) Byte.MAX_VALUE);
        outputStream.write(bytes);
        outputStream.close();
        return bytes;
    }

    private void connectToMyOwnPeerServer() throws FailedToFindServerException, ServersIOException {
        socket = ServersCommon.connectLocallyToInetAddr(ClientState.getExternalSocketAddr());
        writer = ServersCommon.printWriter(socket);
        inputStream = ServersCommon.buffIStream(socket);
    }

    private void requestChunk(MetaP2PFile meta, int chunkIdx) {
        writer.println(PeerTalk.ToPeer.GET_CHUNK);
        writer.println(meta.serializeToString());
        writer.println(Integer.toString(chunkIdx));
        writer.flush();
    }

    @Test public void testServeChunk() throws Exception {
        final int FIRST_CHUNK_INDEX = 0;
        requestChunk(simpleMeta, FIRST_CHUNK_INDEX);
        int responseSize = Common.readIntLineFromStream(inputStream);
        assertEquals(simpleFileBytes.length, responseSize);

        byte[] response = new byte[simpleFileBytes.length];
        int bytesRcvd = 0;
        while (bytesRcvd < simpleFileBytes.length) {
            int rcv = inputStream.read(response, bytesRcvd, simpleFileBytes.length-bytesRcvd);
            if (rcv >= 0) {
                bytesRcvd += rcv;
            } else {
                throw new RuntimeException("it seems the whole file wasn't downloaded");
            }
        }
        assertArrayEquals(simpleFileBytes, response);
    }

    @Test public void testRequestChunkOutOfBounds() throws Exception {
        final int OOB_CHUNK_INDEX = 1;
        requestChunk(simpleMeta, OOB_CHUNK_INDEX);
        int responseSize = Common.readIntLineFromStream(inputStream);
        assertEquals(PeerTalk.ToPeer.OUT_OF_BOUNDS, responseSize);
    }

    @Test public void testRequestUnknownFile() throws Exception {
        MetaP2PFile unknownMeta = new MetaP2PFile("LaLaLand", 234234, "HEXADECIMAL_JOKE");
        requestChunk(unknownMeta, 1);
        int responseSize = Common.readIntLineFromStream(inputStream);
        assertEquals(PeerTalk.ToPeer.FILE_NOT_AVAILABLE, responseSize);
    }

    @Test public void testRequestNotOwnedChunk() throws Exception {
        File unknownFile = new File(serveDir, "partially-chunkless");
        fillFileWithRandomData(unknownFile, Common.NUM_CHUNK_BYTES * 3);
        serveFile(unknownFile);
        P2PFile pFile = ClientState.getLocalP2PFile(unknownFile);
        pFile.getAvailableChunks().setChunkAvailable(1, false);
        MetaP2PFile correspondingMetaFile = pFile.getMetaP2PFile();
        requestChunk(correspondingMetaFile, 1);
        int responseSize = Common.readIntLineFromStream(inputStream);
        assertEquals(PeerTalk.ToPeer.CHUNK_NOT_AVAILABLE, responseSize);
    }
}

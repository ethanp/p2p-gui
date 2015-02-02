package client.server;

import Exceptions.FailedToFindServerException;
import Exceptions.ServersIOException;
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

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

public class PeerServerTest {

    File serveDir, downloadDir, simpleFile;
    MetaP2PFile simpleMeta;
    byte[] simpleFileBytes;
    Socket socket;
    PrintWriter writer;
    BufferedInputStream inputStream;

    @Before
    public void setUp() throws Exception {
        createDirectories();
        makeSimpleFile();
        localConnectToMyOwnPeerServer();
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

    private void makeSimpleFile() throws IOException, CreateP2PFileException {
        /* make a file in it for serving */
        simpleFile = new File(serveDir, "to-serve-1");
        BufferedOutputStream outputStream = new BufferedOutputStream(new FileOutputStream(simpleFile));

        /* give the file one chunk */
        simpleFileBytes = new byte[Common.NUM_CHUNK_BYTES];
        for (int i = 0; i < simpleFileBytes.length; i++)
            simpleFileBytes[i] = (byte) (i % (int) Byte.MAX_VALUE);
        outputStream.write(simpleFileBytes);
        outputStream.flush();

        /* tell State to serve the new file */
        ClientState.addLocalFile(simpleFile);
        simpleMeta = ClientState.getLocalFiles().get(0).getMetaP2PFile();
    }



    private void localConnectToMyOwnPeerServer() throws FailedToFindServerException, ServersIOException {
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

    @Test public void testLocalServeChunk() throws Exception {
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

    @Test public void testLocalRequestChunkOutOfBounds() throws Exception {
        final int OOB_CHUNK_INDEX = 1;
        requestChunk(simpleMeta, OOB_CHUNK_INDEX);
        int responseSize = Common.readIntLineFromStream(inputStream);
        assertEquals(PeerTalk.ToPeer.OUT_OF_BOUNDS, responseSize);
    }
}

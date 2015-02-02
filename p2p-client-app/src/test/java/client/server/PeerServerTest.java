package client.server;

import client.state.ClientState;
import org.junit.Test;
import p2p.file.meta.MetaP2PFile;
import p2p.protocol.fileTransfer.PeerTalk;
import util.Common;
import util.ServersCommon;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

public class PeerServerTest {

    @Test public void testLocalServeChunk() throws Exception {
        /* a ClientApp starts automatically (just by loading the classes) bc of all the static fields */
        final int CHUNK_INDEX = 1;

        /* create directories to serve from and download to */
        File serveDir = makeCleanDir("p2p-gui serve");
        File downloadDir = makeCleanDir("p2p-gui DL");
        ClientState.setUserDownloadDirectory(downloadDir);

        /* make a file in it for serving */
        File toServe = new File(serveDir, "to-serve-1");
        BufferedOutputStream outputStream = new BufferedOutputStream(new FileOutputStream(toServe));

        /* give the file one chunk */
        byte[] bytes = new byte[Common.NUM_CHUNK_BYTES];
        for (int i = 0; i < bytes.length; i++)
            bytes[i] = (byte) (i % (int) Byte.MAX_VALUE);
        outputStream.write(bytes);

        /* Start make it request that chunk FROM ITSELF.
         * This sounds strange but it makes sense as a way to go about testing it
         * because otherwise it might involve starting up another process?
         * Yeah because everything is STATIC
         * meaning SINGLETON meaning JUST ONE EVER on class load. */

        /* tell State to serve the new file */
        ClientState.addLocalFile(toServe);

        /* connect to the server */
        Socket socket = ServersCommon.connectLocallyToInetAddr(ClientState.getExternalSocketAddr());
        PrintWriter writer = ServersCommon.printWriter(socket);
        MetaP2PFile metaP2PFile = ClientState.getLocalFiles().get(0).getMetaP2PFile();
        BufferedInputStream inputStream = ServersCommon.buffIStream(socket);
        Scanner scanner = new Scanner(inputStream);

        /* request Chunk 1 of the file */

        writer.println(PeerTalk.ToPeer.GET_CHUNK);
        writer.println(metaP2PFile.serializeToString());
        writer.println(CHUNK_INDEX);
        writer.flush();

        int responseSize = Integer.parseInt(scanner.nextLine());
        assertEquals(bytes.length, responseSize);

        byte[] response = new byte[bytes.length];
        int bytesRcvd = 0;
        while (bytesRcvd < bytes.length) {
            int rcv = inputStream.read(response, bytesRcvd, bytes.length-bytesRcvd);
            if (bytesRcvd >= 0) {
                bytesRcvd += rcv;
            } else {
                throw new RuntimeException("it seems the whole file wasn't downloaded");
            }
        }

        assertArrayEquals(bytes, response);
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
}

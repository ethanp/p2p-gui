package client.server;

import client.state.ClientState;
import org.junit.Test;
import util.Common;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

public class PeerServerTest {

    @Test public void testServeChunk() throws Exception {
        /* a ClientApp starts automatically (just by loading the classes) bc of all the static fields */


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
        ClientState.addLocalFiles(toServe);

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

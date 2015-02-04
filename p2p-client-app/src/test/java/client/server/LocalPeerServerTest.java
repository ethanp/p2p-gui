package client.server;

import client.LocalDownloadTest;
import client.p2pFile.P2PFile;
import org.junit.Test;
import p2p.file.meta.MetaP2PFile;
import p2p.protocol.fileTransfer.PeerTalk;
import util.Common;

import java.io.File;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

public class LocalPeerServerTest extends LocalDownloadTest {

    @Test public void testServeChunk() throws Exception {
        final int FIRST_CHUNK_INDEX = 0;
        requestChunk(sequenceMeta, FIRST_CHUNK_INDEX);
        int responseSize = Common.readIntLineFromStream(inputStream);
        assertEquals(sequenceFileBytes.length, responseSize);

        byte[] response = new byte[responseSize];
        int bytesRcvd = 0;
        while (bytesRcvd < responseSize) {
            int rcv = inputStream.read(response, bytesRcvd, responseSize-bytesRcvd);
            if (rcv >= 0) {
                bytesRcvd += rcv;
            } else {
                throw new RuntimeException("it seems the whole file wasn't downloaded");
            }
        }
        assertArrayEquals(sequenceFileBytes, response);
    }

    @Test public void testRequestChunkOutOfBounds() throws Exception {
        final int OOB_CHUNK_INDEX = 1;
        requestChunk(sequenceMeta, OOB_CHUNK_INDEX);
        int responseSize = Common.readIntLineFromStream(inputStream);
        assertEquals(PeerTalk.FromPeer.OUT_OF_BOUNDS, responseSize);
    }

    @Test public void testRequestUnknownFile() throws Exception {
        MetaP2PFile unknownMeta = new MetaP2PFile("LaLaLand", 234234, "HEXADECIMAL_JOKE");
        requestChunk(unknownMeta, 1);
        int responseSize = Common.readIntLineFromStream(inputStream);
        assertEquals(PeerTalk.FromPeer.FILE_NOT_AVAILABLE, responseSize);
    }

    @Test public void testRequestNotOwnedChunk() throws Exception {
        File unknownFile = new File(serveDir, "partially-chunkless");
        fillWithRandomData(unknownFile, Common.NUM_CHUNK_BYTES*3);
        P2PFile pFile = serveFile(unknownFile);
        pFile.getAvailableChunks().setChunkAvailable(1, false);
        MetaP2PFile meta = pFile.getMetaPFile();
        requestChunk(meta, 1);
        int responseSize = Common.readIntLineFromStream(inputStream);
        assertEquals(PeerTalk.FromPeer.CHUNK_NOT_AVAILABLE, responseSize);
    }

    @Test public void testRequestSmallerThanNormalChunk() throws Exception {
        File weirdShapeFile = new File(serveDir, "small-last-chunk");
        fillWithRandomData(weirdShapeFile, Common.NUM_CHUNK_BYTES*3-200);
        P2PFile pFile = serveFile(weirdShapeFile);
        MetaP2PFile meta = pFile.getMetaPFile();
        requestChunk(meta, 2);
        int responseSize = Common.readIntLineFromStream(inputStream);
        assertEquals(Common.NUM_CHUNK_BYTES - 200, responseSize);
    }
}

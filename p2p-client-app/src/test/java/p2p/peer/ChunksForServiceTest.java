package p2p.peer;

import org.junit.Test;

import static org.junit.Assert.assertArrayEquals;

public class ChunksForServiceTest {
    @Test public void testSerializeTrivial() throws Exception {
        assertArrayEquals(new byte[]{0}, new ChunksForService(1).serializeToBytes());
    }

    @Test public void testSerializeTrivialLonger() throws Exception {
        assertArrayEquals(new byte[]{0,0}, new ChunksForService(12).serializeToBytes());
    }

    @Test public void testSerializeShort() throws Exception {
        ChunksForService chunksForService = new ChunksForService(1);
        chunksForService.setAllAsAvailable();
        assertArrayEquals(new byte[]{1}, chunksForService.serializeToBytes());
    }

    @Test public void testSerializeLonger() throws Exception {
        ChunksForService chunksForService = new ChunksForService(12);
        chunksForService.setAllAsAvailable();
        int val = (1 << 12) - 1;
        byte lessSignif = (byte) (val & 0xFF);
        byte moreSignif = (byte) ((val & (0xFF << 8)) >> 8);
        assertArrayEquals(new byte[]{lessSignif, moreSignif}, chunksForService.serializeToBytes());
    }

    @Test public void testSerializeTrickier() throws Exception {
        ChunksForService chunksForService = new ChunksForService(18);
        for (int i = 15; i <= 17; i++)
            chunksForService.setChunkAvailable(i, true);
        byte a = (byte) 0;
        byte b = (byte) 0x80;
        byte c = (byte) 3;
        assertArrayEquals(new byte[]{a, b, c}, chunksForService.serializeToBytes());
    }


}

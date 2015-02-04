package client.download;

import client.p2pFile.P2PFile;
import client.state.ClientState;
import org.junit.Test;

import java.net.InetSocketAddress;

public class LocalPeerDownloadTest {
    @Test public void testCreatePeerDownload() throws Exception {
        PeerDownload peerDownload = new PeerDownload(myAddress());
        P2PFile pFile = null;
        FileDownload fileDownload = new FileDownload(pFile);
        fileDownload.addPeerDownloader(peerDownload);
        peerDownload.addFileDownload(fileDownload);

    }

    private InetSocketAddress myAddress() {
        return ClientState.getExternalSocketAddr();
    }
}

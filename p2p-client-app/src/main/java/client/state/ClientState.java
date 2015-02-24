package client.state;

import Exceptions.ListenerCouldntConnectException;
import Exceptions.NoInternetConnectionException;
import Exceptions.ServersIOException;
import client.p2pFile.FileDownload;
import client.p2pFile.P2PFile;
import client.peer.Peer;
import client.tracker.RemoteTracker;
import client.tracker.ClientSwarm;
import p2p.exceptions.ConnectToTrackerException;
import p2p.exceptions.CreateP2PFileException;
import p2p.exceptions.FileUnavailableException;
import p2p.file.MetaP2P;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.file.FileAlreadyExistsException;
import java.util.Collection;

/**
 * Ethan Petuchowski 1/31/15
 */
public class ClientState {

    private TrackersManager trackersManager = new TrackersManager();
    private LocalFilesManager localFilesManager = new LocalFilesManager();
    private DownloadsManager downloadsManager = new DownloadsManager(this);
    private PeerServer peerServer;

    /***************
     * Peer Server *
     ***************/
    {
        /* start the server on creation */
        try {
            peerServer = new PeerServer();
            new Thread(peerServer).start();
        }
        catch (ListenerCouldntConnectException | NoInternetConnectionException | ServersIOException e) {
            e.printStackTrace();
        }
    }

    public String listenAddrStr() {
        return peerServer.listenAddrStr();
    }

    public InetSocketAddress externalListenAddr() {
        return peerServer.getExternalSocketAddr();
    }

    /***********************
     * Local Files Manager *
     ***********************/
    public P2PFile addLocalFile(File file) throws CreateP2PFileException, IOException {
        return localFilesManager.addLocalFile(file);
    }

    public P2PFile getLocalP2PFile(MetaP2P file) {
        return localFilesManager.getLocalP2PFile(file);
    }

    public void addLocalFiles(P2PFile... files) {
        localFilesManager.addLocalFiles(files);
    }

    public boolean hasLocalFile(MetaP2P mFile) {
        return localFilesManager.containsMeta(mFile);
    }

    public void addFakeContent() {
        localFilesManager.addFakeContent();
    }

    public File getDownloadsDir() {
        return localFilesManager.getDownloadsDir();
    }

    public void setDownloadsDir(File downloadsDir) {
        localFilesManager.setDownloadDirectory(downloadsDir);
    }

    /********************
     * Trackers Manager *
     ********************/
    public RemoteTracker addTracker(String addrStr) throws ServersIOException, ConnectToTrackerException, IOException {
        return trackersManager.addTracker(addrStr);
    }

    public Collection<ClientSwarm> listTracker(RemoteTracker tracker) throws IOException, ConnectToTrackerException, ServersIOException {
        return trackersManager.listTracker(tracker);
    }

    public Collection<Peer> collectPeersServing(MetaP2P mFile) {
        return trackersManager.collectPeersServing(mFile);
    }


    /*********************
     * Downloads Manager *
     *********************/
    public FileDownload downloadMeta(MetaP2P metaP2P) throws FileAlreadyExistsException, FileUnavailableException {
        return downloadsManager.downloadMeta(metaP2P);
    }

    public boolean connectToPeer(Peer peer) {
        return downloadsManager.connectToPeer(peer);
    }
}

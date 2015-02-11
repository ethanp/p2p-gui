package client.state;

import Exceptions.ListenerCouldntConnectException;
import Exceptions.NoInternetConnectionException;
import Exceptions.ServersIOException;
import client.managers.DownloadsManager;
import client.managers.LocalFilesManager;
import client.managers.TrackersManager;
import client.p2pFile.P2PFile;
import client.peer.Peer;
import client.server.PeerServer;
import client.tracker.RemoteTracker;
import client.tracker.swarm.ClientSwarm;
import p2p.exceptions.ConnectToTrackerException;
import p2p.exceptions.CreateP2PFileException;
import p2p.exceptions.FileUnavailableException;
import p2p.file.meta.MetaP2P;

import java.io.File;
import java.io.IOException;
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

    /*********************
     * Start Peer Server *
     *********************/
    {
        try {
            peerServer = new PeerServer();
            new Thread(peerServer).start();
        }
        catch (ListenerCouldntConnectException | NoInternetConnectionException | ServersIOException e) {
            e.printStackTrace();
        }
    }

    /***********************
     * Local Files Manager *
     ***********************/
    public void addLocalFile(File file) throws CreateP2PFileException, IOException {
        localFilesManager.addLocalFile(file);
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
    public void downloadMeta(MetaP2P metaP2P) throws FileAlreadyExistsException, FileUnavailableException {
        downloadsManager.downloadMeta(metaP2P);
    }

    public boolean isConnectedTo(Peer peer) {
        return downloadsManager.isConnectedTo(peer);
    }
}

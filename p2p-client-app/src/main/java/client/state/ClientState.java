package client.state;

import Exceptions.ListenerCouldntConnectException;
import Exceptions.NoInternetConnectionException;
import Exceptions.ServersIOException;
import client.managers.DownloadsManager;
import client.managers.LocalFilesManager;
import client.managers.PeersManager;
import client.managers.TrackersManager;
import client.p2pFile.LocalFakeFile;
import client.p2pFile.P2PFile;
import client.peer.Peer;
import client.server.PeerServer;
import client.tracker.RemoteTracker;
import client.tracker.swarm.ClientSwarm;
import p2p.exceptions.ConnectToTrackerException;
import p2p.exceptions.CreateP2PFileException;
import p2p.file.meta.MetaP2P;

import java.io.File;
import java.io.IOException;
import java.util.Collection;

/**
 * Ethan Petuchowski 1/31/15
 */
public class ClientState {

    private TrackersManager trackersManager = new TrackersManager();
    private LocalFilesManager localFilesManager = new LocalFilesManager();
    private PeersManager peersManager = new PeersManager();
    private DownloadsManager downloadsManager = new DownloadsManager(this);

    /* when a ClientState is created, start its PeerServer */
    private PeerServer peerServer;
    {
        try {
            peerServer = new PeerServer();
            new Thread(peerServer).start();
        }
        catch (ListenerCouldntConnectException | NoInternetConnectionException | ServersIOException e) {
            e.printStackTrace();
        }
    }

    public void addLocalFile(File file) throws CreateP2PFileException, IOException {
        localFilesManager.addLocalFile(P2PFile.importLocalFile(file));
    }

    public P2PFile getLocalP2PFile(File file) {
        for (P2PFile pFile : localFilesManager.getLocalFiles())
            if (pFile.getLocalFile().equals(file))
                return pFile;
        return null;
    }

    public P2PFile getLocalP2PFile(MetaP2P file) {
        for (P2PFile pFile : localFilesManager.getLocalFiles())
            if (pFile.getMetaPFile().equals(file))
                return pFile;
        return null;
    }

    public void addLocalFiles(File... files) throws CreateP2PFileException, IOException {
        for (File file:files)
            addLocalFile(file);
    }

    public void addLocalFiles(P2PFile... files) {
        localFilesManager.addLocalFiles(files);
    }

    public void addFakeContent() {
        try {
            LocalFakeFile pFile1 = LocalFakeFile.genFakeFile();
            LocalFakeFile pFile2 = LocalFakeFile.genFakeFile();
            LocalFakeFile pFile3 = LocalFakeFile.genFakeFile();
            addLocalFiles(pFile1, pFile2, pFile3);
        }
        catch (CreateP2PFileException e) {
            e.printStackTrace();
        }
//        knownTrackers.add(FakeRemoteTracker.getDefaultFakeRemoteTracker());
    }

    public Collection<ClientSwarm> addTrackerAndListSwarms(String addrStr) throws IOException, ServersIOException, ConnectToTrackerException {
        RemoteTracker tracker = trackersManager.addTracker(addrStr);
        return listTracker(tracker);
    }

    public Collection<ClientSwarm> listTracker(RemoteTracker tracker) throws IOException, ConnectToTrackerException, ServersIOException {
        return trackersManager.listTracker(tracker);
    }

    public boolean hasLocalFile(MetaP2P mFile) {
        return localFilesManager.containsMeta(mFile);
    }

    public Collection<Peer> collectPeersFor(MetaP2P mFile) {
        return trackersManager.collectPeersFor(mFile);
    }

    public File getDownloadsDir() {
        return localFilesManager.getDownloadsDir();
    }

    public boolean isConnectedTo(Peer peer) {
        return peersManager.isConnectedTo(peer);
    }
}

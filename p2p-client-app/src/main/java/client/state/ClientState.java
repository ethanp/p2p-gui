package client.state;

import Exceptions.ListenerCouldntConnectException;
import Exceptions.NoInternetConnectionException;
import Exceptions.ServersIOException;
import client.managers.FilesManager;
import client.managers.TrackersManager;
import client.p2pFile.LocalFakeFile;
import client.p2pFile.P2PFile;
import client.server.PeerServer;
import client.tracker.RemoteTracker;
import p2p.exceptions.ConnectToTrackerException;
import p2p.exceptions.CreateP2PFileException;
import p2p.file.meta.MetaP2P;

import java.io.File;
import java.io.IOException;

/**
 * Ethan Petuchowski 1/31/15
 */
public class ClientState {

    private PeerServer peerServer;
    private TrackersManager trackersManager = new TrackersManager();
    private FilesManager filesManager = new FilesManager();

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
        filesManager.addLocalFile(P2PFile.importLocalFile(file));
    }

    public P2PFile getLocalP2PFile(File file) {
        for (P2PFile pFile : filesManager.getLocalFiles())
            if (pFile.getLocalFile().equals(file))
                return pFile;
        return null;
    }

    public P2PFile getLocalP2PFile(MetaP2P mFile) {
        for (P2PFile pFile : filesManager.getLocalFiles())
            if (pFile.getMetaPFile().equals(mFile))
                return pFile;
        return null;
    }

    public void addLocalFiles(File... files) throws CreateP2PFileException, IOException {
        for (File file:files)
            addLocalFile(file);
    }

    public void addLocalFiles(P2PFile... files) {
        filesManager.addLocalFiles(files);
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

    public RemoteTracker addTrackerByAddrStr(String addrStr) throws IOException, ServersIOException, ConnectToTrackerException {
        return trackersManager.addTrackerByAddrStr(addrStr);
    }

    public String listTracker(RemoteTracker tracker) throws IOException, ConnectToTrackerException, ServersIOException {
        return trackersManager.listTracker(tracker);
    }
}

package client.state;

import Exceptions.ListenerCouldntConnectException;
import Exceptions.NoInternetConnectionException;
import Exceptions.ServersIOException;
import client.managers.TrackersManager;
import client.p2pFile.LocalFakeFile;
import client.p2pFile.P2PFile;
import client.server.PeerServer;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import p2p.exceptions.CreateP2PFileException;
import p2p.file.meta.MetaP2P;
import util.Common;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Ethan Petuchowski 1/31/15
 */
public class ClientState {

    // singletons...is that a "BAD THING"?
    private PeerServer peerServer;
    private ObjectProperty<File> userDownloadDirectory
            = new SimpleObjectProperty<>(new File("/Users/Ethan/Desktop/P2PDownloadDir"));
    private TrackersManager trackersManager = new TrackersManager();
    private ObservableList<P2PFile> localFiles = FXCollections.observableArrayList();
    private ExecutorService fileDownloadPool
            = Executors.newFixedThreadPool(Common.FILE_DOWNLOADS_POOL_SIZE);

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
        localFiles.add(P2PFile.importLocalFile(file));
    }

    public P2PFile getLocalP2PFile(File file) {
        for (P2PFile pFile : localFiles)
            if (pFile.getLocalFile().equals(file))
                return pFile;
        return null;
    }

    public P2PFile getLocalP2PFile(MetaP2P mFile) {
        for (P2PFile pFile : localFiles)
            if (pFile.getMetaPFile().equals(mFile))
                return pFile;
        return null;
    }

    public void addLocalFiles(File... files) throws CreateP2PFileException, IOException {
        for (File file:files) addLocalFile(file);
    }

    public void addLocalFiles(P2PFile... files) {
        localFiles.addAll(files);
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

    /* getters & setters */
    public PeerServer getPeerServer() { return peerServer; }
    public InetSocketAddress getExternalSocketAddr() { return peerServer.getExternalSocketAddr(); }
    public void setUserDownloadDirectory(File file) { userDownloadDirectory.setValue(file); }
    public File getUserDownloadDirectory() { return userDownloadDirectory.get(); }
    public ObjectProperty<File> userDownloadDirectoryProperty() { return userDownloadDirectory; }
    public ObservableList<P2PFile> getLocalFiles() { return localFiles; }


    public ClientState init() {
        return this;
    }

    public void addTrackerByAddrStr(String addrStr) throws UnknownHostException {
        trackersManager.addTrackerByAddrStr(addrStr);
    }
}

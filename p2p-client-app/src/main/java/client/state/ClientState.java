package client.state;

import Exceptions.ListenerCouldntConnectException;
import Exceptions.NoInternetConnectionException;
import Exceptions.ServersIOException;
import client.p2pFile.LocalFakeFile;
import client.p2pFile.P2PFile;
import client.server.PeerServer;
import client.tracker.FakeRemoteTracker;
import client.tracker.RemoteTracker;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import p2p.exceptions.CreateP2PFileException;
import util.Common;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Ethan Petuchowski 1/31/15
 */
public class ClientState {

    // singletons...is that a "BAD THING"?
    private static PeerServer peerServer;
    private static ObjectProperty<File> userDownloadDirectory
            = new SimpleObjectProperty<>(new File("/Users/Ethan/Desktop/P2PDownloadDir"));
    private static ObservableList<RemoteTracker> knownTrackers = FXCollections.observableArrayList();
    private static ObservableList<P2PFile> localFiles = FXCollections.observableArrayList();
    private static ExecutorService fileDownloadPool
            = Executors.newFixedThreadPool(Common.FILE_DOWNLOADS_POOL_SIZE);

    static {
        try {
            peerServer = new PeerServer();
            new Thread(peerServer).start();
        }
        catch (ListenerCouldntConnectException | NoInternetConnectionException | ServersIOException e) {
            e.printStackTrace();
        }
    }

    public static void addLocalFile(File file) throws CreateP2PFileException, IOException {
        localFiles.add(P2PFile.importLocalFile(file));
    }

    public static void addLocalFiles(File... files) throws CreateP2PFileException, IOException {
        for (File file:files) addLocalFile(file);
    }

    public static void addLocalFiles(P2PFile... files) {
        localFiles.addAll(files);
    }

    public static void addFakeContent() {
        try {
            LocalFakeFile pFile1 = LocalFakeFile.genFakeFile();
            LocalFakeFile pFile2 = LocalFakeFile.genFakeFile();
            LocalFakeFile pFile3 = LocalFakeFile.genFakeFile();
            addLocalFiles(pFile1, pFile2, pFile3);
        }
        catch (CreateP2PFileException e) {
            e.printStackTrace();
        }
        knownTrackers.add(FakeRemoteTracker.getDefaultFakeRemoteTracker());
    }

    /* getters & setters */
    public static PeerServer getPeerServer() { return peerServer; }
    public static InetSocketAddress getExternalSocketAddr() { return peerServer.getExternalSocketAddr(); }
    public static void setUserDownloadDirectory(File file) { userDownloadDirectory.setValue(file); }
    public static File getUserDownloadDirectory() { return userDownloadDirectory.get(); }
    public static ObjectProperty<File> userDownloadDirectoryProperty() { return userDownloadDirectory; }
    public static ObservableList<RemoteTracker> getKnownTrackers() { return knownTrackers; }
    public static ObservableList<P2PFile> getLocalFiles() { return localFiles; }


}

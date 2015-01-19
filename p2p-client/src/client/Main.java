package client;

import client.view.TheWindowCtrl;
import client.view.panes.files.LocalFilesPaneCtrl;
import client.view.panes.trackers.TrackersPaneCtrl;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import p2p.exceptions.CreateP2PFileException;
import p2p.file.p2pFile.LocalFakeFile;
import p2p.file.p2pFile.P2PFile;
import p2p.peer.PeerServer;
import p2p.tracker.AbstractRemoteTracker;
import p2p.tracker.FakeRemoteTracker;

import java.io.IOException;
import java.net.URL;

public class Main extends Application {
    public Main() { /* I could put stuff in here, but at this point there is no need */ }
    public static void main(String[] args) { launch(args); }

    public static Stage getPrimaryStage() { return primaryStage; }
    public static PeerServer getPeerServer() { return peerServer; }
    public static ObservableList<AbstractRemoteTracker> getKnownTrackers() { return knownTrackers; }
    public static ObservableList<P2PFile> getLocalFiles() { return localFiles; }

    private static Stage primaryStage;
    private BorderPane rootLayout;
    private static PeerServer peerServer = new PeerServer();

    private static ObservableList<AbstractRemoteTracker> knownTrackers = FXCollections.observableArrayList();
    private static ObservableList<P2PFile> localFiles = FXCollections.observableArrayList();

    @Override public void start(Stage primaryStage) throws Exception {
        Main.primaryStage = primaryStage;
        Main.primaryStage.setTitle("p2p-gui");
        loadTheWindow();
        addFakeContent();
    }

    private void addFakeContent() {
        try {
            LocalFakeFile pFile1 = LocalFakeFile.genFakeFile();
            LocalFakeFile pFile2 = LocalFakeFile.genFakeFile();
            LocalFakeFile pFile3 = LocalFakeFile.genFakeFile();
            Main.localFiles.addAll(pFile1, pFile2, pFile3);
        }
        catch (CreateP2PFileException e) {
            e.printStackTrace();
        }
        Main.knownTrackers.add(FakeRemoteTracker.getDefaultFakeRemoteTracker());
    }

    private void loadTheWindow() {
        try {
            /* load Base Window */
            URL fxmlLoc = Main.class.getResource("view/TheWindow.fxml");
            FXMLLoader rootLoader = new FXMLLoader();
            rootLoader.setLocation(fxmlLoc);
            rootLayout = rootLoader.load();
            TheWindowCtrl windowCtrl = rootLoader.getController();

            /* load Trackers Pane */
            URL trackersLoc = Main.class.getResource("view/panes/trackers/TrackersPane.fxml");
            FXMLLoader trackersLoader = new FXMLLoader();
            trackersLoader.setLocation(trackersLoc);
            TitledPane trackersRoot = trackersLoader.load();
            TrackersPaneCtrl trackersCtrl = trackersLoader.getController();

            /* load LocalFiles Pane */
            URL localLoc = Main.class.getResource("view/panes/files/LocalFilesPane.fxml");
            FXMLLoader localLoader = new FXMLLoader();
            localLoader.setLocation(localLoc);
            TitledPane localViewRoot = localLoader.load();
            LocalFilesPaneCtrl localCtrl = localLoader.getController();

            /* add Panes to main Window and make them fit properly */
            windowCtrl.trackersAnchor.getChildren().add(trackersRoot);
            trackersRoot.prefWidthProperty().bind(windowCtrl.trackersAnchor.widthProperty());
            trackersRoot.prefHeightProperty().bind(windowCtrl.trackersAnchor.heightProperty());

            windowCtrl.localAnchor.getChildren().add(localViewRoot);
            localViewRoot.prefWidthProperty().bind(windowCtrl.localAnchor.widthProperty());
            localViewRoot.prefHeightProperty().bind(windowCtrl.localAnchor.heightProperty());

            Scene scene = new Scene(rootLayout);
            primaryStage.setScene(scene);
            primaryStage.setTitle("p2p-gui");
            primaryStage.show();
        }
        catch (IOException e) { e.printStackTrace(); }
    }
}

package client;

import client.download.FileDownload;
import client.state.ClientState;
import client.view.TheWindowCtrl;
import client.view.panes.files.LocalFilesPaneCtrl;
import client.view.panes.trackers.TrackersPaneCtrl;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Region;
import javafx.stage.Stage;
import util.Common;

import java.io.IOException;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main extends Application {
    public Main() { /* I could put stuff in here, but at this point there is no need */ }
    public static void main(String[] args) { launch(args); }

    public static Stage getPrimaryStage() { return primaryStage; }


    /* TODO add a GUI option to change the download directory */
    private static Stage primaryStage;
    private BorderPane rootLayout;

    @Override public void start(Stage primaryStage) throws Exception {
        Main.primaryStage = primaryStage;
        Main.primaryStage.setTitle("p2p-gui");
        loadTheWindow();
        ClientState.addFakeContent();
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
            addPaneToWindow(windowCtrl.trackersAnchor, trackersRoot);
            addPaneToWindow(windowCtrl.localAnchor, localViewRoot);

            Scene scene = new Scene(rootLayout);
            primaryStage.setScene(scene);
            primaryStage.setTitle("p2p-gui");
            primaryStage.show();
        }
        catch (IOException e) { e.printStackTrace(); }
    }

    private static void addPaneToWindow(AnchorPane windowLocation, Region innerRegion) {
        windowLocation.getChildren().add(innerRegion);
        innerRegion.prefWidthProperty().bind(windowLocation.widthProperty());
        innerRegion.prefHeightProperty().bind(windowLocation.heightProperty());
    }
}

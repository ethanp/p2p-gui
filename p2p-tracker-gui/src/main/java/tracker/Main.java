package tracker;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import tracker.view.TrackerViewCtrl;

import java.net.URL;

/**
 * Ethan Petuchowski 1/14/15
 */
public class Main extends Application {
    public Main() { /* I could put stuff in here, but at this point there is no need */ }
    public static void main(String[] args) { launch(args); }
    public static Stage getPrimaryStage() { return primaryStage; }
    private static Stage primaryStage;
    private VBox rootLayout;
    private static TrackerServer server;
    public static TrackerServer getServer() { return server; }
    public static TrackerState getTracker() { return getServer().getState(); }

    @Override public void start(Stage primaryStage) throws Exception {

        /* load GUI */
        URL fxmlLoc = Main.class.getResource("view/TrackerView.fxml");
        FXMLLoader rootLoader = new FXMLLoader();
        rootLoader.setLocation(fxmlLoc);
        rootLayout = rootLoader.load();
        TrackerViewCtrl ctrl = rootLoader.getController();
        Scene scene = new Scene(rootLayout);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Tracker Host");
        primaryStage.show();

        /* initialize Server */
        server = new TrackerServer(3000, 3500);
        new Thread(server).start();
        ctrl.setNetLocLabel(server.listenAddrStr());

        ctrl.initializeBasedOnServer();
    }
}

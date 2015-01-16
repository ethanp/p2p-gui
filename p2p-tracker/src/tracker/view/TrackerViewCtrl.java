package tracker.view;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import p2p.tracker.swarm.LocalSwarm;
import p2p.tracker.swarm.Swarm;
import tracker.Main;
import tracker.server.Server;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Ethan Petuchowski 1/14/15
 */
public class TrackerViewCtrl {
    @FXML private ListView<Swarm> pFileList;
    @FXML private ListView<Swarm> seederList;
    @FXML private ListView<Swarm> leecherList;
    private List<ListView<Swarm>> lists = new ArrayList<>();

    @FXML private Label reqCtLabel;
    @FXML private Label netLocLabel;

    @FXML private MenuItem addFakeSwarm;

    public void setNetLocLabel(String location) {
        netLocLabel.setText("Location: "+location);
    }

    private void setReqCtLabel(int n) {
        reqCtLabel.setText(String.valueOf(n));
    }

    @FXML private void initialize() {
        lists.addAll(Arrays.asList(pFileList, seederList, leecherList));
        Server.rcvReqCtProperty().addListener(
                (obsVal, oldVal, newVal) -> setReqCtLabel(newVal.intValue()));
        addFakeSwarm.setOnAction(
                e -> Main.getTracker().getSwarms().add(LocalSwarm.loadedSwarm(Main.getTracker())));

        /* TODO make the pFileList listen to the Tracker's List<Swarm> */
    }
}

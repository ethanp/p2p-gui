package tracker.view;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import p2p.tracker.swarm.Swarm;

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

    public void setNetLocLabel(String location) {
        netLocLabel.setText("Location: "+location);
    }

    @FXML private void initialize() {
        lists.addAll(Arrays.asList(pFileList, seederList, leecherList));
    }
}

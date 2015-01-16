package tracker.view;

import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.util.Callback;
import p2p.tracker.swarm.LocalSwarm;
import tracker.Main;
import tracker.server.Server;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Ethan Petuchowski 1/14/15
 */
public class TrackerViewCtrl {
    @FXML private ListView<LocalSwarm> pFileList;
    @FXML private ListView<LocalSwarm> seederList;
    @FXML private ListView<LocalSwarm> leecherList;
    private List<ListView<LocalSwarm>> lists = new ArrayList<>();

    @FXML private Label reqCtLabel;
    @FXML private Label netLocLabel;

    @FXML private MenuItem addFakeSwarm;

    public void setNetLocLabel(String location) {
        netLocLabel.setText("Location: "+location);
    }

    private void setReqCtLabel(int n) {
        reqCtLabel.setText(String.valueOf(n));
    }

    public void listenToSwarmChanges() {
        Main.getTracker().getSwarms().addListener(swarmChgListener);
    }

    private final ListChangeListener<LocalSwarm> swarmChgListener
    = new ListChangeListener<LocalSwarm>() {
        @Override public void onChanged(Change<? extends LocalSwarm> c) {
            while (c.next()) {
                if (c.wasAdded()) {
                    for (LocalSwarm addedSwarm : c.getAddedSubList()) {
                        pFileList.getItems().add(addedSwarm);
                    }
                }
                if (c.wasRemoved()) {
                    /* this is a terrible algorithm but it just doesn't matter */
                    for (LocalSwarm removedSwarm : c.getRemoved()) {
                        LocalSwarm toRemove = null;
                        for (LocalSwarm localItem : pFileList.getItems()) {
                            if (localItem.equals(removedSwarm)) {
                                toRemove = localItem;
                                break;
                            }
                        }
                        if (toRemove != null) {
                            pFileList.getItems().remove(toRemove);
                        }
                    }
                }
            }
        }
    };

    private final Callback<ListView<LocalSwarm>, ListCell<LocalSwarm>> swarmFileNameFactory
    = new Callback<ListView<LocalSwarm>, ListCell<LocalSwarm>>() {
        @Override public ListCell<LocalSwarm> call(ListView<LocalSwarm> param) {
            return new ListCell<LocalSwarm>() {
                @Override public void updateItem(LocalSwarm item, boolean empty) {
                    super.updateItem(item, empty);
                    if (item == null) {
                        setText(null);
                        setGraphic(null);
                    }
                    else {
                        setText(item.getP2pFile().getFilename());
                    }
                }
            };
        }
    };


    @FXML private void initialize() {
        lists.addAll(Arrays.asList(pFileList, seederList, leecherList));

        /* update the displayed "received request count" in the lower-left of the screen */
        Server.rcvReqCtProperty().addListener(
                (obsVal, oldVal, newVal) -> setReqCtLabel(newVal.intValue()));

        /* make the "Add fake swarm" menu item add fake swarms to the listing */
        addFakeSwarm.setOnAction(
                e -> Main.getTracker().getSwarms().add(
                        LocalSwarm.createLoadedSwarm(Main.getTracker())));

        /* make file list display the filename of tracked swarms */
        pFileList.setCellFactory(swarmFileNameFactory);
    }
}

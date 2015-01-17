package tracker.view;

import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import p2p.peer.ClientPeer;
import p2p.peer.Peer;
import p2p.tracker.swarm.LocalSwarm;
import tracker.Main;

/**
 * Ethan Petuchowski 1/14/15
 */
public class TrackerViewCtrl {
    @FXML private ListView<LocalSwarm> pFileList;
    @FXML private ListView<Peer> seederList;
    @FXML private ListView<Peer> leecherList;

    @FXML private Label reqCtLabel;
    @FXML private Label netLocLabel;

    @FXML private MenuItem addFakeSwarm;
    @FXML private MenuItem realFileAddFromEphemeralPeer;

    public void setNetLocLabel(String location) {
        netLocLabel.setText("Location: "+location);
    }

    private void setReqCtLabel(int n) {
        reqCtLabel.setText(String.valueOf(n));
    }

    public void initializeBasedOnServer() {
        Main.getTracker().getSwarms().addListener(swarmChgListener);

        /* update the displayed "received request count"
         * in the lower-left of the screen
         * TODO this is not working */
        Main.getServer().rcvReqCtProperty().addListener(
                (obsVal, oldCt, newCt) -> setReqCtLabel(newCt.intValue()));

        /* make the "Add fake swarm" menu item add fake swarms to the listing */
        addFakeSwarm.setOnAction(
                e -> Main.getTracker().getSwarms().add(
                        LocalSwarm.createLoadedSwarm(Main.getTracker())));
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

    /**
     * Don't initialize anything relying on the server in here
     * because at the time this is called the server hasn't started yet.
     * Use initializeBasedOnServer() instead.
     */
    @FXML private void initialize() {
        realFileAddFromEphemeralPeer.setOnAction(
                e -> ClientPeer.sendEphemeralRequest(
                        Main.getTracker().asRemote()));

        /* make file list display the filename of tracked swarms */
        pFileList.setCellFactory(p -> new SwarmNameCell());
        seederList.setCellFactory(p -> new PeerCell());
        leecherList.setCellFactory(p -> new PeerCell());

        /* make other columns show information about selected swarm */
        pFileList.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldFile, selectedFile) ->
                        fillOtherColumnsBasedOn(selectedFile));
    }

    private void fillOtherColumnsBasedOn(LocalSwarm swarm) {
        seederList.getItems().clear();
        seederList.getItems().addAll(swarm.getSeeders());
        leecherList.getItems().clear();
        leecherList.getItems().addAll(swarm.getLeechers());
    }

    static class SwarmNameCell extends ListCell<LocalSwarm> {
        @Override public void updateItem(LocalSwarm item, boolean empty) {
            super.updateItem(item, empty);
            if (item == null) {
                setText(null);
                setGraphic(null);
            } else {
                setText(item.getP2pFile().getFilename());
            }
        }
    }

    static class PeerCell extends ListCell<Peer> {
        @Override public void updateItem(Peer item, boolean empty) {
            super.updateItem(item, empty);
            if (item == null) {
                setText(null);
                setGraphic(null);
            } else {
                setText(item.toString());
            }
        }
    }
}

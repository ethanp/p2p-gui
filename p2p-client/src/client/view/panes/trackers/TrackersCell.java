package client.view.panes.trackers;

import client.Main;
import client.util.ClientStateUtiil;
import client.util.ViewUtil;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.TreeTableCell;
import org.controlsfx.dialog.Dialogs;
import p2p.tracker.RealRemoteTracker;
import util.Common;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Optional;

/**
 * Ethan Petuchowski 1/14/15
 */
public class TrackersCell extends TreeTableCell<Celery, Celery> {
    static enum Cols { NAME, SIZE, NUM_SEEDERS, NUM_LEECHERS }

    private Cols col;
    private ContextMenu menu = new ContextMenu();

    private String myTxt() {
        switch (col) {
            case NAME:          return getItem().getName();
            case SIZE:          return getItem().getSize();
            case NUM_SEEDERS:   return getItem().getNumSeeders();
            case NUM_LEECHERS:  return getItem().getNumLeechers();
        }
        throw new RuntimeException("unreachable");
    }

    public TrackersCell(Cols c) {
        super();
        col = c;
    }

    @Override protected void updateItem(Celery item, boolean empty) {
        super.updateItem(item, empty);
        if (empty || item == null) {
            setText(null);
            setGraphic(null);
            return;
        }
        setText(myTxt());
        menu = new ContextMenu();

        if (getItem().isRoot()) {
            addOpt("Add fake tracker", e -> ClientStateUtiil.addFakeTracker());
            addOpt("Add real tracker", e -> addRealTrackerDialog());
        }
        if (getItem().isTracker()) {
            addOpt("Add fake tracker", e -> ClientStateUtiil.addFakeTracker());
            addOpt("Remove tracker", e -> Main.getKnownTrackers().remove(getItem().getTracker()));
            addOpt("Refresh swarms", e -> {
                try {
                    getItem().getTracker().listFiles();
                }
                catch (IOException ex) {
                    catchTrackerConnectionIssue(ex);
                }
            });
        }
        if (getItem().isSwarm()) {
            addOpt("Download file", e -> {});
            addOpt("Update (real or fake) swarm", e -> {
                try {
                    getItem().updateThisSwarm();
                }
                catch (IOException ex) {
                    catchTrackerConnectionIssue(ex);
                }
                refresh();
            });
        }

        ViewUtil.showOnRightClick(this, menu);
    }

    private void addRealTrackerDialog() {
        Optional<String> response =
                Dialogs.create()
                       .owner(Main.getPrimaryStage())
                       .title("Add tracker")
                       .masthead("The given tracker must be currently running")
                       .message("Please enter the tracker's IP address and port")
                       .showTextInput("123.123.123.123:1234");

        response.ifPresent(addr -> {
            try {
                InetSocketAddress isa = Common.stringToIPPort(addr);
                final RealRemoteTracker newTracker = new RealRemoteTracker(isa);
                Main.getKnownTrackers().add(newTracker);
                newTracker.listFiles();
            } catch (IOException e) {
                catchTrackerConnectionIssue(e);
            }
        });
    }

    private void catchTrackerConnectionIssue(IOException e) {
        System.err.println(e.getMessage());
        e.printStackTrace();
        showTrackerNotFoundDialog();
    }

    private void addOpt(String menuItem, EventHandler<ActionEvent> action) {
        ViewUtil.addOpt(menu, menuItem, action);
    }

    private void showTrackerNotFoundDialog() {
        Dialogs.create()
               .owner(Main.getPrimaryStage())
               .title("Tracker not found")
               .masthead("Unable to connect to the tracker")
               .message("Make sure the tracker is currently running")
               .showWarning();
    }

    /**
     * The goal is to refresh this table. It seems like the only simple way to do it in my situation
     * is via hacks. Something that is being observed needs to be invalidated so that the framework
     * refreshes everything properly. Another possible hack is to flip the Celery's `observeMe`
     * property. This one was the most-up-voted (by far) among many possibilities listed on
     * stackoverflow.
     *
     *          http://stackoverflow.com/a/11624805/1959155
     */
    private void refresh() {
        getTableColumn().setVisible(false);
        getTableColumn().setVisible(true);
    }
}

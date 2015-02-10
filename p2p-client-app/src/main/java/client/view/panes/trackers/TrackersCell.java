package client.view.panes.trackers;

import Exceptions.ServersIOException;
import client.Main;
import client.tracker.RemoteTracker;
import client.util.ClientStateUtil;
import client.util.ViewUtil;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.TreeTableCell;
import org.controlsfx.dialog.Dialogs;
import p2p.exceptions.ConnectToTrackerException;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;
import util.ServersCommon;

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
            addOpt("Add fake tracker", e -> ClientStateUtil.addFakeTracker());
            addOpt("Add real tracker", e -> addRealTrackerDialog());
        }
        if (getItem().isTracker()) {
            addOpt("Add fake tracker", e -> ClientStateUtil.addFakeTracker());
//            addOpt("Remove tracker", e -> ClientState.getKnownTrackers().remove(getItem().getTracker()));
            addOpt("Refresh swarms", e -> {
                try {
                    getItem().getTracker().listFiles();
                }
                catch (ServersIOException | IOException | ConnectToTrackerException ex) {
                    catchTrackerConnectionIssue(ex);
                }
            });
        }
        if (getItem().isSwarm()) {
            addOpt("Download file", e -> downloadFile());
            addOpt("Update (real or fake) swarm", e -> {
                try {
                    getItem().updateThisSwarm();
                }
                catch (ServersIOException | IOException | ConnectToTrackerException ex) {
                    catchTrackerConnectionIssue(ex);
                }
                refresh();
            });
        }

        ViewUtil.showOnRightClick(this, menu);
    }

    private void downloadFile() {
        /* I'm not sure how I'm going to actually implement the file download process yet */
//        Main.startFileDownload(
//                new FileDownload(
//                        ClientState.getUserDownloadDirectory(),
//                        getItem().getSwarm(),
//                        ClientState.getLocalFiles()
//                )
//        );
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
                InetSocketAddress isa = ServersCommon.stringToIPPort(addr);
                RemoteTracker newTracker;
                try {
                    newTracker = new RemoteTracker(isa);
//                    ClientState.getKnownTrackers().add(newTracker);
//                    newTracker.listSwarms();
                }
                catch (ServersIOException e) {
                    e.printStackTrace();
                    throw new NotImplementedException();
                }
            } catch (IOException | ConnectToTrackerException e) {
                catchTrackerConnectionIssue(e);
            }
        });
    }

    private void catchTrackerConnectionIssue(Exception e) {
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

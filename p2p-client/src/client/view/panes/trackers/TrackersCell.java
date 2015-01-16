package client.view.panes.trackers;

import client.Main;
import client.util.ClientStateUtiil;
import client.util.ViewUtil;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.TreeTableCell;

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
        }
        if (getItem().isTracker()) {
            addOpt("Add fake tracker", e -> ClientStateUtiil.addFakeTracker());
            addOpt("Remove tracker", e -> Main.knownTrackers.remove(getItem().getTracker()));
        }
        if (getItem().isSwarm()) {
            addOpt("Download file", e -> {});
            addOpt("Update swarm", e ->{ getItem().updateThisSwarm(); refresh(); });
        }

        ViewUtil.showOnRightClick(this, menu);
    }

    private void addOpt(String menuItem, EventHandler<ActionEvent> action) {
        ViewUtil.addOpt(menu, menuItem, action);
    }

    /**
     * The goal is to refresh this table. It seems like the only simple way to do it in my situation
     * is hacks. Something that is being observed needs to be invalidated so that the framework
     * refreshes * everything properly. Another possible hack is to flip the Celery's `observeMe`
     * property. * This one was the most-up-voted (by far) among many possibilities listed on
     * stackoverflow.
     *
     *          http://stackoverflow.com/a/11624805/1959155
     */
    private void refresh() {
        getTableColumn().setVisible(false);
        getTableColumn().setVisible(true);
    }
}

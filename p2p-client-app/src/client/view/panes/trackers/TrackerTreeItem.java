package client.view.panes.trackers;

import client.Main;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.control.TreeItem;
import p2p.tracker.AbstractRemoteTracker;
import p2p.tracker.swarm.ClientSwarm;

/**
 * Ethan Petuchowski 1/12/15
 */
public class TrackerTreeItem extends TreeItem<Celery> {

    private boolean childrenKnown = false;

    public TrackerTreeItem(Celery value) {
        super(value);
    }

    @Override public boolean isLeaf() { return getValue().isSwarm(); }

    private final ListChangeListener<AbstractRemoteTracker> trackersListener = c -> {
        while (c.next()) {
            if (c.wasRemoved()) {
                for (AbstractRemoteTracker f : c.getRemoved()) {
                    TreeItem<Celery> r = null;
                    for (TreeItem<Celery> m : getChildren()) {
                        if (m.getValue().equalsTracker(f)) {
                            r = m;
                            break;
                        }
                    }
                    if (r != null) {
                        getChildren().remove(r);
                    }
                }
            }
            if (c.wasAdded()) {
                for (AbstractRemoteTracker f : c.getAddedSubList()) {
                    getChildren().add(new TrackerTreeItem(new Celery(f)));
                }
            }
        }
    };

    private final ListChangeListener<ClientSwarm> swarmsListener = c -> {
        while (c.next()) {
            if (c.wasRemoved()) {
                for (ClientSwarm s : c.getRemoved()) {
                    TreeItem<Celery> r = null;
                    for (TreeItem<Celery> m : getChildren()) {
                        if (m.getValue().equalsSwarm(s)) {
                            r = m;
                            break;
                        }
                    }
                    if (r != null) {
                        getChildren().remove(r);
                    }
                }
            }
            if (c.wasAdded()) {
                for (ClientSwarm s : c.getAddedSubList()) {
                    getChildren().add(new TrackerTreeItem(new Celery(s)));
                }
            }
        }
    };

    @Override public ObservableList<TreeItem<Celery>> getChildren() {
        if (!childrenKnown) {
            childrenKnown = true;
            if (getValue().isTracker()) {
                getValue().getTracker().getSwarms().addListener(swarmsListener);
                for (ClientSwarm swarm : getValue().getTracker().getSwarms())
                    super.getChildren().add(new TrackerTreeItem(new Celery(swarm)));
            }
            else if (getValue().isRoot()) {
                Main.getKnownTrackers().addListener(trackersListener);
                for (AbstractRemoteTracker tracker : Main.getKnownTrackers())
                    super.getChildren().add(new TrackerTreeItem(new Celery(tracker)));
            }
        }
        return super.getChildren();
    }
}

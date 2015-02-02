package client.view.panes.trackers;

import client.state.ClientState;
import client.tracker.RemoteTracker;
import client.tracker.swarm.ClientSwarm;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.control.TreeItem;

/**
 * Ethan Petuchowski 1/12/15
 */
public class TrackerTreeItem extends TreeItem<Celery> {

    private boolean childrenKnown = false;

    public TrackerTreeItem(Celery value) {
        super(value);
    }

    @Override public boolean isLeaf() { return getValue().isSwarm(); }

    private final ListChangeListener<RemoteTracker> trackersListener = c -> {
        while (c.next()) {
            if (c.wasRemoved()) {
                for (RemoteTracker f : c.getRemoved()) {
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
                for (RemoteTracker f : c.getAddedSubList()) {
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
                ClientState.getKnownTrackers().addListener(trackersListener);
                for (RemoteTracker tracker : ClientState.getKnownTrackers())
                    super.getChildren().add(new TrackerTreeItem(new Celery(tracker)));
            }
        }
        return super.getChildren();
    }
}

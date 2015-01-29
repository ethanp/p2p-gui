package tracker;

import Exceptions.ListenerCouldntConnectException;
import Exceptions.NotConnectedException;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

/**
 * Ethan Petuchowski 1/29/15
 */
public class RunnableTracker implements Runnable {
    ObjectProperty<TrackerServer> trackerServer;

    public RunnableTracker() throws ListenerCouldntConnectException, NotConnectedException {
        trackerServer = new SimpleObjectProperty<>(new TrackerServer());
    }

    @Override public void run() {
        new Thread(trackerServer.getValue()).start();

    }
}

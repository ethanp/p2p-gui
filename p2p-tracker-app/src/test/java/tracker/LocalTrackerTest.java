package tracker;

import Exceptions.ListenerCouldntConnectException;
import Exceptions.NotConnectedException;
import org.junit.Before;
import org.junit.Test;
import util.ServersCommon;

public class LocalTrackerTest {

    LocalTracker localTracker;
    @Before public void setUp() throws ListenerCouldntConnectException, NotConnectedException {
        localTracker = LocalTracker.create();
    }

    @Test
    public void testCreateConnects() {
        System.out.println("Listening at: "+
                           ServersCommon.ipPortToString(
                                   localTracker.getExternalSocketAddr()));
    }
}

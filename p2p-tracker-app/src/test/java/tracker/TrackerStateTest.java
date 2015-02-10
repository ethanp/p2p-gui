package tracker;

import Exceptions.ListenerCouldntConnectException;
import Exceptions.NoInternetConnectionException;
import Exceptions.ServersIOException;
import org.junit.Before;
import org.junit.Test;
import util.ServersCommon;

public class TrackerStateTest {

    TrackerState trackerState;
    @Before public void setUp() throws ListenerCouldntConnectException, NoInternetConnectionException, ServersIOException {
        trackerState = TrackerState.create();
    }

    @Test
    public void testCreateConnects() {
        System.out.println("Listening at: "+
                           ServersCommon.ipPortToString(
                                   trackerState.getExternalAddr()));
    }
}

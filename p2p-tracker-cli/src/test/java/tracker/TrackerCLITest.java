package tracker;

import org.junit.Test;

import static org.junit.Assert.assertTrue;

/* this should use the REAL BaseCLITest, not some fake copy-paste of it */
public class TrackerCLITest extends BaseCLITest {
    @Override protected TrackerCLI create() { return new TrackerCLI(); }

    @Test public void testTesting() throws Exception {
        assertTrue(true);
    }
}

package tracker;

import org.junit.Test;
import p2p.file.meta.MetaP2P;

import java.util.regex.Pattern;

import static org.junit.Assert.assertTrue;

/* this should use the REAL BaseCLITest, not some fake copy-paste of it */
public class TrackerCLITest {
    protected TrackerCLI cli;
    protected static final String STAR = ".*";

    protected void assertContains(String pattern, String text) {

        /* DOTALL means the dot applies to newline chars too
         * so that we can search for the pattern
         * anywhere inside a multiline text
         */
        Pattern p = Pattern.compile(STAR+pattern+STAR, Pattern.DOTALL);
        try {
            assertTrue(p.matcher(text).matches());
        } catch (AssertionError e) {
            throw new AssertionError(
                    "\n\n\tcouldn't find pattern:\n\n"+pattern+"\n\n\tin text:\n\n"+text+"\n");
        }
    }

    @Test public void testListSwarms() throws Exception {

        /* setup */
        cli = new TrackerCLI();
        TrackerSwarm trackerSwarm1 = new TrackerSwarm(MetaP2P.genFake(), cli.getState());
        trackerSwarm1.addSeeders(
                TrackerPeer.genFake(),
                TrackerPeer.genFake(),
                TrackerPeer.genFake());

        TrackerSwarm trackerSwarm2 = new TrackerSwarm(MetaP2P.genFake(), cli.getState());
        trackerSwarm2.addLeechers(
                TrackerPeer.genFake(),
                TrackerPeer.genFake());

        cli.getState().getSwarms().addAll(trackerSwarm1, trackerSwarm2);

        /* act */
        String output = cli.listCommand();
        String[] outputLines = output.split("\n");

        /* verify */
        assertContains("1\\) file-.*3 seeders  0 leechers.*B", outputLines[0]);
        assertContains("2\\) file-.*0 seeders  2 leechers.*B", outputLines[1]);
    }

    @Test
    public void testEmptyListSwarms() throws Exception {
        cli = new TrackerCLI();
        String output = cli.listCommand();
        assertContains("not listing any files", output);
    }
}

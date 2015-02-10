package tracker;

import org.junit.Test;
import p2p.file.meta.MetaP2P;

/* this should use the REAL BaseCLITest, not some fake copy-paste of it */
public class TrackerCLITest extends BaseCLITest<TrackerCLI> {
    @Override protected TrackerCLI create() { return new TrackerCLI(); }

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
}

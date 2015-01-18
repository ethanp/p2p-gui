package p2p.file.meta;

import p2p.tracker.AbstractRemoteTracker;
import p2p.tracker.FakeRemoteTracker;

import java.io.File;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Ethan Petuchowski 1/8/15
 */
public class LocalFakeFile extends P2PFile {
    static Random random = new Random();
    static File containerFolder = new File("/Users/Ethan/Desktop/FakeP2PFiles");

    static {
        if (!containerFolder.exists()) {
            if (!containerFolder.mkdirs()) {
                Logger.getGlobal().log(Level.SEVERE, "couldn't create FakeP2PFile container folder");
            }
        }
    }

    public static LocalFakeFile genFakeFile() {
        File fakeFile = new File(containerFolder, "fakeFile-"+random.nextInt(500));
        long randomFilesize = random.nextInt(5_000_000);
        FakeRemoteTracker fakeTracker = FakeRemoteTracker.getDefaultFakeRemoteTracker();
        return new LocalFakeFile(fakeFile, randomFilesize, fakeTracker);
    }

    public LocalFakeFile(String filename, long filesize, AbstractRemoteTracker tracker) {
        super(containerFolder, new MetaP2PFile(filename, filesize, "FAKE_DIGEST_DEADBEEF"));
        addTracker(tracker);
    }
}

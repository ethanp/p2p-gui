package client.p2pFile;

import p2p.exceptions.CreateP2PFileException;
import p2p.file.meta.MetaP2PFile;
import util.Common;

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

    public static LocalFakeFile genFakeFile() throws CreateP2PFileException {
        File fakeFile = new File(containerFolder, "fakeFile-"+random.nextInt(500));
        int randomFilesize = random.nextInt(5_000_000);
        FakeRemoteTracker fakeTracker = FakeRemoteTracker.getDefaultFakeRemoteTracker();
        return new LocalFakeFile(fakeFile.getName(), randomFilesize, fakeTracker);
    }

    public LocalFakeFile(String filename, int filesize, AbstractRemoteTracker tracker)
            throws CreateP2PFileException
    {
        super(containerFolder, new MetaP2PFile(filename, filesize, "FAKE_DIGEST_DEADBEEF"));
        addTracker(tracker);
    }

    @Override public String getCompletenessString() {
        /* %% prints % in String.format() */
        return String.format("%d%%", Common.randInt(100));
    }
}

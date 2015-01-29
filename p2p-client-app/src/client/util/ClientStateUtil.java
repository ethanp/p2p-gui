package client.util;

import client.Main;
import p2p.exceptions.CreateP2PFileException;
import p2p.file.p2pFile.LocalFakeFile;
import p2p.tracker.FakeRemoteTracker;

/**
 * Ethan Petuchowski 1/15/15
 */
public class ClientStateUtil {

    /* we catch the exception here because adding a FAKE file should never throw an exception */
    public static void addFakeLocalFile() {
        try {
            Main.getLocalFiles().add(LocalFakeFile.genFakeFile());
        }
        catch (CreateP2PFileException e) {
            e.printStackTrace();
        }
    }

    public static void addFakeTracker() {
        Main.getKnownTrackers().add(FakeRemoteTracker.makeFakeTracker());
    }
}

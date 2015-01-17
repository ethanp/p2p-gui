package client.util;

import client.Main;
import p2p.file.FakeP2PFile;
import p2p.tracker.FakeRemoteTracker;

/**
 * Ethan Petuchowski 1/15/15
 */
public class ClientStateUtiil {
    public static void addFakeLocalFile() {
        Main.getLocalFiles().add(FakeP2PFile.genFakeFile());
    }

    public static void addFakeTracker() {
        Main.getKnownTrackers().add(FakeRemoteTracker.makeFakeTracker());
    }

}

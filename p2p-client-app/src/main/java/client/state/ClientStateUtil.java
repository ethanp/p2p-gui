package client.state;

/**
 * Ethan Petuchowski 1/15/15
 */
public class ClientStateUtil {

    /* we catch the exception here because adding a FAKE file should never throw an exception */
    public static void addFakeLocalFile() {
//        try {
//            ClientState.getLocalFiles().add(LocalFakeFile.genFakeFile());
//        }
//        catch (CreateP2PFileException e) {
//            e.printStackTrace();
//        }
    }

    public static void addFakeTracker() {
//        ClientState.getKnownTrackers().add(FakeRemoteTracker.makeFakeTracker());
    }
}

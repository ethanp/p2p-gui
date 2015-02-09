package client;

import base.BaseCLI;
import client.state.ClientState;

import java.net.UnknownHostException;
import java.util.Arrays;

/**
 * Ethan Petuchowski 1/29/15
 *
 * When the User wants to start the service up as a command line utility
 * this is the system that gets invoked.
 *
 * Example session:
 *
 *      $#> tracker 123.123.123.123:123
 *      connected, downloading file list
 *      File list:
 *      1.) abcd efgh   (3, 2)
 *      2.) ijkl        (5, 1)
 *      3.) MnoP 3fSe   (23, 132)
 *
 *      $#> download 2
 *      downloading file "ijkl"
 *      contacting peers
 *      3 peer connections made
 *      type "ps" to see download basics
 *
 *      $#> upload "/Absolute/path/to/file.txt"
 *      this should work with or without the quotation marks
 *      as long as the path has no spaces.
 *
 *      $#> ps
 *      Downloading:
 *      1.) ijkl  23.5 KB/s   23%  13.1 MB
 *
 *      Seeding:
 *      1.) ijkl  52.1 KB/s   1.1 MB
 *
 *      $#> info
 *      known trackers:
 *      123.123.123.123:123   3 files   Avg avbl: 4.1
 */
public class ClientCLI extends BaseCLI {
    /** the args aren't used for anything at this point */
    public static void main(String[] args) { new ClientCLI(); }

    ClientState state = new ClientState().init();

    ClientCLI() {
        commandLoop();
    }

    private void commandLoop() {
        while (true) {
            String[] inputComponents = console.prompt().split(" ");
            String userCommand = inputComponents[0];
            switch (userCommand) {
                case "tracker":
                    trackerCommand(inputComponents);
                    break;
                case "download":
                    downloadCommand(inputComponents);
                    break;
                case "upload":
                    uploadCommand(inputComponents);
                    break;
                case "list":
                    listCommand(inputComponents);
                    break;
                case "ps":
                    /* display info about current downloads */
                    psCommand(inputComponents);
                    break;
                case "info":
                    /* display state info:
                     *   number of known trackers
                     */
                    infoCommand(inputComponents);
                    break;
                default:
                    System.out.println("Unrecognized command: "+
                                       Arrays.toString(inputComponents).replace(",", ""));
                    break;
            }
        }
    }

    /**
     * $#> info
     * known trackers:
     * 123.123.123.123:123   3 files   Avg avbl: 4.1
     *      ...
     */
    private void infoCommand(String[] arguments) {
//        if (state.getKnownTrackers().isEmpty()) {
//            System.out.println("no trackers have been added yet");
//            return;
//        }
//        for (RemoteTracker tracker : state.getKnownTrackers()) {
//            System.out.format("%25s %d files\n", tracker.getIpPortString(), tracker.numSwarms());
//        }
    }

    private void psCommand(String[] arguments) {}

    private void listCommand(String[] arguments) {}

    private void uploadCommand(String[] arguments) {}

    /**
     *  $#> download 2
     *  downloading file "ijkl"
     *  contacting peers
     *  3 peer connections made
     *  type "ps" to see download basics
     */
    private void downloadCommand(String[] arguments) {
        if (arguments.length != 2) {
            System.out.println("That 'download' command was improperly formatted!\n"
                             + "It should look like: download 2\n"
                             + "and it will refer to the last tracker you added or listed.");
            return;
        }
    }

    /**
     *  $#> tracker 123.123.123.123:123
     *  connected, downloading file list
     *  File list:
     *  1.) abcd efgh   (3, 2)
     *  2.) ijkl        (5, 1)
     *  3.) MnoP 3fSe   (23, 132)
     */
    private void trackerCommand(String[] arguments) {
        if (arguments.length != 2) {
            System.out.println("That 'tracker' command was improperly formatted!\n"
                             + "It should look like: tracker 123.123.123.123:1234");
            return;
        }
        try {
            state.addTrackerByAddrStr(arguments[1]);
        }
        catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }
}

class MyConsole {

}

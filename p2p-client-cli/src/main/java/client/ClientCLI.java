package client;

import base.BaseCLI;
import client.state.ClientState;

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
 *      this should work with or without the text marks
 *      as long as the path has no spaces.
 *
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
                case "list":
                case "download":
                case "upload":
                case "tracker":
                case "ps":
                    /* display info about current downloads */
                case "info":
                    /* display state info:
                     *   number of known trackers
                     */
                default:
                    break;
            }
        }
    }
}

class MyConsole {

}

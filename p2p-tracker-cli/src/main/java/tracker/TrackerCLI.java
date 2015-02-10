package tracker;

import Exceptions.ListenerCouldntConnectException;
import Exceptions.NoInternetConnectionException;
import Exceptions.ServersIOException;
import base.BaseCLI;

import java.util.Arrays;

/**
 * Ethan Petuchowski 2/9/15
 */
public class TrackerCLI extends BaseCLI {
    TrackerState state;

    public static void main(String[] args) { new TrackerCLI(); }

    public TrackerCLI() {
        try {
            state = TrackerState.create();
        }
        catch (ListenerCouldntConnectException | NoInternetConnectionException | ServersIOException e) {
            e.printStackTrace();
        }
        commandLoop();
    }

    @Override protected void commandLoop() {
        while (true) {
            String[] inputComponents = console.prompt().split(" ");
            String userCommand = inputComponents[0];
            switch (userCommand) {
                case "info":
                    infoCommand(inputComponents);
                    break;
                case "exit":
                    return;
                default:
                    System.out.println("Unrecognized command: "+
                                       Arrays.toString(inputComponents).replace(",", ""));
                    break;
            }
        }
    }

    private void infoCommand(String[] arguments) {}
}

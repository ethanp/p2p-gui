package tracker;

import Exceptions.ListenerCouldntConnectException;
import Exceptions.NoInternetConnectionException;
import Exceptions.ServersIOException;
import base.BaseCLI;
import p2p.tracker.swarm.Swarm;

import java.util.Arrays;

/**
 * Ethan Petuchowski 2/9/15
 */
public class TrackerCLI extends BaseCLI {
    public static void main(String[] args) { new Thread(new TrackerCLI()).start(); }

    {
        try {
            state = TrackerState.create();
        }
        catch (ListenerCouldntConnectException | NoInternetConnectionException | ServersIOException e) {
            e.printStackTrace();
        }
    }

    private TrackerState state;
    public TrackerState getState() { return state; }

    @Override protected void commandLoop() {
        while (true) {
            String[] inputComponents = console.prompt().split(" ");
            String userCommand = inputComponents[0];
            switch (userCommand) {
                case "list":
                    System.out.println(listCommand());
                    break;
                case "exit": System.exit(0);
                default: System.out.println("Unrecognized command: "+
                                       Arrays.toString(inputComponents).replace(",", "")); break;
            }
        }
    }

    /**
     *   $#> list
     *   1) filename  filesize  #seeders  #leechers
     *   2)    ...
     *   ...
     */
    public String listCommand() {
        int i = 1;
        StringBuilder sb = new StringBuilder();
        for (Swarm swarm : state.getSwarms())
            sb.append(i+++") "+swarm.toCLIString()+'\n');
        if (sb.length() == 0)
            return "this tracker is not listing any files";
        return sb.toString();
    }

    @Override public void run() {
        commandLoop();
    }
}

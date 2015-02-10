package client;

import Exceptions.ServersIOException;
import base.BaseCLI;
import client.state.ClientState;
import client.tracker.swarm.ClientSwarm;
import p2p.exceptions.ConnectToTrackerException;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;
import util.ServersCommon;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.regex.Pattern;

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
 *      1) abcd efgh   3 seeders  2 leechers
 *      2) ijkl        5 seeders  1 leechers
 *      3) MnoP 3fSe   23 seeders  132 leechers
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
 *      1.) 123.123.123.123:123   3 files   Avg avbl: 4.1
 */
public class ClientCLI extends BaseCLI {

    public static void main(String[] args) { new Thread(new ClientCLI()).start(); }
    @Override public void run() { commandLoop(); }

    ClientState state = new ClientState();

    @Override protected void commandLoop() {
        while (true) {
            String[] inputComponents = console.prompt().split(" ");
            String userCommand = inputComponents[0];
            switch (userCommand) {
                case "tracker":  System.out.println(trackerCommand(inputComponents));  break;
                case "download": System.out.println(downloadCommand(inputComponents)); break;
                case "upload":   System.out.println(uploadCommand(inputComponents));   break;
                case "ps":       System.out.println(psCommand(inputComponents));       break;
                case "info":     System.out.println(infoCommand(inputComponents));     break;
                case "exit":     System.exit(0);
                default:         System.out.println("Unrecognized command: "+
                                        Arrays.toString(inputComponents).replace(",", "")); break;
            }
        }
    }


    /**
     *  $#> download 2
     *  downloading file "ijkl"
     *  contacting peers
     *  3 peer connections made
     *  type "ps" to see download basics
     */
    public String downloadCommand(String[] arguments) {
        if (arguments.length != 2) {
            return "That 'download' command was improperly formatted!\n"
                 + "It should look like: download 2\n"
                 + "and it will refer to the last tracker you added or listed.";
        }
        throw new NotImplementedException();
    }

    /**
     *  $#> tracker 123.123.123.123:123
     *  connected, downloading file list
     *  File list:
     *  1) abcd efgh   3 seeders  2 leechers
     *  2) ijkl        5 seeders  1 leechers
     *  3) MnoP 3fSe   23 seeders  132 leechers
     */
    public String trackerCommand(String[] arguments) {
        if (arguments.length != 2 || !Pattern.matches(ServersCommon.IP4_wPORT_REG, arguments[1])) {
            return "That 'tracker' command was improperly formatted!\n"
                 + "It should look like: tracker 123.123.123.123:1234";
        }
        try {
            System.out.println("downloading file list");
            Collection<ClientSwarm> trackerListing = state.addTrackerAndListSwarms(arguments[1]);
            StringBuilder s = new StringBuilder("File list:\n");
            int i = 1;
            for (ClientSwarm swarm : trackerListing) {
                s.append(i++ + ") ");
                s.append(swarm.toCLIString()+"\n");
            }
            if (i == 1)
                s.append("Tracker has no files\n");
            return s.toString();
        }
        catch (ServersIOException | ConnectToTrackerException | IOException e) {
            return "Tracker not found!";
        }
    }


    /**
     * $#> info
     * known trackers:
     * 1.) 123.123.123.123:123   3 files   Avg avbl: 4.1
     *      ...
     */
    public String infoCommand(String[] arguments) {throw new NotImplementedException();}
    public String psCommand(String[] arguments) {throw new NotImplementedException();}
    public String uploadCommand(String[] arguments) {throw new NotImplementedException();}
}

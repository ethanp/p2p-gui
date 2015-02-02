package base;

import client.ClientCLI;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;
import tracker.TrackerCLI;

import java.util.Scanner;

/**
 * Ethan Petuchowski 1/29/15
 */
public class BaseCLI {

    static Scanner in = new Scanner(System.in);

    public static void main(String[] args) {
        System.out.println("Enter:\n1) Client\n2) Tracker");
        int type = in.nextInt();
        switch (type) {
            case 1: new ClientCLI();  return;
            case 2: new TrackerCLI(); return;
        }
    }

    public static void run() {
        // TODO implement BaseCLI run
        throw new NotImplementedException();
    }
}

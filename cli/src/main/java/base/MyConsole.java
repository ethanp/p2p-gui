package base;

import java.util.Scanner;

/**
 * Ethan Petuchowski 2/8/15
 */
public class MyConsole {
    Scanner scanner = new Scanner(System.in);

    public String prompt() {
        return prompt("$#>");
    }

    public String prompt(String promptString) {
        System.out.print(promptString+" ");
        return scanner.nextLine();
    }
}

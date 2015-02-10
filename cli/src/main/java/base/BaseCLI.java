package base;

/**
 * Ethan Petuchowski 2/8/15
 */
public abstract class BaseCLI {
    protected MyConsole console = new MyConsole();
    protected abstract void commandLoop();
}

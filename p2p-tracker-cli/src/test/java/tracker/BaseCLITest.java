package tracker;

import base.BaseCLI;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.regex.Pattern;

import static org.junit.Assert.assertTrue;

/**
 * Ethan Petuchowski 2/9/15
 *
 * This is a COPY-PASTE of an identical class in the CLI module, because after
 * too much pain in trying, I couldn't get this module to find that class. So fine.
 * If I end up USING the TrackerCLITest for something I should really fix it though.
 */
public abstract class BaseCLITest<CLI extends BaseCLI> {

    protected CLI cli;
    protected ByteArrayOutputStream out = new ByteArrayOutputStream();
    protected InputStream in;
    protected static final String STAR = ".*";
    protected static final String END_IN = "\nexit\n";

    protected abstract CLI create();

    protected void assertContains(String pattern, String text) {

        /* DOTALL means the dot applies to newline chars too
         * so that we can search for the pattern
         * anywhere inside a multiline text
         */
        Pattern p = Pattern.compile(STAR+pattern+STAR, Pattern.DOTALL);
        assertTrue(p.matcher(text).matches());
    }

    /**
     * @param input  will be passed to the CLI
     * @return the (multiple lines of) output printed by the CLI
     */
    protected String captureOutputOfInput(String input) throws IOException {
        System.setOut(new PrintStream(out));
        in = new ByteArrayInputStream(input.getBytes());
        System.setIn(in);
        cli = create();
        out.flush();
        return new String(out.toByteArray());
    }
}

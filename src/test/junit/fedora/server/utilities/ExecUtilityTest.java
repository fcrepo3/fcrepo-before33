/* The contents of this file are subject to the license and copyright terms
 * detailed in the license directory at the root of the source tree (also 
 * available online at http://www.fedora.info/license/).
 */
package fedora.server.utilities;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

import junit.framework.TestCase;
import fedora.utilities.ExecUtility;


/**
 *
 * @author Edwin Shin
 * @version $Id$
 */
public class ExecUtilityTest
        extends TestCase {

    private PrintStream _out;
    private PrintStream _err;
    
    public void setUp() throws Exception {
        _out = System.out;
        _err = System.err;
        System.setOut(new PrintStream(new NullOutputStream()));
        System.setErr(new PrintStream(new NullOutputStream()));
    }

    public void tearDown() throws Exception {
        System.setOut(_out);
        System.setErr(_err);
    }
    
    /**
     * Test ExecUtility
     * 
     * Note: I'm not sure what the behavior is in Windows, so this test may
     * need modification.
     */
    public void testExecWithSpaces() {
        String osName = System.getProperty("os.name");
        String cmd;
        String dir = System.getProperty("java.io.tmpdir");
        if (osName.startsWith("Windows")) {
            cmd = "dir /a " + dir;
        } else {
            cmd = "ls -l " + dir;
        }
        assertNull(ExecUtility.execCommandLineUtility(cmd));
        
        String[] cmda = new String[3];
        if (osName.startsWith("Windows")) {
            cmda[0] = "dir";
            cmda[1] = "/a";
        } else {
            cmda[0] = "ls";
            cmda[1] = "-l";
        }
        cmda[2] = dir;
        assertNotNull(ExecUtility.execCommandLineUtility(cmda));
    }
    
    /**
     * A bit bucket so all the extraneous test output doesn't flood stdout.
     *
     * @author Edwin Shin
     * @version $Id$
     */
    class NullOutputStream extends OutputStream {
        @Override
        public void write(int b) throws IOException {}
    }
}

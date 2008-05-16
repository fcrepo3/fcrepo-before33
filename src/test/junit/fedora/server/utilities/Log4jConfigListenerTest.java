/* The contents of this file are subject to the license and copyright terms
 * detailed in the license directory at the root of the source tree (also 
 * available online at http://www.fedora.info/license/).
 */
package fedora.server.utilities;

import static org.junit.Assert.assertEquals;

import org.junit.Test;


/**
 *
 * @author Edwin Shin
 * @version $Id$
 */
public class Log4jConfigListenerTest {
    /**
     * Test method for {@link fedora.server.utilities.Log4jConfigListener#dereferenceSystemProperties(java.lang.String)}.
     */
    @Test
    public void testDereferenceSystemProperties() {
        System.setProperty("test.asdf", "asdf");
        System.setProperty("test.qwerty", "qwerty");
        String input = "${test.asdf}/server/config/log4j.properties/${this.should.not.match}/${test.qwerty}";
        String result = "asdf/server/config/log4j.properties/${this.should.not.match}/qwerty";
        String candidate = Log4jConfigListener.dereferenceSystemProperties(input);
        assertEquals(result, candidate);
    }
}

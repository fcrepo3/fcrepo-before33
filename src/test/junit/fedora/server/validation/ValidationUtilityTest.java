/**
 * 
 */
package fedora.server.validation;

import junit.framework.JUnit4TestAdapter;

import org.junit.Test;

import fedora.server.errors.ValidationException;

/**
 * @author Edwin Shin
 * @since 3.0
 * @version $Id$
 *
 */
public class ValidationUtilityTest {
    private String tmpDir = System.getProperty("java.io.tmpdir");
    
	@Test
	public void testValidUrls() throws Exception {	    
		String[] urls = {"http://localhost", 
						 "http://localhost:8080",
						 "uploaded:///tmp/foo.xml"};

		for (String url : urls) {
			ValidationUtility.validateURL(url);
		}
	}

	@Test(expected=ValidationException.class)
	public void testInvalidUrls() throws Exception {
		String[] urls = {"", "a", 
						 "temp:///etc/passwd", 
						 "copy:///etc/passwd",
						 "temp://" + tmpDir + "/../etc/passwd",
						 "temp://" + tmpDir + "/../../etc/passwd",
                         "file:///etc/passwd",
                         "file:/etc/passwd",
                         "/etc/passwd",
                         "../../etc/passwd"};

		for (String url : urls) {
			ValidationUtility.validateURL(url);
		}
	}
	
	public static junit.framework.Test suite() {
        return new JUnit4TestAdapter(ValidationUtilityTest.class);
    }
}

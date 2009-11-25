/* The contents of this file are subject to the license and copyright terms
 * detailed in the license directory at the root of the source tree (also
 * available online at http://fedora-commons.org/license/).
 */
package fedora.utilities.install.container;

import java.io.File;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * @author Edwin Shin
 */
public class TestFedoraWebXML {

    private String webXMLFilePath;

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
        File f = new File("../fcrepo-webapp/fcrepo-webapp-fedora/src/main/webapp/WEB-INF/web.xml");
        assertTrue("Couldn't find source web.xml file", f.exists());
        webXMLFilePath = f.getAbsolutePath();
    }

    /**
     * @throws java.lang.Exception
     */
    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testOptions() throws Exception {
        // TODO this is just the stub for a proper test

        FedoraWebXML webXML;

        // TestConfigA
        webXML =
                new FedoraWebXML(webXMLFilePath, getOptions(false,
                                                            true,
                                                            true,
                                                            ""));
        assertNotNull(webXML);

        // TestConfigB
        webXML =
                new FedoraWebXML(webXMLFilePath, getOptions(true,
                                                            true,
                                                            true,
                                                            ""));
    }

    private WebXMLOptions getOptions(boolean apiaA,
                                     boolean apiaS,
                                     boolean apimS,
                                     String fedoraHome) {
        WebXMLOptions options = new WebXMLOptions();
        options.setApiaAuth(apiaA);
        options.setApiaSSL(apiaS);
        options.setApimSSL(apimS);
        options.setFedoraHome(new File(fedoraHome));
        return options;
    }

    // Supports legacy test runners
    public static junit.framework.Test suite() {
        return new junit.framework.JUnit4TestAdapter(TestFedoraWebXML.class);
    }
}

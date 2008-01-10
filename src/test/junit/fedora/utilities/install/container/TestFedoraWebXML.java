
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
        File f = new File("src/war/fedora/WEB-INF/web.xml");
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
                                                            false,
                                                            ""));
        assertNotNull(webXML);

        // TestConfigB
        webXML =
                new FedoraWebXML(webXMLFilePath, getOptions(true,
                                                            true,
                                                            true,
                                                            false,
                                                            ""));

        // rest enabled
        webXML =
                new FedoraWebXML(webXMLFilePath, getOptions(true,
                                                            true,
                                                            true,
                                                            true,
                                                            ""));

    }

    private WebXMLOptions getOptions(boolean apiaA,
                                     boolean apiaS,
                                     boolean apimS,
                                     boolean rest,
                                     String fedoraHome) {
        WebXMLOptions options = new WebXMLOptions();
        options.setApiaAuth(apiaA);
        options.setApiaSSL(apiaS);
        options.setApimSSL(apimS);
        options.setRestAPI(rest);
        options.setFedoraHome(new File(fedoraHome));
        return options;
    }

}

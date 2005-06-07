package fedora.test.integration;

import java.io.File;

import junit.extensions.TestSetup;
import junit.framework.Test;
import junit.framework.TestSuite;

import org.custommonkey.xmlunit.SimpleXpathEngine;

import fedora.test.FedoraServerTestCase;
import fedora.test.FedoraServerTestSetup;
import fedora.utilities.ExecUtility;

/**
 * @author Edwin Shin
 *
 */
public class TestCommandLineUtilities extends FedoraServerTestCase {
    public static Test suite() {
        TestSuite suite = new TestSuite(TestCommandLineUtilities.class);
        TestSetup wrapper = new FedoraServerTestSetup(suite) {
            public void setUp() throws Exception {

            }
            
            public void tearDown() throws Exception {
                SimpleXpathEngine.clearNamespaces();
            }
        };
        return new FedoraServerTestSetup(wrapper);
                
    }
    
    public void testFedoraIngestAndPurge() {
        ingestFoxmlFile(new File("src/demo-objects/foxml/local-server-demos/simple-image-demo"));
        purge("demo:5");
    }
    
    private void ingestFoxmlFile(File f) {
        //fedora-ingest f obj1.xml foxml1.0 myrepo.com:8443 jane jpw https
        execute("/client/bin/fedora-ingest f " + f.getAbsolutePath() + 
                " foxml1.0 " + getHost() + ":" + getPort() + " " + getUsername() + 
                " " + getPassword() + " " + getProtocol() + " junit ingest");
    }
    
    private void purge(String pid) {
        execute("/client/bin/fedora-purge " + getHost() + ":" + getPort() +
                " " + getUsername() + " " + getPassword() + " " + pid + " " + 
                getProtocol() + " junit purge");
    }
    
    private void execute(String cmd) {
        ExecUtility.exec(FEDORA_HOME + cmd);
    }
    
    public static void main(String[] args) {
        junit.textui.TestRunner.run(TestCommandLineUtilities.class);
    }

}

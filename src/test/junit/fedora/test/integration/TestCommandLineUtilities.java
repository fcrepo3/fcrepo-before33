package fedora.test.integration;

import java.io.File;

import fedora.test.FedoraServerTestCase;
import fedora.utilities.ExecUtility;

/**
 * @author Edwin Shin
 *
 */
public class TestCommandLineUtilities extends FedoraServerTestCase {
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

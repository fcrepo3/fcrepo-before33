package fedora.test.integration;

import fedora.test.FedoraServerTestCase;
import fedora.utilities.ExecUtility;

/**
 * Tests fedora-ingest-demos.
 * 
 * Usage: 
 *   fedora-ingest-demos <hostname> <port> <username> <password> <protocol>
 * @author Edwin Shin
 */
public class TestIngestDemoObjects extends FedoraServerTestCase {

    public static void main(String[] args) {
        junit.textui.TestRunner.run(TestIngestDemoObjects.class);
    }
    
    public void testIngestDemoObjects() {
        ExecUtility.exec("/fedora-ingest-demos");
    }
}

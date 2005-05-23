package fedora.test.integration;

import fedora.test.FedoraServerTestCase;
import fedora.utilities.ExecUtility;

/**
 * @author Edwin Shin
 *
 */
public class TestCommandLineUtilities extends FedoraServerTestCase {

    public void testFedoraIngest() {
        ExecUtility.exec(getFedoraHome() + "/client/bin/fedora-ingest ");
    }
    
    public void testFedoraPurge() {
        ExecUtility.exec(getFedoraHome() + "/client/bin/fedora-ingest-demos " );
    }
    
    public static void main(String[] args) {
        junit.textui.TestRunner.run(TestCommandLineUtilities.class);
    }

}


package fedora.test;

import junit.extensions.TestSetup;
import junit.framework.Test;

public class DemoObjectTestSetup
        extends TestSetup
        implements FedoraTestConstants {

    public DemoObjectTestSetup(Test test) {
        super(test);
    }

    @Override
    public void setUp() throws Exception {
        System.out.println("Ingesting demo objects...");
        FedoraServerTestCase.ingestDemoObjects();
    }

    @Override
    public void tearDown() throws Exception {
        System.out.println("Purging demo objects...");
        FedoraServerTestCase.purgeDemoObjects();
    }
}

package fedora.test;

import junit.extensions.TestSetup;
import junit.framework.Test;

public class DemoObjectTestSetup extends TestSetup implements FedoraTestConstants {

	public DemoObjectTestSetup(Test test) {
		super(test);
	}
	
	public void setUp() throws Exception {
		System.out.println("Ingesting demo objects...");
		FedoraServerTestCase.ingestDemoObjects();
	}
	
	public void tearDown() throws Exception {
		System.out.println("Purging demo objects...");
		FedoraServerTestCase.purgeDemoObjects();
	}
}

package fedora.test;

import org.custommonkey.xmlunit.XMLTestCase;

/**
 * Base class for Fedora Test Cases
 * 
 * @author Edwin Shin
 */
public abstract class FedoraTestCase extends XMLTestCase implements FedoraTestConstants {   
	public FedoraTestCase() {
        super();
    }
    
    public FedoraTestCase(String name) {
        super(name);
    }
}

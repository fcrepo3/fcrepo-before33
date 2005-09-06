package fedora.test.config;

import java.io.*;
import java.util.Iterator;
import java.util.Set;
import java.util.HashSet;
import javax.xml.parsers.DocumentBuilderFactory;

import junit.extensions.TestSetup;
import junit.framework.*;

import fedora.client.*;
import fedora.server.access.*;
import fedora.server.management.*;
import fedora.server.types.gen.*;
import fedora.test.*;
import fedora.test.SuperAPIALite;
import fedora.test.Trial;
import fedora.test.FedoraServerTestSetup;
import fedora.test.integration.TestIngestDemoObjects;
import org.custommonkey.xmlunit.SimpleXpathEngine;

public class TestAPIALiteSecureAll extends TestAPIALite {
	
    public TestAPIALiteSecureAll() throws Exception {
    	super();
    }
        
    public String getConfiguration() {
    	return Trial.SECURE_ALL;
    }
   
    
    public static Test suite() {
        TestSuite suite = new TestSuite("TestAPIALite SecureAll Configuration");
        suite.addTestSuite(TestAPIALiteSecureAll.class);
        TestSetup wrapper = new TestSetup(suite) {
        //TestSetup wrapper = new TestSetup(suite, TestAPIALiteOutOfTheBoxConfig.class.getName()) {
            public void setUp() throws Exception {
				ssl = "https"; //fixup
				System.out.println("protocol just set ==" + ssl); 
                TestIngestDemoObjects.ingestDemoObjects();
                fcfg = getServerConfiguration();
                client = new FedoraClient(getBaseURL(), getUsername(), getPassword());
                factory = DocumentBuilderFactory.newInstance();
                builder = factory.newDocumentBuilder();
                demoObjects = TestIngestDemoObjects.getDemoObjects(null);
                SimpleXpathEngine.registerNamespace("oai_dc", "http://www.openarchives.org/OAI/2.0/oai_dc/");
                SimpleXpathEngine.registerNamespace(NS_FEDORA_TYPES_PREFIX, NS_FEDORA_TYPES);
                SimpleXpathEngine.registerNamespace("demo", "http://example.org/ns#demo");
                SimpleXpathEngine.registerNamespace(NS_XHTML_PREFIX, NS_XHTML); //                
            }
            public void tearDown() throws Exception {
                SimpleXpathEngine.clearNamespaces();
                TestIngestDemoObjects.purgeDemoObjects();
            }
        };
        return new FedoraServerTestSetup(wrapper, TestAPIALiteSecureAll.class.getName());
    }
    
    public static void main(String[] args) {
        junit.textui.TestRunner.run(TestAPIALiteSecureAll.class);
    }

}
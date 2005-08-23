package fedora.test.config;

import java.io.*;
import java.util.Iterator;
import java.util.Set;

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

/**
 * An example TestSuite that uses it's own set of configuration files.
 *
 * This example uses the default fedora.fcfg, secure-web.xml, a custom 
 * tomcat-users.xml, and the default jaas.config.
 *
 * It uses the default beSecurity.xml file, and a custom repository-policies
 * directory, the default object-policies directory, the default surrogate-policies
 * directory, and the default repository-policies-generated-by-policyguitool
 * directory.
 *
 * So 
 *
 * can be set multiple times:
 *   beSecurity.xml
 *   repository-policies/
 *   object-policies/
 *   surrogate-policies/
 *   repository-policies-generated-by-policyguitool/
 */
public class TestAPIALiteSecure extends SuperAPIALite {
	
	private DescribeRepositoryTest describeRepositoryTestXmlOnly = new DescribeRepositoryTest("Fedora Repository", true);
	private DataSource HTTP302 = null;
	private DataSource HTTPS200 = null;
	private DataSource HTTPS403 = null;
	
    public TestAPIALiteSecure() throws Exception {
    	super();
    	HTTP302 = new HttpDataSource(Trial.HTTP_BASE_URL, 302);
    	HTTPS200 = new HttpDataSource(Trial.HTTPS_BASE_URL, 200);
    	HTTPS403 = new HttpDataSource(Trial.HTTPS_BASE_URL, 403);
    }
	
    public void testDescribeRepository() throws Exception {
		iterate(Trial.ALL_SHIPPED_POLICY_TRIALS, HTTP302, describeRepositoryTestXmlOnly);
    	iterate(Trial.ALL_SHIPPED_POLICY_TRIALS, HTTPS200, describeRepositoryTestXmlOnly);
    	iterate(Trial.ALL_NO_POLICY_TRIALS, HTTPS403, describeRepositoryTestXmlOnly);  	    	
    }
    
    public static Test suite() {
        TestSuite suite = new TestSuite("TestAPIALite Secure Configuration");
        suite.addTestSuite(TestAPIALiteSecure.class);
        TestSetup wrapper = new TestSetup(suite) {
        //TestSetup wrapper = new TestSetup(suite, TestAPIALiteOutOfTheBoxConfig.class.getName()) {
            public void setUp() throws Exception {
            	FedoraServerTestCase.ssl = "s";
                TestIngestDemoObjects.ingestDemoObjects();
                fcfg = getServerConfiguration();
                System.out.println("username=" + getUsername());
                System.out.println("password=" + getPassword());
                client = new FedoraClient(Trial.HTTPS_BASE_URL, getUsername(), getPassword()); //getBaseURL()
                factory = DocumentBuilderFactory.newInstance();
                builder = factory.newDocumentBuilder();
                demoObjects = TestIngestDemoObjects.getDemoObjects(null);
                SimpleXpathEngine.registerNamespace("oai_dc", "http://www.openarchives.org/OAI/2.0/oai_dc/");
                SimpleXpathEngine.registerNamespace(NS_FEDORA_TYPES_PREFIX, NS_FEDORA_TYPES);
                SimpleXpathEngine.registerNamespace("demo", "http://example.org/ns#demo");
                SimpleXpathEngine.registerNamespace(NS_XHTML_PREFIX, NS_XHTML); //                
             	FedoraServerTestCase.ssl = "";
            }
            public void tearDown() throws Exception {
             	FedoraServerTestCase.ssl = "s";
                SimpleXpathEngine.clearNamespaces();
                TestIngestDemoObjects.purgeDemoObjects();
             	FedoraServerTestCase.ssl = "";
            }
        };
        return new FedoraServerTestSetup(wrapper, TestAPIALiteSecure.class.getName());
    }
    
    public static void main(String[] args) {
        junit.textui.TestRunner.run(TestAPIALiteOpen.class);
    }

}
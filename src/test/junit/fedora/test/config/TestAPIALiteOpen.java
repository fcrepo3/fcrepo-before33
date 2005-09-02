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
public class TestAPIALiteOpen extends SuperAPIALite {
	
	private DescribeRepositoryTest describeRepositoryTestXmlOnly = new DescribeRepositoryTest("Fedora Repository", true);
	private DataSource HTTP200 = null;
	private DataSource HTTPS200 = null;
	private DataSource HTTP403 = null;
	private DataSource HTTPS403 = null;
	
    public TestAPIALiteOpen() throws Exception {
    	super();
    	HTTP200 = new HttpDataSource(Trial.HTTP_BASE_URL, 200);
    	HTTPS200 = new HttpDataSource(Trial.HTTPS_BASE_URL, 200);
    	HTTP403 = new HttpDataSource(Trial.HTTP_BASE_URL, 403);
    	HTTPS403 = new HttpDataSource(Trial.HTTPS_BASE_URL, 403);
    }
	
    /*
    public void testDescribeRepository() throws Exception {
		iterate(Trial.ALL_SHIPPED_POLICY_TRIALS, HTTP200, describeRepositoryTestXmlOnly);
    	iterate(Trial.ALL_SHIPPED_POLICY_TRIALS, HTTPS200, describeRepositoryTestXmlOnly);
    	iterate(Trial.ALL_NO_POLICY_TRIALS, HTTP403, describeRepositoryTestXmlOnly);  	
    	iterate(Trial.ALL_NO_POLICY_TRIALS, HTTPS403, describeRepositoryTestXmlOnly);  	    	
    }
    */

    
    private static final Set policiesSet = new HashSet ();
    static {
    	policiesSet.add("defaultPolicies");
    	policiesSet.add("noPolicies");
    }
    
    private static final Set protocolSet = new HashSet ();
    static {
    	protocolSet.add("HTTP");
    	protocolSet.add("HTTPS");
    }
    
    private static final Set objectsetSet = new HashSet ();
    static {
    	objectsetSet.add("demoObjects");
    	//objectsetSet.add("fewPids");
    	//objectsetSet.add("missingPids");
    	//objectsetSet.add("badPids");
    }

    private static final Set expectedStatusSet = new HashSet ();
    static {
    	expectedStatusSet.add(new Integer(302));
    	expectedStatusSet.add(new Integer(401));
    	expectedStatusSet.add(new Integer(403));
    	expectedStatusSet.add(new Integer(500));
    	expectedStatusSet.add(new Integer(200));
    }

    /*
	private ObjectProfileTest objectProfileTestXmlOnly = null;
	void iterateObjectProfile(Iterator iterator, DataSource dataSource, Set trials) throws Exception {
        while (iterator.hasNext()) {
        	String pid = (String) iterator.next();
        	objectProfileTestXmlOnly = new ObjectProfileTest(pid, true);
        	iterate(trials, dataSource, objectProfileTestXmlOnly);
        }		
	}
	*/
    
    public static final Set getObjects(String name) {
    	Set temp = null;
    	if ("demoObjects".equals(name)) {
    		temp = fewPids; //demoObjects;
    	} else if ("badPids".equals(name)) {
    		temp = badPids;
    	} else if ("missingPids".equals(name)) {
    		temp = missingPids;    		
		} else if ("fewPids".equals(name)) {
			temp = fewPids;    		
		}
    	return temp;
    }
	public void testPerObjectOperations() throws Exception {
		//String op = "objectProfile";
		Iterator policiesIterator = policiesSet.iterator();
		while (policiesIterator.hasNext()) {
			String policies = (String) policiesIterator.next();
			Iterator protocolIterator = protocolSet.iterator();
			while (protocolIterator.hasNext()) {
				String protocol = (String) protocolIterator.next();
				Iterator objectsetIterator = objectsetSet.iterator();
				while (objectsetIterator.hasNext()) {
					String objectset = (String) objectsetIterator.next();
					Iterator expectedStatusIterator = expectedStatusSet.iterator();
					while (expectedStatusIterator.hasNext()) {
						int expectedStatus = ((Integer)expectedStatusIterator.next()).intValue();
						System.out.println("PROTOCOL FOR DATASOURCE = " + protocol);
						System.out.println("EXPECTEDSTATUS FOR DATASOURCE = " + expectedStatus);						
						//DataSource dataSource = HttpDataSource.getDataSource(protocol, expectedStatus);
						String baseurl = "HTTPS".equals(protocol) ? Trial.HTTPS_BASE_URL : Trial.HTTP_BASE_URL; 
						DataSource dataSource = new HttpDataSource (baseurl, expectedStatus);						
						System.out.println("DATASOURCE = " + dataSource);
						System.out.println("in loop nest, policies==" + policies);						
						Set objects = getObjects(objectset);
						Iterator objectIterator = objects.iterator();
						while (objectIterator.hasNext()) {
				        	String pid = (String) objectIterator.next();
				        	Set trials = null;
							trials = Trial.getTrialSet(Trial.OPEN, policies, protocol, "objectProfile", objectset, expectedStatus);
				        	ObjectProfileTest objectProfileTestXmlOnly = new ObjectProfileTest(pid, true);					        	
				        	iterate(trials, dataSource, objectProfileTestXmlOnly);
				        	
							trials = Trial.getTrialSet(Trial.OPEN, policies, protocol, "objectProfile", objectset, expectedStatus);
							ListMethodsTest listMethodsTestXmlOnly = new ListMethodsTest(pid, true);					        	
				        	iterate(trials, dataSource, listMethodsTestXmlOnly);
						}
					}					
				}
			}
		}
    }       
	
    public void testOtherOperations() throws Exception {
		//String op = "describeRepository";
		Iterator policiesIterator = policiesSet.iterator();
		while (policiesIterator.hasNext()) {
			String policies = (String) policiesIterator.next();
			Iterator protocolIterator = protocolSet.iterator();
			while (protocolIterator.hasNext()) {
				String protocol = (String) protocolIterator.next();
				
				//Iterator objectsetIterator = objectsetSet.iterator();
				//while (objectsetIterator.hasNext()) {
					//String objectset = (String) objectsetIterator.next();
					Iterator expectedStatusIterator = expectedStatusSet.iterator();
					while (expectedStatusIterator.hasNext()) {
						int expectedStatus = ((Integer)expectedStatusIterator.next()).intValue();
						System.out.println("PROTOCOL FOR DATASOURCE = " + protocol);
						System.out.println("EXPECTEDSTATUS FOR DATASOURCE = " + expectedStatus);						
						//DataSource dataSource = HttpDataSource.getDataSource(protocol, expectedStatus);
						String baseurl = "HTTPS".equals(protocol) ? Trial.HTTPS_BASE_URL : Trial.HTTP_BASE_URL; 
						DataSource dataSource = new HttpDataSource (baseurl, expectedStatus);						
						System.out.println("DATASOURCE = " + dataSource);
						System.out.println("in loop nest, policies==" + policies);						
						//Set objects = getObjects(objectset);
						//Iterator objectIterator = objects.iterator();
						//while (objectIterator.hasNext()) {
				        	//String pid = (String) objectIterator.next();
							Set trials = null;
							trials = Trial.getTrialSet(Trial.OPEN, policies, protocol, "describeRepository", expectedStatus);
							DescribeRepositoryTest describeRepositoryTestXmlOnly = new DescribeRepositoryTest("Fedora Repository", true);					        	
				        	iterate(trials, dataSource, describeRepositoryTestXmlOnly);

							trials = Trial.getTrialSet(Trial.OPEN, policies, protocol, "describeRepository", expectedStatus);
							FindTest findTestXmlOnly = new FindTest("pid=demo:*", 1000000, demoObjects.size(), true);					        	
				        	iterate(trials, dataSource, findTestXmlOnly);
				        	
							trials = Trial.getTrialSet(Trial.OPEN, policies, protocol, "describeRepository", expectedStatus);
							FindTest resumeFindTestXmlOnly = new FindTest("pid=demo:*", 10, demoObjects.size(), true);					        	
							iterate(trials, dataSource, resumeFindTestXmlOnly);
				        	
						//}
					}					
				//}
			}
		}
    }
   
    
    public static Test suite() {
        TestSuite suite = new TestSuite("TestAPIALite Open Configuration");
        suite.addTestSuite(TestAPIALiteOpen.class);
        TestSetup wrapper = new TestSetup(suite) {
        //TestSetup wrapper = new TestSetup(suite, TestAPIALiteOutOfTheBoxConfig.class.getName()) {
            public void setUp() throws Exception {
            	FedoraServerTestCase.ssl = "";
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
        return new FedoraServerTestSetup(wrapper, TestAPIALiteOpen.class.getName());
    }
    
    public static void main(String[] args) {
        junit.textui.TestRunner.run(TestAPIALiteOpen.class);
    }

}
package fedora.test.config;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import fedora.test.DataSource;
import fedora.test.DatastreamDisseminationTest;
import fedora.test.DescribeRepositoryTest;
import fedora.test.DisseminationTest;
import fedora.test.FindTest;
import fedora.test.HttpDataSource;
import fedora.test.IterableTest;
import fedora.test.ListDatastreamsTest;
import fedora.test.ListMethodsTest;
import fedora.test.ObjectHistoryTest;
import fedora.test.ObjectProfileTest;
import fedora.test.Trial;

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
abstract public class TestAPIALite extends IterableTest {
	
	private DescribeRepositoryTest describeRepositoryTestXmlOnly = new DescribeRepositoryTest("Fedora Repository", true);
	private DataSource HTTP200 = null;
	private DataSource HTTPS200 = null;
	private DataSource HTTP403 = null;
	private DataSource HTTPS403 = null;
	
    public TestAPIALite() throws Exception {
    	super();
    	HTTP200 = new HttpDataSource(Trial.HTTP_BASE_URL, 200);
    	HTTPS200 = new HttpDataSource(Trial.HTTPS_BASE_URL, 200);
    	HTTP403 = new HttpDataSource(Trial.HTTP_BASE_URL, 403);
    	HTTPS403 = new HttpDataSource(Trial.HTTPS_BASE_URL, 403);
    }
    
    private static final Set policiesSet = new HashSet ();
    static {
    	policiesSet.add(Trial.SHIPPED_POLICIES);
    	policiesSet.add(Trial.NO_POLICIES);
    }
    
    private static final Set protocolSet = new HashSet ();
    static {
    	protocolSet.add("http");
    	protocolSet.add("https");
    }
    
    private static final Set objectsetSet = new HashSet ();
    static {
    	objectsetSet.add("demoObjects");
    	//objectsetSet.add("fewPids");
    	objectsetSet.add("missingPids");
    	objectsetSet.add("badPids");
    }

    private static final Set expectedStatusSet = new HashSet ();
    static {
    	expectedStatusSet.add(new Integer(302));
    	expectedStatusSet.add(new Integer(401));
    	expectedStatusSet.add(new Integer(403));
    	expectedStatusSet.add(new Integer(500));
    	expectedStatusSet.add(new Integer(200));
    }
    
    public static final Set getObjects(String name) {
    	Set temp = null;
    	if ("demoObjects".equals(name)) {
    		temp = demoObjects;
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
				ssl = protocol; //fixup
				System.out.println("protocol just set ==" + ssl);
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
				        	String label = getLabel(policies, protocol, objectset, expectedStatus);
				        	System.out.println("pid==" + pid);
				        	Set trials = null;
							trials = Trial.getTrialSet(getConfiguration(), policies, protocol, Trial.APIA, objectset, expectedStatus);
				        	ObjectProfileTest objectProfileTestXmlOnly = new ObjectProfileTest(pid, true);					        	
				        	iterate(trials, dataSource, objectProfileTestXmlOnly, null, label);
				        	
							trials = Trial.getTrialSet(getConfiguration(), policies, protocol, Trial.APIA, objectset, expectedStatus);
							ObjectHistoryTest objectHistoryTestXmlOnly = new ObjectHistoryTest(pid, true);					        	
				        	iterate(trials, dataSource, objectHistoryTestXmlOnly, null, label);
				        	
							trials = Trial.getTrialSet(getConfiguration(), policies, protocol, Trial.APIA, objectset, expectedStatus);
							ListMethodsTest listMethodsTestXmlOnly = new ListMethodsTest(pid, true);					        	
				        	iterate(trials, dataSource, listMethodsTestXmlOnly, null, label);
							
							trials = Trial.getTrialSet(getConfiguration(), policies, protocol, Trial.APIA, objectset, expectedStatus);
							ListDatastreamsTest listDatastreamsTestXmlOnly = new ListDatastreamsTest(pid, true);					        	
				        	iterate(trials, dataSource, listDatastreamsTestXmlOnly, null, label);

							trials = Trial.getTrialSet(getConfiguration(), policies, protocol, Trial.APIA, objectset, expectedStatus);
							DatastreamDisseminationTest datastreamDisseminationTestXmlOnly = new DatastreamDisseminationTest(pid, "DC", true);					        	
				        	iterate(trials, dataSource, datastreamDisseminationTestXmlOnly, null, label);
				        	
							trials = Trial.getTrialSet(getConfiguration(), policies, protocol, Trial.APIA, objectset, expectedStatus);
							DisseminationTest disseminationTestXmlOnly = new DisseminationTest(pid, "fedora-system:3", "viewDublinCore", true);					        	
				        	iterate(trials, dataSource, disseminationTestXmlOnly, null, label);				        	

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
			System.out.println("POLICIES = " + policies);
			Iterator protocolIterator = protocolSet.iterator();
			while (protocolIterator.hasNext()) {
				String protocol = (String) protocolIterator.next();
				ssl = protocol; //fixup
				System.out.println("protocol just set ==" + ssl);				
				//Iterator objectsetIterator = objectsetSet.iterator();
				//while (objectsetIterator.hasNext()) {
					//String objectset = (String) objectsetIterator.next();
					Iterator expectedStatusIterator = expectedStatusSet.iterator();
					while (expectedStatusIterator.hasNext()) {
						int expectedStatus = ((Integer)expectedStatusIterator.next()).intValue();
						System.out.println("PROTOCOL FOR DATASOURCE = " + protocol);
						System.out.println("EXPECTEDSTATUS FOR DATASOURCE = " + expectedStatus);						
						//DataSource dataSource = HttpDataSource.getDataSource(protocol, expectedStatus);
						String baseurl = getBaseURL(); //Trial.HTTPS.equals(protocol) ? Trial.HTTPS_BASE_URL : Trial.HTTP_BASE_URL; 
						DataSource dataSource = new HttpDataSource (baseurl, expectedStatus);						
						System.out.println("DATASOURCE = " + dataSource);
						System.out.println("in loop nest, policies==" + policies);						
						//Set objects = getObjects(objectset);
						//Iterator objectIterator = objects.iterator();
						//while (objectIterator.hasNext()) {
				        	//String pid = (String) objectIterator.next();
			        	String label = getLabel(policies, protocol, null, expectedStatus);
			        	//if ("noPolicieshttps403".equals(label)) JAASRealm.debug = true;
			        	Set trials = null;
							trials = Trial.getTrialSet(getConfiguration(), policies, protocol, Trial.APIA, expectedStatus);
							DescribeRepositoryTest describeRepositoryTestXmlOnly = new DescribeRepositoryTest("Fedora Repository", true);					        	
				        	iterate(trials, dataSource, describeRepositoryTestXmlOnly, null, label);

							trials = Trial.getTrialSet(getConfiguration(), policies, protocol, Trial.APIA, expectedStatus);
							FindTest findTestXmlOnly = new FindTest("pid=demo:*", 1000000, demoObjects.size(), true);					        	
//if (expectedStatus == 403) continue;
				        	iterate(trials, dataSource, findTestXmlOnly, null, label);
				        	
							trials = Trial.getTrialSet(getConfiguration(), policies, protocol, Trial.APIA, expectedStatus);
							FindTest resumeFindTestXmlOnly = new FindTest("pid=demo:*", 10, demoObjects.size(), true);					        	
							iterate(trials, dataSource, resumeFindTestXmlOnly, null, label);
				        	
						//}
					}					
				//}
			}
		}
    }
    
    abstract public String getConfiguration();

}
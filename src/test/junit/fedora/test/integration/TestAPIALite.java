package fedora.test.integration;  

import java.io.InputStream;
import java.util.Iterator;
import java.util.Set;
import java.util.HashSet;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import junit.extensions.TestSetup;
import junit.framework.Test;
import junit.framework.TestSuite;

import org.custommonkey.xmlunit.SimpleXpathEngine;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentType;
import org.w3c.dom.Node;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.NodeList;


import fedora.client.FedoraClient;
import fedora.server.config.ServerConfiguration;
import fedora.test.FedoraServerTestCase;
import fedora.test.FedoraServerTestSetup;

/**
 * Test of API-A-Lite using demo objects
 * 
 * @author Edwin Shin
 * @author Bill Niebel 
 */
public class TestAPIALite extends FedoraServerTestCase {
    private static DocumentBuilderFactory factory;
    private static DocumentBuilder builder;
    private static ServerConfiguration fcfg;
    private static FedoraClient client;
    private static Set demoObjects;
    
    public static Test suite() {
        TestSuite suite = new TestSuite(TestAPIALite.class);
        TestSetup wrapper = new TestSetup(suite) {
            public void setUp() throws Exception {
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
        return new FedoraServerTestSetup(wrapper);
    }
    
    private Document getQueryResult(String location) throws Exception {
        InputStream is = client.get(getBaseURL() + location, true);
        return builder.parse(is);
    }
    
    private static final boolean DEBUG = false;
    private static final boolean VERBOSE = false;    
    private static final boolean XML = true;
    private static final boolean XHTML = false;
    
    private static final boolean TEST_XML = true;  //this is mainly to mask out tests of XML output during development
    private static final boolean TEST_XHTML = false;  //this is mainly to mask out tests of XHTML output during development
    
    //http://www.fedora.info/download/2.0/userdocs/server/webservices/apialite/index.html
    
    private static final void debugXpath(String xpath, Document doc) throws Exception {
    	if (! DEBUG) return;
		SimpleXpathEngine simpleXpathEngine = new SimpleXpathEngine();
        System.out.print(xpath + " ==> ");
        System.out.flush();
        try {
        	String temp = simpleXpathEngine.evaluate(xpath, doc);              
        	System.out.println(temp);
        	System.out.flush();
        } catch (Exception e) {
            System.out.println(" [exception]");
            System.out.flush();        	
        }
    }
    
    private static final String TAB = "  ";
    private static void nodeWalk (Node node, String tabs) {
    	if (! VERBOSE) return;
    	if (node == null) {
            System.err.println("no document to nodeWalk()!");  
    		return;
    	}
        System.err.println(tabs + node.getNodeName());
    	NamedNodeMap attributes = node.getAttributes();
    	if (attributes == null) {
            //System.err.println("attributes == null");
    	} else {
        	for (int i = 0; i < attributes.getLength(); i++) {
        		Node attribute = attributes.item(i);
            	if (attribute == null) {
                    //System.err.println("attribute == null " + i);
            	} else {
            		String attributeName = attribute.getNodeName();
            		String attributeValue = attribute.getNodeValue();
            		System.err.println(tabs + TAB + "@" + attributeName + "=" + attributeValue);
            	}
        	}    		
    	}

    	NodeList children = node.getChildNodes();
    	if (children == null) {
            //System.err.println("children == null");
    	} else {
        	for (int i = 0; i <children.getLength(); i++) {
        		Node child = children.item(i);
            	if (child == null) {
                    //System.err.println("child == null " + i);
            	} else if (child.getNodeType() == child.TEXT_NODE) {
            		String textValue = child.getNodeValue();
            		System.err.println(tabs + TAB + textValue.trim());
            	} else {
                	nodeWalk(child, tabs + TAB);            		            			
            	}
        	}    		
    	}

    }
    
    private class UrlString {
    	private boolean parmsBegun = false;
    	private StringBuffer buffer = null;
    	private String parmPrefix = "?";
    	UrlString(String buffer) {
    		this.buffer = new StringBuffer(buffer);
    	}
    	void appendPathinfo(String value) throws Exception {
    		if (parmsBegun) {
    			throw new Exception("no pathinfo after parms");
    		}
    		buffer.append("/" + value);
    	}
    	void appendParm(String name, String value) {
    		parmsBegun = true;
    		buffer.append(parmPrefix + name + "=" + value.replaceAll("=","%7E"));
    		if ("?".equals(parmPrefix)) {
    			parmPrefix = "&";
    		}
    	}
    	public String toString() {
    		return buffer.toString();
    	}
    }

    public static final String getXpath(String inpath, String namespacePrefix) {
    	inpath = inpath.replaceAll("/@", "@@"); //i.e., exclude from next replaceAll
    	inpath = inpath.replaceAll("/\\*", "\\*\\*"); //i.e., exclude from next replaceAll
    	inpath = inpath.replaceAll("/", "/" + namespacePrefix + ":");    
    	inpath = inpath.replaceAll("/" + namespacePrefix + ":/", "//"); //fixup after too aggressive        	
    	inpath = inpath.replaceAll("@@", "/@"); //"
    	inpath = inpath.replaceAll("\\*\\*", "/\\*"); //"
    	//inpath = inpath.replaceAll("@", "@" + namespacePrefix + ":");
    	return inpath;
    }

	public static final String getFedoraXpath(String inpath) {
		return getXpath(inpath, NS_FEDORA_TYPES_PREFIX);
	}
    
    public static final String getXhtmlXpath(String inpath) {
		return getXpath(inpath, NS_XHTML_PREFIX);    	
    }
    public static final String NS_XHTML_PREFIX = "xhtml";
    public static final String NS_XHTML = "http://www.w3.org/1999/xhtml";
    
    private static final Set badPids = new HashSet();
    static {
    	badPids.add("hoo%20doo:%20TheClash"); //unacceptable syntax
    	badPids.add("doowop:667"); //simply not in repository
    }
    
    public void testDescribeRepositoryXML() throws Exception {
    	if (TEST_XML) describeRepository(XML);
    }

    public void testDatastreamDisseminationDemoPidsXML() throws Exception {
    	if (TEST_XML) datastreamDissemination(demoObjects.iterator(), true, XML);
    }

    public void testDatastreamDisseminationBadPidsXML() throws Exception {
    	if (TEST_XML) datastreamDissemination(badPids.iterator(), false, XML);
	}
    
    // no default disseminators return non-XHTML XML, so there are no methods testDisseminationDemoPidsXML() or testDisseminationBadPidsXML()
    
    public void testObjectHistoryDemoObjectsXML() throws Exception {
    	if (TEST_XML) objectHistory(demoObjects.iterator(), true, XML);
    }
    
    public void testObjectHistoryBadPidsXML() throws Exception {
    	if (TEST_XML) objectHistory(badPids.iterator(), false, XML);
    }

    public void testObjectProfileDemoObjectsXML() throws Exception {
    	if (TEST_XML) objectProfile(demoObjects.iterator(), true, XML);
    }

    public void testObjectProfileBadPidsXML() throws Exception {
    	if (TEST_XML) objectProfile(badPids.iterator(), false, XML);
    }

    public void testFindObjectsXML() throws Exception {
    	if (TEST_XML) findObjects(1000000, XML);
    }

    public void testListDatastreamsDemoObjectsXML() throws Exception {
    	if (TEST_XML) listDatastreams(demoObjects.iterator(), true, XML);
    }

    public void testListDatastreamsBadPidsXML() throws Exception {
    	if (TEST_XML) listDatastreams(badPids.iterator(), false, XML);
    }

    public void testListMethodsDemoObjectsXML() throws Exception {
    	if (TEST_XML) listMethods(TestIngestDemoObjects.getDemoObjects(new String[] {"O"}).iterator(), true, XML);
    }    
    
    public void testListMethodsBadPidsXML() throws Exception {
    	if (TEST_XML) listMethods(badPids.iterator(), false, XML);
    }    

    public void testResumeFindObjectsXML() throws Exception {
    	if (TEST_XML) findObjects(10, XML);
    }

    public void testDescribeRepositoryXHTML() throws Exception {
    	if (TEST_XHTML) describeRepository(XHTML);
    }
    
    // no demo XHTML datastreams to test so no methods testDatastreamDisseminationDemoPidsXML() or testDatastreamDisseminationBadPidsXML()

    public void testDisseminationDemoObjectsXHTML() throws Exception {
    	if (TEST_XHTML) dissemination(demoObjects.iterator(), true, XHTML);
    }
    
    public void testDisseminationBadPidsXHTML() throws Exception {
    	if (TEST_XHTML) dissemination(badPids.iterator(), false, XHTML);
    }
        
    public void testFindObjectsXHTML() throws Exception {
    	if (TEST_XHTML) findObjects(1000000, XHTML);
    }
        
    public void testObjectHistoryDemoObjectsXHTML() throws Exception {
    	if (TEST_XHTML) objectHistory(demoObjects.iterator(), true, XHTML);    	
    }

    public void testObjectHistoryBadPidsXHTML() throws Exception {
    	if (TEST_XHTML) objectHistory(badPids.iterator(), false, XHTML);    	
    }

    public void testObjectProfileDemoObjectsXHTML() throws Exception {
    	if (TEST_XHTML) objectProfile(demoObjects.iterator(), true, XHTML);    	
    }

    public void testObjectProfileBadPidsXHTML() throws Exception {
    	if (TEST_XHTML) objectProfile(badPids.iterator(), false, XHTML);    	
    }

    public void testListDatastreamsDemoObjectsXHTML() throws Exception {
    	if (TEST_XHTML) listDatastreams(demoObjects.iterator(), true, XHTML);
    }

    public void testListDatastreamsBadPidsXHTML() throws Exception {
    	if (TEST_XHTML) listDatastreams(badPids.iterator(), false, XHTML);
    }

    public void testListMethodsDemoObjectsXHTML() throws Exception {
    	if (TEST_XHTML) listMethods(TestIngestDemoObjects.getDemoObjects(new String[] {"O"}).iterator(), true, XHTML);
    }    

    public void testListMethodsBadPidsXHTML() throws Exception {
    	if (TEST_XHTML) listMethods(badPids.iterator(), false, XHTML);
    }    

    public void testResumeFindObjectsXHTML() throws Exception {
    	if (TEST_XHTML) findObjects(10, XHTML);
    }

    /** 
     *  methods getXUrl() (for each APIA Lite operation X) can't be static because they contain an instance --inner-- class UrlString.
     *  likewise, methods calling getXUrl() can't be static, because getXUrl() isn't. 
     *  they are otherwise static in use and would be declared static if javac allowed it. 
     */
    
    public final String getUrlForDescribeRepository(boolean xml) {
    	UrlString url = new UrlString("/describe");
    	url.appendParm("xml", Boolean.toString(xml));
    	return url.toString();
    }

    /** 
     *  http://localhost:8080/fedora/get/demo:10/DC?xml=true 
     */
    public final String getUrlForDatastreamDissemination(String pid, String datastream, boolean xml) throws Exception {
    	UrlString url = new UrlString("/get");
    	if (pid != null) {
    		url.appendPathinfo(pid);
    	}    	
    	if (datastream != null) {
    		url.appendPathinfo(datastream);
    	}
    	url.appendParm("xml", Boolean.toString(xml));
    	return url.toString();
    }

    /** 
     *  ?????????????????????
     */
    public final String getUrlForDissemination(String pid, String bDef, String method) throws Exception {
    	UrlString url = new UrlString("/get");
    	if (pid != null) {
    		url.appendPathinfo(pid);
    	}    	
    	if (bDef != null) {
    		url.appendPathinfo(bDef);
    	}
    	if (method != null) {
    		url.appendPathinfo(method);
    	}
    	return url.toString();
    }

    /** 
     *  http://localhost:8080/fedora/get/demo:10?xml=true
     */
    public final String getUrlForObjectProfile(String pid, boolean xml) throws Exception {
    	UrlString url = new UrlString("/get");
    	if (pid != null) {
    		url.appendPathinfo(pid);
    	}
    	url.appendParm("xml", Boolean.toString(xml));
    	return url.toString();
    }    

    /** 
     *  http://localhost:8080/fedora/getObjectHistory/demo:10?xml=true    
     */
    public final String getUrlForObjectHistory(String pid, boolean xml) throws Exception {
    	UrlString url = new UrlString("/getObjectHistory");
    	if (pid != null) {
    		url.appendPathinfo(pid);
    	}
    	url.appendParm("xml", Boolean.toString(xml));
    	return url.toString();
    }    

    public final String getUrlForFindObjects(String query, int maxResults, boolean xml, String sessionToken) {
    	UrlString url = new UrlString("/search");
    	if (query != null) {
    		url.appendParm("query", query);
    	}
    	url.appendParm("pid", "true");    	
    	url.appendParm("maxResults", Integer.toString(maxResults));
    	url.appendParm("xml", Boolean.toString(xml));
    	if (sessionToken != null) {
        	url.appendParm("sessionToken", sessionToken);    		
    	}
    	return url.toString();
    }

    /** 
     *  http://localhost:8080/fedora/listDatastreams/demo:10?xml=true
     */
    public final String getUrlForListDatastreams(String pid, boolean xml) throws Exception {
    	UrlString url = new UrlString("/listDatastreams");
    	if (pid != null) {
    		url.appendPathinfo(pid);
    	}
    	url.appendParm("xml", Boolean.toString(xml));
    	return url.toString();
    }

    /** 
     *  http://localhost:8080/fedora/listMethods/demo:10?xml=true 
     */
    public final String getUrlForListMethods(String pid, boolean xml) throws Exception {
    	UrlString url = new UrlString("/listMethods");
    	if (pid != null) {
    		url.appendPathinfo(pid);
    	}
    	url.appendParm("xml", Boolean.toString(xml));
    	return url.toString();
    }
    
    
    private final void describeRepository(boolean xml) throws Exception {
		String url = getUrlForDescribeRepository(xml);
        Document result = getQueryResult(url);
        //if (DEBUG) nodeWalk (result, "");
        String repositoryName = fcfg.getParameter("repositoryName").getValue();
        if (xml) {
            assertXpathEvaluatesTo(repositoryName, XPATH_XML_DESCRIBE_REPOSITORY_REPOSITORY_NAME, result);
            assertXpathExists(XPATH_XML_DESCRIBE_REPOSITORY_REPOSITORY_BASEURL, result);
            assertXpathExists(XPATH_XML_DESCRIBE_REPOSITORY_REPOSITORY_VERSION, result);
            assertXpathExists(XPATH_XML_DESCRIBE_REPOSITORY_PID_NAMESPACE, result);
            assertXpathExists(XPATH_XML_DESCRIBE_REPOSITORY_PID_DELIMITER, result);
            assertXpathExists(XPATH_XML_DESCRIBE_REPOSITORY_PID_SAMPLE, result);
    		SimpleXpathEngine simpleXpathEngine = new SimpleXpathEngine();
    		int pidsToRetain = Integer.parseInt(simpleXpathEngine.evaluate(XPATH_XML_DESCRIBE_REPOSITORY_COUNT_RETAIN_PIDS, result));
    		assertXpathExists(XPATH_XML_DESCRIBE_REPOSITORY_OAI_NAMESPACE, result);
            assertXpathExists(XPATH_XML_DESCRIBE_REPOSITORY_OAI_DELIMITER, result);
            assertXpathExists(XPATH_XML_DESCRIBE_REPOSITORY_OAI_SAMPLE, result);
            assertXpathExists(XPATH_XML_DESCRIBE_REPOSITORY_SAMPLE_SEARCH_URL, result);
            assertXpathExists(XPATH_XML_DESCRIBE_REPOSITORY_SAMPLE_ACCESS_URL, result);
            assertXpathExists(XPATH_XML_DESCRIBE_REPOSITORY_SAMPLE_OAI_URL, result);
            assertXpathExists(XPATH_XML_DESCRIBE_REPOSITORY_ADMIN_EMAIL, result);        	
        } else {
            assertXpathEvaluatesTo(repositoryName, XPATH_XHTML_DESCRIBE_REPOSITORY_REPOSITORY_NAME, result);
            assertXpathExists(XPATH_XHTML_DESCRIBE_REPOSITORY_REPOSITORY_BASEURL, result);
            assertXpathExists(XPATH_XHTML_DESCRIBE_REPOSITORY_REPOSITORY_VERSION, result);
            assertXpathExists(XPATH_XHTML_DESCRIBE_REPOSITORY_PID_NAMESPACE, result);
            assertXpathExists(XPATH_XHTML_DESCRIBE_REPOSITORY_PID_DELIMITER, result);
            assertXpathExists(XPATH_XHTML_DESCRIBE_REPOSITORY_PID_SAMPLE, result);
    		//TODO:  count test for retain pids in xhtml
            assertXpathExists(XPATH_XHTML_DESCRIBE_REPOSITORY_OAI_NAMESPACE, result);			
            assertXpathExists(XPATH_XHTML_DESCRIBE_REPOSITORY_OAI_DELIMITER, result);
            assertXpathExists(XPATH_XHTML_DESCRIBE_REPOSITORY_OAI_SAMPLE, result);
            assertXpathExists(XPATH_XHTML_DESCRIBE_REPOSITORY_SAMPLE_SEARCH_URL, result);
            assertXpathExists(XPATH_XHTML_DESCRIBE_REPOSITORY_SAMPLE_ACCESS_URL, result);
            assertXpathExists(XPATH_XHTML_DESCRIBE_REPOSITORY_SAMPLE_OAI_URL, result);
            //TODO:  count test for admin emails in xhtml
        }
    }
	//Fedora namespace not declared in result, so these xpaths don't include namespace prefixes
    private static final String XPATH_XML_DESCRIBE_REPOSITORY_REPOSITORY_NAME = "/fedoraRepository/repositoryName"; 
    private static final String XPATH_XML_DESCRIBE_REPOSITORY_REPOSITORY_BASEURL = "/fedoraRepository/repositoryBaseURL";
    private static final String XPATH_XML_DESCRIBE_REPOSITORY_REPOSITORY_VERSION = "/fedoraRepository/repositoryVersion"; 
    private static final String XPATH_XML_DESCRIBE_REPOSITORY_PID_NAMESPACE = "/fedoraRepository/repositoryPID/PID-namespaceIdentifier";
    private static final String XPATH_XML_DESCRIBE_REPOSITORY_PID_DELIMITER = "/fedoraRepository/repositoryPID/PID-delimiter";
    private static final String XPATH_XML_DESCRIBE_REPOSITORY_PID_SAMPLE = "/fedoraRepository/repositoryPID/PID-sample";
    private static final String XPATH_XML_DESCRIBE_REPOSITORY_COUNT_RETAIN_PIDS = "count(/fedoraRepository/repositoryPID/retainPID)";
    private static final String XPATH_XML_DESCRIBE_REPOSITORY_OAI_NAMESPACE = "/fedoraRepository/repositoryOAI-identifier/OAI-namespaceIdentifier";
    private static final String XPATH_XML_DESCRIBE_REPOSITORY_OAI_DELIMITER = "/fedoraRepository/repositoryOAI-identifier/OAI-delimiter";
    private static final String XPATH_XML_DESCRIBE_REPOSITORY_OAI_SAMPLE = "/fedoraRepository/repositoryOAI-identifier/OAI-sample";
    private static final String XPATH_XML_DESCRIBE_REPOSITORY_SAMPLE_SEARCH_URL = "/fedoraRepository/sampleSearch-URL";
    private static final String XPATH_XML_DESCRIBE_REPOSITORY_SAMPLE_ACCESS_URL = "/fedoraRepository/sampleAccess-URL";
    private static final String XPATH_XML_DESCRIBE_REPOSITORY_SAMPLE_OAI_URL = "/fedoraRepository/sampleOAI-URL";
    private static final String XPATH_XML_DESCRIBE_REPOSITORY_ADMIN_EMAIL = "/fedoraRepository/adminEmail";
    private static final String XPATH_XHTML_DESCRIBE_REPOSITORY_REPOSITORY_NAME = getXhtmlXpath("/html/body//font[@id='repositoryName']");
    private static final String XPATH_XHTML_DESCRIBE_REPOSITORY_REPOSITORY_BASEURL = getXhtmlXpath("/html/body//td[@id='repositoryBaseURL']");
    private static final String XPATH_XHTML_DESCRIBE_REPOSITORY_REPOSITORY_VERSION = getXhtmlXpath("/html/body//td[@id='repositoryVersion']"); 
    private static final String XPATH_XHTML_DESCRIBE_REPOSITORY_PID_NAMESPACE = getXhtmlXpath("/html/body//td[@id='PID-namespaceIdentifier']");
    private static final String XPATH_XHTML_DESCRIBE_REPOSITORY_PID_DELIMITER = getXhtmlXpath("/html/body//td[@id='PID-delimiter']");
    private static final String XPATH_XHTML_DESCRIBE_REPOSITORY_PID_SAMPLE = getXhtmlXpath("/html/body//td[@id='PID-sample']");
    private static final String XPATH_XHTML_DESCRIBE_REPOSITORY_OAI_NAMESPACE = getXhtmlXpath("/html/body//td[@id='OAI-namespaceIdentifier']");
    private static final String XPATH_XHTML_DESCRIBE_REPOSITORY_OAI_DELIMITER = getXhtmlXpath("/html/body//td[@id='OAI-delimiter']");
    private static final String XPATH_XHTML_DESCRIBE_REPOSITORY_OAI_SAMPLE = getXhtmlXpath("/html/body//td[@id='OAI-sample']");
    private static final String XPATH_XHTML_DESCRIBE_REPOSITORY_SAMPLE_SEARCH_URL = getXhtmlXpath("/html/body//td[@id='sampleSearch-URL']");
    private static final String XPATH_XHTML_DESCRIBE_REPOSITORY_SAMPLE_ACCESS_URL = getXhtmlXpath("/html/body//td[@id='sampleAccess-URL']");
    private static final String XPATH_XHTML_DESCRIBE_REPOSITORY_SAMPLE_OAI_URL = getXhtmlXpath("/html/body//td[@id='sampleOAI-URL']");

    private void datastreamDissemination(Iterator iterator, boolean shouldWork, boolean xml) throws Exception {
        Document result = null;
        while (iterator.hasNext()) {
        	String pid = (String) iterator.next();
        	if (DEBUG) System.err.println("trying pid=" + pid); System.err.flush();
        	if (DEBUG) nodeWalk (result, "");
        	try {
        		result = getQueryResult(getUrlForDatastreamDissemination(pid, "DC", xml)); // just checking DC
            	if (DEBUG) System.err.println("no exception on pid=" + pid); System.err.flush();
            	System.err.flush();
        	} catch (Exception e) {	  
            	if (DEBUG) {
            		System.err.println("exception on pid=" + pid); 
            		System.err.println(e.getMessage()); 
            		if (e.getCause() != null) {
                		System.err.println(e.getCause().getMessage());             			
            		}
            		System.err.flush();
            	}
        	}
        	if (shouldWork) {
    	        if (xml) {
    		        assertXpathExists(XPATH_XML_DATASTREAM_DISSEMINATION_DC, result);	        	
    	        } else {
		        	//no XHTML datastreams in demo objects
    	        }        		
        	} else {
    	        if (result != null) {
    		        if (xml) {
    			        assertXpathNotExists(XPATH_XML_DATASTREAM_DISSEMINATION_DC, result);	        	
    		        } else {
    		        }
    	        }        		
        	}
        }
    }
    private static final String XPATH_XML_DATASTREAM_DISSEMINATION_DC = "/oai_dc:dc";    
    
    private void dissemination(Iterator iterator, boolean shouldWork, boolean xml) throws Exception {
        Document result = null;
        while (iterator.hasNext()) {
        	String pid = (String) iterator.next();
        	if (DEBUG) System.err.println("trying pid=" + pid); System.err.flush();
        	if (DEBUG) nodeWalk (result, "");
        	try {
        		result = getQueryResult(getUrlForDissemination(pid, "fedora-system:3", "viewDublinCore")); // just checking DC
            	if (DEBUG) System.err.println("no exception on pid=" + pid); System.err.flush();
            	System.err.flush();
        	} catch (Exception e) {	  
            	if (DEBUG) {
            		System.err.println("exception on pid=" + pid); 
            		System.err.println(e.getMessage()); 
            		if (e.getCause() != null) {
                		System.err.println(e.getCause().getMessage());             			
            		}
            		System.err.flush();
            	}
        	}
        	if (shouldWork) {
    	        if (xml) {
    	        	assertXpathExists(XPATH_XHTML_DISSEMINATION_DC, result);	        	
    	        } else {
    	        }        		
        	} else {
    	        if (result != null) {
    		        if (xml) {
    		        	assertXpathNotExists(XPATH_XHTML_DISSEMINATION_DC, result);	        	
    		        } else {
    		        }
    	        }        		
        	}
        }
    }
    private static final String XPATH_XHTML_DISSEMINATION_DC = "/oai_dc:dc"; //<<<<<<<<<<<<<<<<<<<

    public void objectProfile(Iterator iterator, boolean shouldWork, boolean xml) throws Exception {
        Document result = null;
        while (iterator.hasNext()) {
        	String pid = (String) iterator.next();
        	if (DEBUG) System.err.println("trying pid=" + pid); System.err.flush();
        	if (DEBUG) nodeWalk (result, "");
        	try {
        		result = getQueryResult(getUrlForObjectProfile(pid, xml));
            	if (DEBUG) System.err.println("no exception on pid=" + pid); System.err.flush();
            	System.err.flush();
        	} catch (Exception e) {	  
            	if (DEBUG) {
            		System.err.println("exception on pid=" + pid); 
            		System.err.println(e.getMessage()); 
            		if (e.getCause() != null) {
                		System.err.println(e.getCause().getMessage());             			
            		}
            		System.err.flush();
            	}
        	}
        	if (shouldWork) {
    	        if (xml) {	
    		        assertXpathExists(XPATH_XML_OBJECT_PROFILE_LABEL, result);	
    		        assertXpathExists(XPATH_XML_OBJECT_PROFILE_CONTENT_MODEL, result);	
    		        assertXpathExists(XPATH_XML_OBJECT_PROFILE_CREATE_DATE, result);	
    		        assertXpathExists(XPATH_XML_OBJECT_PROFILE_LASTMOD_DATE, result);	
    		        assertXpathExists(XPATH_XML_OBJECT_PROFILE_OBJTYPE, result);	
    		        assertXpathExists(XPATH_XML_OBJECT_PROFILE_DISS_INDEX_VIEW_URL, result);	
    		        assertXpathExists(XPATH_XML_OBJECT_PROFILE_ITEM_INDEX_VIEW_URL, result);
    	            assertXpathEvaluatesTo(pid, XPATH_XML_OBJECT_PROFILE_PID, result);	
    	        } else {
    		        assertXpathExists(XPATH_XHTML_OBJECT_PROFILE_LABEL, result);	
    		        assertXpathExists(XPATH_XHTML_OBJECT_PROFILE_CONTENT_MODEL, result);	
    		        assertXpathExists(XPATH_XHTML_OBJECT_PROFILE_CREATE_DATE, result);	
    		        assertXpathExists(XPATH_XHTML_OBJECT_PROFILE_LASTMOD_DATE, result);	
    		        assertXpathExists(XPATH_XHTML_OBJECT_PROFILE_OBJTYPE, result);	
    		        assertXpathExists(XPATH_XHTML_OBJECT_PROFILE_DISS_INDEX_VIEW_URL, result);	
    		        assertXpathExists(XPATH_XHTML_OBJECT_PROFILE_ITEM_INDEX_VIEW_URL, result);
    	            assertXpathEvaluatesTo(pid, XPATH_XHTML_OBJECT_PROFILE_PID, result);		        

    	            assertXpathEvaluatesTo("Object Profile HTML Presentation", XPATH_XHTML_OBJECT_PROFILE_HEAD_TITLE, result);
    	            assertXpathEvaluatesTo("Object Profile View", XPATH_XHTML_OBJECT_PROFILE_BODY_TITLE, result);	        	
    	        	
    	        }        		
        	} else {
    	        if (result != null) {
    		        if (xml) {
    		        	try {
    		        		assertXpathNotExists(XPATH_XML_OBJECT_PROFILE_PID, result);
    		        	} catch (Exception e) {
    	    	            assertXpathEvaluatesTo("", XPATH_XML_OBJECT_PROFILE_PID, result);
    		        	}
    		        } else {
    		        	try {
    		        		assertXpathNotExists(XPATH_XHTML_OBJECT_PROFILE_PID, result);
    		        	} catch (Exception e) {
    	    	            assertXpathEvaluatesTo("", XPATH_XHTML_OBJECT_PROFILE_PID, result);
    		        	}
    		        }
    	        }        		
        	}
        }    	
    }
    private static final String XPATH_XML_OBJECT_PROFILE_PID = "/objectProfile/@pid";
    private static final String XPATH_XML_OBJECT_PROFILE_LABEL = "/objectProfile/objLabel";
    private static final String XPATH_XML_OBJECT_PROFILE_CONTENT_MODEL = "/objectProfile/objContentModel";    
    private static final String XPATH_XML_OBJECT_PROFILE_CREATE_DATE = "/objectProfile/objCreateDate";
    private static final String XPATH_XML_OBJECT_PROFILE_LASTMOD_DATE = "/objectProfile/objLastModDate";
    private static final String XPATH_XML_OBJECT_PROFILE_OBJTYPE = "/objectProfile/objType";
    private static final String XPATH_XML_OBJECT_PROFILE_DISS_INDEX_VIEW_URL = "/objectProfile/objDissIndexViewURL";
    private static final String XPATH_XML_OBJECT_PROFILE_ITEM_INDEX_VIEW_URL = "/objectProfile/objItemIndexViewURL";    
    private static final String XPATH_XHTML_OBJECT_PROFILE_PID = getXhtmlXpath("/html/body//td[@id='pid']");
    private static final String XPATH_XHTML_OBJECT_PROFILE_LABEL = getXhtmlXpath("/html/body//td[@id='objLabel']");
    private static final String XPATH_XHTML_OBJECT_PROFILE_CONTENT_MODEL = getXhtmlXpath("/html/body//td[@id='objContentModel']");
    private static final String XPATH_XHTML_OBJECT_PROFILE_CREATE_DATE = getXhtmlXpath("/html/body//td[@id='objCreateDate']");
    private static final String XPATH_XHTML_OBJECT_PROFILE_LASTMOD_DATE = getXhtmlXpath("/html/body//td[@id='objLastModDate']");
    private static final String XPATH_XHTML_OBJECT_PROFILE_OBJTYPE = getXhtmlXpath("/html/body//td[@id='objType']");
    private static final String XPATH_XHTML_OBJECT_PROFILE_DISS_INDEX_VIEW_URL = getXhtmlXpath("/html/body//a[@id='objDissIndexViewURL']/@href");
    private static final String XPATH_XHTML_OBJECT_PROFILE_ITEM_INDEX_VIEW_URL = getXhtmlXpath("/html/body//a[@id='objItemIndexViewURL']/@href");
    private static final String XPATH_XHTML_OBJECT_PROFILE_HEAD_TITLE = getXhtmlXpath("/html/head/title");
    private static final String XPATH_XHTML_OBJECT_PROFILE_BODY_TITLE = getXhtmlXpath("/html/body//h3");

    private void objectHistory(Iterator iterator, boolean shouldWork, boolean xml) throws Exception {
        Document result = null;
        while (iterator.hasNext()) {
        	String pid = (String) iterator.next();
        	if (DEBUG) System.err.println("trying pid=" + pid); System.err.flush();
        	if (DEBUG) nodeWalk (result, "");
        	try {
        		result = getQueryResult(getUrlForObjectHistory(pid, xml));
            	if (DEBUG) System.err.println("no exception on pid=" + pid); System.err.flush();
            	System.err.flush();
        	} catch (Exception e) {	  
            	if (DEBUG) {
            		System.err.println("exception on pid=" + pid); 
            		System.err.println(e.getMessage()); 
            		if (e.getCause() != null) {
                		System.err.println(e.getCause().getMessage());             			
            		}
            		System.err.flush();
            	}
        	}
        	if (shouldWork) {
    	        if (xml) {
    		        assertXpathEvaluatesTo(pid, XPATH_XML_OBJECT_HISTORY_PID, result);	        	
    	            assertXpathExists(XPATH_XML_OBJECT_HISTORY_CHANGE_DATE, result);		        	
    	        } else {
    		        assertXpathEvaluatesTo(pid, XPATH_XHTML_OBJECT_HISTORY_PID, result);
    	            assertXpathEvaluatesTo("Object History HTML Presentation", XPATH_XHTML_OBJECT_HISTORY_HEAD_TITLE, result);
    	            assertXpathEvaluatesTo("Object History View", XPATH_XHTML_OBJECT_HISTORY_BODY_TITLE, result);	        	
    	        }        		
        	} else {
    	        if (result != null) {    	        	
    		        if (xml) {
    		        	try {
    		        		assertXpathNotExists(XPATH_XML_OBJECT_HISTORY_PID, result);
    		        	} catch (Exception e) {
    	    	            assertXpathEvaluatesTo("", XPATH_XML_OBJECT_HISTORY_PID, result);
    		        	}
    		        } else {
    		        	try {
    		        		assertXpathNotExists(XPATH_XHTML_OBJECT_HISTORY_PID, result);
    		        	} catch (Exception e) {
    	    	            assertXpathEvaluatesTo("", XPATH_XHTML_OBJECT_HISTORY_PID, result);
    		        	}
    		        }
    	        }        		
        	}
        }    	
    }
    private static final String XPATH_XML_OBJECT_HISTORY_PID = "/fedoraObjectHistory/@pid";
    private static final String XPATH_XML_OBJECT_HISTORY_CHANGE_DATE = "/fedoraObjectHistory/objectChangeDate";
    private static final String XPATH_XHTML_OBJECT_HISTORY_PID = getXhtmlXpath("/html/body//font[@id='pid']");
    private static final String XPATH_XHTML_OBJECT_HISTORY_HEAD_TITLE = getXhtmlXpath("/html/head/title");
    private static final String XPATH_XHTML_OBJECT_HISTORY_BODY_TITLE = getXhtmlXpath("/html/body//h3");    

    
    private final void findObjects(int maxResults, boolean xml) throws Exception {   	
        String sessionToken = null;
		int hitsOnThisPage = -1;
		boolean again = true;
		int cursorShouldBe = 0; //for xml results only (cursor is not included in xhtml results)
		int hitsOnAllPages = 0;

		while (again) { 	       
			if (DEBUG) System.err.println("0"); System.err.flush();
			String url = getUrlForFindObjects("pid=demo:*", maxResults, xml, sessionToken);
			Document result = getQueryResult(url);
	        //if (DEBUG) nodeWalk (result, "");
			SimpleXpathEngine simpleXpathEngine = new SimpleXpathEngine();
			if (DEBUG) System.err.println("A"); System.err.flush();
			if (xml) {
				if (DEBUG) System.err.println("B"); System.err.flush();
				if (DEBUG) System.err.println("b4 getting sessionToken");  System.err.flush();
				if (DEBUG) System.err.println("XPATH_XML_FIND_OBJECTS_SESSION_TOKEN="+XPATH_XML_FIND_OBJECTS_SESSION_TOKEN);  System.err.flush();
		        sessionToken = simpleXpathEngine.evaluate(XPATH_XML_FIND_OBJECTS_SESSION_TOKEN, result);
				if (DEBUG) System.err.println("b4 getting hitsOnThisPage");
				hitsOnThisPage = Integer.parseInt(simpleXpathEngine.evaluate(XPATH_XML_FIND_OBJECTS_COUNT_PIDS, result));
				if (DEBUG) System.err.println("b4 getting hits");
			} else {
				if (DEBUG) System.err.println("C1"); System.err.flush();
				if (DEBUG) System.err.println("b4 getting sessionToken");
		        sessionToken = simpleXpathEngine.evaluate(XPATH_XHTML_FIND_OBJECTS_SESSION_TOKEN, result);
		        if (DEBUG) System.err.println("C2"); System.err.flush();
				if (DEBUG) System.err.println("b4 getting hitsOnThisPage");
				hitsOnThisPage = Integer.parseInt(simpleXpathEngine.evaluate(XPATH_XHTML_FIND_OBJECTS_COUNT_PIDS, result));
				if (DEBUG) System.err.println("C3"); System.err.flush();
				if (DEBUG) System.err.println("b4 getting hits");
				if (DEBUG) System.err.println("C4"); System.err.flush();
			}
			if (DEBUG) System.err.println("hitsOnThisPage=" + hitsOnThisPage);
	        if (DEBUG) System.err.println("sessionToken=" + sessionToken);
	        again = (sessionToken != null) && ! "".equals(sessionToken);
	        
        	if (xml && again) {
            	String cursor = simpleXpathEngine.evaluate(XPATH_XML_FIND_OBJECTS_CURSOR, result);
            	if (DEBUG) System.err.println("cursor=" + cursor);
        		assertEquals(cursorShouldBe, Integer.parseInt(cursor)); //&&again TO WORK AROUND A PROBABLE BUG IN SERVER CODE.
        	}
	        
        	assertTrue(hitsOnThisPage <= maxResults);
			if (hitsOnThisPage < maxResults) {
				assertTrue((sessionToken == null) || "".equals(sessionToken));
			}
			
			if (DEBUG) System.err.println("getNDemoObjects()" + demoObjects.size());
			
			assertTrue (hitsOnThisPage <= demoObjects.size());

			hitsOnAllPages += hitsOnThisPage;
			assertTrue(hitsOnAllPages <= demoObjects.size());
			assertEquals (hitsOnAllPages == demoObjects.size(), (sessionToken == null) || "".equals(sessionToken)); 
			assertEquals(hitsOnAllPages < demoObjects.size(), (sessionToken != null) && ! "".equals(sessionToken)); 

			
	        cursorShouldBe += maxResults;
		}
		assertTrue(hitsOnAllPages == demoObjects.size());
    }    
    private static final String XPATH_XML_FIND_OBJECTS_SESSION_TOKEN = getFedoraXpath("/result/listSession/token");
    private static final String XPATH_XML_FIND_OBJECTS_CURSOR = getFedoraXpath("/result/listSession/cursor");
    private static final String XPATH_XML_FIND_OBJECTS_COUNT_PIDS = getFedoraXpath("count(/result/resultList/objectFields/pid)");
    private static final String XPATH_XML_FIND_OBJECTS_PID = getFedoraXpath("/result/resultList/objectFields/pid");
    private static final String XPATH_XHTML_FIND_OBJECTS_SESSION_TOKEN = getXhtmlXpath("/html/body//form//input[@name=\"sessionToken\"]/@value");
    private static final String XPATH_XHTML_FIND_OBJECTS_COUNT_PIDS = getXhtmlXpath("count(/html/body//form//a[@href])");
    private static final String XPATH_XHTML_FIND_OBJECTS_PID = getXhtmlXpath("/html/body//form//a[@href]");

    private final void listDatastreams(Iterator iterator, boolean shouldWork, boolean xml) throws Exception {
        Document result = null;
        while (iterator.hasNext()) {
        	String pid = (String) iterator.next();
        	if (DEBUG) System.err.println("trying pid=" + pid); System.err.flush();
        	if (DEBUG) nodeWalk (result, "");
        	try {
        		result = getQueryResult(getUrlForListDatastreams(pid, xml));
            	if (DEBUG) System.err.println("no exception on pid=" + pid); System.err.flush();
            	System.err.flush();
        	} catch (Exception e) {	  
            	if (DEBUG) {
            		System.err.println("exception on pid=" + pid); 
            		System.err.println(e.getMessage()); 
            		if (e.getCause() != null) {
                		System.err.println(e.getCause().getMessage());             			
            		}
            		System.err.flush();
            	}
        	}
        	if (shouldWork) {
    	        if (xml) {
    		        assertXpathExists(XPATH_XML_LIST_DATASTREAMS_OBJECT_DATASTREAMS, result);    	        	
    	        } else {
    		        assertXpathExists(XPATH_XHTML_LIST_DATASTREAMS_OBJECT_DATASTREAMS, result);    	        	
    	        }        		
        	} else {
    	        if (result != null) {
        	        if (xml) {
        		        assertXpathNotExists(XPATH_XML_LIST_DATASTREAMS_OBJECT_DATASTREAMS, result);    	        	
        	        } else {
        		        assertXpathNotExists(XPATH_XHTML_LIST_DATASTREAMS_OBJECT_DATASTREAMS, result);    	        	
        	        }        		
    	        }        		
        	}
        }    	
    }
    private static final String XPATH_XML_LIST_DATASTREAMS_OBJECT_DATASTREAMS = "/objectDatastreams"; 
    private static final String XPATH_XHTML_LIST_DATASTREAMS_OBJECT_DATASTREAMS = getXhtmlXpath("");        
    private void listMethods(Iterator iterator, boolean shouldWork, boolean xml) throws Exception {
        Document result = null;
        while (iterator.hasNext()) {
        	String pid = (String) iterator.next();
        	if (DEBUG) System.err.println("trying pid=" + pid); System.err.flush();
        	if (DEBUG) nodeWalk (result, "");
        	try {
        		result = getQueryResult(getUrlForListMethods(pid, xml));
            	if (DEBUG) System.err.println("no exception on pid=" + pid); System.err.flush();
            	System.err.flush();
        	} catch (Exception e) {	  
            	if (DEBUG) {
            		System.err.println("exception on pid=" + pid); 
            		System.err.println(e.getMessage()); 
            		if (e.getCause() != null) {
                		System.err.println(e.getCause().getMessage());             			
            		}
            		System.err.flush();
            	}
        	}
        	if (shouldWork) {
    	        if (xml) {
    		        assertXpathExists(XPATH_XML_LIST_METHODS_OBJECT_METHODS, result);    	        	
    	        } else {
    		        assertXpathExists(XPATH_XHTML_LIST_METHODS_OBJECT_METHODS, result);    	        	
    	        }        		
        	} else {
    	        if (result != null) {
        	        if (xml) {
        		        assertXpathNotExists(XPATH_XML_LIST_METHODS_OBJECT_METHODS, result);    	        	
        	        } else {
        		        assertXpathNotExists(XPATH_XHTML_LIST_METHODS_OBJECT_METHODS, result);    	        	
        	        }        		
    	        }        		
        	}
        }    	    	
    }
    private static final String XPATH_XML_LIST_METHODS_OBJECT_METHODS = "/objectMethods"; 
    private static final String XPATH_XHTML_LIST_METHODS_OBJECT_METHODS = getXhtmlXpath("");

    public static void main(String[] args) {
        junit.textui.TestRunner.run(TestAPIALite.class);
    }
}

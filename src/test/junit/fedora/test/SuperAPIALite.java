package fedora.test;  

import java.io.InputStream;

import org.custommonkey.xmlunit.SimpleXpathEngine;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Test of API-A-Lite using demo objects
 * 
 * @author Bill Niebel 
 */
public abstract class SuperAPIALite extends IterableTest {
    //protected static DocumentBuilderFactory factory;
    //protected static DocumentBuilder builder;
    //protected static ServerConfiguration fcfg;
    //protected static FedoraClient client;
    //protected static Set demoObjects;
    
    public SuperAPIALite() {
    }
    
    private Document getQueryResult(String location) throws Exception {
        InputStream is = client.get(getBaseURL() + location, true, true);
        return builder.parse(is);
    }
    
    protected static final boolean DEBUG = false;
    protected static final boolean VERBOSE = false;    
    public static final boolean XML = true;
    public static final boolean XHTML = false;
    
    protected static final boolean TEST_XML = true;  //this is mainly to mask out tests of XML output during development
    protected static final boolean TEST_XHTML = false;  //this is mainly to mask out tests of XHTML output during development
    
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
            	} else if (child.getNodeType() == Node.TEXT_NODE) {
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
    //public static final String NS_XHTML_PREFIX = "xhtml";
    
    //public static final String NS_XHTML = "http://www.w3.org/1999/xhtml";
    
    
    /*
    protected static final Set badPids = new HashSet();
    static {
    	badPids.add("hoo%20doo:%20TheClash"); //unacceptable syntax
    }
    
    protected static final Set fewPids = new HashSet();
    static {
    	fewPids.add("demo:10");
    }
    
    protected static final Set missingPids = new HashSet();
    static {
    	missingPids.add("doowop:667"); //simply not in repository
    }
    */

    /** 
     *  http://localhost:8080/fedora/get/demo:10/DC?xml=true 
     */
    /*
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
    */

    /** 
     *  ?????????????????????
     */
    /*
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
*/
  

    /** 
     *  http://localhost:8080/fedora/getObjectHistory/demo:10?xml=true    
     */
    /*
    public final String getUrlForObjectHistory(String pid, boolean xml) throws Exception {
    	UrlString url = new UrlString("/getObjectHistory");
    	if (pid != null) {
    		url.appendPathinfo(pid);
    	}
    	url.appendParm("xml", Boolean.toString(xml));
    	return url.toString();
    }    
    */



    /** 
     *  http://localhost:8080/fedora/listDatastreams/demo:10?xml=true
     */
    /*
    public final String getUrlForListDatastreams(String pid, boolean xml) throws Exception {
    	UrlString url = new UrlString("/listDatastreams");
    	if (pid != null) {
    		url.appendPathinfo(pid);
    	}
    	url.appendParm("xml", Boolean.toString(xml));
    	return url.toString();
    }
*/

/*
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
    */
/*    
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
*/
    /*
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

    
    */
    

/*
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
  */  
    
    
    



}

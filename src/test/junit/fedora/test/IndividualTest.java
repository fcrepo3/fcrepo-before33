package fedora.test;  

import org.custommonkey.xmlunit.SimpleXpathEngine;
import org.w3c.dom.Document;

/**
 * @author Bill Niebel 
 */
public abstract class IndividualTest extends FedoraTestCase {
	
	private boolean xml = true;
	private boolean xhtml = false;
	protected boolean again = true; //prime initial test; this is reset on each test
	protected boolean repeating = false;
	
	public final boolean again() {
		return repeating && again;
	}
	
	public final boolean xml() {
		return xml;
	}
	
	public IndividualTest(boolean xml, boolean xhtml) {
		this.xml = xml;
		this.xhtml = xhtml;
	}
	
	SimpleXpathEngine simpleXpathEngine = new SimpleXpathEngine();
	
    protected class UrlString {
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
    
    public static final String getXhtmlXpath(String inpath) {
		return getXpath(inpath, NS_XHTML_PREFIX);    	
    }
    public static final String NS_XHTML_PREFIX = "xhtml";
    
	public static final String getFedoraXpath(String inpath) {
		return getXpath(inpath, NS_FEDORA_TYPES_PREFIX);
	}
	
    public abstract String getUrl(boolean xml) throws Exception;
    
    public abstract void checkResultsXml(Document result) throws Exception;

    public void checkResultsXmlElse(Document result) throws Exception {
    }

    public abstract void checkResultsXhtml(Document result) throws Exception;

    public void checkResultsXhtmlElse(Document result) throws Exception {
    }

    public void checkResults() throws Exception {        
    }
    
    public void checkResultsElse() throws Exception {
    	again = false;
    }
    


}

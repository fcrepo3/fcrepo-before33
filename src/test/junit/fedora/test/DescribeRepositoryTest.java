package fedora.test;  

import org.w3c.dom.Document;

/**
 * @author Bill Niebel 
 */

public class DescribeRepositoryTest extends IndividualTest {
	
	String repositoryName = "";
	
	public DescribeRepositoryTest(String repositoryName, boolean xml) {
		super(xml, false);
		this.repositoryName = repositoryName;
	}
	
    public String getUrl(boolean xml) throws Exception {
    	UrlString url = new UrlString("/describe");
    	url.appendParm("xml", Boolean.toString(xml));
    	return url.toString();    	
    }
    
    public final void checkResultsXml(Document result) throws Exception {
        assertXpathEvaluatesTo(repositoryName, XPATH_XML_DESCRIBE_REPOSITORY_REPOSITORY_NAME, result);
        assertXpathExists(XPATH_XML_DESCRIBE_REPOSITORY_REPOSITORY_BASEURL, result);
        assertXpathExists(XPATH_XML_DESCRIBE_REPOSITORY_REPOSITORY_VERSION, result);
        assertXpathExists(XPATH_XML_DESCRIBE_REPOSITORY_PID_NAMESPACE, result);
        assertXpathExists(XPATH_XML_DESCRIBE_REPOSITORY_PID_DELIMITER, result);
        assertXpathExists(XPATH_XML_DESCRIBE_REPOSITORY_PID_SAMPLE, result);
		//SimpleXpathEngine simpleXpathEngine = new SimpleXpathEngine();
		int pidsToRetain = Integer.parseInt(simpleXpathEngine.evaluate(XPATH_XML_DESCRIBE_REPOSITORY_COUNT_RETAIN_PIDS, result));
		assertXpathExists(XPATH_XML_DESCRIBE_REPOSITORY_OAI_NAMESPACE, result);
        assertXpathExists(XPATH_XML_DESCRIBE_REPOSITORY_OAI_DELIMITER, result);
        assertXpathExists(XPATH_XML_DESCRIBE_REPOSITORY_OAI_SAMPLE, result);
        assertXpathExists(XPATH_XML_DESCRIBE_REPOSITORY_SAMPLE_SEARCH_URL, result);
        assertXpathExists(XPATH_XML_DESCRIBE_REPOSITORY_SAMPLE_ACCESS_URL, result);
        assertXpathExists(XPATH_XML_DESCRIBE_REPOSITORY_SAMPLE_OAI_URL, result);
        assertXpathExists(XPATH_XML_DESCRIBE_REPOSITORY_ADMIN_EMAIL, result);        	    	
    }

    public final void checkResultsXhtml(Document result) throws Exception {
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

		
}



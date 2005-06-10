package fedora.test.integration;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Iterator;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import junit.extensions.TestSetup;
import junit.framework.Test;
import junit.framework.TestSuite;

import org.custommonkey.xmlunit.HTMLDocumentBuilder;
import org.custommonkey.xmlunit.SimpleXpathEngine;
import org.custommonkey.xmlunit.TolerantSaxDocumentBuilder;
import org.custommonkey.xmlunit.XMLUnit;
import org.w3c.dom.Document;

import fedora.client.FedoraClient;
import fedora.client.search.SearchResultParser;
import fedora.server.config.ServerConfiguration;
import fedora.test.FedoraServerTestCase;
import fedora.test.FedoraServerTestSetup;
import fedora.utilities.ExecUtility;

/**
 * Tests fedora-ingest-demos.
 * 
 * Usage: 
 *   fedora-ingest-demos <hostname> <port> <username> <password> <protocol>
 * @author Edwin Shin
 */
public class TestIngestDemoObjects extends FedoraServerTestCase {
    private String baseURL;
    private FedoraClient client;
    public static String[] demoObjects = {
            "demo:1", "demo:2", "demo:3", "demo:4", "demo:6", 
            "demo:7", "demo:8", "demo:9", "demo:10", "demo:11", "demo:12", 
            "demo:13", "demo:15", "demo:16", "demo:17",
            "demo:19", "demo:20", "demo:21", "demo:22", "demo:25", "demo:26", 
            "demo:27", "demo:28", "demo:30", "demo:31", 
            "demo:Collection", "demo:DualResImage", 
            "demo:DualResImageCollection", 
            "demo:SmileyBeerGlass", 
            "demo:SmileyBucket", "demo:SmileyDinnerware", 
            "demo:SmileyEarring", "demo:SmileyKeychain", 
            "demo:SmileyNightlight", "demo:SmileyPens", 
            "demo:SmileyShortRoundCup", "demo:SmileyStuff", 
            "demo:SmileyTallRoundCup", "demo:SmileyToiletBrush", 
            "demo:SmileyWastebasket"
    };
    private DocumentBuilderFactory factory;
    private DocumentBuilder builder;
    private ServerConfiguration fcfg;
    
    public static Test suite() {
        TestSuite suite = new TestSuite(TestIngestDemoObjects.class);
        TestSetup wrapper = new FedoraServerTestSetup(suite) {
            public void setUp() throws Exception {
                ingestDemoObjects();
            }
            public void tearDown() throws Exception {
                purgeDemoObjects();
            }
        };
        return new FedoraServerTestSetup(wrapper);
    }
    
    public void setUp() throws Exception {
        factory = DocumentBuilderFactory.newInstance();
        builder = factory.newDocumentBuilder();
        fcfg = getServerConfiguration();
        SimpleXpathEngine.registerNamespace("oai_dc", "http://www.openarchives.org/OAI/2.0/oai_dc/");
        SimpleXpathEngine.registerNamespace(NS_FEDORA_TYPES_PREFIX, NS_FEDORA_TYPES);
        SimpleXpathEngine.registerNamespace("demo", "http://example.org/ns#demo");
    }
    
    public void tearDown() throws Exception {
        purgeDemoObjects();
        SimpleXpathEngine.clearNamespaces();
    }
    
    public void testIngestDemoObjects() throws Exception {        
        // set up the client for testing
        baseURL = getBaseURL();
        client = new FedoraClient(baseURL, getUsername(), getPassword());
        
        // check that demo objects were ingested
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        InputStream in;
        
        for (int i = 0; i < demoObjects.length; i++) {
            System.out.println("Checking for " + demoObjects[i]);
            in = client.get(baseURL + "/get/" + demoObjects[i] + "?xml=true", true);
            Document result = builder.parse(in);
            assertXpathExists("/objectProfile", result);
        }
    }
    
    public static void ingestDemoObjects() {        
        ExecUtility.execCommandLineUtility(FEDORA_HOME + "/client/bin/fedora-ingest-demos " + 
                getHost() + " " + getPort() + " " + getUsername() + " " + 
                getPassword() + " " + getProtocol());
    }
    
    public static void purgeDemoObjects() throws Exception {
        String[] fTypes = {"O", "M", "D"};
        String baseURL = getBaseURL();
        FedoraClient client = new FedoraClient(baseURL, getUsername(), getPassword());
        InputStream queryResult;
        
        String cmd = FEDORA_HOME + "/client/bin/fedora-purge " + 
                     getHost() + ":" + getPort() + " " + getUsername() + " " + 
                     getPassword();
        
        for (int i = 0; i < fTypes.length; i++) {
            queryResult = client.get(baseURL + "/search?query=pid~demo:*%20fType=" +
            		                 fTypes[i] + "&maxResults=1000&pid=true&xml=true", 
            		                 true);
            SearchResultParser parser = new SearchResultParser(queryResult);
            Set pids = parser.getPIDs();
            Iterator it = pids.iterator();
            while (it.hasNext()) {
                ExecUtility.execCommandLineUtility(cmd + " " + 
                        (String)it.next() + " " + getProtocol() + " for testing");
            }
        }
    }
    
    private void doViewItemIndex(String pid) throws Exception {
        InputStream is = client.get(baseURL + "/get/" + pid + "/fedora-system:3/viewItemIndex", true);
        Document result;
        TolerantSaxDocumentBuilder tolerantSaxDocumentBuilder = new TolerantSaxDocumentBuilder(XMLUnit.getTestParser());
        HTMLDocumentBuilder htmlDocumentBuilder = new HTMLDocumentBuilder(tolerantSaxDocumentBuilder);
        result = htmlDocumentBuilder.parse(new BufferedReader(new InputStreamReader(is)));
        
        
        //assertXpathEvaluatesTo("Item One", "/html/body//li[@id='1']", result);
    }
    
    public static void main(String[] args) {
        junit.textui.TestRunner.run(TestIngestDemoObjects.class);
    }
}

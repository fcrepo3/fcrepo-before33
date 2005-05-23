package fedora.test.integration;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.custommonkey.xmlunit.HTMLDocumentBuilder;
import org.custommonkey.xmlunit.TolerantSaxDocumentBuilder;
import org.custommonkey.xmlunit.XMLUnit;
import org.w3c.dom.Document;

import fedora.client.FedoraClient;
import fedora.server.config.ServerConfiguration;
import fedora.test.FedoraServerTestCase;
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
            "demo:1", "demo:2", "demo:3", "demo:4", "demo:5", "demo:6", 
            "demo:7", "demo:8", "demo:9", "demo:10", "demo:11", "demo:12", 
            "demo:13", "demo:14", "demo:15", "demo:16", "demo:17", "demo:18",
            "demo:19", "demo:20", "demo:21", "demo:22", "demo:25", "demo:26", 
            "demo:27", "demo:28", "demo:29", "demo:30", "demo:31", 
            "demo:Collection", "demo:DualResImage", 
            "demo:DualResImageCollection"
    };
    private DocumentBuilderFactory factory;
    private DocumentBuilder builder;
    private ServerConfiguration fcfg;
    
    public void setUp() throws Exception {
        super.setUp();
        ingestDemoObjects();
        
        factory = DocumentBuilderFactory.newInstance();
        builder = factory.newDocumentBuilder();
        fcfg = getServerConfiguration();
        
    }
    
    public void tearDown() throws Exception {
        super.tearDown();
        purgeDemoObjects();
    }
    
    public void testIngestDemoObjects() throws Exception {        
        // set up the client for testing
        baseURL = getBaseURL();
        client = new FedoraClient(baseURL, getUsername(), getPassword());
        
        // check that demo objects were ingested
        InputStream in;
        for (int i = 0; i < demoObjects.length; i++) {
            System.out.println("Checking for " + demoObjects[i]);
            in = client.get(baseURL + "/get/" + demoObjects[i], true);
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            StringBuffer buf = new StringBuffer();
            String line = reader.readLine();
            while (line != null) {
                buf.append(line + "\n");
                line = reader.readLine();
            }
            //System.out.print(buf.toString());
            if (in != null) in.close();
        }
    }
    
    public static void ingestDemoObjects() {
        ExecUtility.exec(getFedoraHome() + "/client/bin/fedora-ingest-demos " + 
                         getHost() + " " + getPort() + " " + getUsername() + " " + 
                         getPassword() + " " + getProtocol());
    }
    
    public static void purgeDemoObjects() {
        // FIXME
        System.out.println("** TODO **: purging demo objects");
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

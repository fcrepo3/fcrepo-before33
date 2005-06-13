package fedora.test.integration;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashSet;
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
import fedora.client.utility.ingest.Ingest;
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
    private DocumentBuilderFactory factory;
    private DocumentBuilder builder;
    private ServerConfiguration fcfg;
    
    public static Test suite() {
        TestSuite suite = new TestSuite(TestIngestDemoObjects.class);
        TestSetup wrapper = new TestSetup(suite) {
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
        File[] demoDirs = {
                new File(FEDORA_HOME + "/client/demo/foxml/local-server-demos"),
                new File(FEDORA_HOME + "/client/demo/foxml/open-server-demos")
        };
        Set demoObjectFiles = new HashSet();
        for (int i = 0; i < demoDirs.length; i++) {
            demoObjectFiles.addAll(Ingest.getFiles(demoDirs[i], "FedoraBDefObject"));
            demoObjectFiles.addAll(Ingest.getFiles(demoDirs[i], "FedoraBMechObject"));
            demoObjectFiles.addAll(Ingest.getFiles(demoDirs[i], "FedoraObject"));
        }
        Set repositoryDemoObjects = getDemoObjects(null);
        
        // simple test to see if the count of demo object files matches the 
        // count from an APIA-Lite search
        assertEquals(demoObjectFiles.size(), repositoryDemoObjects.size());
        
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        InputStream in;
        
        Iterator it = repositoryDemoObjects.iterator();
        while (it.hasNext()) {
            String pid = (String)it.next();
            System.out.println("Checking for " + pid);
            in = client.get(baseURL + "/get/" + pid + "?xml=true", true);
            Document result = builder.parse(in);
            // simple test of the objects in the repo
            assertXpathExists("/objectProfile", result);
        }
    }
    
    public static void ingestDemoObjects() {        
        ExecUtility.execCommandLineUtility(FEDORA_HOME + "/client/bin/fedora-ingest-demos " + 
                getHost() + " " + getPort() + " " + getUsername() + " " + 
                getPassword() + " " + getProtocol());
    }
    
    /**
     * Gets the PIDs of objects of the specified type in the "demo" pid 
     * namespace that are in the repository
     * @param fTypes any combination of O, D, or M
     * @return 
     * @throws Exception
     */
    public static Set getDemoObjects(String[] fTypes) throws Exception {
        if (fTypes == null || fTypes.length == 0) {
            fTypes = new String[] {"O", "M", "D"};
        }
        String baseURL = getBaseURL();
        FedoraClient client = new FedoraClient(baseURL, getUsername(), getPassword());
        InputStream queryResult;
        Set pids = new HashSet();
        for (int i = 0; i < fTypes.length; i++) {
            queryResult = client.get(baseURL + "/search?query=pid~demo:*%20fType=" +
            		                 fTypes[i] + "&maxResults=1000&pid=true&xml=true", 
            		                 true);
            SearchResultParser parser = new SearchResultParser(queryResult);
            pids.addAll(parser.getPIDs());
        }
        return pids;
    }
    
    public static void purgeDemoObjects() throws Exception {
        String[] fTypes = {"O", "M", "D"};
        Set pids = getDemoObjects(fTypes);
        Iterator it = pids.iterator();
        while (it.hasNext()) {
            ExecUtility.execCommandLineUtility(FEDORA_HOME + "/client/bin/fedora-purge " + 
                    getHost() + ":" + getPort() + " " + getUsername() + " " + 
                    getPassword() + " " + (String)it.next() + " " + getProtocol() + 
                    " for testing");
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

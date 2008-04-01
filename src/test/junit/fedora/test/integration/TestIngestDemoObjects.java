package fedora.test.integration;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.custommonkey.xmlunit.SimpleXpathEngine;
import org.w3c.dom.Document;

import fedora.client.FedoraClient;
import fedora.client.search.SearchResultParser;
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
    
    public void setUp() throws Exception {
        SimpleXpathEngine.registerNamespace("oai_dc", "http://www.openarchives.org/OAI/2.0/oai_dc/");
        SimpleXpathEngine.registerNamespace(NS_FEDORA_TYPES_PREFIX, NS_FEDORA_TYPES);
        SimpleXpathEngine.registerNamespace("demo", "http://example.org/ns#demo");
        ingestDemoObjects();
    }
    
    public void tearDown() throws Exception {
        purgeDemoObjects();
        SimpleXpathEngine.clearNamespaces();
    }
    
    public void xtestIngestDemoObjects() throws Exception {        
        client = getFedoraClient();
        
        // check that demo objects were ingested
        File[] demoDirs = {
                new File(FEDORA_HOME + "/client/demo/foxml/local-server-demos"),
                new File(FEDORA_HOME + "/client/demo/foxml/open-server-demos")
        };
        Set demoObjectFiles = new HashSet();
        for (int i = 0; i < demoDirs.length; i++) {
            demoObjectFiles.addAll(getFiles(demoDirs[i], "FedoraBDefObject"));
            demoObjectFiles.addAll(getFiles(demoDirs[i], "FedoraBMechObject"));
            demoObjectFiles.addAll(getFiles(demoDirs[i], "FedoraObject"));
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
            in = client.get(baseURL + "/get/" + pid + "?xml=true", true, true);
            Document result = builder.parse(in);
            // simple test of the objects in the repo
            assertXpathExists("/objectProfile", result);
        }
    }

    private static Set getFiles(File dir, String searchString) throws Exception {
        Set set = new HashSet();
        File[] files = dir.listFiles();
        for (int i = 0; i < files.length; i++) {
            if (files[i].isDirectory()) {
                set.addAll(getFiles(files[i], searchString));
            } else {
                if (matches(files[i], searchString)) set.add(files[i]);
            }
        }
        return set;
    }

    private static boolean matches(File file, String searchString) throws Exception {
        BufferedReader in = new BufferedReader(new FileReader(file));
        try {
            String line;
            while ( (line=in.readLine()) != null ) {
                if (line.indexOf(searchString)!=-1) return true;
            }
        } finally {
            try { in.close(); } catch (Exception e) { }
        }
        return false;
    }
    
    public static void ingestDemoObjects() {
        String[] cmd = {FEDORA_HOME + "/client/bin/fedora-ingest-demos", 
                getHost(), getPort(), getUsername(), getPassword(), getProtocol()};
        ExecUtility.execCommandLineUtility(cmd);
    }
    
    /**
     * Gets the PIDs of objects of the specified type in the "demo" pid 
     * namespace that are in the repository
     * @param fTypes any combination of O, D, or M
     * @return set of PIDs of the specified object type
     * @throws Exception
     */
    public static Set<String> getDemoObjects(String[] fTypes) throws Exception {
        if (fTypes == null || fTypes.length == 0) {
            fTypes = new String[] {"O", "M", "D"};
        }
        
        FedoraClient client = getFedoraClient();
        InputStream queryResult;
        Set<String> pids = new LinkedHashSet<String>();
        for (int i = 0; i < fTypes.length; i++) {
            queryResult = client.get(getBaseURL() + "/search?query=pid~*%20fType=" +
            		                 fTypes[i] + "&maxResults=1000&pid=true&xml=true", 
            		                 true, true);
            SearchResultParser parser = new SearchResultParser(queryResult);
            pids.addAll(parser.getPIDs());
        }
        return pids;
    }
    
    public static void purgeDemoObjects() throws Exception {
        String[] fTypes = {"O", "M", "D"};
        Set<String> pids = getDemoObjects(fTypes);
        Iterator<String> it = pids.iterator();
        while (it.hasNext()) {
            String[] cmd = {FEDORA_HOME + "/client/bin/fedora-purge", 
                    getHost() + ":" + getPort(), getUsername(), getPassword(), 
                    it.next(), getProtocol(), "for testing"};
            ExecUtility.execCommandLineUtility(cmd);
        }
        //FedoraServerTestSetup.dropDBTables();
        //FedoraServerTestSetup.deleteStores();
    }
    
    public static void main(String[] args) {
        junit.textui.TestRunner.run(TestIngestDemoObjects.class);
    }  
}

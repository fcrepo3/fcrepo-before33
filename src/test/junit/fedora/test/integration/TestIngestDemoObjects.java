package fedora.test.integration;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.Statement;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import junit.extensions.TestSetup;
import junit.framework.Test;
import junit.framework.TestSuite;

import org.custommonkey.xmlunit.SimpleXpathEngine;
import org.w3c.dom.Document;

import fedora.client.FedoraClient;
import fedora.client.search.SearchResultParser;
import fedora.client.utility.ingest.Ingest;
import fedora.server.config.DatastoreConfiguration;
import fedora.server.config.ModuleConfiguration;
import fedora.server.config.ServerConfiguration;
import fedora.server.storage.ConnectionPool;
import fedora.server.utilities.DDLConverter;
import fedora.server.utilities.SQLUtility;
import fedora.server.utilities.TableSpec;
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
        ExecUtility.execCommandLineUtility(FEDORA_HOME + "/client/bin/fedora-ingest-demos " + 
                getHost() + " " + getPort() + " " + getUsername() + " " + 
                getPassword() + " " + getProtocol());
    }
    
    /**
     * Gets the PIDs of objects of the specified type in the "demo" pid 
     * namespace that are in the repository
     * @param fTypes any combination of O, D, or M
     * @return set of PIDs of the specified object type
     * @throws Exception
     */
    public static Set getDemoObjects(String[] fTypes) throws Exception {
        if (fTypes == null || fTypes.length == 0) {
            fTypes = new String[] {"O", "M", "D"};
        }
        String baseURL = getBaseURL();
        FedoraClient client = new FedoraClient(baseURL, getUsername(), getPassword());
        InputStream queryResult;
        Set pids = new LinkedHashSet();
        for (int i = 0; i < fTypes.length; i++) {
            queryResult = client.get(baseURL + "/search?query=pid~*%20fType=" +
            		                 fTypes[i] + "&maxResults=1000&pid=true&xml=true", 
            		                 true, true);
            SearchResultParser parser = new SearchResultParser(queryResult);
            pids.addAll(parser.getPIDs());
        }
        return pids;
    }
    
    public static void purgeDemoObjects() throws Exception {
        /*String[] fTypes = {"O", "M", "D"};
        Set pids = getDemoObjects(fTypes);
        Iterator it = pids.iterator();
        while (it.hasNext()) {
            ExecUtility.execCommandLineUtility(FEDORA_HOME + "/client/bin/fedora-purge " + 
                    getHost() + ":" + getPort() + " " + getUsername() + " " + 
                    getPassword() + " " + (String)it.next() + " " + getProtocol() + 
                    " for testing");
        }*/
        deleteDBTables();
        deleteStore();
    }
    
    public static void main(String[] args) {
        junit.textui.TestRunner.run(TestIngestDemoObjects.class);
    }
    public static void deleteDBTables() throws Exception {
        ConnectionPool cPool = SQLUtility.getConnectionPool(getServerConfiguration());
        Connection conn = cPool.getConnection();
        Statement stmt = conn.createStatement();
        DDLConverter ddlConverter = getDDLConverter();
        try {
	        FileInputStream fis = new FileInputStream("src/dbspec/server/fedora/server/storage/resources/DefaultDOManager.dbspec");
	        Iterator tableSpecs = TableSpec.getTableSpecs(fis).iterator();	        
	        
	        TableSpec tableSpec;
	        Iterator commands;
	        String command;
	        while (tableSpecs.hasNext()) {
	            tableSpec = (TableSpec)tableSpecs.next();
	            commands = ddlConverter.getDDL(tableSpec).iterator();
	            while (commands.hasNext()) {
	                command = ddlConverter.getDeleteDDL((String)commands.next());
	                stmt.execute(command);
	            }
	        }
        } finally {
	        if (stmt != null) stmt.close();
	        if (conn != null) conn.close();
        }
    }  
         
    private static DDLConverter getDDLConverter() throws Exception {
        ServerConfiguration fcfg = getServerConfiguration();
        ModuleConfiguration mcfg = fcfg.getModuleConfiguration("fedora.server.storage.ConnectionPoolManager");
        String defaultPoolName = mcfg.getParameter("defaultPoolName").getValue();
        DatastoreConfiguration dcfg = fcfg.getDatastoreConfiguration(defaultPoolName);
        String ddlConverterClassName = dcfg.getParameter("ddlConverter").getValue();
        return (DDLConverter)Class.forName(ddlConverterClassName).newInstance();
    }
    
    private static String getRIStoreLocation() throws Exception {
        ServerConfiguration fcfg = getServerConfiguration();
        ModuleConfiguration mcfg = fcfg.getModuleConfiguration("fedora.server.resourceIndex.ResourceIndex");
        String datastore = mcfg.getParameter("datastore").getValue();
        DatastoreConfiguration dcfg = fcfg.getDatastoreConfiguration(datastore);
        return dcfg.getParameter("path").getValue();
    }
    
    private static void deleteStore() throws Exception {
        ServerConfiguration fcfg = getServerConfiguration();
        String[] dirs = {
            fcfg.getParameter("object_store_base").getValue(),
            fcfg.getParameter("datastream_store_base").getValue(),
            getRIStoreLocation()
        };
        for (int i = 0; i < dirs.length; i++) {
            deleteDirectory(dirs[i]);
        }
    }
    
    private static boolean deleteDirectory(String directory) {
        boolean result = false;

        if (directory != null) {
            File file = new File(directory);
            if (file.exists() && file.isDirectory()) {
                // 1. delete content of directory:
                File[] files = file.listFiles();
                result = true; //init result flag
                int count = files.length;
                for (int i = 0; i < count; i++) { //for each file:
                    File f = files[i];
                    if (f.isFile()) {
                        result = result && f.delete();
                    } else if (f.isDirectory()) {
                        result = result && deleteDirectory(f.getAbsolutePath());
                    }
                }//next file

                file.delete(); //finally delete (empty) input directory
            }//else: input directory does not exist or is not a directory
        }//else: no input value

        return result;
    }//deleteDirectory()   
    
}

package fedora.test;

import java.io.File;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;

import fedora.client.FedoraClient;
import fedora.client.search.SearchResultParser;
import fedora.client.utility.AutoPurger;
import fedora.client.utility.ingest.Ingest;
import fedora.client.utility.ingest.IngestCounter;

import fedora.common.Constants;

import fedora.server.management.FedoraAPIM;

/**
 * Base class for JUnit tests that assume a running Fedora instance.
 * 
 * @author Edwin Shin
 */
public abstract class FedoraServerTestCase
        extends FedoraTestCase
        implements Constants {

	private static DocumentBuilderFactory factory;
    private static DocumentBuilder builder;
    
    public FedoraServerTestCase() {
        super();
    }
    
    public FedoraServerTestCase(String name) {
        super(name);
    }
    
    /**
     * Returns the requested HTTP resource as an XML Document
     * @param location a URL relative to the Fedora base URL
     * @return Document
     * @throws Exception
     */
    public Document getXMLQueryResult(String location) throws Exception {
        return getXMLQueryResult(getFedoraClient(), location);
    }

    public Document getXMLQueryResult(FedoraClient client, String location) throws Exception {
    	if (factory == null) {
    		factory = DocumentBuilderFactory.newInstance();
    	}
    	if (builder == null) {
    		builder = factory.newDocumentBuilder();
    	}
        InputStream is = client.get(getBaseURL() + location, true, true);
        Document result = builder.parse(is);
        is.close();
        return result;
    }

    public static boolean testingMETS() {
        String format = System.getProperty("demo.format");
        return (format != null && format.equalsIgnoreCase("mets"));
    }
    
    public static void ingestDemoObjects() throws Exception {
        File dir;
        String ingestFormat;
        if (testingMETS()) {
            System.out.println("Ingesting all demo objects in METS format");
            dir = new File(FEDORA_HOME, "client/demo/mets");
            ingestFormat = METS_EXT1_1.uri;
        } else {
            System.out.println("Ingesting all demo objects in FOXML format");
            dir = new File(FEDORA_HOME, "client/demo/foxml");
            ingestFormat = FOXML1_1.uri;
        }

		String fTypes = "DMOC";
		FedoraClient client = FedoraTestCase.getFedoraClient();
		
		Ingest.multiFromDirectory(dir,
                ingestFormat,
                fTypes,
                client.getAPIA(),
                client.getAPIM(),
                null, 
                new PrintStream(File.createTempFile("demo", null)), 
                new IngestCounter());
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
            fTypes = new String[] {"O", "M", "D", "C"};
        }
        
        FedoraClient client = getFedoraClient();
        InputStream queryResult;
        Set pids = new LinkedHashSet();
        for (int i = 0; i < fTypes.length; i++) {
            queryResult = client.get(getBaseURL() + "/search?query=pid~demo:*%20fType=" +
            		                 fTypes[i] + "&maxResults=1000&pid=true&xml=true", 
            		                 true, true);
            SearchResultParser parser = new SearchResultParser(queryResult);
            pids.addAll(parser.getPIDs());
        }
        return pids;
    }
    
    public static void purgeDemoObjects() throws Exception {
    	FedoraClient client = getFedoraClient();
    	FedoraAPIM apim = client.getAPIM();
    	
        String[] fTypes = {"O", "M", "D", "C"};
        Set pids = getDemoObjects(fTypes);
        Iterator it = pids.iterator();
        while (it.hasNext()) {
        	AutoPurger.purge(apim, (String)it.next(), null, false);
        }
    }
    
    public static void main(String[] args) {
        junit.textui.TestRunner.run(FedoraServerTestCase.class);
    }
}

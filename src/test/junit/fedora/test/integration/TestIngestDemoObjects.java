
package fedora.test.integration;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.InputStream;
import java.util.HashSet;
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
 * Tests fedora-ingest-demos. Usage: fedora-ingest-demos <hostname> <port>
 * <username> <password> <protocol>
 * 
 * @author Edwin Shin
 */
public class TestIngestDemoObjects
        extends FedoraServerTestCase {

    private FedoraClient client;

    @Override
    public void setUp() throws Exception {
        SimpleXpathEngine
                .registerNamespace("oai_dc",
                                   "http://www.openarchives.org/OAI/2.0/oai_dc/");
        SimpleXpathEngine.registerNamespace(NS_FEDORA_TYPES_PREFIX,
                                            NS_FEDORA_TYPES);
        SimpleXpathEngine.registerNamespace("demo",
                                            "http://example.org/ns#demo");
        ingestDemoObjects();
    }

    @Override
    public void tearDown() throws Exception {
        purgeDemoObjects();
        SimpleXpathEngine.clearNamespaces();
    }

    public void testIngestDemoObjects() throws Exception {
        client = getFedoraClient();

        // check that demo objects were ingested
        File[] demoDirs =
                {
                        new File(FEDORA_HOME
                                + "/client/demo/foxml/local-server-demos"),
                        new File(FEDORA_HOME
                                + "/client/demo/foxml/open-server-demos")};
        Set<File> demoObjectFiles = new HashSet<File>();
        for (File element : demoDirs) {
            demoObjectFiles.addAll(getFiles(element, "FedoraBDefObject"));
            demoObjectFiles.addAll(getFiles(element, "FedoraBMechObject"));
            demoObjectFiles.addAll(getFiles(element, "FedoraObject"));
        }
        Set<String> repositoryDemoObjects = getDemoObjects(null);

        // simple test to see if the count of demo object files matches the 
        // count from an APIA-Lite search
        assertEquals(demoObjectFiles.size(), repositoryDemoObjects.size());

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        InputStream in;

        for (String pid : repositoryDemoObjects) {
            in = client.get(getBaseURL() + "/get/" + pid + "?xml=true", true, true);
            Document result = builder.parse(in);
            // simple test of the objects in the repo
            assertXpathExists("/objectProfile", result);
        }
    }

    private static Set<File> getFiles(File dir, String searchString) throws Exception {
        Set<File> set = new HashSet<File>();
        File[] files = dir.listFiles();
        for (File element : files) {
            if (element.isDirectory()) {
                set.addAll(getFiles(element, searchString));
            } else {
                if (matches(element, searchString)) {
                    set.add(element);
                }
            }
        }
        return set;
    }

    private static boolean matches(File file, String searchString)
            throws Exception {
        BufferedReader in = new BufferedReader(new FileReader(file));
        try {
            String line;
            while ((line = in.readLine()) != null) {
                if (line.indexOf(searchString) != -1) {
                    return true;
                }
            }
        } finally {
            try {
                in.close();
            } catch (Exception e) {
            }
        }
        return false;
    }

    public static void ingestDemoObjects() {
        String osName = System.getProperty("os.name");
        String[] cmd = {FEDORA_HOME + "/client/bin/fedora-ingest-demos", 
                getHost(), getPort(), getUsername(), getPassword(), getProtocol()};
        if (!osName.startsWith("Windows")) {
            // needed for the Fedora shell scripts
            cmd[0] = cmd[0] + ".sh";
        }
        ExecUtility.execCommandLineUtility(cmd);
    }

    /**
     * Gets the PIDs of objects of the specified type in the "demo" pid
     * namespace that are in the repository
     * 
     * @param fTypes
     *        any combination of O, D, or M
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
        for (String element : fTypes) {
            queryResult =
                    client.get(getBaseURL() + "/search?query=pid~*%20fType="
                                       + element
                                       + "&maxResults=1000&pid=true&xml=true",
                               true,
                               true);
            SearchResultParser parser = new SearchResultParser(queryResult);
            pids.addAll(parser.getPIDs());
        }
        return pids;
    }

    public static void purgeDemoObjects() throws Exception {
        String osName = System.getProperty("os.name");
        String[] fTypes = {"O", "M", "D"};
        Set<String> pids = getDemoObjects(fTypes);
        for (String pid : pids) {
            String[] cmd = {FEDORA_HOME + "/client/bin/fedora-purge", 
                    getHost() + ":" + getPort(), getUsername(), getPassword(), 
                    pid, getProtocol(), "testing"};
            if (!osName.startsWith("Windows")) {
                // needed for the Fedora shell scripts
                cmd[0] = cmd[0] + ".sh";
            }
            ExecUtility.execCommandLineUtility(cmd);
        }
    }

    public static void main(String[] args) {
        junit.textui.TestRunner.run(TestIngestDemoObjects.class);
    }
}

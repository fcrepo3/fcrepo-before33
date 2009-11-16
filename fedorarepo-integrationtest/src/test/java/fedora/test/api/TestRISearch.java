/* The contents of this file are subject to the license and copyright terms
 * detailed in the license directory at the root of the source tree (also
 * available online at http://fedora-commons.org/license/).
 */
package fedora.test.api;

import java.io.IOException;

import java.net.URLEncoder;

import junit.framework.Test;
import junit.framework.TestSuite;

import fedora.client.FedoraClient;

import fedora.common.Constants;
import fedora.common.Models;
import fedora.common.PID;

import fedora.test.DemoObjectTestSetup;
import fedora.test.FedoraServerTestCase;

/**
 * Tests risearch functionality when the resource index is enabled.
 *
 * @author Chris Wilper
 */
public class TestRISearch
        extends FedoraServerTestCase {

    private static final String RISEARCH_COUNT =
            "/risearch?type=triples&lang=spo&format=count&stream=on&"
            + "flush=true&query=";

    public static Test suite() {
        TestSuite suite = new TestSuite("TestRISearch TestSuite");
        suite.addTestSuite(TestRISearch.class);
        return new DemoObjectTestSetup(suite);
    }

    /**
     * Implicit relationship to Fedora object CModel
     * @throws Exception
     */
    public void testRISearchBasicCModel() throws Exception {
        FedoraClient client = getFedoraClient();
        for (String pid : new String[] { "demo:SmileyPens",
                                         "demo:SmileyGreetingCard" }) {
            String query = "<" + PID.toURI(pid) + ">"
                        + " <" + Constants.MODEL.HAS_MODEL.uri + ">"
                        + " <" + Models.FEDORA_OBJECT_CURRENT.uri + ">";
            checkSPOCount(client, query, 1);
        }
    }

    /**
     * Explicit RELS-EXT relation to collection object
     * @throws Exception
     */
    public void testRISearchRelsExtCollection() throws Exception {
        FedoraClient client = getFedoraClient();
        String collectionPid = "demo:SmileyStuff";
        for (String pid : new String[] { "demo:SmileyPens",
                                         "demo:SmileyGreetingCard" }) {
            String query = "<" + PID.toURI(pid) + ">"
                        + " <" + Constants.RELS_EXT.IS_MEMBER_OF.uri + ">"
                        + " <" + PID.toURI(collectionPid) + ">";
            checkSPOCount(client, query, 1);
        }
    }

    /**
     * RELS-INT relationships specifying image size for jpeg datastreams
     * @throws Exception
     */
    public void testRISearchRelsInt() throws Exception {
        FedoraClient client = getFedoraClient();
        for (String pid : new String[] { "demo:SmileyPens" ,
                                         "demo:SmileyGreetingCard" }) {
            String query = "<" + PID.toURI(pid) + "/MEDIUM_SIZE" + ">"
                        + " <" + "http://ns.adobe.com/exif/1.0/PixelXDimension" + ">"
                        + " \"320\"";
            checkSPOCount(client, query, 1);
        }
    }


    private void checkSPOCount(FedoraClient client,
                               String query,
                               int expectedCount) {
        int actualCount = getSPOCount(client, query);
        assertEquals("Expected " + expectedCount + " results from SPO query"
                     + " " + query + ", but got " + actualCount,
                     expectedCount, actualCount);
    }

    private int getSPOCount(FedoraClient client,
                            String query) {
        String response = null;
        try {
            response = client.getResponseAsString(
                    RISEARCH_COUNT + URLEncoder.encode(query, "UTF-8"),
                    true,
                    true).trim();
        } catch (IOException e) {
            e.printStackTrace();
            fail("Error while querying resource index (is it enabled?).  "
                 + "See stack trace");
        }
        int count = 0;
        try {
            count = Integer.parseInt(response);
        } catch (NumberFormatException e) {
            fail("Expected numeric plaintext response body from RI query, but "
                 + "got the following: " + response);
        }
        return count;
    }

}

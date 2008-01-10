
package fedora.test.api;

import java.io.UnsupportedEncodingException;

import java.rmi.RemoteException;

import java.util.HashMap;
import java.util.Map;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.custommonkey.xmlunit.SimpleXpathEngine;

import org.jrdf.graph.Node;

import org.trippi.TupleIterator;

import fedora.client.FedoraClient;

import fedora.common.Constants;
import fedora.common.PID;

import fedora.server.management.FedoraAPIM;
import fedora.server.types.gen.RelationshipTuple;

import fedora.test.FedoraServerTestCase;

/**
 * Tests for the various relationship API-M methods. Tests assume a running
 * instance of the Fedora server with Resource Index enabled.
 * 
 * @author Edwin Shin
 */
public class TestRelationships
        extends FedoraServerTestCase
        implements Constants {

    private FedoraAPIM apim;

    private static byte[] DEMO_888_FOXML;

    private static byte[] DEMO_777_FOXML;

    private String pid;

    static {
        // Test FOXML object with RELS-EXT datastream
        StringBuilder sb = new StringBuilder();
        sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        sb
                .append("<foxml:digitalObject VERSION=\"1.1\" PID=\"demo:888\" xmlns:foxml=\"info:fedora/fedora-system:def/foxml#\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"info:fedora/fedora-system:def/foxml# http://www.fedora.info/definitions/1/0/foxml1-1.xsd\">");
        sb.append("  <foxml:objectProperties>");
        sb
                .append("    <foxml:property NAME=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#type\" VALUE=\"FedoraObject\"/>");
        sb
                .append("    <foxml:property NAME=\"info:fedora/fedora-system:def/model#state\" VALUE=\"A\"/>");
        sb.append("  </foxml:objectProperties>");
        sb
                .append("  <foxml:datastream ID=\"RELS-EXT\" CONTROL_GROUP=\"M\" STATE=\"A\">");
        sb
                .append("    <foxml:datastreamVersion ID=\"RELS-EXT.0\" MIMETYPE=\"text/xml\" LABEL=\"Relationships\">");
        sb.append("      <foxml:xmlContent>");
        sb
                .append("        <rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\""
                        + "                 xmlns:rel=\"info:fedora/fedora-system:def/relations-external#\">");
        sb
                .append("          <rdf:Description rdf:about=\"info:fedora/demo:888\">");
        sb
                .append("            <rel:hasFormalContentModel rdf:resource=\"info:fedora/demo:UVA_STD_IMAGE_1\"/>");
        sb.append("          </rdf:Description>");
        sb.append("        </rdf:RDF>");
        sb.append("      </foxml:xmlContent>");
        sb.append("    </foxml:datastreamVersion>");
        sb.append("  </foxml:datastream>");
        sb.append("</foxml:digitalObject>");

        try {
            DEMO_888_FOXML = sb.toString().getBytes("UTF-8");
        } catch (UnsupportedEncodingException uee) {
        }

        sb = new StringBuilder();
        sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        sb
                .append("<foxml:digitalObject VERSION=\"1.1\" PID=\"demo:777\" xmlns:foxml=\"info:fedora/fedora-system:def/foxml#\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"info:fedora/fedora-system:def/foxml# http://www.fedora.info/definitions/1/0/foxml1-1.xsd\">");
        sb.append("  <foxml:objectProperties>");
        sb
                .append("    <foxml:property NAME=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#type\" VALUE=\"FedoraObject\"/>");
        sb
                .append("    <foxml:property NAME=\"info:fedora/fedora-system:def/model#state\" VALUE=\"A\"/>");
        sb.append("  </foxml:objectProperties>");
        sb.append("</foxml:digitalObject>");

        try {
            DEMO_777_FOXML = sb.toString().getBytes("UTF-8");
        } catch (UnsupportedEncodingException uee) {
        }
    }

    public static Test suite() {
        TestSuite suite = new TestSuite("TestRelationships TestSuite");
        suite.addTestSuite(TestRelationships.class);
        return suite;
    }

    @Override
    public void setUp() throws Exception {
        apim = getFedoraClient().getAPIM();
        SimpleXpathEngine
                .registerNamespace("oai_dc",
                                   "http://www.openarchives.org/OAI/2.0/oai_dc/");
        SimpleXpathEngine.registerNamespace("dc",
                                            "http://purl.org/dc/elements/1.1/");
        SimpleXpathEngine
                .registerNamespace("foxml",
                                   "info:fedora/fedora-system:def/foxml#");
        apim.ingest(DEMO_888_FOXML, FOXML1_1.uri, "ingesting new foxml object");
        apim.ingest(DEMO_777_FOXML, FOXML1_1.uri, "ingesting new foxml object");
        pid = "demo:888";
    }

    @Override
    public void tearDown() throws Exception {
        apim.purgeObject("demo:777", "", false);
        apim.purgeObject("demo:888", "", false);
        SimpleXpathEngine.clearNamespaces();
    }

    public void testAddRelationship() throws Exception {
        String p, o;

        p = "urn:bar";
        o = "urn:baz";
        addRelationship(pid, p, o, false, null);

        // plain literal
        o = "quux";
        addRelationship(pid, p, o, true, null);

        // datatyped literal
        o = "1970-01-01T00:00:00Z";
        addRelationship(pid, p, o, true, Constants.RDF_XSD.DATE_TIME.uri);
    }

    public void testBadRelationships() {
        String p, o;

        p = "http://purl.org/dc/elements/1.1/title";
        o = "A Dictionary of Maqiao";
        try {
            apim.addRelationship(pid, p, o, true, null);
            fail("Adding Dublin Core relationship should have failed");
        } catch (RemoteException e) {
        }

        p = "info:fedora/fedora-system:def/model#foo";
        try {
            apim.addRelationship(pid, p, o, true, null);
            fail("Adding Fedora Model relationship should have failed");
        } catch (RemoteException e) {
        }
    }

    public void testGetRelationships() throws Exception {
        pid = "demo:777";
        String p, o;

        p = "urn:bar";
        o = "urn:baz";
        getRelationship(pid, p, o, false, null);

        p = "urn:title";
        o = "asdf";//"三国演义"; // test unicode
        getRelationship(pid, p, o, true, null);

        p = "urn:temperature";
        o = "98.6";
        getRelationship(pid, p, o, true, Constants.RDF_XSD.FLOAT.uri);
    }

    public void testPurgeRelationships() throws Exception {
        String p, o;

        p = "urn:p";
        o = "urn:o";
        purgeRelationship(pid, p, o, false, null);

        p = "urn:title";
        o = "asdf";//"三国演义"; // test unicode
        purgeRelationship(pid, p, o, true, null);

        p = "urn:temperature";
        o = "98.6";
        purgeRelationship(pid, p, o, true, Constants.RDF_XSD.FLOAT.uri);

        assertFalse("Purging non-existant relation should have failed", apim
                .purgeRelationship(pid, "urn:asdf", "867-5309", true, null));
    }

    private void addRelationship(String pid,
                                 String predicate,
                                 String object,
                                 boolean isLiteral,
                                 String datatype) throws Exception {
        assertTrue(apim.addRelationship(pid,
                                        predicate,
                                        object,
                                        isLiteral,
                                        datatype));
        assertFalse("Adding duplicate relationship should return false", apim
                .addRelationship(pid, predicate, object, isLiteral, datatype));

        // check resource index
        String query = "";
        if (isLiteral) {
            if (datatype != null) {
                query =
                        String
                                .format("select $s from <#ri> where $s <%s> '%s'^^<%s>",
                                        predicate,
                                        object,
                                        datatype);
            } else {
                query =
                        String
                                .format("select $s from <#ri> where $s <%s> '%s'",
                                        predicate,
                                        object);
            }
        } else {
            query =
                    String.format("select $s from <#ri> where $s <%s> <%s>;",
                                  predicate,
                                  object);
        }

        TupleIterator tuples = queryRI(query);
        assertTrue(tuples.hasNext());
        Map<String, Node> row = tuples.next();
        for (String key : row.keySet()) {
            assertEquals(PID.toURI(pid), row.get(key).toString());
        }

    }

    private void getRelationship(String pid,
                                 String predicate,
                                 String object,
                                 boolean isLiteral,
                                 String datatype) throws Exception {
        addRelationship(pid, predicate, object, isLiteral, datatype);
        RelationshipTuple[] tuples = apim.getRelationships(pid, predicate);
        assertNotNull(tuples);
        assertEquals(1, tuples.length);
        assertEquals(PID.toURI(pid), tuples[0].getSubject());
        assertEquals(predicate, tuples[0].getPredicate());
        assertEquals(object, tuples[0].getObject());
        assertEquals(isLiteral, tuples[0].isIsLiteral());
        assertEquals(datatype, tuples[0].getDatatype());
    }

    private void purgeRelationship(String pid,
                                   String predicate,
                                   String object,
                                   boolean isLiteral,
                                   String datatype) throws Exception {
        addRelationship(pid, predicate, object, isLiteral, datatype);
        assertTrue(apim.purgeRelationship(pid,
                                          predicate,
                                          object,
                                          isLiteral,
                                          datatype));
    }

    private TupleIterator queryRI(String query) throws Exception {
        FedoraClient client = getFedoraClient();
        Map<String, String> params = new HashMap<String, String>();
        params.put("lang", "itql");
        params.put("flush", "true");
        params.put("query", query);
        return client.getTuples(params);
    }

    public static void main(String[] args) {
        junit.textui.TestRunner.run(TestRelationships.class);
    }

}

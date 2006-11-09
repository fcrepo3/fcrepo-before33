package fedora.server.resourceIndex;

import java.util.Set;

import org.junit.Test;

import fedora.server.storage.types.DigitalObject;

/**
 * Tests adding and deleting objects from the RI, with respect to their
 * datastreams.
 *
 * Note: All tests run at RI level 1 unless otherwise noted.
 *
 * @author cwilper@cs.cornell.edu
 */
public class ResourceIndexAddDelDSIntegrationTest
        extends ResourceIndexIntegrationTest {

    /**
     * Add, then delete an object with no datastreams.
     */
    @Test
    public void testAddDelObjNoDatastreams()
            throws Exception {
        Set<DigitalObject> objects = getTestObjects(1, 0);
        doAddDelTest(1, objects);
    }

    /**
     * Add, then delete an object with an "E" datastream.
     */
    @Test
    public void testAddDelObjExternalDS()
            throws Exception {
        DigitalObject obj = getTestObject("test:1", "test");
        addEDatastream(obj, "DS1");
        doAddDelTest(1, obj);
    }

    /**
     * Add, then delete an object with an "M" datastream.
     */
    @Test
    public void testAddDelObjManagedDS()
            throws Exception {
        DigitalObject obj = getTestObject("test:1", "test");
        addMDatastream(obj, "DS1");
        doAddDelTest(1, obj);
    }

    /**
     * Add, then delete an object with an "R" datastream.
     */
    @Test
    public void testAddDelObjRedirectDS()
            throws Exception {
        DigitalObject obj = getTestObject("test:1", "test");
        addRDatastream(obj, "DS1");
        doAddDelTest(1, obj);
    }

    /**
     * Add, then delete an object with an "X" datastream.
     */
    @Test
    public void testAddDelObjInlineXMLDS()
            throws Exception {
        DigitalObject obj = getTestObject("test:1", "test");
        addXDatastream(obj, "DS1", "<xmldoc/>");
        doAddDelTest(1, obj);
    }

    /**
     * Add, then delete an object with a "DC" datastream.
     */
    @Test
    public void testAddDelObjDCDS()
            throws Exception {
        DigitalObject obj = getTestObject("test:1", "test");
        StringBuffer x = new StringBuffer();
        x.append("<oai_dc:dc xmlns:dc=\"http://purl.org/dc/elements/1.1/\"");
        x.append(" xmlns:oai_dc=\"http://www.openarchives.org/OAI/2.0/oai_dc/\">\n");
        x.append("<dc:title>test</dc:title>\n");
        x.append("</oai_dc:dc>");
        addXDatastream(obj, "DC", x.toString());
        doAddDelTest(1, obj);
    }

    /**
     * Add, then delete an object with a "RELS-EXT" datastream.
     */
    @Test
    public void testAddDelObjRELSEXTDS()
            throws Exception {
        DigitalObject obj = getTestObject("test:1", "test");
        StringBuffer x = new StringBuffer();
        x.append("<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\"");
        x.append(" xmlns:foo=\"http://example.org/foo#\">\n");
        x.append("<rdf:Description rdf:about=\"info:fedora/test:1\">\n");
        x.append("  <foo:bar rdf:resource=\"http://example.org/baz\"/>\n");
        x.append("</rdf:Description>\n");
        x.append("</rdf:RDF>");
        addXDatastream(obj, "RELS-EXT", x.toString());
        doAddDelTest(1, obj);
    }

    // Supports legacy test runners
    public static junit.framework.Test suite() {
        return new junit.framework.JUnit4TestAdapter(
                ResourceIndexAddDelDSIntegrationTest.class);
    }

}

package fedora.server.resourceIndex;

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
        doAddDelTest(1, getTestObject("test:1", "test"));
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
        addXDatastream(obj, "DC", getDC("<dc:title>test</dc:title>"));
        doAddDelTest(1, obj);
    }

    /**
     * Add, then delete an object with a "RELS-EXT" datastream.
     */
    @Test
    public void testAddDelObjRELSEXTDS()
            throws Exception {
        DigitalObject obj = getTestObject("test:1", "test");
        String rel = "<foo:bar rdf:resource=\"http://example.org/baz\"/>";
        addXDatastream(obj, "RELS-EXT", getRELSEXT(rel));
        doAddDelTest(1, obj);
    }

    // Supports legacy test runners
    public static junit.framework.Test suite() {
        return new junit.framework.JUnit4TestAdapter(
                ResourceIndexAddDelDSIntegrationTest.class);
    }

}

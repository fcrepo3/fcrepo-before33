package fedora.server.resourceIndex;

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
     * Construct the test.
     */
    public ResourceIndexAddDelDSIntegrationTest(String name) {
        super(name);
    }

    /**
     * Add, then delete an object with no datastreams.
     */
    public void testAddDelObjNoDatastreams()
            throws Exception {
    }

    /**
     * Add, then delete an object with an "E" datastream.
     */
    public void testAddDelObjExternalDS()
            throws Exception {
    }

    /**
     * Add, then delete an object with an "M" datastream.
     */
    public void testAddDelObjManagedDS()
            throws Exception {
    }

    /**
     * Add, then delete an object with an "R" datastream.
     */
    public void testAddDelObjRedirectDS()
            throws Exception {
    }

    /**
     * Add, then delete an object with an "X" datastream.
     */
    public void testAddDelObjInlineXMLDS()
            throws Exception {
    }

    /**
     * Add, then delete an object with a "DC" datastream.
     */
    public void testAddDelObjDCDS()
            throws Exception {
    }

    /**
     * Add, then delete an object with a "RELS-EXT" datastream.
     */
    public void testAddDelObjRELSEXTDS()
            throws Exception {
    }

}
package fedora.server.resourceIndex;

/**
 * Tests modifying objects in the RI, with respect to their datastreams.
 *
 * Note: All tests run at RI level 1 unless otherwise noted.
 *
 * @author cwilper@cs.cornell.edu
 */
public class ResourceIndexModDSIntegrationTest
        extends ResourceIndexIntegrationTest {

    /**
     * Construct the test.
     */
    public ResourceIndexModDSIntegrationTest(String name) {
        super(name);
    }

    /**
     * Add a datastream to an existing object.
     */
    public void testModObjOnceAddDS()
            throws Exception {
    }

    /**
     * Delete a datastream from an existing object.
     */
    public void testModObjOnceDelDS()
            throws Exception {
    }

    /**
     * Add a datastream and delete another from an existing object.
     */
    public void testModObjOnceAddOneDSDelAnother()
            throws Exception {
    }

    /**
     * Add a Dublin Core field to the DC datastream of an existing object.
     */
    public void testModObjOnceAddOneDCField()
            throws Exception {
    }

    /**
     * Delete a Dublin Core field from the DC datastream of an existing object.
     */
    public void testModObjOnceDelOneDCField()
            throws Exception {
    }

    /**
     * Add a Dublin Core field and delete another from the DC datastream of
     * an existing object.
     */
    public void testModObjOnceAddOneDCFieldDelAnother()
            throws Exception {
    }

    /**
     * Add a relation to the RELS-EXT datastream of an existing object.
     */
    public void testModObjOnceAddOneRELSEXTField()
            throws Exception {
    }

    /**
     * Delete a relation from the RELS-EXT datastream of an existing object.
     */
    public void testModObjOnceDelOneRELSEXTField()
            throws Exception {
    }

    /**
     * Add a relation and delete another from the RELS-EXT datastream of an
     * existing object.
     */
    public void testModObjOnceAddOneRELSEXTFieldDelAnother()
            throws Exception {
    }

}
package fedora.server.resourceIndex;

import org.junit.Test;

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
     * Add a datastream to an existing object.
     */
    @Test
    public void testModObjOnceAddDS()
            throws Exception {
    }

    /**
     * Delete a datastream from an existing object.
     */
    @Test
    public void testModObjOnceDelDS()
            throws Exception {
    }

    /**
     * Add a datastream and delete another from an existing object.
     */
    @Test
    public void testModObjOnceAddOneDSDelAnother()
            throws Exception {
    }

    /**
     * Add a Dublin Core field to the DC datastream of an existing object.
     */
    @Test
    public void testModObjOnceAddOneDCField()
            throws Exception {
    }

    /**
     * Delete a Dublin Core field from the DC datastream of an existing object.
     */
    @Test
    public void testModObjOnceDelOneDCField()
            throws Exception {
    }

    /**
     * Add a Dublin Core field and delete another from the DC datastream of
     * an existing object.
     */
    @Test
    public void testModObjOnceAddOneDCFieldDelAnother()
            throws Exception {
    }

    /**
     * Add a relation to the RELS-EXT datastream of an existing object.
     */
    @Test
    public void testModObjOnceAddOneRELSEXTField()
            throws Exception {
    }

    /**
     * Delete a relation from the RELS-EXT datastream of an existing object.
     */
    @Test
    public void testModObjOnceDelOneRELSEXTField()
            throws Exception {
    }

    /**
     * Add a relation and delete another from the RELS-EXT datastream of an
     * existing object.
     */
    @Test
    public void testModObjOnceAddOneRELSEXTFieldDelAnother()
            throws Exception {
    }

    // Supports legacy test runners
    public static junit.framework.Test suite() {
        return new junit.framework.JUnit4TestAdapter(
                ResourceIndexModDSIntegrationTest.class);
    }

}
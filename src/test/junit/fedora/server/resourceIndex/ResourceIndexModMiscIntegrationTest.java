package fedora.server.resourceIndex;

/**
 * Miscellaneous tests of modifying existing objects in the RI.
 *
 * Note: All tests run at RI level 1 unless otherwise noted.
 *
 * @author cwilper@cs.cornell.edu
 */
public class ResourceIndexModMiscIntegrationTest
        extends ResourceIndexIntegrationTest {

    /**
     * Construct the test.
     */
    public ResourceIndexModMiscIntegrationTest(String name) {
        super(name);
    }

    /**
     * Modify an object's label with the RI at level 0.
     */
    public void testModObjOnceLabelLv0()
            throws Exception {
    }

    /**
     * Modify an object's label once.
     */
    public void testModObjOnceLabel()
            throws Exception {
    }

    /**
     * Modify an object's label multiple times.
     */
    public void testModObjMultiLabel()
            throws Exception {
    }

    /**
     * Modify an object's label multiple times while flushing the buffer
     * many times from a separate thread.
     */
    public void testModObjMultiLabelAsyncFlush()
            throws Exception {
    }

    /**
     * Modify multiple objects' labels once.
     */
    public void testModMultiObjOnceLabel()
            throws Exception {
    }

    /**
     * Modify multiple objects' labels multiple times.
     */
    public void testModMultiObjMultiLabel()
            throws Exception {
    }

    /**
     * Modify multiple objects' labels multiple times while flushing the 
     * buffer many times from a separate thread.
     */
    public void testModMultiObjMultiLabelAsyncFlush()
            throws Exception {
    }

}
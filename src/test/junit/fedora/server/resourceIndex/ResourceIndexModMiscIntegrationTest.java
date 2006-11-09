package fedora.server.resourceIndex;

import org.junit.Test;

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
     * Modify an object's label with the RI at level 0.
     */
    @Test
    public void testModObjOnceLabelLv0()
            throws Exception {
    }

    /**
     * Modify an object's label once.
     */
    @Test
    public void testModObjOnceLabel()
            throws Exception {
    }

    /**
     * Modify an object's label multiple times.
     */
    @Test
    public void testModObjMultiLabel()
            throws Exception {
    }

    /**
     * Modify an object's label multiple times while flushing the buffer
     * many times from a separate thread.
     */
    @Test
    public void testModObjMultiLabelAsyncFlush()
            throws Exception {
    }

    /**
     * Modify multiple objects' labels once.
     */
    @Test
    public void testModMultiObjOnceLabel()
            throws Exception {
    }

    /**
     * Modify multiple objects' labels multiple times.
     */
    @Test
    public void testModMultiObjMultiLabel()
            throws Exception {
    }

    /**
     * Modify multiple objects' labels multiple times while flushing the 
     * buffer many times from a separate thread.
     */
    @Test
    public void testModMultiObjMultiLabelAsyncFlush()
            throws Exception {
    }

    // Supports legacy test runners
    public static junit.framework.Test suite() {
        return new junit.framework.JUnit4TestAdapter(
                ResourceIndexModMiscIntegrationTest.class);
    }

}
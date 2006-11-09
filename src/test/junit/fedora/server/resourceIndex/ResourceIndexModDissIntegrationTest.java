package fedora.server.resourceIndex;

import org.junit.Test;

/**
 * Tests modifying objects in the RI, with respect to their disseminators.
 *
 * Note: All tests run at RI level 1 unless otherwise noted.
 *
 * @author cwilper@cs.cornell.edu
 */
public class ResourceIndexModDissIntegrationTest
        extends ResourceIndexIntegrationTest {

    /**
     * Add a one-method, no-param disseminator to an existing object.
     */
    @Test
    public void testModObjOnceAddDissOneMethodNoParms()
            throws Exception {
    }

    /**
     * Delete a one-method, no-param disseminator from an existing object.
     */
    @Test
    public void testModObjOnceDelDissOneMethodNoParms()
            throws Exception {
    }

    /**
     * Add a one-method, no-param disseminator to an existing object
     * (RI level 2).
     */
    @Test
    public void testModObjOnceAddDissOneMethodNoParmsLv2()
            throws Exception {
    }

    /**
     * Delete a one-method, no-param disseminator from an existing object
     * (RI level 2)
     */
    @Test
    public void testModObjOnceDelDissOneMethodNoParmsLv2()
            throws Exception {
    }

    /**
     * Add a one-method, fixed-param disseminator to an existing object.
     */
    @Test
    public void testModObjOnceAddDissOneMethodFixedParm()
            throws Exception {
    }

    /**
     * Delete a one-method, fixed-param disseminator from an existing object.
     */
    @Test
    public void testModObjOnceDelDissOneMethodFixedParm()
            throws Exception {
    }

    /**
     * Add a one-method, fixed-param disseminator to an existing object
     * (RI level 2).
     */
    @Test
    public void testModObjOnceAddDissOneMethodFixedParmLv2()
            throws Exception {
    }

    /**
     * Delete a one-method, fixed-param disseminator from an existing object
     * (RI level 2).
     */
    @Test
    public void testModObjOnceDelDissOneMethodFixedParmLv2()
            throws Exception {
    }

    /**
     * Add a one-method, unfixed-param disseminator to an existing object.
     */
    @Test
    public void testModObjOnceAddDissOneMethodUnfixedParm()
            throws Exception {
    }

    /**
     * Delete a one-method, unfixed-param disseminator from an existing object.
     */
    @Test
    public void testModObjOnceDelDissOneMethodUnfixedParm() 
            throws Exception {
    }

    /**
     * Add a one-method, unfixed-param disseminator to an existing object
     * (RI level 2).
     */
    @Test
    public void testModObjOnceAddDissOneMethodUnfixedParmLv2()
            throws Exception {
    }

    /**
     * Delete a one-method, unfixed-param disseminator from an existing object
     * (RI level 2).
     */
    @Test
    public void testModObjOnceDelDissOneMethodUnfixedParmLv2()
            throws Exception {
    }

    // Supports legacy test runners
    public static junit.framework.Test suite() {
        return new junit.framework.JUnit4TestAdapter(
                ResourceIndexModDissIntegrationTest.class);
    }
    
}
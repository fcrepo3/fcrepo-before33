package fedora.server.resourceIndex;

import org.junit.Test;

import fedora.server.storage.types.DigitalObject;

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
        doModObjAddDissTest(1, getBDefOne(), getBMechOne());
    }

    /**
     * Add a one-method, no-param disseminator to an existing object
     * (RI level 2).
     */
    @Test
    public void testModObjOnceAddDissOneMethodNoParmsLv2()
            throws Exception {
        doModObjAddDissTest(2, getBDefOne(), getBMechOne());
    }

    private void doModObjAddDissTest(int riLevel,
                                     DigitalObject bDef,
                                     DigitalObject bMech)
            throws Exception {

        DigitalObject original = getTestObject("test:1", "test");
        addEDatastream(original, "DS1");

        DigitalObject modified = deepCopy(original);
//        addDisseminator(modified, "DISS1", bDef.getPid(), bMech.getPid(), 
//                getBindings(1));

        doModifyTest(riLevel, getObjectSet(bMech, bDef, original), modified);
    }

    /**
     * Delete a one-method, no-param disseminator from an existing object.
     */
    @Test
    public void testModObjOnceDelDissOneMethodNoParms()
            throws Exception {
        doModObjDelDissTest(1, getBDefOne(), getBMechOne());
    }

    /**
     * Delete a one-method, no-param disseminator from an existing object
     * (RI level 2)
     */
    @Test
    public void testModObjOnceDelDissOneMethodNoParmsLv2()
            throws Exception {
        doModObjDelDissTest(2, getBDefOne(), getBMechOne());
    }

    private void doModObjDelDissTest(int riLevel,
                                     DigitalObject bDef,
                                     DigitalObject bMech)
            throws Exception {

        DigitalObject original = getTestObject("test:1", "test");
        addEDatastream(original, "DS1");
//        addDisseminator(original, "DISS1", bDef.getPid(), bMech.getPid(), 
//                getBindings(1));

        DigitalObject modified = deepCopy(original);
//        modified.disseminators("DISS1").clear();

        doModifyTest(riLevel, getObjectSet(bDef, bMech, original), modified);
    }

    /**
     * Add a one-method, fixed-param disseminator to an existing object.
     */
    @Test
    public void testModObjOnceAddDissOneMethodFixedParm()
            throws Exception {
        doModObjAddDissTest(1, getBDefTwo(), getBMechTwo());
    }

    /**
     * Add a one-method, fixed-param disseminator to an existing object
     * (RI level 2).
     */
    @Test
    public void testModObjOnceAddDissOneMethodFixedParmLv2()
            throws Exception {
        doModObjAddDissTest(2, getBDefTwo(), getBMechTwo());
    }

    /**
     * Delete a one-method, fixed-param disseminator from an existing object.
     */
    @Test
    public void testModObjOnceDelDissOneMethodFixedParm()
            throws Exception {
        doModObjDelDissTest(1, getBDefTwo(), getBMechTwo());
    }

    /**
     * Delete a one-method, fixed-param disseminator from an existing object
     * (RI level 2).
     */
    @Test
    public void testModObjOnceDelDissOneMethodFixedParmLv2()
            throws Exception {
        doModObjDelDissTest(2, getBDefTwo(), getBMechTwo());
    }

    /**
     * Add a one-method, unfixed-param disseminator to an existing object.
     */
    @Test
    public void testModObjOnceAddDissOneMethodUnfixedParm()
            throws Exception {
        doModObjAddDissTest(1, getBDefThree(), getBMechThree());
    }

    /**
     * Add a one-method, unfixed-param disseminator to an existing object
     * (RI level 2).
     */
    @Test
    public void testModObjOnceAddDissOneMethodUnfixedParmLv2()
            throws Exception {
        doModObjAddDissTest(2, getBDefThree(), getBMechThree());
    }

    /**
     * Delete a one-method, unfixed-param disseminator from an existing object.
     */
    @Test
    public void testModObjOnceDelDissOneMethodUnfixedParm() 
            throws Exception {
        doModObjDelDissTest(1, getBDefThree(), getBMechThree());
    }

    /**
     * Delete a one-method, unfixed-param disseminator from an existing object
     * (RI level 2).
     */
    @Test
    public void testModObjOnceDelDissOneMethodUnfixedParmLv2()
            throws Exception {
        doModObjDelDissTest(2, getBDefThree(), getBMechThree());
    }

    // Supports legacy test runners
    public static junit.framework.Test suite() {
        return new junit.framework.JUnit4TestAdapter(
                ResourceIndexModDissIntegrationTest.class);
    }
    
}

package fedora.server.resourceIndex;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.junit.Test;

import fedora.server.storage.types.DigitalObject;

/**
 * Tests adding and deleting objects from the RI, with respect to their
 * disseminators.
 *
 * Note: All tests run at RI level 1 unless otherwise noted.
 *
 * @author cwilper@cs.cornell.edu
 */
public class ResourceIndexAddDelDissIntegrationTest 
        extends ResourceIndexIntegrationTest {

    /**
     * Add, then delete an object with one datastream used by one disseminator
     * with one no-parameter method.
     */
    @Test
    public void testAddDelObjOneDSOneDissOneMethodNoParms()
            throws Exception {
        doAddDelTest(1, getObjectSet(getBDefOne(),
                                     getBMechOne(), 
                                     getObjectWithDissem("test:bdef1", 
                                                         "test:bmech1", 1)));
    }

    /**
     * Add, then delete an object with one datastream used by one disseminator
     * with one no-parameter method.
     * (RI level 2).
     */
    @Test
    public void testAddDelObjOneDSOneDissOneMethodNoParmsLv2()
            throws Exception {
        doAddDelTest(2, getObjectSet(getBDefOne(),
                                     getBMechOne(), 
                                     getObjectWithDissem("test:bdef1", 
                                                         "test:bmech1", 1)));
    }

    /**
     * Add, then delete an object with one datastream used by one disseminator
     * with a required one-parameter method with two possible values.
     */
    @Test
    public void testAddDelObjOneDSOneDissOneMethodFixedParm()
            throws Exception {
        doAddDelTest(1, getObjectSet(getBDefTwo(),
                                     getBMechTwo(), 
                                     getObjectWithDissem("test:bdef2", 
                                                         "test:bmech2", 1)));
    }

    /**
     * Add, then delete an object with one datastream used by one disseminator
     * with a required one-parameter method with two possible values.
     * (RI level 2).
     */
    @Test
    public void testAddDelObjOneDSOneDissOneMethodFixedParmLv2()
            throws Exception {
        doAddDelTest(2, getObjectSet(getBDefTwo(),
                                     getBMechTwo(), 
                                     getObjectWithDissem("test:bdef2", 
                                                         "test:bmech2", 1)));
    }

    /**
     * Add, then delete an object with one datastream used by one disseminator
     * with an optional one-parameter method with any possible value.
     */
    @Test
    public void testAddDelObjOneDSOneDissOneMethodUnfixedParm()
            throws Exception {
        doAddDelTest(1, getObjectSet(getBDefThree(),
                                     getBMechThree(), 
                                     getObjectWithDissem("test:bdef3", 
                                                         "test:bmech3", 1)));
    }

    /**
     * Add, then delete an object with one datastream used by one disseminator
     * with an optional one-parameter method with any possible value
     * (RI level 2).
     */
    @Test
    public void testAddDelObjOneDSOneDissOneMethodUnfixedParmLv2()
            throws Exception {
        doAddDelTest(2, getObjectSet(getBDefThree(),
                                     getBMechThree(), 
                                     getObjectWithDissem("test:bdef3", 
                                                         "test:bmech3", 1)));
    }

    /**
     * Add, then delete an object with one datastream used by one disseminator
     * with two methods that take no parameters.
     */
    @Test
    public void testAddDelObjOneDSOneDissTwoMethodsNoParms()
            throws Exception {
        doAddDelTest(1, getObjectSet(getBDefOneB(),
                                     getBMechOneB(), 
                                     getObjectWithDissem("test:bdef1b", 
                                                         "test:bmech1b", 2)));
    }

    /**
     * Add, then delete an object with one datastream used by one disseminator
     * with two no-parameter methods.
     * (RI level 2).
     */
    @Test
    public void testAddDelObjOneDSOneDissTwoMethodsNoParmsLv2()
            throws Exception {
        doAddDelTest(2, getObjectSet(getBDefOneB(),
                                     getBMechOneB(), 
                                     getObjectWithDissem("test:bdef1b", 
                                                         "test:bmech1b", 2)));
    }

    /**
     * Add, then delete an object with one datastream used by one disseminator
     * with two one-parameter methods with two possible values.
     */
    @Test
    public void testAddDelObjOneDSOneDissTwoMethodsBothFixedParms()
            throws Exception {
        doAddDelTest(1, getObjectSet(getBDefTwoB(),
                                     getBMechTwoB(), 
                                     getObjectWithDissem("test:bdef2b", 
                                                         "test:bmech2b", 2)));
    }

    /**
     * Add, then delete an object with one datastream used by one disseminator
     * with two one-parameter methods with two possible values.
     * (RI level 2).
     */
    @Test
    public void testAddDelObjOneDSOneDissTwoMethodsBothFixedParmsLv2()
            throws Exception {
        doAddDelTest(2, getObjectSet(getBDefTwoB(),
                                     getBMechTwoB(), 
                                     getObjectWithDissem("test:bdef2b", 
                                                         "test:bmech2b", 2)));
    }

    /**
     * Add, then delete an object with one datastream used by one disseminator
     * with two one-parameter methods with any possible value.
     */
    @Test
    public void testAddDelObjOneDSOneDissTwoMethodsBothUnfixedParms()
            throws Exception {
        doAddDelTest(1, getObjectSet(getBDefThreeB(),
                                     getBMechThreeB(), 
                                     getObjectWithDissem("test:bdef3b", 
                                                         "test:bmech3b", 2)));
    }

    /**
     * Add, then delete an object with one datastream used by one disseminator
     * with two one-parameter methods with any possible value.
     * (RI level 2).
     */
    @Test
    public void testAddDelObjOneDSOneDissTwoMethodsBothUnfixedParmsLv2()
            throws Exception {
        doAddDelTest(2, getObjectSet(getBDefThreeB(),
                                     getBMechThreeB(), 
                                     getObjectWithDissem("test:bdef3b", 
                                                         "test:bmech3b", 2)));
    }

    /**
     * Add, then delete an object with one datastream used by one disseminator
     * with two one-parameter methods, the first taking two possible values
     * and the second taking any possible value.
     */
    @Test
    public void testAddDelObjOneDSOneDissTwoMethodsOneFixedOneUnfixedParm()
            throws Exception {
        doAddDelTest(1, getObjectSet(getBDefFour(),
                                     getBMechFour(), 
                                     getObjectWithDissem("test:bdef4", 
                                                         "test:bmech4", 2)));
    }

    /**
     * Add, then delete an object with one datastream used by one disseminator
     * with two one-parameter methods, the first taking two possible values
     * and the second taking any possible value
     * (RI level 2).
     */
    @Test
    public void testAddDelObjOneDSOneDissTwoMethodsOneFixedOneUnfixedParmLv2()
            throws Exception {
        doAddDelTest(2, getObjectSet(getBDefFour(),
                                     getBMechFour(), 
                                     getObjectWithDissem("test:bdef4", 
                                                         "test:bmech4", 2)));
    }

    /**
     * Add, then delete an object with one datastream used by two 
     * disseminators, each with one no-parameter method.
     */
    @Test
    public void testAddDelObjOneDSTwoDissOneMethodNoParms()
            throws Exception {
        doAddDelObjOneDSTwoDissOneMethodNoParms(1);
    }

    /**
     * Add, then delete an object with one datastream used by two 
     * disseminators, each with one no-parameter method
     * (RI level 2).
     */
    @Test
    public void testAddDelObjOneDSTwoDissOneMethodNoParmsLv2()
            throws Exception {
        doAddDelObjOneDSTwoDissOneMethodNoParms(2);
    }

    private void doAddDelObjOneDSTwoDissOneMethodNoParms(int riLevel)
            throws Exception {

        DigitalObject obj = getTestObject("test:1", "test");
        addEDatastream(obj, "DS1");
        Map<String, String> bindings = new HashMap<String, String>();
        bindings.put("KEY1", "DS1");
        addDisseminator(obj, "DISS1", "test:bdef1", "test:bmech1", bindings);
        addDisseminator(obj, "DISS2", "test:bdef1c", "test:bmech1c", bindings);

        Set<DigitalObject> objects = new HashSet<DigitalObject>();
        objects.add(obj);
        objects.add(getBDefOne());
        objects.add(getBMechOne());
        objects.add(getBDefOneC());
        objects.add(getBMechOneC());

        doAddDelTest(riLevel, objects);
    }

    /**
     * Add, then delete an object with two datastreams used by one
     * disseminator with one no-parameter method.
     */
    @Test
    public void testAddDelObjTwoDSOneDissOneMethodNoParms()
            throws Exception {
        doAddDelObjTwoDSOneDissOneMethodNoParms(1);
    }

    /**
     * Add, then delete an object with two datastreams used by one
     * disseminator with one no-parameter method
     * (RI level 2).
     */
    @Test
    public void testAddDelObjTwoDSOneDissOneMethodNoParmsLv2()
            throws Exception {
        doAddDelObjTwoDSOneDissOneMethodNoParms(2);
    }

    private void doAddDelObjTwoDSOneDissOneMethodNoParms(int riLevel)
            throws Exception {

        DigitalObject obj = getTestObject("test:1", "test");
        addEDatastream(obj, "DS1");
        addEDatastream(obj, "DS2");
        Map<String, String> bindings = new HashMap<String, String>();
        bindings.put("KEY1", "DS1");
        bindings.put("KEY2", "DS2");
        addDisseminator(obj, "DISS1", "test:bdef1", "test:bmech1d", bindings);

        Set<DigitalObject> objects = new HashSet<DigitalObject>();
        objects.add(obj);
        objects.add(getBDefOne());
        objects.add(getBMechOneD());

        doAddDelTest(riLevel, objects);
    }

    // Supports legacy test runners
    public static junit.framework.Test suite() {
        return new junit.framework.JUnit4TestAdapter(
                ResourceIndexAddDelDissIntegrationTest.class);
    }

}

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

    // bdef:1 has one no-parameter method
    private static DigitalObject getBDefOne() {
        Set<ParamDomainMap> methodDefs = new HashSet<ParamDomainMap>();
        ParamDomainMap methodOne = new ParamDomainMap("methodOne");
        methodDefs.add(methodOne);
        return getTestBDef("test:bdef1", "bdef1", methodDefs);
    }

    // bdef:1b has two no-parameter methods
    private static DigitalObject getBDefOneB() {
        Set<ParamDomainMap> methodDefs = new HashSet<ParamDomainMap>();
        ParamDomainMap methodOne = new ParamDomainMap("methodOne");
        methodDefs.add(methodOne);
        ParamDomainMap methodTwo = new ParamDomainMap("methodTwo");
        methodDefs.add(methodTwo);
        return getTestBDef("test:bdef1b", "bdef1b", methodDefs);
    }

    // bdef:1c has one no-parameter method (same as bdef:1)
    private static DigitalObject getBDefOneC() {
        Set<ParamDomainMap> methodDefs = new HashSet<ParamDomainMap>();
        ParamDomainMap methodOne = new ParamDomainMap("methodOne");
        methodDefs.add(methodOne);
        return getTestBDef("test:bdef1c", "bdef1c", methodDefs);
    }

    // bdef:2 has one required one-parameter method with two possible values
    private static DigitalObject getBDefTwo() {
        Set<ParamDomainMap> methodDefs = new HashSet<ParamDomainMap>();
        ParamDomainMap methodOne = new ParamDomainMap("methodOne");
        ParamDomain argOneDomain = new ParamDomain("argOne", true);
        argOneDomain.add("val1");
        argOneDomain.add("val2");
        methodOne.put("argOne", argOneDomain);
        methodDefs.add(methodOne);
        return getTestBDef("test:bdef2", "bdef2", methodDefs);
    }

    // bdef:2b has two required one-parameter methods with two possible values
    private static DigitalObject getBDefTwoB() {
        Set<ParamDomainMap> methodDefs = new HashSet<ParamDomainMap>();
        ParamDomainMap methodOne = new ParamDomainMap("methodOne");
        ParamDomain argOneDomain = new ParamDomain("argOne", true);
        argOneDomain.add("val1");
        argOneDomain.add("val2");
        methodOne.put("argOne", argOneDomain);
        methodDefs.add(methodOne);
        ParamDomainMap methodTwo = new ParamDomainMap("methodTwo");
        methodTwo.put("argOne", argOneDomain);
        methodDefs.add(methodTwo);
        return getTestBDef("test:bdef2b", "bdef2b", methodDefs);
    }

    // bdef:3 has one optional one-parameter method with any possible value
    private static DigitalObject getBDefThree() {
        Set<ParamDomainMap> methodDefs = new HashSet<ParamDomainMap>();
        ParamDomainMap methodOne = new ParamDomainMap("methodOne");
        ParamDomain argOneDomain = new ParamDomain("argOne", false);
        methodOne.put("argOne", argOneDomain);
        methodDefs.add(methodOne);
        return getTestBDef("test:bdef3", "bdef3", methodDefs);
    }

    // bdef:3b has two optional one-parameter methods with any possible value
    private static DigitalObject getBDefThreeB() {
        Set<ParamDomainMap> methodDefs = new HashSet<ParamDomainMap>();
        ParamDomainMap methodOne = new ParamDomainMap("methodOne");
        ParamDomain argOneDomain = new ParamDomain("argOne", false);
        methodOne.put("argOne", argOneDomain);
        methodDefs.add(methodOne);
        ParamDomainMap methodTwo = new ParamDomainMap("methodTwo");
        methodTwo.put("argOne", argOneDomain);
        methodDefs.add(methodTwo);
        return getTestBDef("test:bdef3b", "bdef3b", methodDefs);
    }

    // bdef:4 has two one-parameter methods, one required with two possible 
    // values and the other optional with any possible value
    private static DigitalObject getBDefFour() {
        Set<ParamDomainMap> methodDefs = new HashSet<ParamDomainMap>();
        ParamDomainMap methodOne = new ParamDomainMap("methodOne");
        ParamDomain argOneDomain = new ParamDomain("argOne", true);
        argOneDomain.add("val1");
        argOneDomain.add("val2");
        methodOne.put("argOne", argOneDomain);
        methodDefs.add(methodOne);
        ParamDomainMap methodTwo = new ParamDomainMap("methodTwo");
        argOneDomain = new ParamDomain("argOne", false);
        methodTwo.put("argOne", argOneDomain);
        methodDefs.add(methodTwo);
        return getTestBDef("test:bdef4", "bdef4", methodDefs);
    }

    private static Map<String, Set<String>> getMap(String key1,
                                                   String[] values1,
                                                   String key2,
                                                   String[] values2) {
        Set<String> valueSet1 = new HashSet<String>();
        for (String value : values1) {
            valueSet1.add(value);
        }
        Map<String, Set<String>> map = new HashMap<String, Set<String>>();
        map.put(key1, valueSet1);
        if (key2 != null) {
            Set<String> valueSet2 = new HashSet<String>();
            for (String value : values2) {
                valueSet2.add(value);
            }
            map.put(key2, valueSet2);
        }
        return map;
    }

    // bmech:1 implements bdef:1 and takes one datastream
    private static DigitalObject getBMechOne() {
        Set<ParamDomainMap> methodDefs = new HashSet<ParamDomainMap>();
        ParamDomainMap methodOne = new ParamDomainMap("methodOne");
        methodDefs.add(methodOne);

        return getTestBMech("test:bmech1", "bmech1", "test:bdef1", 
                methodDefs, 
                getMap("methodOne", new String[] {"KEY1"}, null, null),
                getMap("KEY1", new String[] {"text/xml"}, null, null),
                getMap("methodOne", new String[] {"text/xml"}, null, null));
    }

    // bmech:1b implements bdef:1b and takes one datastream
    private static DigitalObject getBMechOneB() {
        Set<ParamDomainMap> methodDefs = new HashSet<ParamDomainMap>();
        ParamDomainMap methodOne = new ParamDomainMap("methodOne");
        methodDefs.add(methodOne);
        ParamDomainMap methodTwo = new ParamDomainMap("methodTwo");
        methodDefs.add(methodTwo);

        return getTestBMech("test:bmech1b", "bmech1b", "test:bdef1b", 
                methodDefs, 
                getMap("methodOne", new String[] {"KEY1"}, 
                       "methodTwo", new String[] {"KEY2"}),
                getMap("KEY1", new String[] {"text/xml"}, 
                       "KEY2", new String[] {"text/xml"}),
                getMap("methodOne", new String[] {"text/xml"},
                       "methodTwo", new String[] {"text/xml"}));
    }

    // bmech:1c implements bdef:1c and takes one datastream
    private static DigitalObject getBMechOneC() {
        Set<ParamDomainMap> methodDefs = new HashSet<ParamDomainMap>();
        ParamDomainMap methodOne = new ParamDomainMap("methodOne");
        methodDefs.add(methodOne);

        return getTestBMech("test:bmech1c", "bmech1c", "test:bdef1c", 
                methodDefs, 
                getMap("methodOne", new String[] {"KEY1"}, null, null),
                getMap("KEY1", new String[] {"text/xml"}, null, null),
                getMap("methodOne", new String[] {"text/xml"}, null, null));
    }

    // bmech:1d implements bdef:1 and takes TWO datastreams
    private static DigitalObject getBMechOneD() {
        Set<ParamDomainMap> methodDefs = new HashSet<ParamDomainMap>();
        ParamDomainMap methodOne = new ParamDomainMap("methodOne");
        methodDefs.add(methodOne);

        return getTestBMech("test:bmech1d", "bmech1d", "test:bdef1", 
                methodDefs, 
                getMap("methodOne", new String[] {"KEY1", "KEY2"}, null, null),
                getMap("KEY1", new String[] {"text/xml"}, "KEY2", new String[] {"text/xml"}),
                getMap("methodOne", new String[] {"text/xml"}, null, null));
    }

    // bmech:2 implements bdef:2 and takes one datastream
    private static DigitalObject getBMechTwo() {
        Set<ParamDomainMap> methodDefs = new HashSet<ParamDomainMap>();
        ParamDomainMap methodOne = new ParamDomainMap("methodOne");
        ParamDomain argOneDomain = new ParamDomain("argOne", true);
        argOneDomain.add("val1");
        argOneDomain.add("val2");
        methodOne.put("argOne", argOneDomain);
        methodDefs.add(methodOne);

        return getTestBMech("test:bmech2", "bmech2", "test:bdef2", 
                methodDefs, 
                getMap("methodOne", new String[] {"KEY1"}, null, null),
                getMap("KEY1", new String[] {"text/xml"}, null, null),
                getMap("methodOne", new String[] {"text/xml"}, null, null));
    }

    // bmech:2b implements bdef:2b and takes one datastream
    private static DigitalObject getBMechTwoB() {
        Set<ParamDomainMap> methodDefs = new HashSet<ParamDomainMap>();
        ParamDomainMap methodOne = new ParamDomainMap("methodOne");
        ParamDomain argOneDomain = new ParamDomain("argOne", true);
        argOneDomain.add("val1");
        argOneDomain.add("val2");
        methodOne.put("argOne", argOneDomain);
        methodDefs.add(methodOne);
        ParamDomainMap methodTwo = new ParamDomainMap("methodTwo");
        methodTwo.put("argOne", argOneDomain);
        methodDefs.add(methodTwo);

        return getTestBMech("test:bmech2b", "bmech2b", "test:bdef2b", 
                methodDefs, 
                getMap("methodOne", new String[] {"KEY1"}, 
                       "methodTwo", new String[] {"KEY2"}),
                getMap("KEY1", new String[] {"text/xml"}, 
                       "KEY2", new String[] {"text/xml"}),
                getMap("methodOne", new String[] {"text/xml"},
                       "methodTwo", new String[] {"text/xml"}));
    }

    // bmech:3 implements bdef:3 and takes one datastream
    private static DigitalObject getBMechThree() {
        Set<ParamDomainMap> methodDefs = new HashSet<ParamDomainMap>();
        ParamDomainMap methodOne = new ParamDomainMap("methodOne");
        ParamDomain argOneDomain = new ParamDomain("argOne", false);
        methodOne.put("argOne", argOneDomain);
        methodDefs.add(methodOne);

        return getTestBMech("test:bmech3", "bmech3", "test:bdef3", 
                methodDefs, 
                getMap("methodOne", new String[] {"KEY1"}, null, null),
                getMap("KEY1", new String[] {"text/xml"}, null, null),
                getMap("methodOne", new String[] {"text/xml"}, null, null));
    }

    // bmech:3b implements bdef:3b and takes one datastream
    private static DigitalObject getBMechThreeB() {
        Set<ParamDomainMap> methodDefs = new HashSet<ParamDomainMap>();
        ParamDomainMap methodOne = new ParamDomainMap("methodOne");
        ParamDomain argOneDomain = new ParamDomain("argOne", false);
        methodOne.put("argOne", argOneDomain);
        methodDefs.add(methodOne);
        ParamDomainMap methodTwo = new ParamDomainMap("methodTwo");
        methodTwo.put("argOne", argOneDomain);
        methodDefs.add(methodTwo);

        return getTestBMech("test:bmech3b", "bmech3b", "test:bdef3b", 
                methodDefs, 
                getMap("methodOne", new String[] {"KEY1"}, 
                       "methodTwo", new String[] {"KEY2"}),
                getMap("KEY1", new String[] {"text/xml"}, 
                       "KEY2", new String[] {"text/xml"}),
                getMap("methodOne", new String[] {"text/xml"},
                       "methodTwo", new String[] {"text/xml"}));
    }

    // bmech:4 implements bdef:4 and takes one datastream
    private static DigitalObject getBMechFour() {
        Set<ParamDomainMap> methodDefs = new HashSet<ParamDomainMap>();
        ParamDomainMap methodOne = new ParamDomainMap("methodOne");
        ParamDomain argOneDomain = new ParamDomain("argOne", true);
        argOneDomain.add("val1");
        argOneDomain.add("val2");
        methodOne.put("argOne", argOneDomain);
        methodDefs.add(methodOne);
        ParamDomainMap methodTwo = new ParamDomainMap("methodTwo");
        argOneDomain = new ParamDomain("argOne", false);
        methodTwo.put("argOne", argOneDomain);
        methodDefs.add(methodTwo);

        return getTestBMech("test:bmech4", "bmech4", "test:bdef4", 
                methodDefs, 
                getMap("methodOne", new String[] {"KEY1"}, 
                       "methodTwo", new String[] {"KEY2"}),
                getMap("KEY1", new String[] {"text/xml"}, 
                       "KEY2", new String[] {"text/xml"}),
                getMap("methodOne", new String[] {"text/xml"},
                       "methodTwo", new String[] {"text/xml"}));
    }

    /**
     * Add, then delete our test BDef and BMech objects.
     */
//    @Test
    public void testAddDelBDefsBMechs()
            throws Exception {
        Set<DigitalObject> objects = new HashSet<DigitalObject>();

        objects.add(getBDefOne());
        objects.add(getBDefOneB());
        objects.add(getBDefOneC());
        objects.add(getBDefTwo());
        objects.add(getBDefTwoB());
        objects.add(getBDefThree());
        objects.add(getBDefThreeB());
        objects.add(getBDefFour());

        objects.add(getBMechOne());
        objects.add(getBMechOneB());
        objects.add(getBMechOneC());
        objects.add(getBMechOneD());
        objects.add(getBMechTwo());
        objects.add(getBMechTwoB());
        objects.add(getBMechThree());
        objects.add(getBMechThreeB());
        objects.add(getBMechFour());

        doAddDelTest(1, objects);
    }

    // bdef1(bmech1)

    /**
     * Add, then delete an object with one datastream used by one disseminator
     * with one no-parameter method.
     */
    @Test
    public void testAddDelObjOneDSOneDissOneMethodNoParms()
            throws Exception {
        doAddDelObjOneDSOneDissOneMethodNoParms(1);
    }

    /**
     * Add, then delete an object with one datastream used by one disseminator
     * with one no-parameter method.
     * (RI level 2).
     */
    @Test
    public void testAddDelObjOneDSOneDissOneMethodNoParmsLv2()
            throws Exception {
        doAddDelObjOneDSOneDissOneMethodNoParms(2);
    }

    private void doAddDelObjOneDSOneDissOneMethodNoParms(int riLevel)
            throws Exception {
        Set<DigitalObject> objects = new HashSet<DigitalObject>();

        objects.add(getBDefOne());
        objects.add(getBMechOne());
        
        DigitalObject obj = getTestObject("test:1", "test");
        addEDatastream(obj, "DS1");
        Map<String, String> bindings = new HashMap<String, String>();
        bindings.put("KEY1", "DS1");
        addDisseminator(obj, "DISS1", "test:bdef1", "test:bmech1", bindings);

        objects.add(obj);
        doAddDelTest(riLevel, objects);
    }

    // bdef2(bmech2)

    /**
     * Add, then delete an object with one datastream used by one disseminator
     * with a required one-parameter method with two possible values.
     */
    @Test
    public void testAddDelObjOneDSOneDissOneMethodFixedParm()
            throws Exception {
    }

    /**
     * Add, then delete an object with one datastream used by one disseminator
     * with a required one-parameter method with two possible values.
     * (RI level 2).
     */
    @Test
    public void testAddDelObjOneDSOneDissOneMethodFixedParmLv2()
            throws Exception {
    }

    // bdef3(bmech3)

    /**
     * Add, then delete an object with one datastream used by one disseminator
     * with an optional one-parameter method with any possible value.
     */
    @Test
    public void testAddDelObjOneDSOneDissOneMethodUnfixedParm()
            throws Exception {
    }

    /**
     * Add, then delete an object with one datastream used by one disseminator
     * with an optional one-parameter method with any possible value
     * (RI level 2).
     */
    @Test
    public void testAddDelObjOneDSOneDissOneMethodUnfixedParmLv2()
            throws Exception {
    }

    // bdef1b(bmech1b)

    /**
     * Add, then delete an object with one datastream used by one disseminator
     * with two methods that take no parameters.
     */
    @Test
    public void testAddDelObjOneDSOneDissTwoMethodsNoParms()
            throws Exception {
    }

    /**
     * Add, then delete an object with one datastream used by one disseminator
     * with two no-parameter methods.
     * (RI level 2).
     */
    @Test
    public void testAddDelObjOneDSOneDissTwoMethodsNoParmsLv2()
            throws Exception {
    }

    // bdef2b(bmech2b)

    /**
     * Add, then delete an object with one datastream used by one disseminator
     * with two one-parameter methods with two possible values.
     */
    @Test
    public void testAddDelObjOneDSOneDissTwoMethodsBothFixedParms()
            throws Exception {
    }

    /**
     * Add, then delete an object with one datastream used by one disseminator
     * with two one-parameter methods with two possible values.
     * (RI level 2).
     */
    @Test
    public void testAddDelObjOneDSOneDissTwoMethodsBothFixedParmsLv2()
            throws Exception {
    }

    // bdef3b(bmech3b)

    /**
     * Add, then delete an object with one datastream used by one disseminator
     * with two one-parameter methods with any possible value.
     */
    @Test
    public void testAddDelObjOneDSOneDissTwoMethodsBothUnfixedParms()
            throws Exception {
    }

    /**
     * Add, then delete an object with one datastream used by one disseminator
     * with two one-parameter methods with any possible value.
     * (RI level 2).
     */
    @Test
    public void testAddDelObjOneDSOneDissTwoMethodsBothUnfixedParmsLv2()
            throws Exception {
    }

    // bdef4(bmech4)

    /**
     * Add, then delete an object with one datastream used by one disseminator
     * with two one-parameter methods, the first taking two possible values
     * and the second taking any possible value.
     */
    @Test
    public void testAddDelObjOneDSOneDissTwoMethodsOneFixedOneUnfixedParm()
            throws Exception {
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
    }

    // bdef1(bmech1),bdef1c(bmech1c)

    /**
     * Add, then delete an object with one datastream used by two 
     * disseminators, each with one no-parameter method.
     */
    @Test
    public void testAddDelObjOneDSTwoDissOneMethodNoParms()
            throws Exception {
    }

    /**
     * Add, then delete an object with one datastream used by two 
     * disseminators, each with one no-parameter method
     * (RI level 2).
     */
    @Test
    public void testAddDelObjOneDSTwoDissOneMethodNoParmsLv2()
            throws Exception {
    }

    // bdef1(bmech1d)

    /**
     * Add, then delete an object with two datastreams used by one
     * disseminator with one no-parameter method.
     */
    @Test
    public void testAddDelObjTwoDSOneDissOneMethodNoParms()
            throws Exception {
    }

    /**
     * Add, then delete an object with two datastreams used by one
     * disseminator with one no-parameter method
     * (RI level 2).
     */
    @Test
    public void testAddDelObjTwoDSOneDissOneMethodNoParmsLv2()
            throws Exception {
    }

    // Supports legacy test runners
    public static junit.framework.Test suite() {
        return new junit.framework.JUnit4TestAdapter(
                ResourceIndexAddDelDissIntegrationTest.class);
    }

}

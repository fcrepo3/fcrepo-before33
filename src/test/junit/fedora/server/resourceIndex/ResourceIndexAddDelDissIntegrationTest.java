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

    // bdef:2b has two required one-parameter method with two possible values
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

    /*
String pid, 
            String label,
            String bDefPID, 
            Set<ParamDomainMap> methodDefs,
            Map<String, Set<String>> inputKeys, 
            Map<String, Set<String>> inputTypes,
            Map<String, Set<String>> outputTypes) 
*/

    private static DigitalObject getBMechOne() {
        Set<ParamDomainMap> methodDefs = new HashSet<ParamDomainMap>();
        ParamDomainMap methodOne = new ParamDomainMap("methodOne");
        methodDefs.add(methodOne);

        Set<String> methodOneKeys = new HashSet<String>();
        methodOneKeys.add("KEY1");
        Map<String, Set<String>> inputKeys = new HashMap<String, Set<String>>();
        inputKeys.put("methodOne", methodOneKeys);

        Set<String> methodOneInputs = new HashSet<String>();
        methodOneInputs.add("text/xml");
        Map<String, Set<String>> inputTypes = new HashMap<String, Set<String>>();
        inputTypes.put("methodOne", methodOneInputs);

        Set<String> methodOneOutputs = new HashSet<String>();
        methodOneOutputs.add("text/xml");
        Map<String, Set<String>> outputTypes = new HashMap<String, Set<String>>();
        outputTypes.put("methodOne", methodOneOutputs);

        return getTestBMech("test:bmech1", "bmech1", "test:bdef1", methodDefs, 
                inputKeys, inputTypes, outputTypes);

    }

    /**
     * Add, then delete our test BDef objects.
     */
    @Test
    public void testAddDelBDefs()
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

        doAddDelTest(1, objects);
    }

    // bdef1

    /**
     * Add, then delete an object with one datastream used by one disseminator
     * with one no-parameter method.
     */
    @Test
    public void testAddDelObjOneDSOneDissOneMethodNoParms()
            throws Exception {
    }

    /**
     * Add, then delete an object with one datastream used by one disseminator
     * with one no-parameter method.
     * (RI level 2).
     */
    @Test
    public void testAddDelObjOneDSOneDissOneMethodNoParmsLv2()
            throws Exception {
    }

    // bdef2

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

    // bdef3

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

    // bdef1b

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

    // bdef2b

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

    // bdef3b

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

    // bdef4

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

    // bdef1,bdef1c

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

    // bdef1

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

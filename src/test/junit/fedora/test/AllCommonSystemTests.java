package fedora.test;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
	fedora.test.api.TestAPIA.class,
	fedora.test.api.TestAPIALite.class,
	fedora.test.api.TestAPIM.class,
	fedora.test.api.TestAPIMLite.class,
	fedora.test.integration.TestCommandLineUtilities.class
})

public class AllCommonSystemTests {

    // Supports legacy tests runners
    public static junit.framework.Test suite() throws Exception {

        junit.framework.TestSuite suite = 
                new junit.framework.TestSuite(AllCommonSystemTests.class.getName());
   
        suite.addTest(fedora.test.api.TestAPIA.suite());
        suite.addTest(fedora.test.api.TestAPIALite.suite());
        suite.addTest(fedora.test.api.TestAPIM.suite());
        suite.addTest(fedora.test.api.TestAPIMLite.suite());
        suite.addTest(fedora.test.api.TestHTTPStatusCodes.suite());
        suite.addTest(fedora.test.integration.TestCommandLineUtilities.suite());

        return suite;
    }
}


package fedora.test;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses( {fedora.test.api.TestAPIA.class,
        fedora.test.api.TestAPIALite.class, fedora.test.api.TestAPIM.class,
        fedora.test.api.TestAPIMLite.class,
        fedora.test.api.TestHTTPStatusCodes.class,
        fedora.test.api.TestManagedDatastreams.class,
        fedora.test.integration.TestOAIService.class,
        fedora.test.integration.TestCommandLineUtilities.class,
        fedora.test.integration.TestCommandLineFormats.class})
public class AllCommonSystemTests {

    // Supports legacy tests runners
    public static junit.framework.Test suite() throws Exception {

        junit.framework.TestSuite suite =
                new junit.framework.TestSuite(AllCommonSystemTests.class
                        .getName());

        suite.addTest(fedora.test.api.TestAPIA.suite());
        suite.addTest(fedora.test.api.TestAPIALite.suite());
        suite.addTest(fedora.test.api.TestAPIM.suite());
        suite.addTest(fedora.test.api.TestAPIMLite.suite());
        suite.addTest(fedora.test.api.TestHTTPStatusCodes.suite());
        suite.addTest(fedora.test.api.TestManagedDatastreams.suite());
        suite.addTest(fedora.test.integration.TestOAIService.suite());
        suite.addTest(fedora.test.integration.TestCommandLineUtilities.suite());
        suite.addTest(fedora.test.integration.TestCommandLineFormats.suite());

        return suite;
    }
}

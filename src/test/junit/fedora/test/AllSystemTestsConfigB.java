
package fedora.test;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses( {fedora.test.AllCommonSystemTests.class,
        fedora.test.api.TestAuthentication.class,
        fedora.test.api.TestHTTPStatusCodesConfigB.class,
        fedora.test.api.TestXACMLPolicies.class,
        fedora.test.api.TestRelationships.class,
        fedora.test.api.TestManagementNotifications.class})
public class AllSystemTestsConfigB {

    // Supports legacy tests runners
    public static junit.framework.Test suite() throws Exception {

        junit.framework.TestSuite suite =
                new junit.framework.TestSuite(AllSystemTestsConfigB.class
                        .getName());

        suite.addTest(fedora.test.AllCommonSystemTests.suite());
        suite.addTest(fedora.test.api.TestAuthentication.suite());
        suite.addTest(fedora.test.api.TestHTTPStatusCodesConfigB.suite());
        suite.addTest(fedora.test.api.TestXACMLPolicies.suite());
        suite.addTest(fedora.test.api.TestRelationships.suite());
        suite.addTest(fedora.test.api.TestManagementNotifications.suite());

        return suite;
    }
}

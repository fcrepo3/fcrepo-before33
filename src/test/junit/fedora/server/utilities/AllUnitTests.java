
package fedora.server.utilities;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses( {TestDateUtility.class, DCFieldsTest.class})
public class AllUnitTests {

    // Supports legacy tests runners
    public static junit.framework.Test suite() throws Exception {

        junit.framework.TestSuite suite =
                new junit.framework.TestSuite(AllUnitTests.class.getName());

        suite.addTestSuite(TestDateUtility.class);
        suite.addTestSuite(DCFieldsTest.class);

        return suite;
    }
}

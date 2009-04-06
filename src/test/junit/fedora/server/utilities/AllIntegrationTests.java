
package fedora.server.utilities;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses( {DerbyDDLConverterIntegrationTest.class})
public class AllIntegrationTests {

    // Supports legacy test runners
    public static junit.framework.Test suite() throws Exception {

        junit.framework.TestSuite suite =
                new junit.framework.TestSuite(AllIntegrationTests.class
                        .getName());

        suite.addTest(DerbyDDLConverterIntegrationTest.suite());

        return suite;

    }

}

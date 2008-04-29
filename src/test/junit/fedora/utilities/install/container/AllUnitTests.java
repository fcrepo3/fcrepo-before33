
package fedora.utilities.install.container;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses( {TestFedoraWebXML.class, TestTomcatServerXML.class})
public class AllUnitTests {

    // Supports legacy tests runners
    public static junit.framework.Test suite() throws Exception {

        junit.framework.TestSuite suite =
                new junit.framework.TestSuite(AllUnitTests.class.getName());

        suite.addTest(TestFedoraWebXML.suite());
        suite.addTest(TestTomcatServerXML.suite());

        return suite;
    }
}

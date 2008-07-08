
package fedora.server.validation;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses( {RelsExtValidatorTest.class, 
                      DOValidatorXMLSchemaTest.class,
                      ValidationUtilityTest.class})
public class AllUnitTests {

    // Supports legacy tests runners
    public static junit.framework.Test suite() throws Exception {

        junit.framework.TestSuite suite =
                new junit.framework.TestSuite(AllUnitTests.class.getName());

        suite.addTestSuite(RelsExtValidatorTest.class);
        suite.addTest(DOValidatorXMLSchemaTest.suite());
        suite.addTest(ValidationUtilityTest.suite());

        return suite;
    }
}

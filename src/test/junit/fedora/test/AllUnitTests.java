
package fedora.test;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses( {fedora.client.AllUnitTests.class,
                      fedora.common.AllUnitTests.class,
                      fedora.server.AllUnitTests.class,
                      fedora.utilities.AllUnitTests.class})
public class AllUnitTests {

    // Supports legacy tests runners
    public static junit.framework.Test suite() throws Exception {

        junit.framework.TestSuite suite =
                new junit.framework.TestSuite(AllUnitTests.class.getName());

        suite.addTest(fedora.client.AllUnitTests.suite());
        suite.addTest(fedora.common.AllUnitTests.suite());
        suite.addTest(fedora.server.AllUnitTests.suite());
        suite.addTest(fedora.utilities.AllUnitTests.suite());

        return suite;
    }
}

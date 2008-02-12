
package fedora.server;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses( {fedora.server.config.AllUnitTests.class,
        fedora.server.journal.AllUnitTests.class,
        fedora.server.search.AllUnitTests.class,
        fedora.server.storage.AllUnitTests.class,
        fedora.server.utilities.AllUnitTests.class,
        fedora.server.validation.AllUnitTests.class})
public class AllUnitTests {

    // Supports legacy tests runners
    public static junit.framework.Test suite() throws Exception {

        junit.framework.TestSuite suite =
                new junit.framework.TestSuite(AllUnitTests.class.getName());

        suite.addTest(fedora.server.config.AllUnitTests.suite());
        suite.addTest(fedora.server.journal.AllUnitTests.suite());
        suite.addTest(fedora.server.search.AllUnitTests.suite());
        suite.addTest(fedora.server.storage.AllUnitTests.suite());
        suite.addTest(fedora.server.utilities.AllUnitTests.suite());
        suite.addTest(fedora.server.validation.AllUnitTests.suite());

        return suite;
    }
}

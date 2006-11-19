package fedora.server;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
	fedora.server.journal.AllUnitTests.class
})

public class AllUnitTests {

    // Supports legacy tests runners
    public static junit.framework.Test suite() throws Exception {

        junit.framework.TestSuite suite = 
                new junit.framework.TestSuite(AllUnitTests.class.getName());
   
        suite.addTest(fedora.server.journal.AllUnitTests.suite());    

        return suite;
    }
}

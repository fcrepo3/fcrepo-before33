package fedora.test;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
	fedora.test.AllUnitTests.class,
	fedora.test.AllIntegrationTests.class
})

public class AllOfflineTests {

    // Supports legacy tests runners
    public static junit.framework.Test suite() throws Exception {

        junit.framework.TestSuite suite = 
                new junit.framework.TestSuite(AllOfflineTests.class.getName());
   
        suite.addTest(fedora.test.AllUnitTests.suite());    
        suite.addTest(fedora.test.AllIntegrationTests.suite());    

        return suite;
    }
}

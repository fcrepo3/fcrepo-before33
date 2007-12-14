package fedora.utilities;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
	TestFileUtils.class,
	TestZip.class
})

public class AllUnitTests {

    // Supports legacy tests runners
    public static junit.framework.Test suite() throws Exception {

        junit.framework.TestSuite suite = 
                new junit.framework.TestSuite(AllUnitTests.class.getName());
   
        suite.addTestSuite(TestFileUtils.class); 
        suite.addTestSuite(TestZip.class); 

        return suite;
    }
}

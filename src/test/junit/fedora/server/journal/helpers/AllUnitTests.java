package fedora.server.journal.helpers;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
	TestParameterHelper.class,
	TestPasswordCipher.class
})

public class AllUnitTests {

    // Supports legacy tests runners
    public static junit.framework.Test suite() throws Exception {

        junit.framework.TestSuite suite = 
                new junit.framework.TestSuite(AllUnitTests.class.getName());
   
        suite.addTestSuite(TestParameterHelper.class);    
        suite.addTestSuite(TestPasswordCipher.class);    

        return suite;
    }
}

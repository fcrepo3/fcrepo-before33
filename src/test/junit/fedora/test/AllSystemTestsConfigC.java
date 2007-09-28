package fedora.test;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
	fedora.test.api.TestAPIAConfigC.class,
	fedora.test.api.TestAPIALiteConfigC.class
})

public class AllSystemTestsConfigC {

    // Supports legacy tests runners
    public static junit.framework.Test suite() throws Exception {

        junit.framework.TestSuite suite = 
                new junit.framework.TestSuite(AllSystemTestsConfigC.class.getName());
   
        suite.addTest(fedora.test.api.TestAPIAConfigC.suite());
        suite.addTest(fedora.test.api.TestAPIALiteConfigC.suite());

        return suite;
    }
}

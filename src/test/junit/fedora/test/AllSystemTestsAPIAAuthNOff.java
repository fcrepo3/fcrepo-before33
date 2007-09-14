package fedora.test;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
	fedora.test.AllCommonSystemTests.class
})

public class AllSystemTestsAPIAAuthNOff {

    // Supports legacy tests runners
    public static junit.framework.Test suite() throws Exception {

        junit.framework.TestSuite suite = 
                new junit.framework.TestSuite(AllSystemTestsAPIAAuthNOff.class.getName());
   
        suite.addTest(fedora.test.AllCommonSystemTests.suite());

        return suite;
    }
}

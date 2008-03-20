
package fedora.server.journal.readerwriter.multicast.rmi;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses( {TestRmiTransportWriter.class,
        TestRmiJournalReceiver.class})
public class AllUnitTests {

    // Supports legacy tests runners
    public static junit.framework.Test suite() throws Exception {

        junit.framework.TestSuite suite =
                new junit.framework.TestSuite(AllUnitTests.class.getName());

        suite.addTest(TestRmiTransportWriter.suite());
        suite.addTest(TestRmiJournalReceiver.suite());

        return suite;
    }
}

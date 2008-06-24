
package fedora.client.utility.validate.process;

import junit.framework.JUnit4TestAdapter;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses( {TestValidatorProcessParameters.class})
public class AllUnitTests {

    // Supports legacy tests runners
    public static junit.framework.Test suite() {
        return new JUnit4TestAdapter(AllUnitTests.class);
    }
}

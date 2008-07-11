
package fedora.client.utility.validate;

import junit.framework.JUnit4TestAdapter;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses( {
        fedora.client.utility.validate.process.AllUnitTests.class,
        fedora.client.utility.validate.types.AllUnitTests.class,
        TestObjectValidator.class})
public class AllUnitTests {

    // Supports legacy tests runners
    public static junit.framework.Test suite() {
        return new JUnit4TestAdapter(AllUnitTests.class);
    }
}

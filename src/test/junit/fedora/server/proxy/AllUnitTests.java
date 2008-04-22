
package fedora.server.proxy;

import junit.framework.JUnit4TestAdapter;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses( {fedora.server.messaging.AtomAPIMMessageTest.class,
    fedora.server.proxy.ProxyFactoryTest.class})
public class AllUnitTests {

    // Supports legacy tests runners
    public static junit.framework.Test suite() throws Exception {
        return new JUnit4TestAdapter(AllUnitTests.class);
    }
}

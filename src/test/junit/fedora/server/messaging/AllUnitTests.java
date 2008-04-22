
package fedora.server.messaging;

import junit.framework.JUnit4TestAdapter;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses( {fedora.server.messaging.AtomAPIMMessageTest.class,
                           fedora.server.messaging.JMSManagerTest.class,
        fedora.server.messaging.NotificationInvocationHandlerTest.class})
public class AllUnitTests {

    // Supports legacy tests runners
    public static junit.framework.Test suite() throws Exception {
        return new JUnit4TestAdapter(AllUnitTests.class);
    }
}

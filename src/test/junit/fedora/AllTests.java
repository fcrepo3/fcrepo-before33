package fedora;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
  fedora.server.resourceIndex.AllTests.class
})

public class AllTests {}
package fedora.server.journal.helpers;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
	TestParameterHelper.class,
	TestPasswordCipher.class
})

public class AllTests {}
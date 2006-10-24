package fedora.server.journal;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
	fedora.server.journal.helpers.AllTests.class,
	fedora.server.journal.readerwriter.AllTests.class,
	fedora.server.journal.xmlhelpers.AllTests.class
})

public class AllTests {}
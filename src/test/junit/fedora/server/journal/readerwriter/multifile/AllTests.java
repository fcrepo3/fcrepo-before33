package fedora.server.journal.readerwriter.multifile;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
	TestLockingFollowingJournalReader.class
})

public class AllTests {}

package fedora.server.journal.readerwriter.multifile;

/**
 * <p>
 * <b>Title:</b> MultiFileJournalConstants.java
 * </p>
 * <p>
 * <b>Description:</b> Parameters, formats and default values for use by the
 * Multi-file journalling classes.
 * </p>
 * 
 * @author jblake@cs.cornell.edu
 * @version $Id: MultiFileJournalConstants.java 6760 2008-03-09 19:24:02 +0000
 *          (Sun, 09 Mar 2008) j2blake $
 */

public interface MultiFileJournalConstants {

    String PARAMETER_JOURNAL_DIRECTORY = "journalDirectory";

    String PARAMETER_ARCHIVE_DIRECTORY = "archiveDirectory";

    /** Used by following readers */
    String PARAMETER_FOLLOW_POLLING_INTERVAL = "followPollingInterval";

    String DEFAULT_FOLLOW_POLLING_INTERVAL = "3";

    /** Used by locking readers like {@link LockingFollowingJournalReader} */
    String PARAMETER_LOCK_REQUESTED_FILENAME = "lockRequestedFilename";

    /** Used by locking readers like {@link LockingFollowingJournalReader} */
    String PARAMETER_LOCK_ACCEPTED_FILENAME = "lockAcceptedFilename";

    /** Used by locking readers like {@link LockingFollowingJournalReader} */
    String PARAMETER_PAUSE_BEFORE_POLLING = "pauseBeforePolling";
}

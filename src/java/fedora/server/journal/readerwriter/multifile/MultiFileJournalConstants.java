/* The contents of this file are subject to the license and copyright terms
 * detailed in the license directory at the root of the source tree (also 
 * available online at http://www.fedora.info/license/).
 */

package fedora.server.journal.readerwriter.multifile;

/**
 * 
 * <p>
 * <b>Title:</b> MultiFileJournalConstants.java
 * </p>
 * <p>
 * <b>Description:</b> Parameters, formats and default values for use by the
 * {@link MultiFileJournalReader}, {@link MultiFileJournalWriter}, and
 * {@link MultiFileFollowingJournalReader} classes.
 * </p>
 * 
 * @author jblake@cs.cornell.edu
 * @version $Id$
 */

public interface MultiFileJournalConstants {
    String PARAMETER_JOURNAL_DIRECTORY = "journalDirectory";

    String PARAMETER_JOURNAL_FILENAME_PREFIX = "journalFilenamePrefix";

    String PARAMETER_JOURNAL_FILE_SIZE_LIMIT = "journalFileSizeLimit";

    String PARAMETER_JOURNAL_FILE_AGE_LIMIT = "journalFileAgeLimit";

    String PARAMETER_ARCHIVE_DIRECTORY = "archiveDirectory";

    String DEFAULT_FILENAME_PREFIX = "fedoraJournal";

    String DEFAULT_SIZE_LIMIT = "5M";

    String DEFAULT_AGE_LIMIT = "1D";

    String FORMAT_JOURNAL_FILENAME_TIMESTAMP = "yyyyMMdd.HHmmss.SSS";

    /** Used by following readers like {@link MultiFileFollowingJournalReader} */
    String PARAMETER_FOLLOW_POLLING_INTERVAL = "followPollingInterval";

    /** Used by following readers like {@link MultiFileFollowingJournalReader} */
    String DEFAULT_FOLLOW_POLLING_INTERVAL = "3";

    /** Used by locking readers like {@link LockingFollowingJournalReader} */
    String PARAMETER_LOCK_REQUESTED_FILENAME = "lockRequestedFilename";

    /** Used by locking readers like {@link LockingFollowingJournalReader} */
    String PARAMETER_LOCK_ACCEPTED_FILENAME = "lockAcceptedFilename";

    /** Used by locking readers like {@link LockingFollowingJournalReader} */
    String PARAMETER_PAUSE_BEFORE_POLLING = "pauseBeforePolling";
}

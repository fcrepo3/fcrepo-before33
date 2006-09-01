/*
 * -----------------------------------------------------------------------------
 *
 * <p><b>License and Copyright: </b>The contents of this file are subject to the
 * Educational Community License (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License
 * at <a href="http://www.opensource.org/licenses/ecl1.txt">
 * http://www.opensource.org/licenses/ecl1.txt.</a></p>
 *
 * <p>Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License for
 * the specific language governing rights and limitations under the License.</p>
 *
 * <p>The entire file consists of original code.  Copyright &copy; 2002-2006 by 
 * The Rector and Visitors of the University of Virginia and Cornell University.
 * All rights reserved.</p>
 *
 * -----------------------------------------------------------------------------
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

package fedora.server.journal.readerwriter.multifile;

import java.io.File;
import java.io.FileFilter;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.Map;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import fedora.server.journal.JournalConstants;
import fedora.server.journal.JournalException;

/**
 * 
 * <p>
 * <b>Title:</b> MultiFileJournalHelper.java
 * </p>
 * <p>
 * <b>Description:</b> Utility methods for use by the
 * {@link MultiFileJournalReader}, {@link MultiFileJournalWriter},
 * {@link MultiFileFollowingJournalReader}, and
 * {@link LockingFollowingJournalReader} classes.
 * </p>
 * 
 * @author jblake@cs.cornell.edu
 * @version $Id$
 */

public class MultiFileJournalHelper implements JournalConstants,
        MultiFileJournalConstants {

    /**
     * Get the value of a parameter if there is one, or the default value if
     * there isn't.
     */
    static String getOptionalParameter(Map parameters, String parameterName,
            String defaultValue) {
        String value = (String) parameters.get(parameterName);
        return (value == null) ? defaultValue : value;
    }

    /**
     * Get the requested parameter, or throw an exception if it is not found.
     */
    static String getRequiredParameter(Map parameters, String parameterName)
            throws JournalException {
        String value = (String) parameters.get(parameterName);
        if (value == null) {
            throw new JournalException("'" + parameterName + "' is required.");
        }
        return value;
    }

    /**
     * Inspect the parameters to find out what directory the journal files are
     * stored in. No default.
     */
    static File parseParametersForDirectory(Map parameters, String parameterName)
            throws JournalException {
        String directoryString = (String) parameters.get(parameterName);
        if (directoryString == null) {
            throw new JournalException("'" + parameterName + "' is required.");
        }
        File directory = new File(directoryString);
        if (!directory.exists()) {
            throw new JournalException("Directory '" + directory
                    + "' does not exist.");
        }
        if (!directory.isDirectory()) {
            throw new JournalException("Directory '" + directory
                    + "' is not a directory.");
        }
        if (!directory.canWrite()) {
            throw new JournalException("Directory '" + directory
                    + "' is not writable.");
        }
        return directory;
    }

    /**
     * Find the polling interval that we will choose when checking for new
     * journal files to appear.
     */
    static long parseParametersForPollingInterval(Map parameters)
            throws JournalException {
        String intervalString = (String) parameters
                .get(PARAMETER_FOLLOW_POLLING_INTERVAL);
        if (intervalString == null) {
            intervalString = DEFAULT_FOLLOW_POLLING_INTERVAL;
        }
        Pattern p = Pattern.compile("([0-9]+)([HM]?)");
        Matcher m = p.matcher(intervalString);
        if (!m.matches()) {
            throw new JournalException("Parameter '"
                    + PARAMETER_FOLLOW_POLLING_INTERVAL
                    + "' must be an positive integer number of seconds, "
                    + "optionally followed by 'H'(hours), or 'M'(minutes)");
        }
        long interval = Long.parseLong(m.group(1)) * 1000;
        String factor = m.group(2);
        if ("H".equals(factor)) {
            interval *= 60 * 60;
        } else if ("M".equals(factor)) {
            interval *= 60;
        }
        return interval;
    }

    /**
     * Look for a string to use as a prefix for the names of the journal files.
     * Default is "fedoraJournal"
     */
    static String parseParametersForFilenamePrefix(Map parameters) {
        return getOptionalParameter(parameters,
                PARAMETER_JOURNAL_FILENAME_PREFIX, DEFAULT_FILENAME_PREFIX);
    }

    /**
     * Create the name for a Journal file, based on the prefix and the current
     * date.
     */
    static String createJournalFilename(String filenamePrefix, Date date) {
        SimpleDateFormat formatter = new SimpleDateFormat(
                FORMAT_JOURNAL_FILENAME_TIMESTAMP);
        formatter.setTimeZone(TimeZone.getTimeZone("GMT"));
        return filenamePrefix + formatter.format(date) + "Z";
    }

    /**
     * Get the Journal Files that exist the Journal Directory, sorted by name.
     */
    static File[] getSortedArrayOfJournalFiles(File journalDirectory,
            String filenamePrefix) {
        JournalFileFilter filter = new JournalFileFilter(filenamePrefix);
        File[] journalFiles = journalDirectory.listFiles(filter);
        Arrays.sort(journalFiles, new FilenameComparator());
        return journalFiles;
    }

    /**
     * Allows us to search a directory for files that match the prefix.
     */
    private static class JournalFileFilter implements FileFilter {
        private final String filenamePrefix;

        JournalFileFilter(String filenamePrefix) {
            this.filenamePrefix = filenamePrefix;
        }

        public boolean accept(File file) {
            String filename = file.getName();
            return filename.startsWith(this.filenamePrefix);
        }
    }

    /**
     * A comparator that sorts files by their names.
     */
    private static class FilenameComparator implements Comparator {
        public int compare(Object first, Object second) {
            return ((File) first).getName()
                    .compareTo(((File) second).getName());
        }
    }

}

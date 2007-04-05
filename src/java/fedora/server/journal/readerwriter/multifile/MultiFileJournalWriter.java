/* The contents of this file are subject to the license and copyright terms
 * detailed in the license directory at the root of the source tree (also 
 * available online at http://www.fedora.info/license/).
 */

package fedora.server.journal.readerwriter.multifile;

import java.io.File;
import java.util.Date;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLStreamException;

import fedora.server.journal.JournalException;
import fedora.server.journal.JournalWriter;
import fedora.server.journal.ServerInterface;
import fedora.server.journal.entry.CreatorJournalEntry;

/**
 * 
 * <p>
 * <b>Title:</b> MultiFileJournalWriter.java
 * </p>
 * <p>
 * <b>Description:</b> An implementation of JournalWriter that writes a series
 * of Journal files to a specified directory. New files are begun when the
 * current file becomes too large or too old.
 * </p>
 * 
 * @author jblake@cs.cornell.edu
 * @version $Id$
 */

public class MultiFileJournalWriter extends JournalWriter implements
        MultiFileJournalConstants {

    /** the directory that will hold the journal files. */
    private final File journalDirectory;

    /** journal file names will start with this string. */
    private final String filenamePrefix;

    /** number of bytes before we start a new file - 0 means no limit */
    private final long sizeLimit;

    /** number of milliseconds before we start a new file - 0 means no limit */
    private final long ageLimit;

    /** the current journal file - start with a dummy that is already closed. */
    private JournalOutputFile currentJournal = JournalOutputFile.DUMMY_FILE;

    private boolean open = true;

    /**
     * Parse the parameters to find out how we are operating.
     */
    public MultiFileJournalWriter(Map parameters, String role,
            ServerInterface server) throws JournalException {
        super(parameters, role, server);
        this.journalDirectory = MultiFileJournalHelper
                .parseParametersForDirectory(parameters,
                        PARAMETER_JOURNAL_DIRECTORY);
        this.filenamePrefix = MultiFileJournalHelper
                .parseParametersForFilenamePrefix(parameters);
        this.sizeLimit = parseParametersForSizeLimit();
        this.ageLimit = parseParametersForAgeLimit();

        checkForPotentialFilenameConflict();
    }

    /**
     * Get the size limit parameter (or let it default), and convert it to
     * bytes.
     */
    private long parseParametersForSizeLimit() throws JournalException {
        String sizeString = MultiFileJournalHelper.getOptionalParameter(
                parameters, PARAMETER_JOURNAL_FILE_SIZE_LIMIT,
                DEFAULT_SIZE_LIMIT);
        Pattern p = Pattern.compile("([0-9]+)([KMG]?)");
        Matcher m = p.matcher(sizeString);
        if (!m.matches()) {
            throw new JournalException("Parameter '"
                    + PARAMETER_JOURNAL_FILE_SIZE_LIMIT
                    + "' must be an integer number of bytes, "
                    + "optionally followed by 'K', 'M', or 'G', "
                    + "or a 0 to indicate no size limit");
        }
        long size = Long.parseLong(m.group(1));
        String factor = m.group(2);
        if ("K".equals(factor)) {
            size *= 1024;
        } else if ("M".equals(factor)) {
            size *= 1024 * 1024;
        } else if ("G".equals(factor)) {
            size *= 1024 * 1024 * 1024;
        }
        return size;
    }

    /**
     * Get the age limit parameter (or let it default), and convert it to
     * milliseconds.
     */
    private long parseParametersForAgeLimit() throws JournalException {
        String ageString = MultiFileJournalHelper
                .getOptionalParameter(parameters,
                        PARAMETER_JOURNAL_FILE_AGE_LIMIT, DEFAULT_AGE_LIMIT);
        Pattern p = Pattern.compile("([0-9]+)([DHM]?)");
        Matcher m = p.matcher(ageString);
        if (!m.matches()) {
            throw new JournalException("Parameter '"
                    + PARAMETER_JOURNAL_FILE_AGE_LIMIT
                    + "' must be an integer number of seconds, optionally "
                    + "followed by 'D'(days), 'H'(hours), or 'M'(minutes), "
                    + "or a 0 to indicate no age limit");
        }
        long age = Long.parseLong(m.group(1)) * 1000;
        String factor = m.group(2);
        if ("D".equals(factor)) {
            age *= 24 * 60 * 60;
        } else if ("H".equals(factor)) {
            age *= 60 * 60;
        } else if ("M".equals(factor)) {
            age *= 60;
        }
        return age;
    }

    /**
     * Look at the list of files in the current directory, and make sure that
     * any new files we create won't conflict with them.
     */
    private void checkForPotentialFilenameConflict() throws JournalException {
        File[] journalFiles = MultiFileJournalHelper
                .getSortedArrayOfJournalFiles(journalDirectory, filenamePrefix);
        if (journalFiles.length == 0) {
            return;
        }

        String newestFilename = journalFiles[journalFiles.length - 1].getName();
        String potentialFilename = MultiFileJournalHelper
                .createTimestampedFilename(filenamePrefix, new Date());
        if (newestFilename.compareTo(potentialFilename) > 0) {
            throw new JournalException(
                    "The name of one or more existing files in the journal "
                            + "directory (e.g. '" + newestFilename
                            + "') may conflict with new Journal "
                            + "files. Has the system clock changed?");
        }
    }

    /**
     * Before writing an entry, check to see whether we need to close the
     * current file and/or open a new one.
     */
    public void prepareToWriteJournalEntry() throws JournalException {
        if (open) {
            this.currentJournal.closeIfAppropriate();

            if (!this.currentJournal.isOpen()) {
                this.currentJournal = new JournalOutputFile(this,
                        filenamePrefix, journalDirectory, sizeLimit, ageLimit);
            }
        }
    }

    /**
     * We've prepared for the entry, so just write it, but remember to
     * synchronize on the file, so we don't get an asynchronous close while
     * we're writing. After writing the entry, flush the file.
     */
    public void writeJournalEntry(CreatorJournalEntry journalEntry)
            throws JournalException {
        if (open) {
            try {
                synchronized (JournalWriter.SYNCHRONIZER) {
                    XMLEventWriter xmlWriter = this.currentJournal
                            .getXmlWriter();
                    super.writeJournalEntry(journalEntry, xmlWriter);
                    xmlWriter.flush();
                    this.currentJournal.closeIfAppropriate();
                }
            } catch (XMLStreamException e) {
                throw new JournalException(e);
            }
        }
    }

    /**
     * Close the current journal file.
     */
    public void shutdown() throws JournalException {
        if (open) {
            this.currentJournal.close();
            open = false;
        }
    }

    /**
     * A convenience method so the JournalOutputFile can request its own header.
     */
    void getDocumentHeader(XMLEventWriter xmlWriter) throws JournalException {
        super.writeDocumentHeader(xmlWriter);
    }

    /**
     * A convenience method so the JournalOutputFile can request its own
     * trailer.
     */
    void getDocumentTrailer(XMLEventWriter xmlWriter) throws JournalException {
        super.writeDocumentTrailer(xmlWriter);
    }

    /**
     * Create an informative message for debugging purposes.
     */
    public String toString() {
        return super.toString() + ", journalDirectory='" + journalDirectory
                + "', filenamePrefix='" + filenamePrefix + "', sizeLimit="
                + sizeLimit + "(bytes), ageLimit=" + ageLimit + "(msec)";
    }

}

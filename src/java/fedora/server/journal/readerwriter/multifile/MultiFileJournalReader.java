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

import java.io.File;
import java.util.Map;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

import fedora.server.journal.JournalException;
import fedora.server.journal.JournalReader;
import fedora.server.journal.ServerInterface;
import fedora.server.journal.entry.ConsumerJournalEntry;
import fedora.server.journal.recoverylog.JournalRecoveryLog;

/**
 * 
 * <p>
 * <b>Title:</b> MultiFileJournalReader.java
 * </p>
 * <p>
 * <b>Description:</b> A JournalReader implementation for "recovering", when
 * the is using a {@link MultiFileJournalWriter}, or the equivalent. The
 * recovery is complete when the all of the files in the journal directory have
 * been processed and moved to the archive directory.
 * </p>
 * 
 * @author jblake@cs.cornell.edu
 * @version $Id$
 */

public class MultiFileJournalReader extends JournalReader implements
        MultiFileJournalConstants {
    // the directory that holds the journal files before they are processed.
    private final File journalDirectory;

    // the directory that will hold the journal files after they are processed.
    private final File archiveDirectory;

    // journal file names will start with this.
    private final String filenamePrefix;

    protected JournalInputFile currentFile;

    protected boolean open = true;

    public MultiFileJournalReader(Map parameters, String role,
            JournalRecoveryLog recoveryLog, ServerInterface server)
            throws JournalException {
        super(parameters, role, recoveryLog, server);
        this.journalDirectory = MultiFileJournalHelper
                .parseParametersForDirectory(parameters,
                        PARAMETER_JOURNAL_DIRECTORY);
        this.archiveDirectory = MultiFileJournalHelper
                .parseParametersForDirectory(parameters,
                        PARAMETER_ARCHIVE_DIRECTORY);
        this.filenamePrefix = MultiFileJournalHelper
                .parseParametersForFilenamePrefix(parameters);
        checkDirectoriesAreDifferent();
    }

    private void checkDirectoriesAreDifferent() throws JournalException {
        if (this.archiveDirectory.equals(this.journalDirectory)) {
            throw new JournalException(
                    "Archive directory and Journal directory are identical: '"
                            + this.archiveDirectory.getPath() + "'");
        }
    }

    /*
     * Close the current file and set the closed flag.
     */
    public synchronized void shutdown() throws JournalException {
        if (open) {
            recoveryLog.log("Shutdown requested by server.");
            closeCurrentFile();
            open = false;
        }
    }

    /*
     * Advance to the next tag. If its end of file, close and get next file. If
     * null, return a null entry.
     */
    public synchronized ConsumerJournalEntry readJournalEntry()
            throws JournalException, XMLStreamException {
        if (!open) {
            return null;
        }

        scanThroughFilesForNextJournalEntry();

        if (currentFile == null) {
            return null;
        } else {
            String identifier = peekAtJournalEntryIdentifier(currentFile);
            ConsumerJournalEntry journalEntry = super
                    .readJournalEntry(currentFile.getReader());
            journalEntry.setIdentifier(identifier);
            return journalEntry;
        }
    }

    /**
     * Create an identifier string for the Journal Entry, so we can easily
     * connect the entries in the Recovery Log with those in the Journal.
     * 
     * Call this before calling
     * {@link JournalReader#readJournalEntry(XMLEventReader)}, because the
     * reader is positioned at the beginning of the JournalEntry, so a peek()
     * will give us the start tag, with the info we need.
     */
    private String peekAtJournalEntryIdentifier(JournalInputFile file)
            throws XMLStreamException {
        String fileName = file.getFilename();
        XMLEvent event = file.getReader().peek();

        String timeString = "unknown";
        if (event.isStartElement()) {
            StartElement start = event.asStartElement();
            Attribute timeStamp = start
                    .getAttributeByName(QNAME_ATTR_TIMESTAMP);
            if (timeStamp != null) {
                timeString = timeStamp.getValue();
            }
        }

        return "file='" + fileName + "', entry='" + timeString + "'";
    }

    /**
     * Advance to the next journal entry if there is one. If we find one, the
     * current file will be pointing to it. If we don't find one, there will be
     * no current file.
     */
    private void scanThroughFilesForNextJournalEntry() throws JournalException {
        try {
            while (true) {
                if (currentFile != null) {
                    // Check to see whether the current file contains any more
                    // entries. 
                    advancePastWhitespace(currentFile.getReader());

                    XMLEvent next = currentFile.getReader().peek();
                    if (isStartTagEvent(next, QNAME_TAG_JOURNAL_ENTRY)) {
                        // found it
                        return;
                    } else if (isEndTagEvent(next, QNAME_TAG_JOURNAL)) {
                        // need to get the next file
                        closeCurrentFile();
                    } else {
                        // problems
                        throw getNotNextMemberOrEndOfGroupException(
                                QNAME_TAG_JOURNAL, QNAME_TAG_JOURNAL_ENTRY,
                                next);
                    }
                }

                // If we don't have a file open, try to open one.
                if (currentFile == null) {
                currentFile = openNextFile();
                }
                
                // If we still don't have a file open, we're finished.
                if (currentFile == null) {
                    // if there was no next file, we're done.
                    return;
                }
                
                // A new file needs to be advanced before using.
                advanceIntoFile(currentFile.getReader());
            }
        } catch (XMLStreamException e) {
            throw new JournalException(e);
        }
    }

    /**
     * Look in the directory for files that match the prefix. If there are none,
     * leave with currentFile still null. If we find one, advance into it.
     */
    protected JournalInputFile openNextFile() throws JournalException {
        File[] journalFiles = MultiFileJournalHelper
                .getSortedArrayOfJournalFiles(journalDirectory, filenamePrefix);

        if (journalFiles.length == 0) {
            return null;
        }

        JournalInputFile nextFile = new JournalInputFile(journalFiles[0]);
        recoveryLog.log("Opening journal file: '" + nextFile.getFilename()
                + "'");
        return nextFile;
    }

    /**
     * Advance past the document header to the first JournalEntry.
     */
    private void advanceIntoFile(XMLEventReader reader)
            throws XMLStreamException, JournalException {
        XMLEvent event = reader.nextEvent();
        if (!event.isStartDocument()) {
            throw new JournalException(
                    "Expecting XML document header, but event was '" + event
                            + "'");
        }

        event = reader.nextTag();
        if (!isStartTagEvent(event, QNAME_TAG_JOURNAL)) {
            throw new JournalException(
                    "Expecting FedoraJournal start tag, but event was '"
                            + event + "'");
        }

        String hash = getOptionalAttributeValue(event.asStartElement(),
                QNAME_ATTR_REPOSITORY_HASH);
        checkRepositoryHash(hash);
    }

    private void closeCurrentFile() throws JournalException {
        if (currentFile != null) {
            recoveryLog.log("Closing journal file: '"
                    + currentFile.getFilename() + "'");
            currentFile.closeAndRename(archiveDirectory);
            currentFile = null;
        }
    }

    public String toString() {
        return super.toString() + ", journalDirectory='" + journalDirectory
                + "', archiveDirectory='" + archiveDirectory
                + "', filenamePrefix='" + filenamePrefix + "'";

    }

}

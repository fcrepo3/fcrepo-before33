package fedora.server.journal.readerwriter.multifile;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import javanet.staxutils.IndentingXMLEventWriter;

import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;

import fedora.server.journal.JournalException;
import fedora.server.journal.JournalWriter;
import fedora.server.journal.helpers.FileMovingUtil;

/**
 * 
 * <p>
 * <b>Title:</b> JournalOutputFile.java
 * </p>
 * <p>
 * <b>Description:</b> Encapsulate the information that goes with the creation
 * of a Journal file.
 * </p>
 * <p>
 * <b>CAUTION:</b> This file includes an asynchronous timer thread that can
 * close the file. The {@link #isOpen()} and {@link #close()} methods are
 * synchronized against the {@link JournalWriter#SYNCHRONIZER} to guard against
 * problems. Any other operations on the file or on its
 * <code>XMLEventWriter</code> should also be synchronized against the
 * {@link JournalWriter#SYNCHRONIZER}.
 * </p>
 * 
 * @author jblake@cs.cornell.edu
 * @version $Id$
 */

class JournalOutputFile implements MultiFileJournalConstants {
    /** A "dummy" file instance that is already closed. */
    public static final JournalOutputFile DUMMY_FILE = new JournalOutputFile();

    /** The parent JournalWriter, used for the document header and trailer. */
    private final MultiFileJournalWriter parent;

    /** The name of the file after it is closed. */
    private final File file;

    /** The name of the file while it is being written. */
    private final File tempFile;

    private final FileWriter fileWriter;

    private final XMLEventWriter xmlWriter;

    /** If the file is larger than this (in bytes), close it. */
    private final long sizeLimit;

    /** The timer that monitors the age of this file. */
    private final Timer timer;

    /** Is this file still open? */
    private boolean open = true;

    /**
     * This private constructor creates a "dummy" file that is closed to start
     * with.
     */
    private JournalOutputFile() {
        this.sizeLimit = 0;
        this.file = null;
        this.tempFile = null;
        this.fileWriter = null;
        this.xmlWriter = null;
        this.parent = null;
        this.timer = null;
        this.open = false;
    }

    /**
     * Open the file, initialize all of the fields, write the document header,
     * and set the timer.
     */
    JournalOutputFile(MultiFileJournalWriter parent, String filenamePrefix,
            File journalDirectory, long sizeLimit, long ageLimit)
            throws JournalException {
        try {
            this.parent = parent;
            this.sizeLimit = sizeLimit;
            this.file = createFilename(filenamePrefix, journalDirectory);
            this.tempFile = createTempFilename(file, journalDirectory);
            this.fileWriter = createTempFile(this.tempFile);
            this.xmlWriter = createXmlEventWriter(this.fileWriter);
            this.parent.getDocumentHeader(this.xmlWriter);
            this.timer = createTimer(ageLimit);
        } catch (IOException e) {
            throw new JournalException(e);
        } catch (XMLStreamException e) {
            throw new JournalException(e);
        }
    }

    /**
     * Create a <code>File</code> object with the name of this Journal file.
     * Check to be sure that no such file already exists.
     */
    private File createFilename(String filenamePrefix, File journalDirectory)
            throws JournalException {
        String filename = MultiFileJournalHelper.createTimestampedFilename(
                filenamePrefix, new Date());
        File theFile = new File(journalDirectory, filename);

        if (theFile.exists()) {
            throw new JournalException("File '" + theFile.getPath()
                    + "' already exists.");
        }

        return theFile;
    }

    /**
     * The "temporary" filename is the permanent name preceded by an underscore.
     */
    private File createTempFilename(File permanentFile, File journalDirectory) {
        String tempFilename = "_" + permanentFile.getName();
        File file2 = new File(journalDirectory, tempFilename);
        return file2;
    }

    /**
     * Create and open the temporary file.
     */
    private FileWriter createTempFile(File file) throws IOException,
            JournalException {
        boolean created = file.createNewFile();
        if (!created) {
            throw new JournalException("Unable to create file '"
                    + file.getPath() + "'.");
        }
        return new FileWriter(file);
    }

    /**
     * Create an XMLEventWriter for this file. Make it a pretty, indenting
     * writer.
     */
    private XMLEventWriter createXmlEventWriter(FileWriter fileWriter)
            throws FactoryConfigurationError, XMLStreamException {
        XMLOutputFactory factory = XMLOutputFactory.newInstance();
        return new IndentingXMLEventWriter(factory
                .createXMLEventWriter(fileWriter));
    }

    /**
     * Create the timer, and schedule a task that will let us know when the file
     * is too old to continue. If the age limit is 0 or negative, we treat it as
     * "no limit".
     */
    private Timer createTimer(long ageLimit) {
        Timer fileTimer = new Timer();

        // if the age limit is 0 or negative, treat it as "no limit".
        if (ageLimit >= 0) {
            fileTimer.schedule(new CloseFileTimerTask(), ageLimit);
        }

        return fileTimer;
    }

    /**
     * Get an XMLEventWriter that we can write the JournalEvents to. NOTE: any
     * operations against this writer should be synchronized on the
     * {@link JournalWriter#SYNCHRONIZER}.
     */
    public XMLEventWriter getXmlWriter() {
        return this.xmlWriter;
    }

    /**
     * Check the size limit and see whether the file is big enough to close. We
     * could also check the age limit here, but we trust the timer to handle
     * that.
     */
    public void closeIfAppropriate() throws JournalException {
        synchronized (JournalWriter.SYNCHRONIZER) {
            if (!open) {
                return;
            }

            // if the size limit is 0 or negative, treat it as "no limit".
            long currentSize = this.tempFile.length();
            if ((sizeLimit > 0) && (currentSize > sizeLimit)) {
                close();
            }
        }
    }

    /**
     * Is this file available for writing?
     */
    public boolean isOpen() {
        synchronized (JournalWriter.SYNCHRONIZER) {
            return open;
        }
    }

    /**
     * Write the document trailer, clean up everything and rename the file. Set
     * the flag saying we are closed.
     */
    public void close() throws JournalException {
        synchronized (JournalWriter.SYNCHRONIZER) {
            if (!open) {
                return;
            }

            try {
                parent.getDocumentTrailer(xmlWriter);
                xmlWriter.close();
                fileWriter.close();
                timer.cancel();

                /*
                 * java.io.File.renameTo() has a known bug when working across
                 * file-systems, see:
                 * http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=4073756
                 * 
                 * So instead of this call: tempFile.renameTo(file);
                 * 
                 * We use the following line...
                 */
                boolean renamed = FileMovingUtil.move(tempFile, file);
                if (!renamed) {
                    throw new JournalException("Unable to rename file '" 
                            + tempFile + "' to '" + file + "'.");
                }
    
                open = false;
            } catch (XMLStreamException e) {
                throw new JournalException(e);
            } catch (IOException e) {
                throw new JournalException(e);
            }
        }
    }

    /**
     * When the timer goes off, close the file.
     */
    private final class CloseFileTimerTask extends TimerTask {
        public void run() {
            try {
                JournalOutputFile.this.close();
            } catch (JournalException e) {
                /*
                 * What to do with this exception? If we print it, where is the
                 * console? If we throw it, where will it be recorded?
                 */
                e.printStackTrace();
                IllegalStateException ise = new IllegalStateException();
                ise.initCause(e);
                throw ise;
            }
        }
    }

}

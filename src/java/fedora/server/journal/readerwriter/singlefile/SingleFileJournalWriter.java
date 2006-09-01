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

package fedora.server.journal.readerwriter.singlefile;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;

import javanet.staxutils.IndentingXMLEventWriter;

import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;

import fedora.server.journal.JournalException;
import fedora.server.journal.JournalWriter;
import fedora.server.journal.ServerInterface;
import fedora.server.journal.entry.CreatorJournalEntry;

/**
 * 
 * <p>
 * <b>Title:</b> SingleFileJournalWriter.java
 * </p>
 * <p>
 * <b>Description:</b> A rudimentary implementation of JournalWriter that just
 * writes all entries to a single Journal file. Useful only for System tests.
 * </p>
 * 
 * @author jblake@cs.cornell.edu
 * @version $Id$
 */

public class SingleFileJournalWriter extends JournalWriter implements
        SingleFileJournalConstants {
    private final FileWriter out;
    private final XMLEventWriter writer;
    private boolean fileHasHeader = false;

    /**
     * Get the name of the journal file from the server parameters, create the
     * file, wrap it in an XMLEventWriter, and initialize it with a document
     * header.
     */
    public SingleFileJournalWriter(Map parameters, String role, ServerInterface server)
            throws JournalException {
        super(parameters, role, server);

        if (!parameters.containsKey(PARAMETER_JOURNAL_FILENAME)) {
            throw new JournalException("Parameter '"
                    + PARAMETER_JOURNAL_FILENAME + "' not set.");
        }

        try {
            this.out = new FileWriter(((String) parameters
                    .get(PARAMETER_JOURNAL_FILENAME)));

            XMLOutputFactory factory = XMLOutputFactory.newInstance();
            this.writer = new IndentingXMLEventWriter(factory
                    .createXMLEventWriter(out));
        } catch (IOException e) {
            throw new JournalException(e);
        } catch (XMLStreamException e) {
            throw new JournalException(e);
        }
    }

    /**
     * Make sure that the file has been initialized before writing any journal
     * entries.
     */
    public void prepareToWriteJournalEntry() throws JournalException {
        if (!fileHasHeader) {
            super.writeDocumentHeader(this.writer);
            fileHasHeader = true;
        }
    }

    /**
     * Every journal entry just gets added to the file.
     */
    public void writeJournalEntry(CreatorJournalEntry journalEntry)
            throws JournalException {
        try {
            super.writeJournalEntry(journalEntry, this.writer);
            this.writer.flush();
        } catch (XMLStreamException e) {
            throw new JournalException(e);
        }
    }

    /**
     * Add the document trailer and close the journal file.
     */
    public void shutdown() throws JournalException {
        try {
            if (fileHasHeader) {
                super.writeDocumentTrailer(this.writer);
            }
            this.writer.close();
            out.close();
        } catch (XMLStreamException e) {
            throw new JournalException(e);
        } catch (IOException e) {
            throw new JournalException(e);
        }
    }

}

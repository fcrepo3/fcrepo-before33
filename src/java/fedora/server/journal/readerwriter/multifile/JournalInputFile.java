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
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;

import fedora.server.journal.JournalException;
import fedora.server.journal.helpers.FileMovingUtil;

/**
 * 
 * <p>
 * <b>Title:</b> JournalInputFile.java
 * </p>
 * <p>
 * <b>Description:</b> Encapsulate the information that goes with consuming a
 * Journal file.
 * </p>
 * 
 * @author jblake@cs.cornell.edu
 * @version $Id$
 */

class JournalInputFile {

    private final File file;

    private final FileReader fileReader;

    private final XMLEventReader xmlReader;

    public JournalInputFile(File file) throws JournalException {
        if (!file.isFile()) {
            throw new JournalException("Journal file '" + file.getPath()
                    + "' is not a file.");
        }
        if (!file.canRead()) {
            throw new JournalException("Journal file '" + file.getPath()
                    + "' is not readable.");
        }

        try {
            this.file = file;
            XMLInputFactory factory = XMLInputFactory.newInstance();
            this.fileReader = new FileReader(file);
            this.xmlReader = factory.createXMLEventReader(fileReader);
        } catch (FileNotFoundException e) {
            throw new JournalException(e);
        } catch (XMLStreamException e) {
            throw new JournalException(e);
        }
    }

    public String getFilename() {
        return this.file.getPath();
    }

    /**
     * When we have processed the file, move it to the archive directory.
     */
    public void closeAndRename(File archiveDirectory) throws JournalException {
        try {
            xmlReader.close();
            fileReader.close();
            File archiveFile = new File(archiveDirectory, file.getName());
            
            /*
             * java.io.File.renameTo() has a known bug when working across
             * file-systems, see:
             * http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=4073756
             * 
             * So instead of this call: file.renameTo(archiveFile);
             * 
             * We use the following line...
             */
            boolean renamed = FileMovingUtil.move(file, archiveFile);
            if (!renamed) {
                throw new JournalException("Failed to rename file from '"
                        + file.getPath() + "' to '" + archiveFile.getPath()
                        + "'");
            }
        } catch (XMLStreamException e) {
            throw new JournalException(e);
        } catch (IOException e) {
            throw new JournalException(e);
        }
    }

    public XMLEventReader getReader() {
        return this.xmlReader;
    }

}
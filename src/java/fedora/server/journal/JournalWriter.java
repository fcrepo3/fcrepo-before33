/* The contents of this file are subject to the license and copyright terms
 * detailed in the license directory at the root of the source tree (also 
 * available online at http://www.fedora.info/license/).
 */

package fedora.server.journal;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;

import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLStreamException;

import org.apache.log4j.Logger;

import fedora.common.Constants;
import fedora.server.errors.ServerException;
import fedora.server.journal.entry.CreatorJournalEntry;
import fedora.server.journal.helpers.EncodingBase64InputStream;
import fedora.server.journal.helpers.JournalHelper;
import fedora.server.journal.xmlhelpers.AbstractXmlWriter;
import fedora.server.journal.xmlhelpers.ContextXmlWriter;

/**
 * 
 * <p>
 * <b>Title:</b> JournalWriter.java
 * </p>
 * <p>
 * <b>Description:</b> The abstract base for all JournalWriter classes. Each
 * child class is responsible for providing an XMLEventWriter that will receive
 * the JournalEntry tag. This class will format a JournalEntry obbject into XML
 * and add it to the XMLEventWriter.
 * </p>
 * 
 * @author jblake@cs.cornell.edu
 * @version $Id$
 */

public abstract class JournalWriter extends AbstractXmlWriter {

    /** Logger for this class. */
    private static final Logger LOG = Logger.getLogger(
            JournalWriter.class.getName());

    /**
     * A single object on which to synchronize all writing operations. The most
     * obvious use is in CreatorJournalEntry to be sure that Management methods
     * are single-threaded. A less obvious, but necessary use is to synchronize
     * the timeout on JournalFiles, so a file is not closed in the middle of an
     * operation.
     */
    public static final Object SYNCHRONIZER = new Object();

    /**
     * Create an instance of the proper JournalWriter child class, as determined
     * by the server parameters.
     */
    public static JournalWriter getInstance(Map parameters, String role,
            ServerInterface server) throws JournalException {
        Object journalWriter = JournalHelper
                .createInstanceAccordingToParameter(
                        PARAMETER_JOURNAL_WRITER_CLASSNAME,
                        new Class[] { Map.class, String.class,
                                ServerInterface.class }, new Object[] {
                                parameters, role, server }, parameters);
        LOG.info("JournalWriter is " + journalWriter.toString());
        return (JournalWriter) journalWriter;
    }

    protected final String role;

    protected final Map parameters;

    protected final ServerInterface server;

    /**
     * Concrete sub-classes must implement this constructor.
     */
    protected JournalWriter(Map parameters, String role, ServerInterface server) {
        this.parameters = parameters;
        this.role = role;
        this.server = server;
    }

    public abstract void shutdown() throws JournalException;

    /**
     * Concrete sub-classes should insure that a message transport is ready, and
     * call {@link #writeDocumentHeader(XMLEventWriter) if needed. This method
     * is called separately from {@link #writeJournalEntry(CreatorJournalEntry)},
     * so we can obtain the repository hash before the Management method is
     * invoked.
     */
    public abstract void prepareToWriteJournalEntry() throws JournalException;

    /**
     * Concrete sub-classes should provide an XMLEventWriter, and call
     * {@link #writeJournalEntry(XMLEventWriter)}, after which, they should
     * probably flush the XMLEventWriter.
     */
    public abstract void writeJournalEntry(CreatorJournalEntry journalEntry)
            throws JournalException;

    /**
     * Subclasses should call this method to initialize a new Journal file.
     */
    protected void writeDocumentHeader(XMLEventWriter writer)
            throws JournalException {
        try {
            putStartDocument(writer);
            putStartTag(writer, QNAME_TAG_JOURNAL);
            putAttribute(writer, QNAME_ATTR_REPOSITORY_HASH,
                    getRepositoryHash());
            putAttribute(writer, QNAME_ATTR_TIMESTAMP, JournalHelper
                    .formatDate(new Date()));
        } catch (XMLStreamException e) {
            throw new JournalException(e);
        }
    }

    /**
     * Subclasses should call this method to close a Journal file.
     */
    protected void writeDocumentTrailer(XMLEventWriter writer)
            throws JournalException {
        try {
            putEndDocument(writer);
        } catch (XMLStreamException e) {
            throw new JournalException(e);
        }
    }

    /**
     * Format a JournalEntry object and write a JournalEntry tag to the journal.
     */
    protected void writeJournalEntry(CreatorJournalEntry journalEntry,
            XMLEventWriter writer) throws JournalException {
        try {
            writeJournaEntryStartTag(journalEntry, writer);

            new ContextXmlWriter().writeContext(journalEntry.getContext(),
                    writer);

            writeArguments(journalEntry.getArgumentsMap(), writer);

            putEndTag(writer, QNAME_TAG_JOURNAL_ENTRY);
        } catch (XMLStreamException e) {
            throw new JournalException(e);
        }
    }

    private void writeJournaEntryStartTag(CreatorJournalEntry journalEntry,
            XMLEventWriter writer) throws XMLStreamException {
        putStartTag(writer, QNAME_TAG_JOURNAL_ENTRY);
        putAttribute(writer, QNAME_ATTR_METHOD, journalEntry.getMethodName());
        putAttribute(writer, QNAME_ATTR_TIMESTAMP, JournalHelper
                .formatDate(journalEntry.getContext().now()));

        String[] clientIpArray = journalEntry.getContext()
                .getEnvironmentValues(
                        Constants.HTTP_REQUEST.CLIENT_IP_ADDRESS.uri);
        if ((clientIpArray != null) && (clientIpArray.length > 0)) {
            putAttribute(writer, QNAME_ATTR_CLIENT_IP, clientIpArray[0]);
        }

        String[] loginIdArray = journalEntry.getContext().getSubjectValues(
                Constants.SUBJECT.LOGIN_ID.uri);
        if ((loginIdArray != null) && (loginIdArray.length > 0)) {
            putAttribute(writer, QNAME_ATTR_LOGIN_ID, loginIdArray[0]);
        }
    }

    private void writeArguments(Map arguments, XMLEventWriter writer)
            throws XMLStreamException, JournalException {
        for (Iterator args = arguments.keySet().iterator(); args.hasNext();) {
            String key = (String) args.next();
            Object value = arguments.get(key);
            if (value == null) {
                writeNullArgument(key, writer);
            } else if (value instanceof String) {
                writeStringArgument(key, (String) value, writer);
            } else if (value instanceof String[]) {
                writeStringArrayArgument(key, (String[]) value, writer);
            } else if (value instanceof Date) {
                writeDateArgument(key, (Date) value, writer);
            } else if (value instanceof Integer) {
                writeIntegerArgument(key, (Integer) value, writer);
            } else if (value instanceof Boolean) {
                writeBooleanArgument(key, (Boolean) value, writer);
            } else if (value instanceof File) {
                writeFileArgument(key, (File) value, writer);
            } else {
                throw new JournalException("Unknown argument type: name='"
                        + key + "', type='" + value.getClass().getName() + "'");
            }
        }
    }

    private void writeNullArgument(String key, XMLEventWriter writer)
            throws XMLStreamException {
        putStartTag(writer, QNAME_TAG_ARGUMENT);
        putAttribute(writer, QNAME_ATTR_NAME, key);
        putAttribute(writer, QNAME_ATTR_TYPE, ARGUMENT_TYPE_NULL);
        putEndTag(writer, QNAME_TAG_ARGUMENT);
    }

    private void writeStringArgument(String key, String value,
            XMLEventWriter writer) throws XMLStreamException {
        putStartTag(writer, QNAME_TAG_ARGUMENT);
        putAttribute(writer, QNAME_ATTR_NAME, key);
        putAttribute(writer, QNAME_ATTR_TYPE, ARGUMENT_TYPE_STRING);
        putCharacters(writer, value);
        putEndTag(writer, QNAME_TAG_ARGUMENT);
    }

    private void writeDateArgument(String key, Date date, XMLEventWriter writer)
            throws XMLStreamException {
        putStartTag(writer, QNAME_TAG_ARGUMENT);
        putAttribute(writer, QNAME_ATTR_NAME, key);
        putAttribute(writer, QNAME_ATTR_TYPE, ARGUMENT_TYPE_DATE);
        putCharacters(writer, JournalHelper.formatDate(date));
        putEndTag(writer, QNAME_TAG_ARGUMENT);
    }

    private void writeIntegerArgument(String key, Integer value,
            XMLEventWriter writer) throws XMLStreamException {
        putStartTag(writer, QNAME_TAG_ARGUMENT);
        putAttribute(writer, QNAME_ATTR_NAME, key);
        putAttribute(writer, QNAME_ATTR_TYPE, ARGUMENT_TYPE_INTEGER);
        putCharacters(writer, value.toString());
        putEndTag(writer, QNAME_TAG_ARGUMENT);
    }

    private void writeBooleanArgument(String key, Boolean value,
            XMLEventWriter writer) throws XMLStreamException {
        putStartTag(writer, QNAME_TAG_ARGUMENT);
        putAttribute(writer, QNAME_ATTR_NAME, key);
        putAttribute(writer, QNAME_ATTR_TYPE, ARGUMENT_TYPE_BOOLEAN);
        putCharacters(writer, value.toString());
        putEndTag(writer, QNAME_TAG_ARGUMENT);
    }

    private void writeStringArrayArgument(String key, String[] value,
            XMLEventWriter writer) throws XMLStreamException {
        putStartTag(writer, QNAME_TAG_ARGUMENT);
        putAttribute(writer, QNAME_ATTR_NAME, key);
        putAttribute(writer, QNAME_ATTR_TYPE, ARGUMENT_TYPE_STRINGARRAY);

        for (int i = 0; i < value.length; i++) {
            putStartTag(writer, QNAME_TAG_ARRAYELEMENT);
            putCharacters(writer, value[i]);
            putEndTag(writer, QNAME_TAG_ARRAYELEMENT);
        }

        putEndTag(writer, QNAME_TAG_ARGUMENT);

    }

    /**
     * An InputStream argument must be written as a Base64-encoded String. It is
     * read from the temp file in segments. Each segment is encoded and written
     * to the XML writer as a series of character events.
     */
    private void writeFileArgument(String key, File file, XMLEventWriter writer)
            throws XMLStreamException, JournalException {
        try {
            putStartTag(writer, QNAME_TAG_ARGUMENT);
            putAttribute(writer, QNAME_ATTR_NAME, key);
            putAttribute(writer, QNAME_ATTR_TYPE, ARGUMENT_TYPE_STREAM);

            EncodingBase64InputStream encoder = new EncodingBase64InputStream(
                    new BufferedInputStream(new FileInputStream(file)));
            String encodedChunk;
            while (null != (encodedChunk = encoder.read(1000))) {
                putCharacters(writer, encodedChunk);
            }
            encoder.close();
            putEndTag(writer, QNAME_TAG_ARGUMENT);
        } catch (IOException e) {
            throw new JournalException("IO Exception on temp file", e);
        }
    }

    /**
     * This method must not be called before the server has completed
     * initialization. That's the only way we can be confident that the
     * DOManager is present, and ready to create the repository has that we will
     * compare to.
     */
    private String getRepositoryHash() throws JournalException {
        if (!server.hasInitialized()) {
            throw new IllegalStateException(
                    "The repository hash is not available until "
                            + "the server is fully initialized.");
        }

        try {
            return server.getRepositoryHash();
        } catch (ServerException e) {
            throw new JournalException(e);
        }
    }

}

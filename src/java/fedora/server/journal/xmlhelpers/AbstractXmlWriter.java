
package fedora.server.journal.xmlhelpers;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventFactory;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLStreamException;

import fedora.server.journal.JournalConstants;

/**
 * <p>
 * <b>Title:</b> AbstractXmlWriter.java
 * </p>
 * <p>
 * <b>Description:</b> An abstract base class that provides some useful methods
 * for the XML writer classes.
 * </p>
 * 
 * @author jblake@cs.cornell.edu
 * @version $Id: AbstractXmlWriter.java 5025 2006-09-01 22:08:17 +0000 (Fri, 01
 *          Sep 2006) cwilper $
 */

public class AbstractXmlWriter
        implements JournalConstants {

    private final XMLEventFactory factory = XMLEventFactory.newInstance();

    protected void putStartDocument(XMLEventWriter writer)
            throws XMLStreamException {
        writer.add(factory.createStartDocument(DOCUMENT_ENCODING,
                                               DOCUMENT_VERSION));
    }

    protected void putStartTag(XMLEventWriter writer, QName tagName)
            throws XMLStreamException {
        writer.add(factory.createStartElement(tagName, null, null));
    }

    protected void putAttribute(XMLEventWriter writer, QName name, String value)
            throws XMLStreamException {
        writer.add(factory.createAttribute(name, value));
    }

    protected void putAttributeIfNotNull(XMLEventWriter writer,
                                         QName attributeName,
                                         String value)
            throws XMLStreamException {
        if (value != null) {
            putAttribute(writer, attributeName, value);
        }
    }

    protected void putCharacters(XMLEventWriter writer, String chars)
            throws XMLStreamException {
        writer.add(factory.createCharacters(chars));
    }

    protected void putEndTag(XMLEventWriter writer, QName tagName)
            throws XMLStreamException {
        writer.add(factory.createEndElement(tagName, null));
    }

    protected void putEndDocument(XMLEventWriter writer)
            throws XMLStreamException {
        writer.add(factory.createEndDocument());
    }

}

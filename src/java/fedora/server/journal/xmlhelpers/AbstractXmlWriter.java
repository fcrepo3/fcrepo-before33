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

package fedora.server.journal.xmlhelpers;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventFactory;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLStreamException;

import fedora.server.journal.JournalConstants;

/**
 * 
 * <p>
 * <b>Title:</b> AbstractXmlWriter.java
 * </p>
 * <p>
 * <b>Description:</b> An abstract base class that provides some useful methods
 * for the XML writer classes.
 * </p>
 * 
 * @author jblake@cs.cornell.edu
 * @version $Id$
 */

public class AbstractXmlWriter implements JournalConstants {

    private XMLEventFactory factory = XMLEventFactory.newInstance();

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
            QName attributeName, String value) throws XMLStreamException {
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

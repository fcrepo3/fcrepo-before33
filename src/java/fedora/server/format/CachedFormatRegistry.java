package fedora.server.format;

import java.util.*;
import java.io.*;
import javax.xml.parsers.*;
import org.w3c.dom.*;
import org.xml.sax.*;
import org.xml.sax.helpers.*;

import fedora.server.errors.ServerException;

/**
 * A format registry that keeps everything in memory.
 * It can be instantiated empty or via an xml stream, and can subsequently
 * have Formats added to it.
 * -----------------------------------------------------------------------------
 *
 * <p><b>License and Copyright: </b>The contents of this file are subject to the
 * Mozilla Public License Version 1.1 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License
 * at <a href="http://www.mozilla.org/MPL">http://www.mozilla.org/MPL/.</a></p>
 *
 * <p>Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License for
 * the specific language governing rights and limitations under the License.</p>
 *
 * <p>The entire file consists of original code.  Copyright &copy; 2002, 2003 by The
 * Rector and Visitors of the University of Virginia and Cornell University.
 * All rights reserved.</p>
 *
 * -----------------------------------------------------------------------------
 *
 * @author cwilper@cs.cornell.edu
 * @version $Id$
 */
public class CachedFormatRegistry 
        extends DefaultHandler
        implements FormatRegistry {

    // namespace URI of the xml format this class reads
    public final static String REGISTRY_XML_NAMESPACE=
            "http://www.fedora.info/definitions/format-registry/";

    // currently cached Formats, keyed by identifier
    private Map m_formats;

    // used during sax parse
    private StringBuffer m_currentTextBuffer;
    private String m_currentLabel;
    private String m_currentIdentifier;
    private String m_currentXMLSchemaLocation;
    private String m_currentXMLNamespace;
    private String m_currentOAIPrefix;

    /**
	 * Create a new, empty CachedFormatRegistry.
	 */
    public CachedFormatRegistry() {  
        m_formats=new HashMap();
    }

    /**
     * Create a new CachedFormatRegistry with the given xml stream.
     */
    public CachedFormatRegistry(InputStream xmlStream) 
            throws IOException {
        m_formats=new HashMap();
        try {
            SAXParserFactory spf = SAXParserFactory.newInstance();
            spf.setNamespaceAware(true);
            SAXParser parser = spf.newSAXParser();
            parser.parse(xmlStream, this);
        } catch (Exception e) {
            if (e.getMessage()!=null) {
                throw new IOException(e.getMessage());
            } else {
                throw new IOException("Error parsing search result xml: "
                        + e.getClass().getName());
            }
        }
    }

    /**
     * Add or replace a Format in the registry.
     * If null is given for the format, it is removed (if it existed)
     */
    public void put(String identifier, Format format) {
        if (format==null) {
            m_formats.remove(identifier);
        } else {
            m_formats.put(identifier, format);
        }
    }

    /**
	 * Get the format with the given identifier, null if not found.
	 */
    public Format getFormat(String identifier) {
        return (Format) m_formats.get(identifier);
    }

    /**
     * Get an iterator of identifiers of the known formats.
     */
    public Iterator identifiers() {
        return m_formats.keySet().iterator();
    }

    public void startElement(String uri, 
                             String localName, 
                             String qName, 
                             Attributes a) {
        if (localName.equals("formats")) {
            m_currentTextBuffer=new StringBuffer();
        } else if (localName.equals("format")) {
            m_currentLabel=a.getValue(REGISTRY_XML_NAMESPACE, "label");
            if (m_currentLabel==null) {
                m_currentLabel=a.getValue("label");
            }
        }
    }

    public void characters(char[] ch, int start, int length) {
        m_currentTextBuffer.append(ch, start, length);
    }

    public void endElement(String uri, 
                           String localName, 
                           String qName) 
            throws SAXException {
        String text=m_currentTextBuffer.toString().trim();
        if (localName.equals("identifier"))
                m_currentIdentifier=text;
        if (localName.equals("xml-schema-location"))
                m_currentXMLSchemaLocation=text;
        if (localName.equals("xml-namespace"))
                m_currentXMLNamespace=text;
        if (localName.equals("oai-prefix"))
                m_currentOAIPrefix=text;
        if (localName.equals("format")) {
            // make sure it's got an identifier, then add the format to this registry object
            if (m_currentIdentifier==null || m_currentIdentifier.length()==0) {
                throw new SAXException("Error parsing format registry xml: "
                        + "<identifier> in <format> is missing or empty.");
            }
            put(m_currentIdentifier, new Format(m_currentLabel,
                                                m_currentIdentifier,
                                                m_currentXMLSchemaLocation,
                                                m_currentXMLNamespace,
                                                m_currentOAIPrefix));
            // prepare for parse of next format element
            m_currentIdentifier=null;
            m_currentXMLSchemaLocation=null;
            m_currentXMLNamespace=null;
            m_currentOAIPrefix=null;
        } else {
            // prepare for parse of next text-containing element
            m_currentTextBuffer=new StringBuffer();
        }
    }

}

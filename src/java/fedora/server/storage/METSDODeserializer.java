package fedora.server.storage;

import fedora.server.errors.ObjectIntegrityException;
import fedora.server.errors.StreamIOException;
import fedora.server.errors.StreamReadException;
import fedora.server.storage.types.DigitalObject;

import java.io.InputStream;
import java.io.IOException;
import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;


/**
 * Reads a METS-Fedora XML stream into a DigitalObject.
 */
public class METSDODeserializer 
        extends DefaultHandler
        implements DODeserializer {

    private SAXParser m_parser;
    private DigitalObject m_obj;
    
    /**
     * Initializes by setting up a parser that doesn't validate.
     */
    public METSDODeserializer() 
            throws FactoryConfigurationError, ParserConfigurationException, 
            SAXException {
        this(false);
    }

    /**
     * Initializes by setting up a parser that validates only if validate=true.
     */
    public METSDODeserializer(boolean validate)
            throws FactoryConfigurationError, ParserConfigurationException, 
            SAXException {
        SAXParserFactory spf = SAXParserFactory.newInstance();
        spf.setValidating(validate);
        spf.setNamespaceAware(true);
        m_parser=spf.newSAXParser();
    }
    
    public void deserialize(InputStream in, DigitalObject obj) 
            throws ObjectIntegrityException, StreamIOException {
        m_obj=obj;
        try {
            m_parser.parse(in, this);
        } catch (IOException ioe) {
            throw new StreamIOException("low-level stream io problem occurred "
                    + "while sax was parsing this object.");
        } catch (SAXException se) {
            throw new ObjectIntegrityException("mets stream was bad : " + se.getMessage());
        }
    }
    
    public void startElement(String uri, String localName, String qName, 
            Attributes attributes) {
        System.out.println("uri       : " + uri + "\n");
        System.out.println("localname : " + localName + "\n");
        System.out.println("qname     : " + qName + "\n\n");
    }
    
}
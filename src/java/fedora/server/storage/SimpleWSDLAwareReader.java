package fedora.server.storage;

import java.util.Date;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import fedora.server.Context;
import fedora.server.Logging;
import fedora.server.errors.DatastreamNotFoundException;
import fedora.server.errors.ObjectIntegrityException;
import fedora.server.errors.RepositoryConfigurationException;
import fedora.server.errors.StreamIOException;
import fedora.server.errors.UnsupportedTranslationException;
import fedora.server.storage.RepositoryReader;
import fedora.server.storage.translation.DOTranslator;
import fedora.server.storage.types.Datastream;
import fedora.server.storage.types.DatastreamXMLMetadata;
import fedora.server.storage.types.DigitalObject;

public class SimpleWSDLAwareReader
        extends SimpleDOReader {
        
    public SimpleWSDLAwareReader(Context context, RepositoryReader repoReader, 
            DOTranslator translator, String shortExportFormat, 
            String longExportFormat, String currentFormat,
            String encoding, InputStream serializedObject, Logging logTarget) 
            throws ObjectIntegrityException, StreamIOException,
            UnsupportedTranslationException {
        super(context, repoReader, translator, shortExportFormat, 
                longExportFormat, currentFormat, encoding, serializedObject,
                logTarget);
    }
    
    protected DatastreamXMLMetadata getWSDLDatastream(Date versDateTime) 
            throws DatastreamNotFoundException, ObjectIntegrityException {
        Datastream ds=GetDatastream("WSDL", versDateTime);
        if (ds==null) {
            throw new DatastreamNotFoundException("The object, " 
                    + GetObjectPID() + " does not have a WSDL datastream"
                    + " existing at " + getWhenString(versDateTime));
        }
        DatastreamXMLMetadata wsdlDS=null;
        try {
            wsdlDS=(DatastreamXMLMetadata) ds;
        } catch (Throwable th) {
            throw new ObjectIntegrityException("The object, "
                    + GetObjectPID() + " has a WSDL datastream existing at "
                    + getWhenString(versDateTime) + ", but it's not an "
                    + "XML metadata datastream");
        }
        return wsdlDS;
    }
    
    protected WSDLBehaviorDeserializer getDeserializedWSDL(Date versDateTime) 
            throws DatastreamNotFoundException, ObjectIntegrityException,
            RepositoryConfigurationException {
        InputStream wsdlInputStream=new ByteArrayInputStream(
                getWSDLDatastream(versDateTime).xmlContent);
        WSDLBehaviorDeserializer wsdl = new WSDLBehaviorDeserializer();
        XMLReader xmlReader=null;
        try {
            SAXParserFactory saxfactory=SAXParserFactory.newInstance();
            saxfactory.setValidating(false);
            SAXParser parser=saxfactory.newSAXParser();
            xmlReader=parser.getXMLReader();
            xmlReader.setContentHandler(wsdl);
            xmlReader.setFeature("http://xml.org/sax/features/namespaces", false);
            xmlReader.setFeature("http://xml.org/sax/features/namespace-prefixes", false);
        } catch (Exception e) {
            throw new RepositoryConfigurationException("Internal SAX error while "
                    + "preparing for WSDL datastream deserialization: "
                    + e.getMessage());
        }
        try {
            xmlReader.parse(new InputSource(wsdlInputStream));
        } catch (Exception e) {
            throw new ObjectIntegrityException(
                    "Error parsing WSDL datastream in '" + GetObjectPID() 
                    + "': " + e.getClass().getName() + ": " + e.getMessage());
        }
        return wsdl;
    }
    
}
package fedora.server.storage;

import java.util.Date;
import java.io.InputStream;
import java.io.ByteArrayInputStream;

import fedora.server.Context;
import fedora.server.Logging;
import fedora.server.errors.DatastreamNotFoundException;
import fedora.server.errors.ObjectIntegrityException;
import fedora.server.errors.RepositoryConfigurationException;
import fedora.server.errors.StreamIOException;
import fedora.server.errors.UnsupportedTranslationException;
import fedora.server.storage.translation.DOTranslator;
import fedora.server.storage.RepositoryReader;
import fedora.server.storage.types.Datastream;
import fedora.server.storage.types.DatastreamXMLMetadata;
import fedora.server.storage.types.DigitalObject;
import fedora.server.storage.types.MethodDef;

public class SimpleBDefReader
        extends SimpleWSDLAwareReader
        implements BDefReader {
        
    public SimpleBDefReader(Context context, RepositoryReader repoReader, 
            DOTranslator translator, String shortExportFormat, 
            String longExportFormat, String currentFormat,
            String encoding, InputStream serializedObject, Logging logTarget) 
            throws ObjectIntegrityException, StreamIOException,
            UnsupportedTranslationException {
        super(context, repoReader, translator, shortExportFormat, 
                longExportFormat, currentFormat, encoding, serializedObject,
                logTarget);
    }    
    
    public MethodDef[] GetBehaviorMethods(Date versDateTime) 
            throws DatastreamNotFoundException, ObjectIntegrityException,
            RepositoryConfigurationException {
        return getDeserializedWSDL(versDateTime).methodDefs;
    }
    
    public InputStream GetBehaviorMethodsWSDL(Date versDateTime)
            throws DatastreamNotFoundException, ObjectIntegrityException {
        return new ByteArrayInputStream(
                getWSDLDatastream(versDateTime).xmlContent);
    }
    
}
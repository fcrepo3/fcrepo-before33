package fedora.server.storage;

import java.util.Date;
import java.io.InputStream;
import java.io.ByteArrayInputStream;

import fedora.server.Context;
import fedora.server.errors.DatastreamNotFoundException;
import fedora.server.errors.ObjectIntegrityException;
import fedora.server.errors.RepositoryConfigurationException;
import fedora.server.storage.DOTranslator;
import fedora.server.storage.RepositoryReader;
import fedora.server.storage.types.BMechDSBindSpec;
import fedora.server.storage.types.Datastream;
import fedora.server.storage.types.DatastreamXMLMetadata;
import fedora.server.storage.types.DigitalObject;
import fedora.server.storage.types.MethodDef;

public class SimpleBMechReader
        extends SimpleWSDLAwareReader
        implements BMechReader {
        
    public SimpleBMechReader(DigitalObject obj, Context context, 
            RepositoryReader repoReader, DOTranslator translator, 
            String shortFormat, String longFormat) {
        super(obj, context, repoReader, translator, shortFormat, longFormat);
    }
    
    public MethodDef[] GetBehaviorMethods(Date versDateTime) 
            throws DatastreamNotFoundException, ObjectIntegrityException,
            RepositoryConfigurationException {
        return getDeserializedWSDL(versDateTime).methodDefBind;
    }
    
    public InputStream GetBehaviorMethodsWSDL(Date versDateTime)
            throws DatastreamNotFoundException, ObjectIntegrityException {
        return new ByteArrayInputStream(
                getWSDLDatastream(versDateTime).xmlContent);
    }

    private DatastreamXMLMetadata getBindSpecDatastream(Date versDateTime) 
            throws DatastreamNotFoundException, ObjectIntegrityException {
        Datastream ds=GetDatastream("DSBIND", versDateTime);
        if (ds==null) {
            throw new DatastreamNotFoundException("The object, " 
                    + GetObjectPID() + " does not have a DSBIND datastream"
                    + " existing at " + getWhenString(versDateTime));
        }
        DatastreamXMLMetadata bindSpecDS=null;
        try {
            bindSpecDS=(DatastreamXMLMetadata) ds;
        } catch (Throwable th) {
            throw new ObjectIntegrityException("The object, "
                    + GetObjectPID() + " has a DSBIND datastream existing at "
                    + getWhenString(versDateTime) + ", but it's not an "
                    + "XML metadata datastream");
        }
        return bindSpecDS;
    }
    
    public BMechDSBindSpec GetDSBindingSpec(Date versDateTime) 
            throws DatastreamNotFoundException, ObjectIntegrityException,
            RepositoryConfigurationException {
        return new DSBindSpecDeserializer(new ByteArrayInputStream(
                getBindSpecDatastream(versDateTime).xmlContent), 
                GetObjectPID()).getDSBindSpec();
    }
    
}
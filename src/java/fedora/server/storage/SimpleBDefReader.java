package fedora.server.storage;

import java.util.Date;
import java.io.InputStream;
import java.io.ByteArrayInputStream;

import fedora.server.Context;
import fedora.server.Logging;
import fedora.server.errors.DatastreamNotFoundException;
import fedora.server.errors.ObjectIntegrityException;
import fedora.server.errors.RepositoryConfigurationException;
import fedora.server.errors.GeneralException;
import fedora.server.errors.ServerException;
import fedora.server.errors.StreamIOException;
import fedora.server.errors.UnsupportedTranslationException;
import fedora.server.storage.translation.DOTranslator;
import fedora.server.storage.RepositoryReader;
import fedora.server.storage.types.Datastream;
import fedora.server.storage.types.DatastreamXMLMetadata;
import fedora.server.storage.types.DigitalObject;
import fedora.server.storage.types.MethodDef;
import fedora.server.storage.service.ServiceMapper;
import org.xml.sax.InputSource;

public class SimpleBDefReader
        extends SimpleServiceAwareReader
        implements BDefReader {

    private ServiceMapper serviceMapper;

    public SimpleBDefReader(Context context, RepositoryReader repoReader,
            DOTranslator translator, String shortExportFormat,
            String longExportFormat, String currentFormat,
            String encoding, InputStream serializedObject, Logging logTarget)
            throws ObjectIntegrityException, StreamIOException,
            UnsupportedTranslationException, ServerException {
        super(context, repoReader, translator, shortExportFormat,
                longExportFormat, currentFormat, encoding, serializedObject,
                logTarget);
        serviceMapper = new ServiceMapper(GetObjectPID());
    }

    public MethodDef[] getAbstractMethods(Date versDateTime)
            throws DatastreamNotFoundException, ObjectIntegrityException,
            RepositoryConfigurationException, GeneralException {
        return serviceMapper.getMethodDefs(
          new InputSource(new ByteArrayInputStream(
              getMethodMapDatastream(versDateTime).xmlContent)));
    }

    public InputStream getAbstractMethodsXML(Date versDateTime)
            throws DatastreamNotFoundException, ObjectIntegrityException {
        return new ByteArrayInputStream(
                getMethodMapDatastream(versDateTime).xmlContent);
    }

}
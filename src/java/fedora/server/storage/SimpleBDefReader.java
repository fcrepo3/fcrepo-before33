/* The contents of this file are subject to the license and copyright terms
 * detailed in the license directory at the root of the source tree (also 
 * available online at http://www.fedora.info/license/).
 */

package fedora.server.storage;

import java.util.Date;
import java.io.InputStream;
import java.io.ByteArrayInputStream;

import org.xml.sax.InputSource;

import fedora.server.Context;
import fedora.server.errors.DatastreamNotFoundException;
import fedora.server.errors.ObjectIntegrityException;
import fedora.server.errors.RepositoryConfigurationException;
import fedora.server.errors.GeneralException;
import fedora.server.errors.ServerException;
import fedora.server.errors.StreamIOException;
import fedora.server.errors.UnsupportedTranslationException;
import fedora.server.storage.translation.DOTranslator;
import fedora.server.storage.RepositoryReader;
import fedora.server.storage.types.DigitalObject;
import fedora.server.storage.types.MethodDef;
import fedora.server.storage.service.ServiceMapper;

/**
 * A BDefReader based on a DigitalObject.
 *
 * @author cwilper@cs.cornell.edu
 * @version $Id$
 */
public class SimpleBDefReader
        extends SimpleServiceAwareReader
        implements BDefReader {

    private ServiceMapper serviceMapper;

    public SimpleBDefReader(Context context, RepositoryReader repoReader,
            DOTranslator translator,
            String exportFormat, String storageFormat,
            String encoding,
            InputStream serializedObject)
            throws ObjectIntegrityException, StreamIOException,
            UnsupportedTranslationException, ServerException {
		super(context, repoReader, translator,
                exportFormat, storageFormat, 
                encoding,
                serializedObject);
        serviceMapper = new ServiceMapper(GetObjectPID());
    }

    /**
     * Alternate constructor for when a DigitalObject is already
     * available for some reason.
     */
    public SimpleBDefReader(Context context,
                            RepositoryReader repoReader,
                            DOTranslator translator,
                            String exportFormat,
                            String encoding,
                            DigitalObject obj) {
        super(context, repoReader, translator, exportFormat,
                encoding, obj);
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

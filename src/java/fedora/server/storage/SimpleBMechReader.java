package fedora.server.storage;

import java.util.Date;
import java.io.InputStream;
import java.io.ByteArrayInputStream;

import fedora.server.Context;
import fedora.server.Logging;
import fedora.server.errors.DatastreamNotFoundException;
import fedora.server.errors.MethodNotFoundException;
import fedora.server.errors.ObjectIntegrityException;
import fedora.server.errors.RepositoryConfigurationException;
import fedora.server.errors.ServerException;
import fedora.server.errors.StreamIOException;
import fedora.server.errors.GeneralException;
import fedora.server.errors.UnsupportedTranslationException;
import fedora.server.storage.translation.DOTranslator;
import fedora.server.storage.RepositoryReader;
import fedora.server.storage.types.BMechDSBindSpec;
import fedora.server.storage.types.Datastream;
import fedora.server.storage.types.DatastreamXMLMetadata;
import fedora.server.storage.types.DigitalObject;
import fedora.server.storage.types.MethodDef;
import fedora.server.storage.types.MethodParmDef;
import fedora.server.storage.types.MethodDefOperationBind;
import fedora.server.storage.service.ServiceMapper;
import org.xml.sax.InputSource;

/**
 *
 * <p><b>Title:</b> SimpleBMechReader.java</p>
 * <p><b>Description:</b> </p>
 *
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
 * <p>The entire file consists of original code.  Copyright &copy; 2002-2004 by The
 * Rector and Visitors of the University of Virginia and Cornell University.
 * All rights reserved.</p>
 *
 * -----------------------------------------------------------------------------
 *
 * @author cwilper@cs.cornell.edu
 * @version $Id$
 */
public class SimpleBMechReader
        extends SimpleServiceAwareReader
        implements BMechReader {

    private ServiceMapper serviceMapper;

    public SimpleBMechReader(Context context, RepositoryReader repoReader,
            DOTranslator translator,
			//DOTranslator translator, String storageExportFormat,
            String longExportFormat, String currentFormat,
            String encoding, InputStream serializedObject, Logging logTarget)
            throws ObjectIntegrityException, StreamIOException,
            UnsupportedTranslationException, ServerException {
		//super(context, repoReader, translator, storageExportFormat,
        super(context, repoReader, translator,
                longExportFormat, currentFormat, encoding, serializedObject,
                logTarget);
        serviceMapper = new ServiceMapper(GetObjectPID());
    }

    public MethodDef[] getServiceMethods(Date versDateTime)
            throws DatastreamNotFoundException, ObjectIntegrityException,
            RepositoryConfigurationException, GeneralException {
        return serviceMapper.getMethodDefs(
          new InputSource(new ByteArrayInputStream(
              getMethodMapDatastream(versDateTime).xmlContent)));
    }

    public MethodParmDef[] getServiceMethodParms(String methodName, Date versDateTime)
            throws MethodNotFoundException, ServerException {
        return getParms(getServiceMethods(versDateTime), methodName);
    }

    public MethodDefOperationBind[] getServiceMethodBindings(Date versDateTime)
            throws DatastreamNotFoundException, ObjectIntegrityException,
            RepositoryConfigurationException, GeneralException {
        return serviceMapper.getMethodDefBindings(
          new InputSource(new ByteArrayInputStream(
              getWSDLDatastream(versDateTime).xmlContent)),
          new InputSource(new ByteArrayInputStream(
              getMethodMapDatastream(versDateTime).xmlContent)));
    }

    public BMechDSBindSpec getServiceDSInputSpec(Date versDateTime)
            throws DatastreamNotFoundException, ObjectIntegrityException,
            RepositoryConfigurationException, GeneralException {
        return serviceMapper.getDSInputSpec(
          new InputSource(new ByteArrayInputStream(
              getDSInputSpecDatastream(versDateTime).xmlContent)));
    }

    public InputStream getServiceMethodsXML(Date versDateTime)
            throws DatastreamNotFoundException, ObjectIntegrityException {
        return new ByteArrayInputStream(
              getMethodMapDatastream(versDateTime).xmlContent);
    }

    /**
     * Get the parms out of a particular service method definition.
     * @param methods
     * @return
     */
     private MethodParmDef[] getParms(MethodDef[] methods, String methodName)
      throws MethodNotFoundException, ServerException
     {
        for (int i=0; i<methods.length; i++)
        {
          if (methods[i].methodName.equalsIgnoreCase(methodName))
          {
            return methods[i].methodParms;
          }
        }
        throw new MethodNotFoundException("[getParms] The behavior mechanism object, " + m_obj.getPid()
                    + ", does not have a service method named '" + methodName);
     }
}
package fedora.server.storage;

import java.util.Date;
import java.io.InputStream;

import fedora.server.Context;
import fedora.server.Logging;
import fedora.server.errors.DatastreamNotFoundException;
import fedora.server.errors.ObjectIntegrityException;
import fedora.server.errors.ServerException;
import fedora.server.errors.StreamIOException;
import fedora.server.errors.UnsupportedTranslationException;
import fedora.server.storage.RepositoryReader;
import fedora.server.storage.translation.DOTranslator;
import fedora.server.storage.types.Datastream;
import fedora.server.storage.types.DatastreamXMLMetadata;

/**
 *
 * <p><b>Title:</b> SimpleServiceAwareReader.java</p>
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
 * <p>The entire file consists of original code.  Copyright &copy; 2002-2005 by The
 * Rector and Visitors of the University of Virginia and Cornell University.
 * All rights reserved.</p>
 *
 * -----------------------------------------------------------------------------
 *
 * @author cwilper@cs.cornell.edu
 * @version $Id$
 */
public class SimpleServiceAwareReader
        extends SimpleDOReader {

    public SimpleServiceAwareReader(Context context, RepositoryReader repoReader,
            DOTranslator translator,
            String exportFormat, String storageFormat,
            String encoding,
            InputStream serializedObject, Logging logTarget)
            throws ObjectIntegrityException, StreamIOException,
            UnsupportedTranslationException, ServerException {
        super(context, repoReader, translator,
                exportFormat, storageFormat, 
                encoding,
                serializedObject, logTarget);
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

    protected DatastreamXMLMetadata getMethodMapDatastream(Date versDateTime)
            throws DatastreamNotFoundException, ObjectIntegrityException {
        Datastream ds=GetDatastream("METHODMAP", versDateTime);
        if (ds==null) {
            throw new DatastreamNotFoundException("The object, "
                    + GetObjectPID() + " does not have a METHODMAP datastream"
                    + " existing at " + getWhenString(versDateTime));
        }
        DatastreamXMLMetadata mmapDS=null;
        try {
            mmapDS=(DatastreamXMLMetadata) ds;
        } catch (Throwable th) {
            throw new ObjectIntegrityException("The object, "
                    + GetObjectPID() + " has a METHODMAP datastream existing at "
                    + getWhenString(versDateTime) + ", but it's not an "
                    + "XML metadata datastream");
        }
        return mmapDS;
    }

    protected DatastreamXMLMetadata getDSInputSpecDatastream(Date versDateTime)
            throws DatastreamNotFoundException, ObjectIntegrityException {
        Datastream ds=GetDatastream("DSINPUTSPEC", versDateTime);
        if (ds==null) {
            throw new DatastreamNotFoundException("The object, "
                    + GetObjectPID() + " does not have a DSINPUTSPEC datastream"
                    + " existing at " + getWhenString(versDateTime));
        }
        DatastreamXMLMetadata dsInSpecDS=null;
        try {
            dsInSpecDS=(DatastreamXMLMetadata) ds;
        } catch (Throwable th) {
            throw new ObjectIntegrityException("The object, "
                    + GetObjectPID() + " has a DSINPUTSPEC datastream existing at "
                    + getWhenString(versDateTime) + ", but it's not an "
                    + "XML metadata datastream");
        }
        return dsInSpecDS;
    }
}
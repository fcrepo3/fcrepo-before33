/* The contents of this file are subject to the license and copyright terms
 * detailed in the license directory at the root of the source tree (also 
 * available online at http://www.fedora.info/license/).
 */

package fedora.server.storage.translation;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.Map;

import org.apache.log4j.Logger;

import fedora.server.errors.ObjectIntegrityException;
import fedora.server.errors.ServerException;
import fedora.server.errors.StreamIOException;
import fedora.server.errors.UnsupportedTranslationException;
import fedora.server.storage.types.DigitalObject;

/**
 * DOTranslation implementation.
 *
 * @author cwilper@cs.cornell.edu
 * @version $Id$
 */
public class DOTranslatorImpl
        implements DOTranslator {

    /** Logger for this class. */
    private static final Logger LOG = Logger.getLogger(
            DOTranslatorImpl.class.getName());

    private Map m_serializers;
    private Map m_deserializers;

    public DOTranslatorImpl(Map serializers, Map deserializers) {
        m_serializers=serializers;
        m_deserializers=deserializers;
    }

    public void deserialize(InputStream in, DigitalObject out,
			String format, String encoding, int transContext)
            throws ObjectIntegrityException, StreamIOException,
            UnsupportedTranslationException, ServerException {
        try {
        	LOG.debug("Grabbing deserializer for: " + format);
            DODeserializer des=(DODeserializer) m_deserializers.get(format);
            if (des==null) {
                throw new UnsupportedTranslationException("No deserializer exists "
                        + "for format: " + format);
            }
            DODeserializer newDes=des.getInstance();
			newDes.deserialize(in, out, encoding, transContext);
        } catch (UnsupportedEncodingException uee) {
            throw new UnsupportedTranslationException("Deserializer for format: "
                    + format + " does not support encoding: " + encoding);
        }
    }

    public void serialize(DigitalObject in, OutputStream out,
			String format, String encoding, int transContext)
            throws ObjectIntegrityException, StreamIOException,
            UnsupportedTranslationException, ServerException {
        try {
			LOG.debug("Grabbing serializer for: " + format);
            DOSerializer ser=(DOSerializer) m_serializers.get(format);
            if (ser==null) {
                throw new UnsupportedTranslationException(
					"No serializer exists for format: " + format);
            }
            DOSerializer newSer=ser.getInstance();
			newSer.serialize(in, out, encoding, transContext);
        } catch (UnsupportedEncodingException uee) {
            throw new UnsupportedTranslationException("Serializer for format: "
                    + format + " does not support encoding: " + encoding);
        }
    }

}

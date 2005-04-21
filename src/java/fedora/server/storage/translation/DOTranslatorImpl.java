package fedora.server.storage.translation;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.Map;

import fedora.server.Logging;
import fedora.server.StdoutLogging;
import fedora.server.errors.ObjectIntegrityException;
import fedora.server.errors.ServerException;
import fedora.server.errors.StreamIOException;
import fedora.server.errors.UnsupportedTranslationException;
import fedora.server.storage.types.DigitalObject;

/**
 *
 * <p><b>Title:</b> DOTranslatorImpl.java</p>
 * <p><b>Description:</b> </p>
 *
 * @author cwilper@cs.cornell.edu
 * @version $Id$
 */
public class DOTranslatorImpl
        extends StdoutLogging
        implements DOTranslator {

    private Map m_serializers;
    private Map m_deserializers;

    public DOTranslatorImpl(Map serializers, Map deserializers, Logging logTarget) {
        super(logTarget);
        m_serializers=serializers;
        m_deserializers=deserializers;
    }

    public void deserialize(InputStream in, DigitalObject out,
			String format, String encoding, int transContext)
            //String format, String encoding)
            throws ObjectIntegrityException, StreamIOException,
            UnsupportedTranslationException, ServerException {
        try {
        	if (fedora.server.Debug.DEBUG) System.out.println("Grabbing deserializer for: " + format);
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
			if (fedora.server.Debug.DEBUG) System.out.println("Grabbing serializer for: " + format);
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

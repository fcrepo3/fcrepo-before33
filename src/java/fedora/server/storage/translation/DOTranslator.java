package fedora.server.storage.translation;

import java.io.InputStream;
import java.io.OutputStream;

import fedora.server.errors.ObjectIntegrityException;
import fedora.server.errors.ServerException;
import fedora.server.errors.StreamIOException;
import fedora.server.errors.UnsupportedTranslationException;
import fedora.server.storage.types.DigitalObject;

/**
 *
 * <p><b>Title:</b> DOTranslator.java</p>
 * <p><b>Description:</b> </p>
 *
 * @author cwilper@cs.cornell.edu
 * @version $Id$
 */
public interface DOTranslator {

    public abstract void deserialize(InputStream in, DigitalObject out,
			String format, String encoding, int transContext)
			//String format, String encoding)
            throws ObjectIntegrityException, StreamIOException,
            UnsupportedTranslationException, ServerException;

    public abstract void serialize(DigitalObject in, OutputStream out,
			String format, String encoding, int transContext)
            throws ObjectIntegrityException, StreamIOException,
            UnsupportedTranslationException, ServerException;

}
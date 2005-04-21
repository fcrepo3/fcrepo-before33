package fedora.server.storage.translation;

import fedora.server.errors.ObjectIntegrityException;
import fedora.server.errors.ServerException;
import fedora.server.errors.StreamIOException;
import fedora.server.storage.types.DigitalObject;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;

/**
 *
 * <p><b>Title:</b> DODeserializer.java</p>
 * <p><b>Description:</b> Reads an InputStream into a DigitalObject.</p>
 *
 * @author cwilper@cs.cornell.edu
 * @version $Id$
 */
public interface DODeserializer {

    public DODeserializer getInstance() throws ServerException;

    //public void deserialize(InputStream in, DigitalObject obj, String encoding)
	public void deserialize(InputStream in, DigitalObject obj, 
			String encoding, int transContext)
            throws ObjectIntegrityException, StreamIOException,
            UnsupportedEncodingException;

}
package fedora.server.storage.lowlevel;

import java.io.File;

import fedora.server.Server;
import fedora.server.errors.LowlevelStorageException;
import fedora.server.errors.MalformedPidException;

/**
 *
 * <p><b>Title:</b> PathAlgorithm.java</p>
 * <p><b>Description:</b> </p>
 *
 * @author wdn5e@virginia.edu
 * @version $Id$
 */
abstract class PathAlgorithm implements IPathAlgorithm {

	//protected static final Configuration configuration = Configuration.getInstance();

	protected static final String sep = File.separator;

	private final String storeBase;

	protected final String getStoreBase() {
		return storeBase;
	}

	protected PathAlgorithm (String storeBase) {
		this.storeBase = storeBase;
	}

	private static final String encode(String unencoded) throws LowlevelStorageException {
        try {
            int i = unencoded.indexOf("+");
            if (i != -1) {
                return Server.getPID(unencoded.substring(0, i)).toFilename() 
                        + unencoded.substring(i);
            } else {
    		    return Server.getPID(unencoded).toFilename();
            }
        } catch (MalformedPidException e) {
            throw new LowlevelStorageException(true, e.getMessage(), e);
        }
	}

	public static final String decode(String encoded) throws LowlevelStorageException {
        try {
            int i = encoded.indexOf("+");
            if (i != -1) {
    		    return Server.pidFromFilename(encoded.substring(0, i)).toString() 
    		            + encoded.substring(i);
            } else {
    		    return Server.pidFromFilename(encoded).toString();
            }
        } catch (MalformedPidException e) {
            throw new LowlevelStorageException(true, e.getMessage(), e);
        }
	}

	abstract protected String format (String pid) throws LowlevelStorageException;

	public final String get (String pid) throws LowlevelStorageException {
		return format(encode(pid));
	}

}

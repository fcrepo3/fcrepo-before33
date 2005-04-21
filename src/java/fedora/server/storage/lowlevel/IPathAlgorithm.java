package fedora.server.storage.lowlevel;
import fedora.server.errors.LowlevelStorageException;

/**
 *
 * <p><b>Title:</b> IPathAlgorithm.java</p>
 * <p><b>Description:</b> </p>
 *
 * @author wdn5e@virginia.edu
 * @version $Id$
 */
interface IPathAlgorithm {
	public String get (String pid) throws LowlevelStorageException;
}

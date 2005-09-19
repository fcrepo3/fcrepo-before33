package fedora.server.storage.lowlevel;

import java.io.File;
import java.io.InputStream;

import fedora.server.errors.LowlevelStorageException;

/**
 *
 * <p><b>Title:</b> IFileSystem.java</p>
 * <p><b>Description:</b> </p>
 *
 * @author wdn5e@virginia.edu
 * @version $Id$
 */
interface IFileSystem {
	public void write(File file, InputStream content) throws LowlevelStorageException;
	public void rewrite(File file, InputStream content) throws LowlevelStorageException;
	public InputStream read(File file) throws LowlevelStorageException;
	public void delete(File file) throws LowlevelStorageException;
}

package fedora.server.storage.lowlevel;
import java.io.IOException;
import java.io.File;
import java.io.InputStream;
import java.io.FileInputStream;
import fedora.server.errors.LowlevelStorageException;
interface IFileSystem {
	public void write(File file, InputStream content) throws LowlevelStorageException;
	public void rewrite(File file, InputStream content) throws LowlevelStorageException;
	public FileInputStream read(File file) throws LowlevelStorageException;
	public void delete(File file) throws LowlevelStorageException;
}

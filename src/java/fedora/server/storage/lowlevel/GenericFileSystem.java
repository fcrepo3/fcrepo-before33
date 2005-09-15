//synch issues
//assume that there might be a registry rebuild process which might erroneously add
//entries from orphaned files

//check existing low-level in file model, cp w/ properties
package fedora.server.storage.lowlevel;
import java.io.IOException;
import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Date;
import fedora.server.errors.LowlevelStorageException;

/**
 *
 * <p><b>Title:</b> GenericFileSystem.java</p>
 * <p><b>Description:</b> </p>
 *
 * @author wdn5e@virginia.edu
 * @version $Id$
 */
class GenericFileSystem implements IFileSystem {
	private static int delay = 0;
	GenericFileSystem() {
		//this.delay = 5000;
	}

	void log(String string) {
		System.err.println(string);
	}

	private final File wrappedNewFile(File file, String suffix) throws LowlevelStorageException {
		File temp;
		String path = "";
		try {
			path = file.getCanonicalPath() + suffix;
			temp = new File(path);
		} catch (Exception e) {
			throw new LowlevelStorageException(true, "GenericFileSystem.wrappedNewFile(): couldn't create File for [" + path + "]", e);
		}
		return temp;
	}

	private final String getPath(File file) { //<===================
		String temp;
		try {
			temp = file.getCanonicalPath();
		} catch (Exception eCaughtFiles) {
			temp = "";
		}
		return temp;
	}

	public final void write(File file, InputStream content) throws LowlevelStorageException {
		try {
			writeIntoExistingDirectory(file,content);
		} catch (LowlevelStorageException eCaught) {
			File containingDirectories = null;
			try {
				containingDirectories = file.getParentFile();
				containingDirectories.mkdirs();
			} catch (Exception e) {
				throw new LowlevelStorageException(true, "GenericFileSystem.write(): couldn't make directories for [" + getPath(file) + "]", e);
			}
			writeIntoExistingDirectory(file,content);
		}
	}

	private static final int bufferLength = 512;
	private static final void stream2streamCopy (InputStream in, OutputStream out) throws IOException {
		byte[] buffer= new byte[bufferLength];
		int bytesRead = 0;
		while ((bytesRead = in.read(buffer,0,bufferLength)) != -1) {
			out.write(buffer,0,bytesRead);
		}
	}

	private final void writeIntoExistingDirectory(File file, InputStream content) throws LowlevelStorageException {
		//buffered writer?
		FileOutputStream fileOutputStream = null;
		try {
			fileOutputStream = new FileOutputStream(file);
		} catch (Exception eCaughtFileNotCreated) {
			throw new LowlevelStorageException(true, "couldn't create file " + getPath(file), eCaughtFileNotCreated);
		}
		try {
			stream2streamCopy (content, fileOutputStream);
		} catch (IOException eCaughtFileNotWritten) {
			throw new LowlevelStorageException(true, "couldn't write new file " + getPath(file), eCaughtFileNotWritten);
		} finally {
			try {
				fileOutputStream.close();
				content.close();
			} catch (Exception eCaughtFileNotClosed) {
				throw new LowlevelStorageException(true, "couldn't close new file " + getPath(file), eCaughtFileNotClosed);
			}
		}
	}

	public void rewrite(File file, InputStream content) throws LowlevelStorageException {
		//buffered writer?
		if (! file.exists()) {
			throw new LowlevelStorageException(true, "couldn't open old file for writing " + getPath(file));
		}

		long now; {
			Date date = new Date();
			now = date.getTime();
		}

		File temp = wrappedNewFile(file, ".temp." + now);

		File old = wrappedNewFile(file, ".old." + now);

		FileOutputStream fileOutputStream = null;
		try {
			fileOutputStream = new FileOutputStream(temp);
		} catch (Exception eCaughtOpen) {
			throw new LowlevelStorageException(true, "file couldn't be opened for writing " + getPath(temp), eCaughtOpen);
		}
		try {
			stream2streamCopy (content, fileOutputStream);
		} catch (Exception eCaughtWrite) {
			throw new LowlevelStorageException(true, "file couldn't be opened for renaming " + getPath(old), eCaughtWrite);
		} finally {
			try {
				fileOutputStream.close();
				content.close();
			} catch (Exception eCaughtCloseFile) {
				throw new LowlevelStorageException(true, "file couldn't be opened for writing " + getPath(temp), eCaughtCloseFile);
			}
		}
if (0 < delay) {try {Thread.sleep(delay);} catch (InterruptedException ie) {}} // so to watch file creation/renaming/deletion
		try {
			file.renameTo(old);
		} catch (Exception eCaughtRenameOld) {
			throw new LowlevelStorageException(true, "file " + getPath(file) + "couldn't be renamed to " + getPath(old), eCaughtRenameOld);
		}
if (0 < delay) {try {Thread.sleep(delay);} catch (InterruptedException ie) {}} // so to watch file creation/renaming/deletion
		try {
			temp.renameTo(file);
		} catch (Exception eCaughtRenameTemp) {
			throw new LowlevelStorageException(true, "file " + getPath(file) + "couldn't be renamed to " + getPath(temp), eCaughtRenameTemp);
		}

if (0 < delay) {try {Thread.sleep(delay);} catch (InterruptedException ie) {}} // so to watch file creation/renaming/deletion
		try {
			if (! old.delete()) {
				throw new LowlevelStorageException(true, "file " + getPath(temp) + " couldn't be deleted");
			}
		} catch (Exception eCaughtDelete) {
			throw new LowlevelStorageException(true, "file " + getPath(temp) + " couldn't be deleted", eCaughtDelete);
		}
if (0 < delay) {try {Thread.sleep(delay);} catch (InterruptedException ie) {}} // so to watch file creation/renaming/deletion
}

	public final InputStream read(File file) throws LowlevelStorageException {
		//buffered reader?
		FileInputStream fileInputStream = null; {
			if (! file.exists()) {
				throw new LowlevelStorageException(true, "file "  + getPath(file) + "doesn't exist for reading");
			}
			if (! file.canRead()) {
				throw new LowlevelStorageException(true, "file "  + getPath(file) + "not readable");
			}

			/* compiler complains on new array[long], i.e., on new byte[file.length()];
			   also, signature fileInputStream.read(byte[],int,int) balks on ...,long,long) */
			int fileLength; {
				long lFileLength;
				try {
					lFileLength = file.length();
				} catch (Exception eCaughtStatFile) { //<== make specific
					throw new LowlevelStorageException(true, "file " + getPath(file) + "couldn't be statted for reading", eCaughtStatFile);
				}
				if (lFileLength > Integer.MAX_VALUE) {
					throw new LowlevelStorageException(true, "file " + getPath(file) + "too large for reading");
				}

				fileLength = (int) lFileLength;
			}
			try {
				fileInputStream = new FileInputStream(file);
			} catch (IOException eCaughtOpenFile) {
				throw new LowlevelStorageException(true, "file " + getPath(file) + "couldn't be opened for reading", eCaughtOpenFile);
			}
		} return fileInputStream;
	}

	public final void delete(File file) throws LowlevelStorageException {
		file.delete();
	}
}

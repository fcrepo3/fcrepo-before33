//synch issues
//assume that there might be a registry rebuild process which might erroneously add
//entries from orphaned files

//check existing low-level in file model, cp w/ properties
package fedora.server.storage.lowlevel;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;

import org.apache.log4j.Logger;

import fedora.server.errors.LowlevelStorageException;

/**
 * @author wdn5e@virginia.edu
 * @version $Id$
 */
public class GenericFileSystem extends FileSystem {

    /** Logger for this class. */
    private static Logger LOG = Logger.getLogger(
            GenericFileSystem.class.getName());
    
	public GenericFileSystem(Map configuration) {
		super(configuration);
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

        File backupFile = wrappedNewFile(file, ".bak");

        if (!file.renameTo(backupFile)) {
            try { content.close(); } catch (IOException e) { }
            throw new LowlevelStorageException(true, "failed to rename with "
                    + ".bak extension " + getPath(file));
        }

        boolean needToRevert = false;
        String err = null;
        FileOutputStream out = null;

        try {
            out = new FileOutputStream(file);
            stream2streamCopy(content, out);
        } catch (IOException e) {
            needToRevert = true;
            err = "failed to write content to file " + file.getPath();
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    LOG.warn("Could not close file for writing " + file.getPath(), e);
                }
            }
            try {
                content.close();
            } catch (IOException e) {
                LOG.warn("Could not close content stream for reading", e);
            }
        }

        if (needToRevert) {
            if (backupFile.renameTo(file)) {
                err += ", so reverted to original";
            } else {
                err += ", AND failed to revert to original from .bak!";
            }
            throw new LowlevelStorageException(true, err);
        } else {
            if (!backupFile.delete()) {
                LOG.warn("Could not delete backup file " + backupFile.getPath());
            }
        }

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

	public String[] list(File d) {
		return d.list();
	}

	public boolean isDirectory(File f) {
		return f.isDirectory();
	}
}

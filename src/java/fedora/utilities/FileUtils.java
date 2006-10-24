package fedora.utilities;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;

public class FileUtils {
	public static final int BUFFER = 2048;
	
	/**
	 * Copy an InputStream to an OutputStream.
	 * 
	 * @param source
	 * @param destination
	 * @return <code>true</code> if the operation was successful;
	 * 		   <code>false</code> otherwise (which includes a null input).
	 */
	public static boolean copy(InputStream source, OutputStream destination) {
    	int count;
    	byte data[] = new byte[BUFFER];
    	BufferedOutputStream dest = new BufferedOutputStream(destination, BUFFER);
    	
    	try {
	    	while ((count = source.read(data, 0, BUFFER)) != -1) {
	    		dest.write(data, 0, count);
	    	}
			dest.flush();
			dest.close();
			return true;
    	} catch (IOException e) {
    		return false;
    	}
    }
	
	/**
	 * Copy a File.
	 * 
	 * @param source
	 * @param destination
	 * @return <code>true</code> if the operation was successful;
	 * 		   <code>false</code> otherwise (which includes a null input).
	 */
	public static boolean copy(File source, File destination) {
		boolean result = true;
		if (!destination.exists() && destination.isDirectory()) {
			result = result && destination.mkdirs();
		}
		if (source.isDirectory()) {
			File[] children = source.listFiles();
			for (int i = 0; i < children.length; i++) {
				result = result && copy(new File(source, children[i].getName()), 
						new File(destination, children[i].getName()));
			}
			return result;
		} else {
			try {
				InputStream in = new FileInputStream(source);
		    	OutputStream out = new FileOutputStream(destination);
		    	result = result && copy(in, out);
		    	out.close();
		    	in.close();
		    	return result;
			} catch (IOException e) {
	    		return false;
	    	}
		}
    }
	
	/**
	 * Delete a File.
	 * 
	 * @param file the File to delete.
	 * 
	 * @return <code>true</code> if the operation was successful;
	 * 		   <code>false</code> otherwise (which includes a null input).
	 */
	public static boolean delete(File file) {
        boolean result = true;

        if (file == null) {
        	return false;
        }
        if (file.exists()) {
        	if (file.isDirectory()) {
                // 1. delete content of directory:
                File[] children = file.listFiles();
                for (int i = 0; i < children.length; i++) { //for each file:
                    File child = children[i];
                    result = result && delete(child);
                }//next file
        	}
        	result = result && file.delete();
        } //else: input directory does not exist or is not a directory
        return result;
    }
	
	/**
	 * Delete the specified file or directory.
	 * @param file File or directory to delete
	 * @return <code>true</code> if the operation was successful;
	 * 		   <code>false</code> otherwise (which includes a null input).
	 */
	public static boolean delete(String file) {
        return delete(new File(file));
    }
	
	/**
	 * Move a File.
	 * Initally attempts to move the File using java.io.File.renameTo(). 
	 * However, should this operation fail (e.g. when source and destination 
	 * are across different filesystems), will attempt to copy and then delete 
	 * the source. 
	 * 
	 * @param source
	 * @param destination
	 * @return <code>true</code> if the operation was successful;
	 * 		   <code>false</code> otherwise (which includes a null input).
	 */
	public static boolean move(File source, File destination) {
		if (source == null || destination == null) {
			return false;
		}
		if (source.renameTo(destination)) {
			return true;
		} else {
			return copy(source, destination) && delete(source);
		}
	}
	
	/**
     * Load properties from the given file.
     */
    public static Properties loadProperties(File f) throws IOException {
        Properties props = new Properties();
        FileInputStream in = new FileInputStream(f);
        try {
            props.load(in);
            return props;
        } finally {
            try { in.close(); } catch (IOException e) { }
        }
    }
    
    public static FileFilter getPrefixFileFilter(String prefix) {
    	return new PrefixFileFilter(prefix);
    }
    
    public static FileFilter getSuffixFileFilter(String suffix) {
    	return new SuffixFileFilter(suffix);
    }
    
    private static class PrefixFileFilter implements FileFilter {
        private final String filenamePrefix;

        PrefixFileFilter(String filenamePrefix) {
            this.filenamePrefix = filenamePrefix;
        }

        public boolean accept(File file) {
            String filename = file.getName();
            return filename.startsWith(this.filenamePrefix);
        }
    }
    
    private static class SuffixFileFilter implements FileFilter {
        private final String filenameSuffix;

        SuffixFileFilter(String filenameSuffix) {
            this.filenameSuffix = filenameSuffix;
        }

        public boolean accept(File file) {
            String filename = file.getName();
            return filename.endsWith(this.filenameSuffix);
        }
    }
}

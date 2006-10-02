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
	
	public static void copy(InputStream source, OutputStream destination) throws IOException {
    	int count;
    	byte data[] = new byte[BUFFER];
    	BufferedOutputStream dest = new BufferedOutputStream(destination, BUFFER);
    	while ((count = source.read(data, 0, BUFFER)) != -1) {
              dest.write(data, 0, count);
    	}
       dest.flush();
       dest.close();
    }
	
	public static void copy(File source, File destination) throws IOException {
		if (!destination.exists() && destination.isDirectory()) {
			destination.mkdirs();
		}
		if (source.isDirectory()) {
			File[] children = source.listFiles();
			for (int i = 0; i < children.length; i++) {
				copy(new File(source, children[i].getName()), 
						new File(destination, children[i].getName()));
			}
		} else {
			InputStream in = new FileInputStream(source);
	    	OutputStream out = new FileOutputStream(destination);
	    	copy(in, out);
	    	out.close();
	    	in.close();
		}
    }
	
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
	 * 
	 * @param file File or directory to delete
	 * @return 
	 */
	public static boolean delete(String file) {
        return delete(new File(file));
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

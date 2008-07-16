/* The contents of this file are subject to the license and copyright terms
 * detailed in the license directory at the root of the source tree (also 
 * available online at http://www.fedora.info/license/).
 */

package fedora.utilities;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.Map.Entry;

import fedora.common.FaultException;

public class FileUtils {

    public static final int BUFFER = 2048;

    /**
     * Copy an InputStream to an OutputStream.
     * <p>
     * Both streams will be closed automatically, whether copying ultimately
     * succeeds or fails.
     * 
     * @param source
     * @param destination
     * @return <code>true</code> if the operation was successful;
     *         <code>false</code> otherwise (which includes a null input).
     */
    public static boolean copy(InputStream source, OutputStream destination) {
        int count;
        byte data[] = new byte[BUFFER];
        BufferedOutputStream dest =
                new BufferedOutputStream(destination, BUFFER);
        try {
            while ((count = source.read(data, 0, BUFFER)) != -1) {
                dest.write(data, 0, count);
            }
            dest.flush();
            return true;
        } catch (IOException e) {
            return false;
        } finally {
            try {
                source.close();
                dest.close();
            } catch (IOException e) {
                throw new FaultException("Failed to close stream after copy",
                                         e);
            }
        }
    }

    /**
     * Copy a file or directory.
     * 
     * @param source
     * @param destination
     * @return <code>true</code> if the operation was successful;
     *         <code>false</code> otherwise (which includes a null input).
     */
    public static boolean copy(File source, File destination) {
        boolean result = true;
        if (source.isDirectory()) {
            if (destination.exists()) {
                result = result && destination.isDirectory();
            } else {
                result = result && destination.mkdirs();
            }
            File[] children = source.listFiles();
            for (File element : children) {
                result =
                        result
                                && copy(new File(source, element.getName()),
                                        new File(destination, element.getName()));
            }
            return result;
        } else {
            try {
                InputStream in = new FileInputStream(source);
                OutputStream out = new FileOutputStream(destination);
                result = result && copy(in, out);
                return result;
            } catch (IOException e) {
                return false;
            }
        }
    }
    
    /**
     * Create a temporary directory.
     * 
     * @param prefix
     * @param directory
     * @return
     * @throws IOException
     */
    public static File createTempDir(String prefix, File directory) throws IOException {
    	File tempFile = File.createTempFile(prefix, "", directory);
        if (!tempFile.delete())
            throw new IOException();
        if (!tempFile.mkdir())
            throw new IOException();
        return tempFile;
    }
    
    /**
     * Delete a File.
     * 
     * @param file
     *        the File to delete.
     * @return <code>true</code> if the operation was successful;
     *         <code>false</code> otherwise (which includes a null input).
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
                for (File child : children) { //for each file:
                    result = result && delete(child);
                }//next file
            }
            result = result && file.delete();
        } //else: input directory does not exist or is not a directory
        return result;
    }

    /**
     * Delete the specified file or directory.
     * 
     * @param file
     *        File or directory to delete
     * @return <code>true</code> if the operation was successful;
     *         <code>false</code> otherwise (which includes a null input).
     */
    public static boolean delete(String file) {
        return delete(new File(file));
    }

    /**
     * Move a file or directory. Initally attempts to move the File using
     * java.io.File.renameTo(). However, should this operation fail (e.g. when
     * source and destination are across different filesystems), will attempt to
     * copy and then delete the source.
     * 
     * @param source
     * @param destination
     * @return <code>true</code> if the operation was successful;
     *         <code>false</code> otherwise (which includes a null input).
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
            try {
                in.close();
            } catch (IOException e) {
            }
        }
    }

    /**
     * Loads a Map from the given Properties file.
     * 
     * @param f
     *        the Properties file to parse.
     * @return a Map<String, String> representing the given Properties file.
     * @throws IOException
     * @see java.util.Properties
     * @see java.util.Map
     */
    public static Map<String, String> loadMap(File f) throws IOException {
        Properties props = new Properties();
        FileInputStream in = new FileInputStream(f);
        try {
            props.load(in);
            Map<String, String> map = new HashMap<String, String>();
            Set<Entry<Object, Object>> entrySet = props.entrySet();
            for (Entry<Object, Object> entry : entrySet) {
                // The casts to String should always succeed
                map.put((String) entry.getKey(), (String) entry.getValue());
            }
            return map;
        } finally {
            try {
                in.close();
            } catch (IOException e) {
            }
        }
    }

    public static FileFilter getPrefixFileFilter(String prefix) {
        return new PrefixFileFilter(prefix);
    }

    public static FileFilter getSuffixFileFilter(String suffix) {
        return new SuffixFileFilter(suffix);
    }

    private static class PrefixFileFilter
            implements FileFilter {

        private final String filenamePrefix;

        PrefixFileFilter(String filenamePrefix) {
            this.filenamePrefix = filenamePrefix;
        }

        public boolean accept(File file) {
            String filename = file.getName();
            return filename.startsWith(filenamePrefix);
        }
    }

    private static class SuffixFileFilter
            implements FileFilter {

        private final String filenameSuffix;

        SuffixFileFilter(String filenameSuffix) {
            this.filenameSuffix = filenameSuffix;
        }

        public boolean accept(File file) {
            String filename = file.getName();
            return filename.endsWith(filenameSuffix);
        }
    }

}

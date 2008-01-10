/* The contents of this file are subject to the license and copyright terms
 * detailed in the license directory at the root of the source tree (also 
 * available online at http://www.fedora.info/license/).
 */

package fedora.server.storage.lowlevel;

import java.io.File;
import java.io.InputStream;

import java.util.Map;

import fedora.server.errors.LowlevelStorageException;

/**
 * @author Bill Niebel
 */
public abstract class FileSystem {

    public FileSystem(Map configuration) {
    }

    public abstract InputStream read(File file) throws LowlevelStorageException;

    public abstract void write(File file, InputStream content)
            throws LowlevelStorageException;

    public abstract void rewrite(File file, InputStream content)
            throws LowlevelStorageException;

    public abstract void delete(File file) throws LowlevelStorageException;

    /**
     * THIS IS ONLY FOR TESTING. Use of this method on a production system may
     * cause irreparable data loss. YOU HAVE BEEN WARNED.
     * 
     * @param directory
     * @return true if the delete was successful
     */
    public boolean deleteDirectory(String directory) {
        boolean result = false;

        if (directory != null) {
            File file = new File(directory);
            if (file.exists() && file.isDirectory()) {
                // 1. delete content of directory:
                File[] files = file.listFiles();
                result = true; //init result flag
                int count = files.length;
                for (int i = 0; i < count; i++) { //for each file:
                    File f = files[i];
                    if (f.isFile()) {
                        result = result && f.delete();
                    } else if (f.isDirectory()) {
                        result = result && deleteDirectory(f.getAbsolutePath());
                    }
                }//next file

                file.delete(); //finally delete (empty) input directory
            }//else: input directory does not exist or is not a directory
        }//else: no input value

        return result;
    }

    public abstract String[] list(File directory);

    public abstract boolean isDirectory(File file);
}

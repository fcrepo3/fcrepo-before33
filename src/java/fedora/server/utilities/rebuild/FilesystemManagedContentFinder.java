package fedora.server.utilities.rebuild;

import java.io.InputStream;

/**
 * Finds managed content in lowlevel storage.
 *
 * NOTE: Proper functionality hinges on the following filename convention:
 *
 * <pre>
 *     ds-filename  = pid-filename "+" ds-id "+" ds-version-id
 *     pid-filename = described in fedora.common.PID
 * </pre>
 *
 * From the baseDir provided in the constructor, the filesystem will
 * be traversed depth-first in alphanumeric order.
 */
public class FilesystemManagedContentFinder implements ManagedContentFinder {

    /**
     * Prepare for use.
     * Index things, set up needed connections, etc.
     */
    public void init() { 
    }

    /**
     * Find the indicated managed datastream.
     *
     * @returns an InputStream or null if not found.
     */
    public InputStream find(String pid,
                            String dsID,
                            String dsVersionID) {
        return null;
    }

    /**
     * Release any resources used.
     */
    public void finish() {
    }

}
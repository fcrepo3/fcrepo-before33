package fedora.server.utilities.rebuild;

import java.io.InputStream;

public interface ManagedContentFinder {

    /**
     * Find the indicated managed datastream.
     *
     * @returns an InputStream or null if not found.
     */
    public InputStream find(String pid,
                            String dsID,
                            String dsVersionID) throws Exception;

    /**
     * Release any resources used.
     */
    public void finish() throws Exception;

}
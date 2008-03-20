
package fedora.server.journal.helpers;

import java.io.File;

/**
 * Subclass of File is used as a marker. An instance of this class behaves
 * exactly as a java.io.File object would behave. However, it can be tested with
 * instanceof to reveal that it is in fact a temp file, and as such can safely
 * be deleted after use.
 * 
 * @author jblake@cs.cornell.edu
 * @version $Id: JournalTempFile.java 5408 2006-12-12 11:46:18 +0000 (Tue, 12
 *          Dec 2006) eddie $
 */

public class JournalTempFile
        extends File {

    private static final long serialVersionUID = 1L;

    public JournalTempFile(File file) {
        super(file.getPath());
    }
}

/*
 * -----------------------------------------------------------------------------
 *
 * <p><b>License and Copyright: </b>The contents of this file are subject to the
 * Educational Community License (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License
 * at <a href="http://www.opensource.org/licenses/ecl1.txt">
 * http://www.opensource.org/licenses/ecl1.txt.</a></p>
 *
 * <p>Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License for
 * the specific language governing rights and limitations under the License.</p>
 *
 * <p>The entire file consists of original code.  Copyright &copy; 2002-2006 by 
 * The Rector and Visitors of the University of Virginia and Cornell University.
 * All rights reserved.</p>
 *
 * -----------------------------------------------------------------------------
 */

package fedora.server.journal.helpers;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * 
 * <p>
 * <b>Title:</b> FileMovingUtil.java
 * </p>
 * <p>
 * <b>Description:</b> Provides a workaround to the fact that
 * {@link java.io.File.renameTo(java.io.File)} doesn't work across NFS file
 * systems.
 * </p>
 * <p>
 * This code is taken from a workaround provided on the Sun Developer Network
 * Bug Database (http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=4073756), 
 * by mailto:morgan.sziraki@cartesian.co.uk
 * </p>
 * 
 * @author jblake@cs.cornell.edu
 * @version $Id$
 */

public class FileMovingUtil {

    private FileMovingUtil() {
        // No need to instantiate, since the only public method is static.
    }

    /**
     * Move a File
     * 
     * The renameTo method does not allow action across NFS mounted filesystems.
     * This method is the workaround.
     * 
     * @param fromFile
     *            The existing File
     * @param toFile
     *            The new File
     * @return <code>true</code> if and only if the renaming succeeded;
     *         <code>false</code> otherwise
     */
    public final static boolean move(File fromFile, File toFile) {
        if (fromFile.renameTo(toFile)) {
            return true;
        }

        // delete if copy was successful, otherwise move will fail
        if (copy(fromFile, toFile)) {
            return fromFile.delete();
        }

        return false;
    }

    /**
     * Copy a File
     * 
     * @param fromFile
     *            The existing File
     * @param toFile
     *            The new File
     * @return <code>true</code> if and only if the renaming succeeded;
     *         <code>false</code> otherwise
     */
    private final static boolean copy(File fromFile, File toFile) {
        try {
            FileInputStream in = new FileInputStream(fromFile);
            FileOutputStream out = new FileOutputStream(toFile);
            BufferedInputStream inBuffer = new BufferedInputStream(in);
            BufferedOutputStream outBuffer = new BufferedOutputStream(out);

            int theByte = 0;

            while ((theByte = inBuffer.read()) > -1) {
                outBuffer.write(theByte);
            }

            outBuffer.close();
            inBuffer.close();
            out.close();
            in.close();

            // cleanupif files are not the same length
            if (fromFile.length() != toFile.length()) {
                toFile.delete();

                return false;
            }

            return true;
        } catch (IOException e) {
            return false;
        }
    }

}
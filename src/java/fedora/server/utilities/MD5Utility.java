package fedora.server.utilities;

import com.twmacinta.util.MD5;

/**
 *
 * <p><b>Title:</b> ND5Utility.java</p>
 * <p><b>Description:</b> Static methods for creating evenly-distributed hashes.
 * </p>
 *
 * -----------------------------------------------------------------------------
 *
 * <p><b>License and Copyright: </b>The contents of this file are subject to the
 * Mozilla Public License Version 1.1 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License
 * at <a href="http://www.mozilla.org/MPL">http://www.mozilla.org/MPL/.</a></p>
 *
 * <p>Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License for
 * the specific language governing rights and limitations under the License.</p>
 *
 * <p>The entire file consists of original code.  Copyright &copy; 2002-2005 by The
 * Rector and Visitors of the University of Virginia and Cornell University.
 * All rights reserved.</p>
 *
 * -----------------------------------------------------------------------------
 *
 * @author cwilper@cs.cornell.edu
 * @version $Id$
 */
public abstract class MD5Utility {

    static {
        MD5.initNativeLibrary(true); // don't attempt to use the native libs, ever.
    }

    /**
     * Get hash of the given String in hex.
     */
    public static String getBase16Hash(String in) {
        return MD5.asHex(new MD5(in).Final());
    }

    /**
     * Command-line test that returns the hash of the argument, in hex.
     */
    public static void main(String[] args) {
        System.out.println(MD5Utility.getBase16Hash(args[0]));
    }

}
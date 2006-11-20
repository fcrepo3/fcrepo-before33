package fedora.server.utilities;

import com.twmacinta.util.MD5;

/**
 * Static methods for creating evenly-distributed hashes.
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

}
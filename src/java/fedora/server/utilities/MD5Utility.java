package fedora.server.utilities;

import com.twmacinta.util.MD5;

/**
 *
 * <p><b>Title:</b> ND5Utility.java</p>
 * <p><b>Description:</b> Static methods for creating evenly-distributed hashes.
 * </p>
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
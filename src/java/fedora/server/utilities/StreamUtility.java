package fedora.server.utilities;

/**
 * Static utility methods useful when working with character-based
 * or raw sequences of data.
 *
 * @author cwilper@cs.cornell.edu
 */
public abstract class StreamUtility {

    /**
     * Returns an XML-appropriate encoding of the given String.
     *
     * @param in The String to encode.
     * @return A new, encoded String.
     */
    public static String enc(String in) {
        StringBuffer out=new StringBuffer();
        enc(in, out);
        return out.toString();
    }

    /**
     * Appends an XML-appropriate encoding of the given String to the given 
     * StringBuffer.
     *
     * @param in The String to encode.
     * @param buf The StringBuffer to write to.
     */
    public static void enc(String in, StringBuffer out) {
        for (int i=0; i<in.length(); i++) {
            enc(in.charAt(i),out);
        }
    }

    /**
     * Appends an XML-appropriate encoding of the given range of characters
     * to the given StringBuffer.
     *
     * @param in The char buffer to read from.
     * @param start The starting index.
     * @param length The number of characters in the range.
     * @param out The StringBuffer to write to.
     */
    public static void enc(char[] in, int start, int length, StringBuffer out) {
        for (int i=start; i<length+start; i++) {
            enc(in[i], out);
        }
    }

    /**
     * Appends an XML-appropriate encoding of the given character to the
     * given StringBuffer.
     *
     * @param in The character.
     * @param out The StringBuffer to write to.
     */
    public static void enc(char in, StringBuffer out) {
        if (in=='&') {
            out.append("&amp;");
        } else if (in=='<') {
            out.append("&lt;");
        } else if (in=='>') {
            out.append("&gt;");
        } else if (in=='\"') {
            out.append("&quot;");
        } else if (in=='\'') {
            out.append("&apos;");
        } else {
            out.append(in);
        }
    }

}
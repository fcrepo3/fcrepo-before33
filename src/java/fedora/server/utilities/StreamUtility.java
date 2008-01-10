/* The contents of this file are subject to the license and copyright terms
 * detailed in the license directory at the root of the source tree (also 
 * available online at http://www.fedora.info/license/).
 */

package fedora.server.utilities;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.axis.encoding.Base64;

import org.apache.log4j.Logger;

/**
 * Utility methods for working with character-based or raw sequences of data.
 * 
 * @author Chris Wilper
 */
public abstract class StreamUtility {

    /** Logger for this class. */
    private static final Logger LOG =
            Logger.getLogger(StreamUtility.class.getName());

    /**
     * Returns an XML-appropriate encoding of the given String.
     * 
     * @param in
     *        The String to encode.
     * @return A new, encoded String.
     */
    public static String enc(String in) {
        String inStr = in;
        if (inStr == null) {
            inStr = "";
        }
        StringBuffer out = new StringBuffer();
        enc(inStr, out);
        return out.toString();
    }

    /**
     * Appends an XML-appropriate encoding of the given String to the given
     * StringBuffer.
     * 
     * @param in
     *        The String to encode.
     * @param out
     *        The StringBuffer to write to.
     */
    public static void enc(String in, StringBuffer out) {
        for (int i = 0; i < in.length(); i++) {
            enc(in.charAt(i), out);
        }
    }

    /**
     * Appends an XML-appropriate encoding of the given range of characters to
     * the given StringBuffer.
     * 
     * @param in
     *        The char buffer to read from.
     * @param start
     *        The starting index.
     * @param length
     *        The number of characters in the range.
     * @param out
     *        The StringBuffer to write to.
     */
    public static void enc(char[] in, int start, int length, StringBuffer out) {
        for (int i = start; i < length + start; i++) {
            enc(in[i], out);
        }
    }

    /**
     * Appends an XML-appropriate encoding of the given character to the given
     * StringBuffer.
     * 
     * @param in
     *        The character.
     * @param out
     *        The StringBuffer to write to.
     */
    public static void enc(char in, StringBuffer out) {
        if (in == '&') {
            out.append("&amp;");
        } else if (in == '<') {
            out.append("&lt;");
        } else if (in == '>') {
            out.append("&gt;");
        } else if (in == '\"') {
            out.append("&quot;");
        } else if (in == '\'') {
            out.append("&apos;");
        } else {
            out.append(in);
        }
    }

    /**
     * Copies the contents of an InputStream to an OutputStream, then closes
     * both.
     * 
     * @param in
     *        The source stream.
     * @param out
     *        The target stram.
     * @param bufSize
     *        Number of bytes to attempt to copy at a time.
     * @throws IOException
     *         If any sort of read/write error occurs on either stream.
     */
    public static void pipeStream(InputStream in, OutputStream out, int bufSize)
            throws IOException {
        try {
            byte[] buf = new byte[bufSize];
            int len;
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
        } finally {
            try {
                in.close();
                out.close();
            } catch (IOException e) {
                LOG.warn("Unable to close stream", e);
            }
        }
    }

    public static byte[] decodeBase64(String data) {
        return Base64.decode(data);
    }

    public static String encodeBase64(byte[] data) {
        return Base64.encode(data);
    }

    public static String encodeBase64(InputStream is) {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        try {
            pipeStream(is, os, 1024);
        } catch (IOException ioe) {
            LOG.warn("Unable to encode stream", ioe);
        }
        return Base64.encode(os.toByteArray());
    }

}

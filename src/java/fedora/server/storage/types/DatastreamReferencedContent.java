package fedora.server.storage.types;

import fedora.server.errors.StreamIOException;

import java.io.InputStream;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.net.HttpURLConnection;

/**
 * Referenced content.
 */
public class DatastreamReferencedContent
        extends Datastream {
 
    public DatastreamReferencedContent() {
    }
    
    public URL DSLocation;  // only here.. not in Datastream.java

    /**
     * Gets an InputStream to the content of this externally-referenced
     * datastream.
     * <p></p>
     * The DSLocation of this datastream must be non-null before invoking
     * this method.
     * <p></p>
     * If successful, the DSMIME type is automatically set based on the
     * web server's response header.  If the web server doesn't send a
     * valid Content-type: header, as a last resort, the content-type
     * is guessed by using a map of common extensions to mime-types.
     */
    public InputStream getContentStream() 
            throws StreamIOException {
        try {
            HttpURLConnection conn=(HttpURLConnection) DSLocation.openConnection();
            if (conn.getResponseCode()!=HttpURLConnection.HTTP_OK) {
                throw new StreamIOException(
                        "Server returned a non-200 response code from URL: " 
                        + DSLocation.toString());
            }
            // DSSize -- a String in Datastream.java?
            InputStream ret=conn.getInputStream();
            DSMIME=conn.getContentType();
            if (DSMIME==null) {
                DSMIME=HttpURLConnection.guessContentTypeFromName(
                        DSLocation.getFile());
            }
            return ret;
        } catch (IOException ioe) {
            throw new StreamIOException("Can't get InputStream from URL: " 
                    + DSLocation.toString());
        } catch (ClassCastException cce) {
            throw new StreamIOException("Non-http URLs not supported");
        }
    }
}

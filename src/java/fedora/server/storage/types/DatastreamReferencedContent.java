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
        extends DatastreamContent {
 
    public DatastreamReferencedContent() {
    }
    
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
     * <p></p>
     * If the content-length header is present in the response, DSSize
     * will be set accordingly.
     */
    public InputStream getContentStream() 
            throws StreamIOException {
        try {
            HttpURLConnection conn=(HttpURLConnection) 
                    new URL(DSLocation).openConnection();
            if (conn.getResponseCode()!=HttpURLConnection.HTTP_OK) {
                throw new StreamIOException(
                        "Server returned a non-200 response code ("
                        + conn.getResponseCode() + ") from GET request of URL: " 
                        + DSLocation.toString());
            }
            // Ensure the stream is available before setting any fields.
            InputStream ret=conn.getInputStream();
            // If content-length available, set DSSize.
            int reportedLength=conn.getContentLength();
            if (reportedLength>-1) {
                DSSize=reportedLength;
            }
            // If Content-type available, set DSMIME.
            DSMIME=conn.getContentType();
            if (DSMIME==null) {
                DSMIME=HttpURLConnection.guessContentTypeFromName(
                        DSLocation);
            }
            return ret;
        } catch (IOException ioe) {
            throw new StreamIOException("Can't get InputStream from URL: " 
                    + DSLocation.toString());
        } catch (ClassCastException cce) {
            throw new StreamIOException("Non-http URLs not supported.");
        }
    }
}

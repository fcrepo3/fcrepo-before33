package fedora.server.storage.types;

import fedora.server.errors.StreamIOException;

import java.io.InputStream;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.net.HttpURLConnection;

/**
 * Managed content.
 */
public class DatastreamManagedContent
        extends DatastreamContent {
 
    public DatastreamManagedContent() {
    }
    
    URL DSLocation;  // so it compiles, for now..
    
    /**
     * Gets an InputStream to the content of this managed content
     * datastream.
     */
    public InputStream getContentStream() 
            throws StreamIOException {
        try {
            HttpURLConnection conn=(HttpURLConnection) 
                    DSLocation.openConnection();
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
                        DSLocation.getFile());
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

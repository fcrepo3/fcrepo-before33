package fedora.server.storage.types;

import fedora.server.errors.StreamIOException;

import java.io.InputStream;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.net.HttpURLConnection;

/**
 *
 * <p><b>Title:</b> DatastreamReferencedContent.java</p>
 * <p><b>Description:</b> Referenced Content.</p>
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
 * <p>The entire file consists of original code.  Copyright &copy; 2002, 2003 by The
 * Rector and Visitors of the University of Virginia and Cornell University.
 * All rights reserved.</p>
 *
 * -----------------------------------------------------------------------------
 *
 * @author cwilper@cs.cornell.edu
 * @version $Id$
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

            // SDP: removed because some web servers will reset the mime type
            // to unpredictable things.  We'll keep the mime type originally
            // recorded with the datastream.
            /*
            // If Content-type available, set DSMIME.
            DSMIME=conn.getContentType();
            if (DSMIME==null) {
                DSMIME=HttpURLConnection.guessContentTypeFromName(
                        DSLocation);
            }
            */
            return ret;
        } catch (IOException ioe) {
            throw new StreamIOException("Can't get InputStream from URL: "
                    + DSLocation.toString());
        } catch (ClassCastException cce) {
            throw new StreamIOException("Non-http URLs not supported.");
        }
    }
}

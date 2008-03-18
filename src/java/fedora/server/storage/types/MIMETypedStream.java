/* The contents of this file are subject to the license and copyright terms
 * detailed in the license directory at the root of the source tree (also 
 * available online at http://www.fedora.info/license/).
 */

package fedora.server.storage.types;

import java.io.IOException;
import java.io.InputStream;

import org.apache.log4j.Logger;

/**
 * Data structure for holding a MIME-typed stream.
 * 
 * @author Ross Wayland
 */
public class MIMETypedStream {

    private static final Logger LOG = Logger.getLogger(MIMETypedStream.class);

    public String MIMEType;

    private InputStream stream;

    public Property[] header;

    /**
     * Constructs a MIMETypedStream.
     * 
     * @param MIMEType
     *        The MIME type of the byte stream.
     * @param stream
     *        The byte stream.
     */
    public MIMETypedStream(String MIMEType,
                           InputStream stream,
                           Property[] header) {
        this.MIMEType = MIMEType;
        this.header = header;
        setStream(stream);
    }

    public InputStream getStream() {
        return stream;
    }

    public void setStream(InputStream stream) {
        this.stream = stream;
    }

    /**
     * Closes the underlying stream if it's not already closed.
     * 
     * In the event of an error, a warning will be logged.
     */
    public void close() {
        if (this.stream != null) {
            try {
                this.stream.close();
                this.stream = null;
            } catch (IOException e) {
                LOG.warn("Error closing stream", e);
            }
        }
    }
   
    /**
     * Ensures the underlying stream is closed at garbage-collection time.
     * 
     * {@inheritDoc}
     */
    @Override
    public void finalize() {
        close();
    }
}

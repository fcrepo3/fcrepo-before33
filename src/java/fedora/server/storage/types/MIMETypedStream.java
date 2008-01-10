/* The contents of this file are subject to the license and copyright terms
 * detailed in the license directory at the root of the source tree (also 
 * available online at http://www.fedora.info/license/).
 */

package fedora.server.storage.types;

import java.io.InputStream;

/**
 * Data structure for holding a MIME-typed stream.
 * 
 * @author Ross Wayland
 */
public class MIMETypedStream {

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
}

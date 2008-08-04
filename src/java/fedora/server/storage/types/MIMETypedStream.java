package fedora.server.storage.types;

import java.io.IOException;
import java.io.InputStream;

import org.apache.log4j.Logger;

/**
 *
 * <p><b>Title:</b> MIMETypedStream.java</p>
 * <p><b>Description:</b> Data structure for holding a MIME-typed stream.</p>
 *
 * @author rlw@virginia.edu
 * @version $Id$
 */
public class MIMETypedStream
{
    private static final Logger LOG = Logger.getLogger(MIMETypedStream.class);

    public String MIMEType;
    private InputStream stream;
    public Property[] header;
    private boolean gotStream = false;

    /**
     * <p>Constructs a MIMETypedStream.</p>
     *
     * @param MIMEType The MIME type of the byte stream.
     * @param stream The byte stream.
     */
    public MIMETypedStream(String MIMEType, InputStream stream, Property[] header)
    {
        this.MIMEType = MIMEType;
        this.header = header;
        this.setStream(stream);
    }

    /**
     * Retrieves the underlying stream.
     * Caller is responsible to close the stream,
     * either by calling MIMETypedStream.close()
     * or by calling close() on the stream.
     *
     * @return The byte stream
     */
    public InputStream getStream()
    {
      gotStream = true;
      return stream;
    }

    public void setStream(InputStream stream)
    {
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
     * Ensures the underlying stream is closed at garbage-collection time
     * if the stream has not been retrieved. If getStream() has been called
     * the caller is responsible to close the stream.
     *
     * {@inheritDoc}
     */
    @Override
    public void finalize() {
        if (!gotStream) {
            close();
        }
    }
}
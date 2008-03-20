
package fedora.server.journal.readerwriter.multicast.request;

import java.util.Date;

import fedora.server.journal.JournalException;
import fedora.server.journal.readerwriter.multicast.Transport;

/**
 * TransportRequest that asks the Transports to open a new file, using the
 * supplied filename and repository hash.
 */
public class OpenFileRequest
        extends TransportRequest {

    private final String hash;

    private final String filename;

    private final Date currentDate;

    public OpenFileRequest(final String hash,
                           final String filename,
                           final Date currentDate) {
        this.hash = hash;
        this.filename = filename;
        this.currentDate = currentDate;
    }

    @Override
    public void performRequest(Transport transport) throws JournalException {
        transport.openFile(hash, filename, currentDate);
    }

}
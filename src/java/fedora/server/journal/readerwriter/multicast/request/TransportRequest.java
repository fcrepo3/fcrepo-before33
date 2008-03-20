
package fedora.server.journal.readerwriter.multicast.request;

import fedora.server.journal.JournalException;
import fedora.server.journal.readerwriter.multicast.Transport;

/**
 * The base class for a request that can be sent to each Transport in turn.
 */
public abstract class TransportRequest {

    public abstract void performRequest(Transport transport)
            throws JournalException;
}
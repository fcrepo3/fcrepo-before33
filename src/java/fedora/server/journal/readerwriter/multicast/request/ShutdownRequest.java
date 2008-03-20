
package fedora.server.journal.readerwriter.multicast.request;

import fedora.server.journal.JournalException;
import fedora.server.journal.readerwriter.multicast.Transport;

/**
 * TransportRequest that asks the Transports to shut down.
 */
public class ShutdownRequest
        extends TransportRequest {

    @Override
    public void performRequest(Transport transport) throws JournalException {
        transport.shutdown();
    }
}
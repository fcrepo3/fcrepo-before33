
package fedora.server.journal.readerwriter.multifile;

import java.util.ArrayList;
import java.util.List;

import fedora.server.errors.ServerException;
import fedora.server.journal.ServerInterface;
import fedora.server.management.ManagementDelegate;

class MockServerForJournalTesting
        implements ServerInterface {

    // -------------------------------------------------------------------------
    // Mocking infrastructure.
    // -------------------------------------------------------------------------

    private final String hashValue;

    private final List logCache = new ArrayList();

    private final ManagementDelegate managementDelegate;

    public MockServerForJournalTesting(String hashValue) {
        this.hashValue = hashValue;
        managementDelegate = new MockManagementDelegateForJournalTesting();
    }

    // -------------------------------------------------------------------------
    // Mocked methods.
    // -------------------------------------------------------------------------

    public ManagementDelegate getManagementDelegate() {
        return managementDelegate;
    }

    public String getRepositoryHash() throws ServerException {
        return hashValue;
    }

    public void logSevere(String message) {
        logCache.add(message);

    }

    public void logInfo(String message) {
        logCache.add(message);
    }

    public boolean hasInitialized() {
        return true;
    }

    // -------------------------------------------------------------------------
    // Non-implemented methods.
    // -------------------------------------------------------------------------

}
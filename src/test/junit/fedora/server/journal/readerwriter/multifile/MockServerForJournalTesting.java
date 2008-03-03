
package fedora.server.journal.readerwriter.multifile;

import java.util.ArrayList;
import java.util.List;

import fedora.server.errors.ServerException;
import fedora.server.journal.ServerInterface;
import fedora.server.management.ManagementDelegate;

public class MockServerForJournalTesting
        implements ServerInterface {

    // -------------------------------------------------------------------------
    // Mocking infrastructure.
    // -------------------------------------------------------------------------

    private final String hashValue;

    private final List<String> logCache = new ArrayList<String>();

    private final ManagementDelegate managementDelegate;

    public MockServerForJournalTesting(ManagementDelegate managementDelegate,
                                       String hashValue) {
        this.hashValue = hashValue;
        this.managementDelegate = managementDelegate;
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
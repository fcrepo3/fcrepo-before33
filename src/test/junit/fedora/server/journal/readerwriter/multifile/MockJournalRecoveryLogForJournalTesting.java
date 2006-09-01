package fedora.server.journal.readerwriter.multifile;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import fedora.server.errors.ModuleInitializationException;
import fedora.server.journal.ServerInterface;
import fedora.server.journal.recoverylog.JournalRecoveryLog;

public class MockJournalRecoveryLogForJournalTesting extends JournalRecoveryLog {
    // -------------------------------------------------------------------------
    // Mocking infrastructure.
    // -------------------------------------------------------------------------

    private static MockJournalRecoveryLogForJournalTesting instance;

    public static MockJournalRecoveryLogForJournalTesting getInstance() {
        return instance;
    }

    private final List logMessages = new ArrayList();

    private boolean running = true;

    public MockJournalRecoveryLogForJournalTesting(Map parameters, String role,
            ServerInterface server) throws ModuleInitializationException {
        super(parameters, role, server);
        instance = this;
    }

    public List getLogMessages() {
        return Collections.unmodifiableList(logMessages);
    }
    
    public String getLogSummary() {
        StringBuffer result = new StringBuffer("Log Summary:\n");
        for (int i = 0; i < logMessages.size(); i++) {
            result.append("    ");
            String message = (String) logMessages.get(i);
            if (message.length() > 70) {
                result.append(message.substring(0, 67)).append("...");
            } else {
                result.append(message);
            }
            result.append('\n');
        }
        return result.toString();
    }

    // -------------------------------------------------------------------------
    // Mocked methods.
    // -------------------------------------------------------------------------

    public void log(String message) {
        logMessages.add(message);
    }

    public void shutdown() {
        running = false;
    }

    // -------------------------------------------------------------------------
    // Non-implemented methods.
    // -------------------------------------------------------------------------

}

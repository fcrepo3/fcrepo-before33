
package fedora.server.journal;

import fedora.server.Server;
import fedora.server.errors.ServerException;
import fedora.server.management.ManagementDelegate;
import fedora.server.storage.DOManager;

/**
 * Wrap a Server in an object that implements an interface, so it can be passed
 * to the JournalWorker classes and their dependents. It's also easy to mock,
 * for unit tests.
 * 
 * @author jblake@cs.cornell.edu
 * @version $Id: ServerWrapper.java 5220 2006-11-20 13:52:20 +0000 (Mon, 20 Nov
 *          2006) cwilper $
 */

public class ServerWrapper
        implements ServerInterface {

    private final Server server;

    public ServerWrapper(Server server) {
        this.server = server;
    }

    public boolean hasInitialized() {
        return server.hasInitialized();
    }

    public ManagementDelegate getManagementDelegate() {
        return (ManagementDelegate) server
                .getModule("fedora.server.management.ManagementDelegate");
    }

    public String getRepositoryHash() throws ServerException {
        DOManager doManager =
                (DOManager) server.getModule("fedora.server.storage.DOManager");
        return doManager.getRepositoryHash();
    }

}

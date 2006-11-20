package fedora.server.journal;

import fedora.server.errors.ServerException;
import fedora.server.management.ManagementDelegate;

/**
 * Pass this to the constructors of the JournalWorker 
 * classes and their dependents instead of passing a Server. This makes it much
 * easier to write unit tests, since I don't need to create a Server instance. 
 * 
 * @author jblake@cs.cornell.edu
 * @version $Id$
 */

public interface ServerInterface {

    ManagementDelegate getManagementDelegate();
    
    String getRepositoryHash() throws ServerException;

    boolean hasInitialized();

}

/*
 * -----------------------------------------------------------------------------
 *
 * <p><b>License and Copyright: </b>The contents of this file are subject to the
 * Educational Community License (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License
 * at <a href="http://www.opensource.org/licenses/ecl1.txt">
 * http://www.opensource.org/licenses/ecl1.txt.</a></p>
 *
 * <p>Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License for
 * the specific language governing rights and limitations under the License.</p>
 *
 * <p>The entire file consists of original code.  Copyright &copy; 2002-2006 by 
 * The Rector and Visitors of the University of Virginia and Cornell University.
 * All rights reserved.</p>
 *
 * -----------------------------------------------------------------------------
 */

package fedora.server.journal;

import fedora.server.Server;
import fedora.server.errors.ServerException;
import fedora.server.management.ManagementDelegate;
import fedora.server.storage.DOManager;

/**
 * 
 * <p>
 * <b>Title:</b> ServerWrapper.java
 * </p>
 * <p>
 * <b>Description:</b> Wrap a Server in an object that implements an interface,
 * so it can be passed to the JournalWorker classes and their dependents. It's
 * also easy to mock, for unit tests.
 * </p>
 * 
 * @author jblake@cs.cornell.edu
 * @version $Id$
 */

public class ServerWrapper implements ServerInterface {
    private final Server server;

    public ServerWrapper(Server server) {
        this.server = server;
    }

    public void logSevere(String message) {
        server.logSevere(message);
    }

    public void logInfo(String message) {
        server.logInfo(message);
    }

    public boolean hasInitialized() {
        return server.hasInitialized();
    }

    public ManagementDelegate getManagementDelegate() {
        return (ManagementDelegate) server
                .getModule("fedora.server.management.ManagementDelegate");
    }

    public String getRepositoryHash() throws ServerException {
        DOManager doManager = (DOManager) server
                .getModule("fedora.server.storage.DOManager");
        return doManager.getRepositoryHash();
    }

}

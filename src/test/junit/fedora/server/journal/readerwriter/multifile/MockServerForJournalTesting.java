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

package fedora.server.journal.readerwriter.multifile;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import fedora.server.Context;
import fedora.server.errors.ServerException;
import fedora.server.journal.ServerInterface;
import fedora.server.management.ManagementDelegate;

class MockServerForJournalTesting implements ServerInterface {
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
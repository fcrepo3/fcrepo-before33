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

import fedora.server.errors.ServerException;
import fedora.server.management.ManagementDelegate;

/**
 * 
 * <p>
 * <b>Title:</b> ServerInterface.java
 * </p>
 * <p>
 * <b>Description:</b> Pass this to the constructors of the JournalWorker 
 * classes and their dependents instead of passing a Server. This makes it much
 * easier to write unit tests, since I don't need to create a Server instance. 
 * </p>
 * 
 * @author jblake@cs.cornell.edu
 * @version $Id$
 */

public interface ServerInterface {

    ManagementDelegate getManagementDelegate();
    
    String getRepositoryHash() throws ServerException;

    void logSevere(String message);

    void logInfo(String message);

    boolean hasInitialized();

}

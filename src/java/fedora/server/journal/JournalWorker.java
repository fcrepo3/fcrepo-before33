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

import fedora.server.errors.ModuleInitializationException;
import fedora.server.errors.ModuleShutdownException;
import fedora.server.management.Management;
import fedora.server.management.ManagementDelegate;

/**
 * <p>
 * <b>Title:</b> JournalWorker.java
 * </p>
 * <p>
 * <b>Description:</b> A common interface for the <code>JournalConsumer</code>
 * and <code>JournalCreator</code> classes. These classes form the
 * implementation layer between the <code>Journaller</code> and the
 * <code>ManagementDelegate</code>.
 * </p>
 * 
 * @author jblake@cs.cornell.edu
 * @version $Id$
 */

public interface JournalWorker extends Management {
    /**
     * Called by the Journaller during post-initialization, with a reference to
     * the ManagementDelegate module.
     */
    public void setManagementDelegate(ManagementDelegate delegate)
            throws ModuleInitializationException;

    /**
     * Called when the Journaller module receives a shutdown() from the server.
     */
    public void shutdown() throws ModuleShutdownException;
}

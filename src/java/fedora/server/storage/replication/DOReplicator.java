package fedora.server.storage.replication;

import java.sql.SQLException;

import fedora.server.errors.ReplicationException;
import fedora.server.storage.BDefReader;
import fedora.server.storage.BMechReader;
import fedora.server.storage.DOReader;

/**
 *
 * <p><b>Title:</b> DOReplicator.java</p>
 * <p><b>Description:</b> </p>
 *
 * -----------------------------------------------------------------------------
 *
 * <p><b>License and Copyright: </b>The contents of this file are subject to the
 * Mozilla Public License Version 1.1 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License
 * at <a href="http://www.mozilla.org/MPL">http://www.mozilla.org/MPL/.</a></p>
 *
 * <p>Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License for
 * the specific language governing rights and limitations under the License.</p>
 *
 * <p>The entire file consists of original code.  Copyright &copy; 2002-2005 by The
 * Rector and Visitors of the University of Virginia and Cornell University.
 * All rights reserved.</p>
 *
 * -----------------------------------------------------------------------------
 *
 * @author Paul Charlton, cwilper@cs.cornell.edu
 * @version $Id$
 */
public interface DOReplicator {

    /**
     * Replicates a Fedora behavior definition object.
     *
     * @param bDefReader behavior definition reader
     * @exception ReplicationException replication processing error
     * @exception SQLException JDBC, SQL error
     */
    public void replicate(BDefReader bDefReader)
            throws ReplicationException, SQLException;

    /**
     * Replicates a Fedora behavior mechanism object.
     *
     * @param bMechReader behavior mechanism reader
     * @exception ReplicationException replication processing error
     * @exception SQLException JDBC, SQL error
     */
    public void replicate(BMechReader bMechReader)
            throws ReplicationException, SQLException;

    /**
     *
     * Replicates a Fedora data object.
     *
     * @param doReader data object reader
     * @exception ReplicationException replication processing error
     * @exception SQLException JDBC, SQL error
     */
    public void replicate(DOReader doReader)
            throws ReplicationException, SQLException;

    public void delete(String pid)
            throws ReplicationException;
}

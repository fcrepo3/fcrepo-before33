package fedora.server.storage.replication;

import java.sql.SQLException;

import fedora.server.errors.ReplicationException;
import fedora.server.storage.BDefReader;
import fedora.server.storage.BMechReader;
import fedora.server.storage.DOReader;

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

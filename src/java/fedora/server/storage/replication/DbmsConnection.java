/* The contents of this file are subject to the license and copyright terms
 * detailed in the license directory at the root of the source tree (also 
 * available online at http://www.fedora.info/license/).
 */

package fedora.server.storage.replication;

import java.io.File;

import java.sql.Connection;
import java.sql.SQLException;

import org.apache.log4j.Logger;

import fedora.common.Constants;

import fedora.server.Server;
import fedora.server.errors.ConnectionPoolNotFoundException;
import fedora.server.errors.InitializationException;
import fedora.server.storage.ConnectionPool;
import fedora.server.storage.ConnectionPoolManager;

/**
 * @author Paul Charlton
 */
public class DbmsConnection {

    /** Logger for this class. */
    private static final Logger LOG =
            Logger.getLogger(DbmsConnection.class.getName());

    private static ConnectionPool connectionPool = null;

    public DbmsConnection()
            throws Exception {
        initDB();
        LOG.debug("DbmsConnection constructor: using connectionPool: "
                + connectionPool);
    }

    public Connection getConnection() throws Exception {
        Connection connection = null;
        connection = connectionPool.getConnection();
        return connection;
    }

    public void freeConnection(Connection connection) {
        if (connection != null) {
            connectionPool.free(connection);
        }
    }

    /**
     * Initializes the relational database connection.
     * 
     * @throws Exception
     *         if unable to establish database connection
     */
    public static void initDB() throws Exception {
        try {

            ConnectionPoolManager cpmgr =
                    (ConnectionPoolManager) s_server
                            .getModule("fedora.server.storage.ConnectionPoolManager");
            if (cpmgr == null) {
                throw new SQLException("Server module not loaded: "
                        + "fedora.server.storage.ConnectionPoolManager");
            } else {
                try {
                    connectionPool = cpmgr.getPool();
                } catch (ConnectionPoolNotFoundException cpnfe) {
                    throw new SQLException("Can't get default pool from cpmgr.");
                }
            }

        } catch (SQLException sqle) {
            // Problem with connection pool and/or database
            LOG.error("Unable to create connection pool", sqle);
            // FIXME!! - Decide on Exception handling
            Exception e =
                    new Exception("SQLException in DbmsConnection: "
                            + sqle.getMessage());
            e.initCause(sqle);
            throw e;
        }
    }

    private static Server s_server;

    static {
        try {
            s_server =
                    Server.getInstance(new File(Constants.FEDORA_HOME), false);
        } catch (InitializationException ie) {
            LOG.error("Error getting server instance", ie);
        }
    }

    /**
     * Used for unit testing and demonstration purposes.
     * 
     * @param args
     *        program arguments
     * @exception Exception
     *            exceptions that are thrown from called methods
     */
    public static void main(String[] args) throws Exception {
        Connection connection;

        DbmsConnection db = new DbmsConnection();
        connection = db.getConnection();
        db.freeConnection(connection);
    }
}

package fedora.server.storage.replication;

import java.sql.*;
import java.io.*;

import org.apache.log4j.Logger;

import fedora.server.storage.*;
import fedora.server.Server;
import fedora.server.errors.ConnectionPoolNotFoundException;
import fedora.server.errors.InitializationException;

/**
 * @author Paul Charlton
 * @version $Id$
 */
public class DbmsConnection {

    /** Logger for this class. */
    private static final Logger LOG = Logger.getLogger(
            DbmsConnection.class.getName());

    private static ConnectionPool connectionPool = null;
	private static final String dbPropsFile = "db.properties";
	private static boolean debug = true;

	public DbmsConnection() throws Exception {
		initDB();
		LOG.debug("DbmsConnection constructor: using connectionPool: " + connectionPool);
	}

	public Connection getConnection() throws Exception {
		Connection connection  = null;
		connection = connectionPool.getConnection();
		return connection;
	}

	public void freeConnection(Connection connection) {
		if (connection!=null) connectionPool.free(connection);
	}


        /**
         * Initializes the relational database connection.
         *
         * @throws Exception if unable to establish database connection
         */
        public static void initDB() throws Exception
        {
          try
          {

        ConnectionPoolManager cpmgr=(ConnectionPoolManager) s_server.getModule(
                "fedora.server.storage.ConnectionPoolManager");
        if (cpmgr==null) {
            throw new SQLException( "Server module not loaded: "
                    + "fedora.server.storage.ConnectionPoolManager");
        } else {
            try {
                connectionPool=cpmgr.getPool();
            } catch (ConnectionPoolNotFoundException cpnfe) {
                throw new SQLException("Can't get default pool from cpmgr.");
            }
        }




          } catch (SQLException sqle)
          {
            // Problem with connection pool and/or database
            LOG.error("Unable to create connection pool", sqle);
            ConnectionPool connectionPool = null;
            connectionPool = null;
            // FIXME!! - Decide on Exception handling
            Exception e = new Exception("SQLException in DbmsConnection: " + sqle.getMessage());
            e.initCause(sqle);
            throw e;
          }
        }

        private static Server s_server;

         static
         {
           try
           {
             s_server=Server.getInstance(new File(System.getProperty("fedora.home")));
           } catch (InitializationException ie)
           {
            LOG.error("Error getting server instance", ie);
           }
         }

        /**
        *
        * Used for unit testing and demonstration purposes.
        *
        * @param args program arguments
        *
        * @exception Exception exceptions that are thrown from called methods
        */
        public static void main(String[] args) throws Exception {
		Connection connection;

		DbmsConnection db = new DbmsConnection();
		connection = db.getConnection();
		db.freeConnection(connection);
	}
}

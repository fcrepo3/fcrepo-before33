package fedora.server.storage.replication;

/**
 * Title: DbmsConnection.java
 * Description: Manages databases connection for the replication code. 
 * Copyright: Copyright (c) 2002
 * Company: 
 * @author Paul Charlton
 * @version 1.0
 */

import java.util.*;
import java.sql.*;
import java.io.*;
import fedora.server.storage.*;


/**
* 
* Description: Manages database connections for the replication code. 
* Note: this will be revised to work with more general configuration parameter 
* setting code.
*
* @version 1.0
*
*/
public class DbmsConnection {   
        private static ConnectionPool connectionPool = null;
	private static final String dbPropsFile = "db.properties";


	public DbmsConnection() throws Exception {
		initDB();
System.out.println("connectionPool: " + connectionPool);
	}

	public Connection getConnection() throws Exception {
		Connection connection  = null;
		connection = connectionPool.getConnection();
		return connection;
	}

	public void freeConnection(Connection connection) {
		connectionPool.free(connection);
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
            // read database properties file and init connection pool
            FileInputStream fis = new FileInputStream(dbPropsFile);
            Properties dbProps = new Properties();
            dbProps.load(fis);
            String driver = dbProps.getProperty("drivers");
            String username = dbProps.getProperty("username");
            String password = dbProps.getProperty("password");
            String url = dbProps.getProperty("url");
            Integer i1 = new Integer(dbProps.getProperty("initConnections"));
            int initConnections = i1.intValue();
            Integer i2 = new Integer(dbProps.getProperty("maxConnections"));
            int maxConnections = i2.intValue();
            // FIXME!! above section of code to be replaced with the following
            // section when Server.java is functional
            /*
            Server serverInstance =
            Server.getInstance(System.getProperty(Server.HOME_PROPERTY));
            String driver = serverInstance.getDatastoreConfig("drivers");
            String username = serverInstance.getDatastoreConfig("username");
            String password = serverInstance.getDatastoreConfig("password");
            String url = serverInstance.getDatastoreConfig("url");
            Integer i1 = new Integer(serverInstance.getDatastoreConfig("minConnections"););
            int initConnections = i1.intValue();
            Integer i2 = new Integer(serverInstance.getDatastoreConfig("maxConnections"););
            int maxConnections = i2.intValue();
            */
            //if(debug) System.out.println("\nurl = "+url);
      
            // initialize connection pool
            connectionPool = new ConnectionPool(driver, url, username, password,
                initConnections, maxConnections, true);
          } catch (SQLException sqle)
          {
            // Problem with connection pool and/or database
            System.out.println("Unable to create connection pool: "+sqle);
            ConnectionPool connectionPool = null;
            connectionPool = null;
            // FIXME!! - Decide on Exception handling
            Exception e = new Exception("");
            e.initCause(sqle);
            throw e;
          } catch (FileNotFoundException fnfe)
          {
            System.out.println("Unable to read the properties file: " +
                dbPropsFile);
            Exception e = new Exception("");
            e.initCause(fnfe);
            throw e;
          } catch (IOException ioe)
          {
            System.out.println(ioe);
            Exception e = new Exception("");
            e.initCause(ioe);
            throw e;
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

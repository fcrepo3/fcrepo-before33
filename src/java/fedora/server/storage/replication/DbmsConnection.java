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
import fedora.server.Server;
import fedora.server.errors.ConnectionPoolNotFoundException;
import fedora.server.errors.InitializationException;


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
	private static boolean debug = true;

	public DbmsConnection() throws Exception {
		initDB();
		// Debug statement
		System.out.println("DbmsConnection constructor: using connectionPool: " + connectionPool);
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
/*
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
*/
            // FIXME!! above section of code to be replaced with the following
            // section when Server.java is functional


            //String id = s_server.getModule("fedora.server.storage.DOManager").
            //            getParameter("fast_db");
            //FIXME!! - temporary fix until problem with above line is resolved
            
/*            
            
            String id = "mysql1";
            System.out.println("id: "+id);
            System.out.flush();
            String driv = s_server.getDatastoreConfig("mysql1").
                          getParameter("jdbc_driver_class");
            System.out.println("driver: "+driv);
            String label = s_server.getParameter("label");
            System.out.println("label: "+label);
            System.out.flush();
            String driver = s_server.getDatastoreConfig(id).
                            getParameter("jdbc_driver_class");
            String username = s_server.getDatastoreConfig(id).
                              getParameter("dbuser");
            String password = s_server.getDatastoreConfig(id).
                              getParameter("dbpass");
            String url = s_server.getDatastoreConfig(id).
                         getParameter("connect_string");
            Integer i1 = new Integer(s_server.getDatastoreConfig(id).
                                     getParameter("pool_min"));
            int initConnections = i1.intValue();
            Integer i2 = new Integer(s_server.getDatastoreConfig(id).
                               getParameter("pool_max"));
            int maxConnections = i2.intValue();
            System.out.println("id: "+id+"\ndriver: "+driver+"\nuser"+username+
                               "\npass: "+password+"\nurl: "+url+"\nmin: "+
                               initConnections+"\nmax: "+maxConnections);
            System.out.flush();
            if(debug) System.out.println("\nurl = "+url);

            // initialize connection pool
            connectionPool = new ConnectionPool(driver, url, username, password,
                initConnections, maxConnections, true);
*/

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
            System.out.println("Unable to create connection pool: "+sqle);
            ConnectionPool connectionPool = null;
            connectionPool = null;
            // FIXME!! - Decide on Exception handling
            Exception e = new Exception("SQLException in DbmsConnection: " + sqle.getMessage());
            e.initCause(sqle);
            throw e;
          }
          //} catch (FileNotFoundException fnfe)
          //{
          //  System.out.println("Unable to read the properties file: " +
          //      dbPropsFile);
          //  Exception e = new Exception("");
          //  e.initCause(fnfe);
          //  throw e;
          //} catch (IOException ioe)
          //{
          //  System.out.println(ioe);
          //  Exception e = new Exception("");
          //  e.initCause(ioe);
          //  throw e;
          //}
        }

        private static Server s_server;

         static
         {
           try
           {
             s_server=Server.getInstance(new File(System.getProperty("fedora.home")));
           } catch (InitializationException ie)
           {
             System.err.println(ie.getMessage());
             System.err.flush();
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

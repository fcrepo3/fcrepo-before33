package fedora.server.storage.replication;

/**
 * <p>Title: DbmsConnection.java</p>
 * <p>Description: Manages databases connection for the replication code. 
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 * @author Paul Charlton
 * @version 1.0
 */

import java.util.*;
import java.sql.*;
import java.io.*;


/**
* <p>
* Description: Manages database connections for the replication code. 
* Note: this will be revised to work with more general configuration parameter 
* setting code.
*
* @version 1.0
*
*/
public class DbmsConnection {   
	public Connection connection  = null;

        final private static String hostname = "icarus.lib.virginia.edu";


        /** login username for MYSQL database containing repository */
        final private static String username = "mysql";
        /** password for above user login on MYSQL database server */
        final private static String password = "my@sql";
        /** Database name being used to store repository tables */
        final private static String databasename = "FedoraObjects";
        /** JDBC url required to access MYSQL database on host */
        final private static String url = 
                        "jdbc:mysql://"+hostname+":3306/"+databasename;
        /** JDBC driver classes */
        final private static String mysqlJDBCDrivers = "org.gjt.mm.mysql.Driver";



        /**
        * <p>
        * Creates a connection to the Fedora database.
        *
        * @exception ClassNotFoundException JDBC driver error
        * @exception SQLException JDBC, SQL error
        */
	public void connectDatabase() throws ClassNotFoundException, SQLException {
		Class.forName(mysqlJDBCDrivers);
		connection = DriverManager.getConnection(url, username, password);
	}

        /**
        * <p>
        * Used for unit testing and demonstration purposes.
        *
        * @param args program arguments
        *
        * @exception Exception exceptions that are thrown from called methods
        */
        public static void main(String[] args) throws Exception {
		int rc;
		String returnString;

		DbmsConnection db = new DbmsConnection();
		db.connectDatabase();
	}
}

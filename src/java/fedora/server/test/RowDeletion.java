package fedora.server.test;

import java.io.*;
import java.sql.*;
import java.util.*;
import fedora.server.storage.replication.*;

/**
 *
 * <p><b>Title:</b> RowDeletion.java</p>
 * <p><b>Description:</b> Program to delete Fedora test objects from the
 * database.</p>
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
 * <p>The entire file consists of original code.  Copyright &copy; 2002, 2003 by The
 * Rector and Visitors of the University of Virginia and Cornell University.
 * All rights reserved.</p>
 *
 * -----------------------------------------------------------------------------
 *
 * @author Paul Charlton
 * @version $Id$
 */
public class RowDeletion {

        /**
        *
        * Deletes BehaviorDefinition database rows.
        *
        * @param connection JDBC DBMS connection
        * @param bDefDBID BehaviorDefinition DBID
        *
        * @exception SQLException JDBC, SQL error
        */
	public void deleteBehaviorDefinitionRow(Connection connection, String bDefDBID) throws SQLException {
		String deletionStatement = "DELETE FROM bDef WHERE bDefDbID = " + bDefDBID + " LIMIT 1";

		deleteGen(connection, deletionStatement);
	}

        /**
        *
        * Deletes BehaviorMechanism database rows.
        *
        * @param connection JDBC DBMS connection
        * @param bMechDBID BehaviorMechanism DBID
        *
        * @exception SQLException JDBC, SQL error
        */
	public void deleteBehaviorMechanismRow(Connection connection, String bMechDBID) throws SQLException {
		String deletionStatement = "DELETE FROM bMech WHERE bMechDbID = " + bMechDBID + " LIMIT 1";

		deleteGen(connection, deletionStatement);
	}

        /**
        *
        * Deletes dsBind database rows.
        *
        * @param connection JDBC DBMS connection
        * @param doDBID DigitalObject DBID
        *
        * @exception SQLException JDBC, SQL error
        */
	public void deleteDataStreamBindingRow(Connection connection, String doDBID) throws SQLException {

		// Not limited to single row deletes.
		String deletionStatement = "DELETE FROM dsBind WHERE doDbID = " +  doDBID + "";

		deleteGen(connection, deletionStatement);
	}

        /**
        *
        * Deletes dsBindMap database rows.
        *
        * @param connection JDBC DBMS connection
        * @param bMechDBID BehaviorMechanism DBID
        *
        * @exception SQLException JDBC, SQL error
        */
	public void deleteDataStreamBindingMapRow(Connection connection, String bMechDBID) throws SQLException {

		String deletionStatement = "DELETE FROM dsBindMap WHERE bMechDbID = " + bMechDBID + "";

		deleteGen(connection, deletionStatement);
	}

        /**
        *
        * Deletes dsBindSpec database rows.
        *
        * @param connection JDBC DBMS connection
        * @param bMechDBID BehaviorMechanism DBID
        *
        * @exception SQLException JDBC, SQL error
        */
	public void deleteDataStreamBindingSpecRow(Connection connection, String bMechDBID) throws SQLException {

		// Not limited to single row deletes.
		String deletionStatement = "DELETE FROM dsBindSpec WHERE bMechDbID = " + bMechDBID + "";

		deleteGen(connection, deletionStatement);
	}

        /**
        *
        * Deletes dsMIME database rows.
	* Note that this needs to be called before deleteDataStreamBindingSpecRow
	* in order to get the set of dsBindKeyDbID values to delete with.
        *
        * @param connection JDBC DBMS connection
        * @param bMechDBID BehaviorMechanism DBID
        *
        * @exception SQLException JDBC, SQL error
        */
	public void deleteDataStreamMIMERow(Connection connection, String bMechDBID) throws SQLException {
                String query;
                String dsBindKeyDbID;
                Statement statement;
		String deletionStatement;
                ResultSet rs;

                query = "SELECT dsBindKeyDbID FROM dsBindSpec WHERE ";
                query += "bMechDbID = " + bMechDBID + "";
System.out.println("deleteDataStream, query = " + query);

                statement = connection.createStatement();
                rs = statement.executeQuery(query);

                while (rs.next()) {
                        dsBindKeyDbID = rs.getString(1);
			deletionStatement = "DELETE FROM dsMIME WHERE dsBindKeyDbID = " + dsBindKeyDbID + "";

			deleteGen(connection, deletionStatement);
		}

                statement.close();
                rs.close();
	}

        /**
        *
        * Deletes DigitalObject database rows.
        *
        * @param connection JDBC DBMS connection
        * @param doDBID DigitalObject DBID
        *
        * @exception SQLException JDBC, SQL error
        */
	public void deleteDigitalObjectRow(Connection connection, String doDBID) throws SQLException {

		String deletionStatement = "DELETE FROM do WHERE doDbID = " +  doDBID + " LIMIT 1";

		deleteGen(connection, deletionStatement);
	}

        /**
        *
        * Deletes doDissAssoc database rows.
        *
        * @param connection JDBC DBMS connection
        * @param doDBID DigitalObject DBID
        *
        * @exception SQLException JDBC, SQL error
        */
	public void deleteDigitalObjectDissAssocRow(Connection connection, String doDBID) throws SQLException {

		// Not limited to single row deletes.
		String deletionStatement = "DELETE FROM doDissAssoc WHERE doDbID = " +  doDBID + "";

		deleteGen(connection, deletionStatement);
	}

        /**
        *
        * Deletes Disseminator database rows.
        *
        *   The way Disseminator rows are being deleted for the test case is different
        * from how they were inserted.  They are inserted by processing the Data Object.
        * Here they are deleted by bMechDBID.  Another way to delete would be to
        * delete the Disseminator rows by gathering the Disseminator.dissDbID values
        * from the doDissAssoc table and using these values for the deletes.
        * Both ways work properly for the test case, but may cause problems with more
        * general use.
        *
        * The problems for unrestricted general use:
        *   1) delete by bMechDBID: Disseminator rows are deleted for all objects that
        * refer to bMechDBID, not just for the related Data Object.
        *   2) delete with dissDbIDs from doDissAssoc:  this would remove
        * Disseminator rows that are associated with a particular Data Object.
        * However, it would also have the side effect of breaking any 'links' that
        * other Data Objects would have that also used this dissDbID, bMechDbID
        * combination.
        *
        * @param connection JDBC DBMS connection
        * @param bMechDBID BehaviorMechanism DBID
        *
        * @exception SQLException JDBC, SQL error
        */
	public void deleteDisseminatorRow(Connection connection, String bMechDBID) throws SQLException {

		// Not limited to single row deletes.
		String deletionStatement = "DELETE FROM diss WHERE bMechDbID = " +  bMechDBID + "1";

		deleteGen(connection, deletionStatement);
	}

        /**
        *
        * Deletes mechImpl database rows.
        *
        * @param connection JDBC DBMS connection
        * @param bMechDBID BehaviorMechanism DBID
        *
        * @exception SQLException JDBC, SQL error
        */
	public void deleteMechanismImplRow(Connection connection, String bMechDBID) throws SQLException {
		// Not limited to single row deletes.
		String deletionStatement = "DELETE FROM mechImpl WHERE bMechDbID = " +  bMechDBID + "";

		deleteGen(connection, deletionStatement);
	}

        /**
        *
        * Deletes method database rows.
        *
        * @param connection JDBC DBMS connection
        * @param bDefDBID BehaviorDefinition DBID
        *
        * @exception SQLException JDBC, SQL error
        */
	public void deleteMethodRow(Connection connection, String bDefDBID) throws SQLException {

		// Not limited to single row deletes.
		String deletionStatement = "DELETE FROM method WHERE bDefDbID = " + bDefDBID + "";

		deleteGen(connection, deletionStatement);
	}


        /**
        *
        * General JDBC row deletion method.
        *
        * @param connection JDBC DBMS connection
        * @param deletionStatement SQL row deletion statement
        *
        * @exception SQLException JDBC, SQL error
        */
	public void deleteGen(Connection connection, String deletionStatement) throws SQLException {
		int rowCount = 0;
		Statement statement = null;

		statement = connection.createStatement();

System.out.println("deleteGen: deletionStatement = " + deletionStatement);
		rowCount = statement.executeUpdate(deletionStatement);
System.out.println("rowCount = " + rowCount);
		statement.close();
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
		Connection connection;
		DbmsConnection db;
		String returnString;

		String pid;
		String usage = "Usage (deletes a do row): java rowDeletion pid";

    		if (args.length != 1) {
			System.out.println(usage);
			System.exit(1);
		}

		pid = args[0];

		System.out.println("pid=" + pid);

		db = new DbmsConnection();
		connection = db.getConnection();

		RowDeletion rd = new RowDeletion();
		rd.deleteDigitalObjectRow(connection, pid);
		System.out.println("deleteDigitalObject returned");

		db.freeConnection(connection);
	}
}

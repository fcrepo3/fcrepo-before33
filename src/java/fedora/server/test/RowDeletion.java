package fedora.server.test;

/**
 * Title: RowDeletion.java
 * Description: Methods to delete Fedora object database rows. 
 * Copyright: Copyright (c) 2002
 * Company: 
 * @author Paul Charlton
 * @version 1.0
 */


import java.io.*;
import java.sql.*;
import java.util.*;
import fedora.server.storage.replication.*;


/**
*
* Description: Program to delete Fedora test objects from the database.
*
* @version 1.0
*
*/
public class RowDeletion {   

        /**
        *
        * Deletes BehaviorDefinition database rows.
        *
        * @param connection JDBC DBMS connection
        * @param bDefPID BehaviorDefinition DBID
        *
        * @exception SQLException JDBC, SQL error
        */
	public void deleteBehaviorDefinitionRow(Connection connection, String bDefDBID) throws SQLException {
		String deletionStatement = "DELETE FROM BehaviorDefinition WHERE BDEF_DBID = " + bDefDBID + " LIMIT 1;";

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
		String deletionStatement = "DELETE FROM BehaviorMechanism WHERE BMECH_DBID = " + bMechDBID + " LIMIT 1;";

		deleteGen(connection, deletionStatement);
	}

        /**
        *
        * Deletes DataStreamBinding database rows.
        *
        * @param connection JDBC DBMS connection
        * @param doDBID DigitalObject DBID
        *
        * @exception SQLException JDBC, SQL error
        */
	public void deleteDataStreamBindingRow(Connection connection, String doDBID) throws SQLException {

		// Not limited to single row deletes.
		String deletionStatement = "DELETE FROM DataStreamBinding WHERE DO_DBID = " +  doDBID + ";";

		deleteGen(connection, deletionStatement);
	}

        /**
        *
        * Deletes DataStreamBindingMap database rows.
        *
        * @param connection JDBC DBMS connection
        * @param bMechDBID BehaviorMechanism DBID
        *
        * @exception SQLException JDBC, SQL error
        */
	public void deleteDataStreamBindingMapRow(Connection connection, String bMechDBID) throws SQLException {

		String deletionStatement = "DELETE FROM DataStreamBindingMap WHERE BMECH_DBID = " + bMechDBID + ";";

		deleteGen(connection, deletionStatement);
	}

        /**
        *
        * Deletes DataStreamBindingSpec database rows.
        *
        * @param connection JDBC DBMS connection
        * @param bMechDBID BehaviorMechanism DBID
        *
        * @exception SQLException JDBC, SQL error
        */
	public void deleteDataStreamBindingSpecRow(Connection connection, String bMechDBID) throws SQLException {

		// Not limited to single row deletes.
		String deletionStatement = "DELETE FROM DataStreamBindingSpec WHERE BMECH_DBID = " + bMechDBID + ";";

		deleteGen(connection, deletionStatement);
	}

        /**
        *
        * Deletes DataStreamMIME database rows.
	* Note that this needs to be called before deleteDataStreamBindingSpecRow
	* in order to get the set of DSBindingKey_DBID values to delete with.
        *
        * @param connection JDBC DBMS connection
        * @param bMechDBID BehaviorMechanism DBID
        *
        * @exception SQLException JDBC, SQL error
        */
	public void deleteDataStreamMIMERow(Connection connection, String bMechDBID) throws SQLException {
                String query;
                String dsbindingkey_dbid;
                Statement statement;
		String deletionStatement;
                ResultSet rs;

                query = "SELECT DSBindingKey_DBID FROM DataStreamBindingSpec WHERE ";
                query += "BMECH_DBID = " + bMechDBID + ";";
System.out.println("deleteDataStream, query = " + query);

                statement = connection.createStatement();
                rs = statement.executeQuery(query);

                while (rs.next()) {
                        dsbindingkey_dbid = rs.getString(1);
			deletionStatement = "DELETE FROM DataStreamMIME WHERE DSBindingKey_DBID = " + dsbindingkey_dbid + ";";

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

		String deletionStatement = "DELETE FROM DigitalObject WHERE DO_DBID = " +  doDBID + " LIMIT 1;";

		deleteGen(connection, deletionStatement);
	}

        /**
        *
        * Deletes DigitalObjectDissAssoc database rows.
        *
        * @param connection JDBC DBMS connection
        * @param doDBID DigitalObject DBID
        *
        * @exception SQLException JDBC, SQL error
        */
	public void deleteDigitalObjectDissAssocRow(Connection connection, String doDBID) throws SQLException {

		// Not limited to single row deletes.
		String deletionStatement = "DELETE FROM DigitalObjectDissAssoc WHERE DO_DBID = " +  doDBID + ";";

		deleteGen(connection, deletionStatement);
	}

        /**
        *
        * Deletes Disseminator database rows.
        *
        *   The way Disseminator rows are being deleted for the test case is different
        * from how they were inserted.  They are inserted by processing the Data Object.
        * Here they are deleted by bMechDBID.  Another way to delete would be to
        * delete the Disseminator rows by gathering the Disseminator.DISS_DBID values 
        * from the DigitalObjectDissAssoc table and using these values for the deletes.
        * Both ways work properly for the test case, but may cause problems with more 
        * general use.
        * 
        * The problems for unrestricted general use:
        *   1) delete by bMechDBID: Disseminator rows are deleted for all objects that 
        * refer to bMechDBID, not just for the related Data Object.
        *   2) delete with DISS_DBIDs from DigitalObjectDissAssoc:  this would remove 
        * Disseminator rows that are associated with a particular Data Object.  
        * However, it would also have the side effect of breaking any 'links' that 
        * other Data Objects would have that also used this DISS_DBID, BMECH_DBID 
        * combination.
        *
        * @param connection JDBC DBMS connection
        * @param bMechDBID BehaviorMechanism DBID
        *
        * @exception SQLException JDBC, SQL error
        */
	public void deleteDisseminatorRow(Connection connection, String bMechDBID) throws SQLException {

		// Not limited to single row deletes.
		String deletionStatement = "DELETE FROM Disseminator WHERE BMECH_DBID = " +  bMechDBID + "1;";

		deleteGen(connection, deletionStatement);
	}

        /**
        *
        * Deletes MechanismImpl database rows.
        *
        * @param connection JDBC DBMS connection
        * @param bMechDBID BehaviorMechanism DBID
        *
        * @exception SQLException JDBC, SQL error
        */
	public void deleteMechanismImplRow(Connection connection, String bMechDBID) throws SQLException {
		// Not limited to single row deletes.
		String deletionStatement = "DELETE FROM MechanismImpl WHERE BMECH_DBID = " +  bMechDBID + ";";

		deleteGen(connection, deletionStatement);
	}

        /**
        *
        * Deletes Method database rows.
        *
        * @param connection JDBC DBMS connection
        * @param bDefDBID BehaviorDefinition DBID
        *
        * @exception SQLException JDBC, SQL error
        */
	public void deleteMethodRow(Connection connection, String bDefDBID) throws SQLException {

		// Not limited to single row deletes.
		String deletionStatement = "DELETE FROM Method WHERE BDEF_DBID = " + bDefDBID + ";";

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
		String usage = "Usage (deletes a DigitalObject row): java rowDeletion pid";

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

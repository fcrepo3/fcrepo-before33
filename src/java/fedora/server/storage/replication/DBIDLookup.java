package fedora.server.storage.replication;

/**
 * <p>Title: DBIDLookup.java</p>
 * <p>Description: Database DBID lookup code.
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
* Description: Looks up and returns the DBID for a row that matches the column
* values passed in for that particular row.
*
* @version 1.0
*
*/
public class DBIDLookup {   

        /**
        * <p>
        * Looks up a BehaviorDefinition DBID.
        *
        * @param db Database connection object
        * @param bDefPID Behavior definition PID
        *
        * @return The DBID of the specified Behavior Definition row.
        *
        * @exception SQLException JDBC, SQL error
        */
	public String lookupBehaviorDefinitionDBID(DbmsConnection db, String bDefPID) throws SQLException {
		return lookupDBID1(db, "BDEF_DBID", "BehaviorDefinition", "BDEF_PID", bDefPID);
	}

        /**
        * <p>
        * Looks up a BehaviorMechanism DBID.
        *
        * @param db Database connection object
        * @param bMechPID Behavior mechanism PID
        *
        * @return The DBID of the specified Behavior Mechanism row.
        *
        * @exception SQLException JDBC, SQL error
        */
	public String lookupBehaviorMechanismDBID(DbmsConnection db, String bMechPID) throws SQLException {
		return lookupDBID1(db, "BMECH_DBID", "BehaviorMechanism", "BMECH_PID", bMechPID);
	}

        /**
        * <p>
        * Looks up a DataStreamBindingMap DBID.
        *
        * @param db Database connection object
        * @param bMechDBID Behavior mechanism DBID
        * @param dsBindingMapID Data stream binding map ID
        *
        * @return The DBID of the specified DataStreamBindingMap row.
        *
        * @exception SQLException JDBC, SQL error
        */
	public String lookupDataStreamBindingMapDBID(DbmsConnection db, String bMechDBID, String dsBindingMapID) throws SQLException {
		return lookupDBID2(db, "BindingMap_DBID", "DataStreamBindingMap", "BMECH_DBID", bMechDBID, "DSBindingMap_ID", dsBindingMapID);
	}

        /**
        * <p>
        * Looks up a DataStreamBindingSpec DBID.
        *
        * @param db Database connection object
        * @param bMechDBID Behavior mechanism DBID
        * @param dsBindingSpecName Data stream binding spec name
        *
        * @return The DBID of the specified DataStreamBindingSpec row.
        *
        * @exception SQLException JDBC, SQL error
        */
	public String lookupDataStreamBindingSpecDBID(DbmsConnection db, String bMechDBID, String dsBindingSpecName) throws SQLException {
		return lookupDBID2(db, "DSBindingKey_DBID", "DataStreamBindingSpec", "BMECH_DBID", bMechDBID, "DSBindingSpec_Name", dsBindingSpecName);
	}

        /**
        * <p>
        * Looks up a DigitalObject DBID.
        *
        * @param db Database connection object
        * @param doPID Data object PID
        *
        * @return The DBID of the specified DigitalObject row.
        *
        * @exception SQLException JDBC, SQL error
        */
	public String lookupDigitalObjectDBID(DbmsConnection db, String doPID) throws SQLException {
		return lookupDBID1(db, "DO_DBID", "DigitalObject", "DO_PID", doPID);
	}

        /**
        * <p>
        * Looks up a Disseminator DBID.
        *
        * @param db database Connection object
        * @param bDefDBID Behavior definition DBID
        * @param bMechDBID Behavior mechanism DBID
        * @param dissID Disseminator ID
        *
        * @return The DBID of the specified Disseminator row.
        *
        * @exception SQLException JDBC, SQL error
        */
	public String lookupDisseminatorDBID(DbmsConnection db, String bDefDBID, String bMechDBID, String dissID) throws SQLException {
		String query;
		String ID = null;
		Statement statement;
		ResultSet rs;

		query = "SELECT DISS_DBID FROM Disseminator WHERE ";
		query += "BDEF_DBID = " + bDefDBID + " AND ";
		query += "BMECH_DBID = " + bMechDBID + " AND ";
		query += "DISS_ID = '" + dissID + "';";
System.out.println("lookupDisseminator, query = " + query);

		statement = db.connection.createStatement();
		rs = statement.executeQuery(query); 

		while (rs.next()) 
			ID = rs.getString(1);

		statement.close();
		rs.close();

		return ID;
	}

        /**
        * <p>
        * Looks up a Method DBID.
        *
        * @param db Database connection object
        * @param bDefDBID Behavior definition DBID
        * @param methName Method name
        *
        * @return The DBID of the specified Method row.
        *
        * @exception SQLException JDBC, SQL error
        */
	public String lookupMethodDBID(DbmsConnection db, String bDefDBID, String methName) throws SQLException {
		return lookupDBID2(db, "METH_DBID", "Method", "BDEF_DBID", bDefDBID, "METH_Name", methName);
	}

        /**
        * <p>
        * General JDBC lookup method with 1 lookup column value.
        *
        * @param db Database connection object
        * @param DBIDName DBID column name
        * @param tableName Table name
        * @param lookupColumnName Lookup column name
        * @param lookupColumnValue Lookup column value
        *
        * @return The DBID of the specified row.
        *
        * @exception SQLException JDBC, SQL error
        */
	public String lookupDBID1(DbmsConnection db, String DBIDName, String tableName, String lookupColumnName, String lookupColumnValue) throws SQLException {
		String query;
		String ID = null;
		Statement statement;
		ResultSet rs;

		query = "SELECT " + DBIDName + " FROM " + tableName + " WHERE ";
		query += lookupColumnName + " = '" + lookupColumnValue + "';";
System.out.println("lookupDBID1, query = " + query);

		statement = db.connection.createStatement();
		rs = statement.executeQuery(query); 

		while (rs.next()) 
			ID = rs.getString(1);

		statement.close();
		rs.close();

		return ID;
	}

        /**
        * <p>
        * General JDBC lookup method with 2 lookup column values.
        *
        * @param db Database connection object
        * @param DBIDName DBID Column name
        * @param tableName Table name
        * @param lookupColumnName1 First lookup column name
        * @param lookupColumnValue1 First lookup column value
        * @param lookupColumnName2 Second lookup column name
        * @param lookupColumnValue2 Second lookup column value
        *
        * @return The DBID of the specified row.
        *
        * @exception SQLException JDBC, SQL error
        */
	public String lookupDBID2(DbmsConnection db, String DBIDName, String tableName, String lookupColumnName1, String lookupColumnValue1, String lookupColumnName2, String lookupColumnValue2) throws SQLException {
		String query;
		String ID = null;
		Statement statement;
		ResultSet rs;

		query = "SELECT " + DBIDName + " FROM " + tableName + " WHERE ";
		query += lookupColumnName1 + " = '" + lookupColumnValue1 + "' AND ";
		query += lookupColumnName2 + " = '" + lookupColumnValue2 + "';";
System.out.println("lookupDBID2, query = " + query);

		statement = db.connection.createStatement();
		rs = statement.executeQuery(query); 

		while (rs.next()) 
			ID = rs.getString(1);

		statement.close();
		rs.close();

		return ID;
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

		DBIDLookup dl = new DBIDLookup();
		/*
		returnString = lookupDBID("DO_DBID", "DigitalObject", "DO_PID", "1007.lib.dl.test/image/iva/archerp01");

		returnString = dl.lookupDBID(db, args[0], args[1], args[2], args[3]);
		System.out.println("lookupDBID returns: " + returnString);
		*/

		returnString = dl.lookupDigitalObjectDBID(db, args[0]);
		System.out.println("lookupDigitalObjectDBID returns: " + returnString);
	}
}

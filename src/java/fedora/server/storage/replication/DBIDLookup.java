package fedora.server.storage.replication;

/**
 * Title: DBIDLookup.java
 * Description: Database DBID lookup code.
 * Copyright: Copyright (c) 2002
 * Company:
 * @author Paul Charlton
 * @version 1.0
 */

import java.util.*;
import java.sql.*;
import java.io.*;

import fedora.server.errors.StorageDeviceException;

/**
*
* Description: Looks up and returns the DBID for a row that matches the column
* values passed in for that particular row.
*
* @version 1.0
*
*/
public class DBIDLookup {

        /**
        *
        * Looks up a BehaviorDefinition DBID.
        *
        * @param connection JDBC DBMS connection
        * @param bDefPID Behavior definition PID
        *
        * @return The DBID of the specified Behavior Definition row.
        *
        * @exception SQLException JDBC, SQL error
        */
	public String lookupBehaviorDefinitionDBID(Connection connection, String bDefPID) throws StorageDeviceException {
		return lookupDBID1(connection, "bDefDbID", "bDef", "bDefPID", bDefPID);
	}

        /**
        *
        * Looks up a BehaviorMechanism DBID.
        *
        * @param connection JDBC DBMS connection
        * @param bMechPID Behavior mechanism PID
        *
        * @return The DBID of the specified Behavior Mechanism row.
        *
        * @exception SQLException JDBC, SQL error
        */
	public String lookupBehaviorMechanismDBID(Connection connection, String bMechPID) throws StorageDeviceException {
		return lookupDBID1(connection, "bMechDbID", "bMech", "bMechPID", bMechPID);
	}

        /**
        *
        * Looks up a dsBindMap DBID.
        *
        * @param connection JDBC DBMS connection
        * @param bMechDBID Behavior mechanism DBID
        * @param dsBindingMapID Data stream binding map ID
        *
        * @return The DBID of the specified dsBindMap row.
        *
        * @exception SQLException JDBC, SQL error
        */
	public String lookupDataStreamBindingMapDBID(Connection connection, String bMechDBID, String dsBindingMapID) throws StorageDeviceException {
		return lookupDBID2FirstNum(connection, "dsBindMapDbID", "dsBindMap", "bMechDbID", bMechDBID, "dsBindMapID", dsBindingMapID);
	}

        /**
        *
        * Looks up a dsBindSpec DBID.
        *
        * @param connection JDBC DBMS connection
        * @param bMechDBID Behavior mechanism DBID
        * @param dsBindingSpecName Data stream binding spec name
        *
        * @return The DBID of the specified dsBindSpec row.
        *
        * @exception SQLException JDBC, SQL error
        */
	public String lookupDataStreamBindingSpecDBID(Connection connection, String bMechDBID, String dsBindingSpecName) throws StorageDeviceException {
		return lookupDBID2FirstNum(connection, "dsBindKeyDbID", "dsBindSpec", "bMechDbID", bMechDBID, "dsBindSpecName", dsBindingSpecName);
	}

        /**
        *
        * Looks up a do DBID.
        *
        * @param connection JDBC DBMS connection
        * @param doPID Data object PID
        *
        * @return The DBID of the specified DigitalObject row.
        *
        * @exception SQLException JDBC, SQL error
        */
	public String lookupDigitalObjectDBID(Connection connection, String doPID) throws StorageDeviceException {
		return lookupDBID1(connection, "doDbID", "do", "doPID", doPID);
	}

        /**
        *
        * Looks up a Disseminator DBID.
        *
        * @param connection JDBC DBMS connection
        * @param bDefDBID Behavior definition DBID
        * @param bMechDBID Behavior mechanism DBID
        * @param dissID Disseminator ID
        *
        * @return The DBID of the specified Disseminator row.
        *
        * @exception SQLException JDBC, SQL error
        */
	public String lookupDisseminatorDBID(Connection connection, String bDefDBID, String bMechDBID, String dissID) throws StorageDeviceException {
            Statement statement = null;
            ResultSet rs = null;
            String query = null;
            String ID = null;
            try
            {
		query = "SELECT dissDbID FROM diss WHERE ";
		query += "bDefDbID = " + bDefDBID + " AND ";
		query += "bMechDbID = " + bMechDBID + " AND ";
		query += "dissID = '" + dissID + "';";

		// Debug statement
		// System.out.println("lookupDisseminator, query = " + query);

		statement = connection.createStatement();
		rs = statement.executeQuery(query);

		while (rs.next())
			ID = rs.getString(1);

            } catch (Throwable th)
            {
              throw new StorageDeviceException("[DBIDLookup] An error has "
                  + "occurred. The error was \" " + th.getClass().getName()
                  + " \". The cause was \" " + th.getMessage() + " \"");
            } finally
            {
                try
                {
                    if (rs != null) rs.close();
                    if (statement != null) statement.close();

                } catch (SQLException sqle)
                {
                    throw new StorageDeviceException("[DBIDLookup] An error has "
                        + "occurred. The error was \" " + sqle.getClass().getName()
                        + " \". The cause was \" " + sqle.getMessage() + " \"");
                }
            }
            return ID;
	}

        /**
        *
        * Looks up a method DBID.
        *
        * @param connection JDBC DBMS connection
        * @param bDefDBID Behavior definition DBID
        * @param methName Method name
        *
        * @return The DBID of the specified method row.
        *
        * @exception SQLException JDBC, SQL error
        */
	public String lookupMethodDBID(Connection connection, String bDefDBID, String methName) throws StorageDeviceException {
		return lookupDBID2FirstNum(connection, "methodDbID", "method", "bDefDbID", bDefDBID, "methodName", methName);
	}

        /**
        *
        * General JDBC lookup method with 1 lookup column value.
        *
        * @param connection JDBC DBMS connection
        * @param DBIDName DBID column name
        * @param tableName Table name
        * @param lookupColumnName Lookup column name
        * @param lookupColumnValue Lookup column value
        *
        * @return The DBID of the specified row.
        *
        * @exception SQLException JDBC, SQL error
        */
	public String lookupDBID1(Connection connection, String DBIDName, String tableName, String lookupColumnName, String lookupColumnValue) throws StorageDeviceException {
		String query = null;
		String ID = null;
		Statement statement = null;
		ResultSet rs = null;

                try
                {

    		    query = "SELECT " + DBIDName + " FROM " + tableName + " WHERE ";
		    query += lookupColumnName + " = '" + lookupColumnValue + "';";

                    // Debug statement
                    // System.out.println("lookupDBID1, query = " + query);

                    statement = connection.createStatement();
                    rs = statement.executeQuery(query);

                    while (rs.next())
			ID = rs.getString(1);

                } catch (Throwable th)
                {
                    throw new StorageDeviceException("[DBIDLookup] An error has "
                        + "occurred. The error was \" " + th.getClass().getName()
                        + " \". The cause was \" " + th.getMessage() + " \"");
                } finally
                {
                    try
                    {
                        if (rs != null) rs.close();
                        if (statement != null) statement.close();

                    } catch (SQLException sqle)
                    {
                        throw new StorageDeviceException("[DBIDLookup] An error has "
                            + "occurred. The error was \" " + sqle.getClass().getName()
                            + " \". The cause was \" " + sqle.getMessage() + " \"");
                    }
                }
                return ID;
	}

        /**
        *
        * General JDBC lookup method with 2 lookup column values.
        *
        * @param connection JDBC DBMS connection
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
	public String lookupDBID2(Connection connection, String DBIDName, String tableName, String lookupColumnName1, String lookupColumnValue1, String lookupColumnName2, String lookupColumnValue2) throws StorageDeviceException {
		String query = null;
		String ID = null;
		Statement statement = null;
		ResultSet rs = null;

                try
                {

		    query = "SELECT " + DBIDName + " FROM " + tableName + " WHERE ";
                    query += lookupColumnName1 + " = '" + lookupColumnValue1 + "' AND ";
                    query += lookupColumnName2 + " = '" + lookupColumnValue2 + "';";

                    // Debug statement
                    // System.out.println("lookupDBID2, query = " + query);

                    statement = connection.createStatement();
                    rs = statement.executeQuery(query);

                    while (rs.next())
			ID = rs.getString(1);

                } catch (Throwable th)
                {
                    throw new StorageDeviceException("[DBIDLookup] An error has "
                        + "occurred. The error was \" " + th.getClass().getName()
                        + " \". The cause was \" " + th.getMessage() + " \"");
                } finally
                {
                    try
                    {
                        if (rs != null) rs.close();
                        if (statement != null) statement.close();

                    } catch (SQLException sqle)
                    {
                        throw new StorageDeviceException("[DBIDLookup] An error has "
                            + "occurred. The error was \" " + sqle.getClass().getName()
                            + " \". The cause was \" " + sqle.getMessage() + " \"");
                    }
                }
                return ID;
	}

	public String lookupDBID2FirstNum(Connection connection, String DBIDName, String tableName, String lookupColumnName1, String lookupColumnValue1, String lookupColumnName2, String lookupColumnValue2) throws StorageDeviceException {
		String query = null;
		String ID = null;
		Statement statement = null;
		ResultSet rs = null;

                try
                {
		    query = "SELECT " + DBIDName + " FROM " + tableName + " WHERE ";
                    query += lookupColumnName1 + " =" + lookupColumnValue1 + " AND ";
                    query += lookupColumnName2 + " = '" + lookupColumnValue2 + "';";

                    // Debug statement
                    //System.out.println("lookupDBID2FirstNum, query = " + query);

                    statement = connection.createStatement();
                    rs = statement.executeQuery(query);

                    while (rs.next())
			ID = rs.getString(1);

                } catch (Throwable th)
                {
                    throw new StorageDeviceException("[DBIDLookup] An error has "
                        + "occurred. The error was \" " + th.getClass().getName()
                        + " \". The cause was \" " + th.getMessage() + " \"");
                } finally
                {
                    try
                    {
                        if (rs != null) rs.close();
                        if (statement != null) statement.close();

                    } catch (SQLException sqle)
                    {
                        throw new StorageDeviceException("[DBIDLookup] An error has "
                            + "occurred. The error was \" " + sqle.getClass().getName()
                            + " \". The cause was \" " + sqle.getMessage() + " \"");
                    }
                }
                return ID;
	}

}

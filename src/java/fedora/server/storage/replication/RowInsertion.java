package fedora.server.storage.replication;

import java.util.*;
import java.sql.*;
import java.io.*;

import fedora.server.utilities.SQLUtility;

/**
 *
 * <p><b>Title:</b> RowInsertion.java</p>
 * <p><b>Description:</b> Provides methods to insert Fedora database rows.</p>
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
 * @author wdn5e@virginia.edu
 * @version $Id$
 */
public class RowInsertion {

        /**
        *
        * Inserts a Behavior Definition row.
        *
        * @param connection JDBC DBMS connection
        * @param bDefPID Behavior definition PID
        * @param bDefLabel Behavior definition label
        *
        * @exception SQLException JDBC, SQL error
        */
	public void insertBehaviorDefinitionRow(Connection connection, String bDefPID, String bDefLabel) throws SQLException {

		String insertionStatement = "INSERT INTO bDef (bDefPID, bDefLabel) VALUES ('" + bDefPID + "', '" + SQLUtility.aposEscape(bDefLabel) + "')";

		insertGen(connection, insertionStatement);
	}

        /**
        *
        * Inserts a Behavior Mechanism row.
        *
        * @param connection JDBC DBMS connection
        * @param bDefDbID Behavior definition DBID
        * @param bMechPID Behavior mechanism PID
        * @param bMechLabel Behavior mechanism label
        *
        * @exception SQLException JDBC, SQL error
        */
	public void insertBehaviorMechanismRow(Connection connection, String bDefDbID, String bMechPID, String bMechLabel) throws SQLException {

		String insertionStatement = "INSERT INTO bMech (bDefDbID, bMechPID, bMechLabel) VALUES ('" + bDefDbID + "', '" + bMechPID + "', '" + SQLUtility.aposEscape(bMechLabel) + "')";

		insertGen(connection, insertionStatement);
	}

        /**
        *
        * Inserts a DataStreamBindingRow row.
        *
        * @param connection JDBC DBMS connection
        * @param doDbID Digital object DBID
        * @param dsBindKeyDbID Datastream binding key DBID
        * @param dsBindMapDbID Binding map DBID
        * @param dsBindKeySeq Datastream binding key sequence number
        * @param dsID Datastream ID
        * @param dsLabel Datastream label
        * @param dsMIME Datastream mime type
        * @param dsLocation Datastream location
        * @param dsControlGroupType Datastream type.
        * @param dsCurrentVersionID Datastream current version ID.
        * @param policyDbID Policy DBID
        *
        * @exception SQLException JDBC, SQL error
        */
	public void insertDataStreamBindingRow(Connection connection, String doDbID, String dsBindKeyDbID, String dsBindMapDbID, String dsBindKeySeq, String dsID, String dsLabel, String dsMIME, String dsLocation, String dsControlGroupType, String dsCurrentVersionID, String policyDbID) throws SQLException {

		String insertionStatement = "INSERT INTO dsBind (doDbID, dsBindKeyDbID, dsBindMapDbID, dsBindKeySeq, dsID, dsLabel, dsMIME, dsLocation, dsControlGroupType, dsCurrentVersionID, policyDbID) VALUES ('" + doDbID + "', '" + dsBindKeyDbID + "', '" + dsBindMapDbID + "', '" + dsBindKeySeq + "', '" + dsID + "', '" + SQLUtility.aposEscape(dsLabel) + "', '" + dsMIME + "', '" + dsLocation + "', '" + dsControlGroupType + "', '" + dsCurrentVersionID + "', '" + policyDbID + "')";

		insertGen(connection, insertionStatement);
	}

        /**
        *
        * Inserts a dsBindMap row.
        *
        * @param connection JDBC DBMS connection
        * @param bMechDbID Behavior mechanism DBID
        * @param dsBindMapID Datastream binding map ID
        * @param dsBindMapLabel Datastream binding map label
        *
        * @exception SQLException JDBC, SQL error
        */
	public void insertDataStreamBindingMapRow(Connection connection, String bMechDbID, String dsBindMapID, String dsBindMapLabel) throws SQLException {

		String insertionStatement = "INSERT INTO dsBindMap (bMechDbID, dsBindMapID, dsBindMapLabel) VALUES ('" + bMechDbID + "', '" + dsBindMapID + "', '" + SQLUtility.aposEscape(dsBindMapLabel) + "')";
		insertGen(connection, insertionStatement);
	}

        /**
        *
        * Inserts a dsBindSpec row.
        *
        * @param connection JDBC DBMS connection
        * @param bMechDbID Behavior mechanism DBID
        * @param dsBindSpecName Datastream binding spec name
        * @param dsBindSpecOrdinality Datastream binding spec ordinality flag
        * @param dsBindSpecCardinality Datastream binding cardinality
        * @param dsBindSpecLabel Datastream binding spec lable
        *
        * @exception SQLException JDBC, SQL error
        */
	public void insertDataStreamBindingSpecRow(Connection connection, String bMechDbID, String dsBindSpecName, String dsBindSpecOrdinality, String dsBindSpecCardinality, String dsBindSpecLabel) throws SQLException {

		String insertionStatement = "INSERT INTO dsBindSpec (bMechDbID, dsBindSpecName, dsBindSpecOrdinality, dsBindSpecCardinality, dsBindSpecLabel) VALUES ('" + bMechDbID + "', '" + SQLUtility.aposEscape(dsBindSpecName) + "', '" + dsBindSpecOrdinality + "', '" + dsBindSpecCardinality + "', '" + SQLUtility.aposEscape(dsBindSpecLabel) + "')";

		insertGen(connection, insertionStatement);
	}

        /**
        *
        * Inserts a dsMIME row.
        *
        * @param connection JDBC DBMS connection
        * @param dsBindKeyDbID Datastream binding key DBID
        * @param dsMIMEName Datastream MIME type name
        *
        * @exception SQLException JDBC, SQL error
        */
	public void insertDataStreamMIMERow(Connection connection, String dsBindKeyDbID, String dsMIMEName) throws SQLException {

		String insertionStatement = "INSERT INTO dsMIME (dsBindKeyDbID, dsMIMEName) VALUES ('" + dsBindKeyDbID + "', '" + dsMIMEName + "')";

		insertGen(connection, insertionStatement);
	}

        /**
        *
        * Inserts a do row.
        *
        * @param connection JDBC DBMS connection
        * @param doPID DigitalObject PID
        * @param doLabel DigitalObject label
        *
        * @exception SQLException JDBC, SQL error
        */
	public void insertDigitalObjectRow(Connection connection, String doPID, String doLabel) throws SQLException {

		String insertionStatement = "INSERT INTO do (doPID, doLabel) VALUES ('" + doPID + "', '" +  SQLUtility.aposEscape(doLabel) + "')";

		insertGen(connection, insertionStatement);
	}

        /**
        *
        * Inserts a doDissAssoc row.
        *
        * @param connection JDBC DBMS connection
        * @param doDbID DigitalObject DBID
        * @param dissDbID Disseminator DBID
        *
        * @exception SQLException JDBC, SQL error
        */
	public void insertDigitalObjectDissAssocRow(Connection connection, String doDbID, String dissDbID) throws SQLException {

		String insertionStatement = "INSERT INTO doDissAssoc (doDbID, dissDbID) VALUES ('" + doDbID + "', '" + dissDbID + "')";
		insertGen(connection, insertionStatement);
	}

        /**
        *
        * Inserts a Disseminator row.
        *
        * @param connection JDBC DBMS connection
        * @param bDefDbID Behavior definition DBID
        * @param bMechDbID Behavior mechanism DBID
        * @param dissID Disseminator ID
        * @param dissLabel Disseminator label
        *
        * @exception SQLException JDBC, SQL error
        */
	public void insertDisseminatorRow(Connection connection, String bDefDbID, String bMechDbID, String dissID, String dissLabel) throws SQLException {

		String insertionStatement = "INSERT INTO diss (bDefDbID, bMechDbID, dissID, dissLabel) VALUES ('" + bDefDbID + "', '" + bMechDbID + "', '" + dissID + "', '" + SQLUtility.aposEscape(dissLabel) + "')";
		insertGen(connection, insertionStatement);
	}

        /**
        *
        * Inserts a mechImpl row.
        *
        * @param connection JDBC DBMS connection
        * @param bMechDbID Behavior mechanism DBID
        * @param bDefDbID Behavior definition DBID
        * @param methodDbID Method DBID
        * @param dsBindKeyDbID Datastream binding key DBID
        * @param protocolType Mechanism implementation protocol type
        * @param returnType Mechanism implementation return type
        * @param addressLocation Mechanism implementation address location
        * @param operationLocation Mechanism implementation operation location
        * @param policyDbID Policy DBID
        *
        * @exception SQLException JDBC, SQL error
        */
	public void insertMechanismImplRow(Connection connection, String bMechDbID, String bDefDbID, String methodDbID, String dsBindKeyDbID, String protocolType, String returnType, String addressLocation, String operationLocation, String policyDbID) throws SQLException {

		String insertionStatement = "INSERT INTO mechImpl (bMechDbID, bDefDbID, methodDbID, dsBindKeyDbID, protocolType, returnType, addressLocation, operationLocation, policyDbID) VALUES ('" + bMechDbID + "', '" + bDefDbID + "', '" + methodDbID + "', '" + dsBindKeyDbID + "', '" + protocolType + "', '" + returnType + "', '" + addressLocation + "', '" + operationLocation + "', '" + policyDbID + "')";

		insertGen(connection, insertionStatement);
	}

        /**
        *
        * Inserts a method row.
        *
        * @param connection JDBC DBMS connection
        * @param bDefDbID Behavior definition DBID
        * @param methodName Behavior definition label
        * @param methodLabel Behavior definition label
        *
        * @exception SQLException JDBC, SQL error
        */
	public void insertMethodRow(Connection connection, String bDefDbID, String methodName, String methodLabel) throws SQLException {

		String insertionStatement = "INSERT INTO method (bDefDbID, methodName, methodLabel) VALUES ('" + bDefDbID + "', '" + SQLUtility.aposEscape(methodName) + "', '" + SQLUtility.aposEscape(methodLabel) + "')";

		insertGen(connection, insertionStatement);
	}

       /**
        *
        * @param connection An SQL Connection.
        * @param methDBID The method database ID.
        * @param bdefDBID The behavior Definition object database ID.
        * @param parmName the parameter name.
        * @param parmDefaultValue A default value for the parameter.
        * @param parmDomainValues A list of possible values for the parameter.
        * @param parmRequiredFlag A boolean flag indicating whether the
        *        parameter is required or not.
        * @param parmLabel The parameter label.
        * @param parmType The parameter type.
        * @throws SQLException JDBC, SQL error
        */
        public void insertMethodParmRow(Connection connection, String methDBID,
            String bdefDBID, String parmName, String parmDefaultValue,
            String parmDomainValues, String parmRequiredFlag,
            String parmLabel, String parmType)
            throws SQLException {
                String insertionStatement = "INSERT INTO parm "
                + "(methodDbID, bDefDbID, parmName, parmDefaultValue, "
                + "parmDomainValues, parmRequiredFlag, parmLabel, "
                + "parmType) VALUES ('"
                + methDBID + "', '" + bdefDBID + "', '"
                + SQLUtility.aposEscape(parmName) + "', '" + SQLUtility.aposEscape(parmDefaultValue) + "', '"
                + SQLUtility.aposEscape(parmDomainValues) + "', '"
                + parmRequiredFlag + "', '" + SQLUtility.aposEscape(parmLabel) + "', '"
                + parmType + "')";
                insertGen(connection, insertionStatement);
	}

        /**
         *
         * @param connection An SQL Connection.
         * @param methDBID The method database ID.
         * @param bmechDBID The behavior Mechanism object database ID.
         * @param parmName the parameter name.
         * @param parmDefaultValue A default value for the parameter.
         * @param parmDomainValues A list of possible values for the parameter.
         * @param parmRequiredFlag A boolean flag indicating whether the
         *        parameter is required or not.
         * @param parmLabel The parameter label.
         * @param parmType The parameter type.
         * @throws SQLException JDBC, SQL error
         */
         public void insertMechDefaultMethodParmRow(Connection connection, String methDBID,
             String bmechDBID, String parmName, String parmDefaultValue,
             String parmDomainValues, String parmRequiredFlag,
             String parmLabel, String parmType)
             throws SQLException {
                 String insertionStatement = "INSERT INTO mechDefParm "
                 + "(methodDbID, bMechDbID, defParmName, defParmDefaultValue, "
                 + "defParmDomainValues, defParmRequiredFlag, defParmLabel, "
                 + "defParmType) VALUES ('"
                 + methDBID + "', '" + bmechDBID + "', '"
                 + SQLUtility.aposEscape(parmName) + "', '" + SQLUtility.aposEscape(parmDefaultValue) + "', '"
                 + SQLUtility.aposEscape(parmDomainValues) + "', '"
                 + parmRequiredFlag + "', '" + SQLUtility.aposEscape(parmLabel) + "', '"
                 + parmType + "')";
                 insertGen(connection, insertionStatement);
	}

        /**
        *
        * General JDBC row insertion method.
        *
        * @param connection JDBC DBMS connection
        * @param insertionStatement SQL row insertion statement
        *
        * @exception SQLException JDBC, SQL error
        */
	public void insertGen(Connection connection, String insertionStatement) throws SQLException {
		int rowCount = 0;
		Statement statement = null;

		statement = connection.createStatement();

		// Debug statement
		//System.out.println("insertGen: insertionStatement = " + insertionStatement);
		rowCount = statement.executeUpdate(insertionStatement);
		statement.close();
	}
}

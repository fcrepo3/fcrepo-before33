package fedora.server.storage.replication;

/**
 * Title: RowInsertion.java
 * Description: Database row insertion code.
 * Copyright: Copyright (c) 2002
 * Company:
 * @author Paul Charlton
 * @version 1.0
 */

import java.util.*;
import java.sql.*;
import java.io.*;

/**
*
* Description: Provides methods to insert Fedora database rows.
*
* @version 1.0
*
*/
public class RowInsertion {

        /**
        *
        * Inserts a Behavior Definition row.
        *
        * @param connection JDBC DBMS connection
        * @param bdef_pid Behavior definition PID
        * @param bdef_label Behavior definition label
        *
        * @exception SQLException JDBC, SQL error
        */
	public void insertBehaviorDefinitionRow(Connection connection, String bdef_pid, String bdef_label) throws SQLException {

		String insertionStatement = "INSERT INTO BehaviorDefinition (BDEF_PID, BDEF_Label) VALUES ('" + bdef_pid + "', '" + bdef_label + "');";

		insertGen(connection, insertionStatement);
	}

        /**
        *
        * Inserts a Behavior Mechanism row.
        *
        * @param connection JDBC DBMS connection
        * @param bdef_dbid Behavior definition DBID
        * @param bmech_dbid Behavior mechanism DBID
        * @param bmech_label Behavior mechanism label
        *
        * @exception SQLException JDBC, SQL error
        */
	public void insertBehaviorMechanismRow(Connection connection, String bdef_dbid, String bmech_pid, String bmech_label) throws SQLException {

		String insertionStatement = "INSERT INTO BehaviorMechanism (BDEF_DBID, BMECH_PID, BMECH_Label) VALUES ('" + bdef_dbid + "', '" + bmech_pid + "', '" + bmech_label + "');";

		insertGen(connection, insertionStatement);
	}

        /**
        *
        * Inserts a DataStreamBindingRow row.
        *
        * @param connection JDBC DBMS connection
        * @param do_pid Digital object PID
        * @param dsbindingkey_dbid Datastream binding key DBID
        * @param bindingmap_dbid Binding map DBID
        * @param dsbinding_ds_bindingkey_seq Datastream binding key sequence number
        * @param dsbinding_ds_id Datastream ID
        * @param dsbinding_ds_label Datastream label
        * @param dsbinding_ds_mime Datastream mime type
        * @param dsbinding_ds_location Datastream location
        * @param policy_dbid Policy DBID
        *
        * @exception SQLException JDBC, SQL error
        */
	public void insertDataStreamBindingRow(Connection connection, String do_dbid, String dsbindingkey_dbid, String bindingmap_dbid, String dsbinding_ds_bindingkey_seq, String dsbinding_ds_id, String dsbinding_ds_label, String dsbinding_ds_mime, String dsbinding_ds_location, String dsbinding_ds_control_group_type, String dsbinding_ds_current_version_id, String policy_dbid) throws SQLException {

		String insertionStatement = "INSERT INTO DataStreamBinding (DO_DBID, DSBindingKey_DBID, BindingMap_DBID, DSBinding_DS_BindingKey_Seq, DSBinding_DS_ID, DSBinding_DS_Label, DSBinding_DS_MIME, DSBinding_DS_Location, DSBINDING_DS_Control_Group_Type, DSBINDING_DS_Current_Version_ID, POLICY_DBID) VALUES ('" + do_dbid + "', '" + dsbindingkey_dbid + "', '" + bindingmap_dbid + "', '" + dsbinding_ds_bindingkey_seq + "', '" + dsbinding_ds_id + "', '" + dsbinding_ds_label + "', '" + dsbinding_ds_mime + "', '" + dsbinding_ds_location + "', '" + dsbinding_ds_control_group_type + "', '" + dsbinding_ds_current_version_id + "', '" + policy_dbid + "');";

		insertGen(connection, insertionStatement);
	}

        /**
        *
        * Inserts a DataStreamBindingMap row.
        *
        * @param connection JDBC DBMS connection
        * @param bmech_dbid Behavior mechanism DBID
        * @param dsbindingmap_id Datastream binding map ID
        * @param dsbindingmap_label Datastream binding map label
        *
        * @exception SQLException JDBC, SQL error
        */
	public void insertDataStreamBindingMapRow(Connection connection, String bmech_dbid, String dsbindingmap_id, String dsbindingmap_label) throws SQLException {

		String insertionStatement = "INSERT INTO DataStreamBindingMap (BMECH_DBID, DSBindingMap_ID, DSBindingMap_Label) VALUES ('" + bmech_dbid + "', '" + dsbindingmap_id + "', '" + dsbindingmap_label + "');";
		insertGen(connection, insertionStatement);
	}

        /**
        *
        * Inserts a DataStreamBindingSpec row.
        *
        * @param connection JDBC DBMS connection
        * @param bmech_dbid Behavior mechanism DBID
        * @param dsbindingspec_name Datastream binding spec name
        * @param dsbindingspec_ordinality_flag Datastream binding spec ordinality flag
        * @param dsbindingspec_cardinality Datastream binding cardinality
        * @param dsbindingspec_label Datastream binding spec lable
        *
        * @exception SQLException JDBC, SQL error
        */
	public void insertDataStreamBindingSpecRow(Connection connection, String bmech_dbid, String dsbindingspec_name, String dsbindingspec_ordinality_flag, String dsbindingspec_cardinality, String dsbindingspec_label) throws SQLException {

		String insertionStatement = "INSERT INTO DataStreamBindingSpec (BMECH_DBID, DSBindingSpec_Name, DSBindingSpec_Ordinality_Flag, DSBindingSpec_Cardinality, DSBindingSpec_Label) VALUES ('" + bmech_dbid + "', '" + dsbindingspec_name + "', '" + dsbindingspec_ordinality_flag + "', '" + dsbindingspec_cardinality + "', '" + dsbindingspec_label + "');";

		insertGen(connection, insertionStatement);
	}

        /**
        *
        * Inserts a DataStreamMIME row.
        *
        * @param connection JDBC DBMS connection
        * @param dsbindingkey_dbid Datastream binding key DBID
        * @param dsmime_name Datastream MIME type name
        *
        * @exception SQLException JDBC, SQL error
        */
	public void insertDataStreamMIMERow(Connection connection, String dsbindingkey_dbid, String dsmime_name) throws SQLException {

		String insertionStatement = "INSERT INTO DataStreamMIME (DSBindingKey_DBID, DSMIME_Name) VALUES ('" + dsbindingkey_dbid + "', '" + dsmime_name + "');";

		insertGen(connection, insertionStatement);
	}

        /**
        *
        * Inserts a DigitalObject row.
        *
        * @param connection JDBC DBMS connection
        * @param do_pid DigitalObject PID
        * @param do_label DigitalObject label
        *
        * @exception SQLException JDBC, SQL error
        */
	public void insertDigitalObjectRow(Connection connection, String do_pid, String do_label) throws SQLException {

		String insertionStatement = "INSERT INTO DigitalObject (DO_PID, DO_Label) VALUES ('" + do_pid + "', '" +  do_label + "');";

		insertGen(connection, insertionStatement);
	}

        /**
        *
        * Inserts a DigitalObjectDissAssoc row.
        *
        * @param connection JDBC DBMS connection
        * @param do_dbid DigitalObject DBID
        * @param diss_dbid Disseminator DBID
        *
        * @exception SQLException JDBC, SQL error
        */
	public void insertDigitalObjectDissAssocRow(Connection connection, String do_dbid, String diss_dbid) throws SQLException {

		String insertionStatement = "INSERT INTO DigitalObjectDissAssoc (DO_DBID, DISS_DBID) VALUES ('" + do_dbid + "', '" + diss_dbid + "');";
		insertGen(connection, insertionStatement);
	}

        /**
        *
        * Inserts a Disseminator row.
        *
        * @param connection JDBC DBMS connection
        * @param bdef_dbid Behavior definition DBID
        * @param bmech_dbid Behavior mechanism DBID
        * @param diss_id Disseminator ID
        * @param diss_label Disseminator label
        *
        * @exception SQLException JDBC, SQL error
        */
	public void insertDisseminatorRow(Connection connection, String bdef_dbid, String bmech_dbid, String diss_id, String diss_label) throws SQLException {

		String insertionStatement = "INSERT INTO Disseminator (BDEF_DBID, BMECH_DBID, DISS_ID, DISS_Label) VALUES ('" + bdef_dbid + "', '" + bmech_dbid + "', '" + diss_id + "', '" + diss_label + "');";
		insertGen(connection, insertionStatement);
	}

        /**
        *
        * Inserts a MechanismImpl row.
        *
        * @param connection JDBC DBMS connection
        * @param bmech_dbid Behavior mechanism DBID
        * @param bdef_dbid Behavior definition DBID
        * @param meth_dbid Method DBID
        * @param dsbindingkey_dbid Datastream binding key DBID
        * @param mechimpl_protocol_type Mechanism implementation protocol type
        * @param mechimpl_return_type Mechanism implementation return type
        * @param mechimpl_address_location Mechanism implementation address location
        * @param mechimpl_operation_location Mechanism implementation operation location
        * @param policy_dbid Policy DBID
        *
        * @exception SQLException JDBC, SQL error
        */
	public void insertMechanismImplRow(Connection connection, String bmech_dbid, String bdef_dbid, String meth_dbid, String dsbindingkey_dbid, String mechimpl_protocol_type, String mechimpl_return_type, String mechimpl_address_location, String mechimpl_operation_location, String policy_dbid) throws SQLException {

		String insertionStatement = "INSERT INTO MechanismImpl (BMECH_DBID, BDEF_DBID, METH_DBID, DSBindingKey_DBID, MECHImpl_Protocol_Type, MECHImpl_Return_Type, MECHImpl_Address_Location, MECHImpl_Operation_Location, POLICY_DBID) VALUES ('" + bmech_dbid + "', '" + bdef_dbid + "', '" + meth_dbid + "', '" + dsbindingkey_dbid + "', '" + mechimpl_protocol_type + "', '" + mechimpl_return_type + "', '" + mechimpl_address_location + "', '" + mechimpl_operation_location + "', '" + policy_dbid + "');";

		insertGen(connection, insertionStatement);
	}

        /**
        *
        * Inserts a Method row.
        *
        * @param connection JDBC DBMS connection
        * @param bdef_dbid Behavior definition DBID
        * @param meth_name Behavior definition label
        * @param meth_label Behavior definition label
        *
        * @exception SQLException JDBC, SQL error
        */
	public void insertMethodRow(Connection connection, String bdef_dbid, String meth_name, String meth_label) throws SQLException {

		String insertionStatement = "INSERT INTO Method (BDEF_DBID, METH_Name, METH_Label) VALUES ('" + bdef_dbid + "', '" + meth_name + "', '" + meth_label + "');";

		insertGen(connection, insertionStatement);
	}

       /**
        *
        * @param connection An SQL Connection.
        * @param methDBID The method database ID.
        * @param bdefDBID The behavior Definition object database ID.
        * @param parmName the parameter name.
        * @param parmDefaultValue A default value for the parameter.
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
                String insertionStatement = "INSERT INTO Parameter "
                + "(METH_DBID, BDEF_DBID, PARM_Name, PARM_Default_Value, "
                + "PARM_Domain_Values, PARM_Required_Flag, PARM_Label, "
                + "PARM_Type) VALUES ('"
                + methDBID + "', '" + bdefDBID + "', '"
                + parmName + "', '" + parmDefaultValue + "', '"
                + parmDomainValues + "', '"
                + parmRequiredFlag + "', '" + parmLabel + "', '"
                + parmType + "');";
                insertGen(connection, insertionStatement);
	}

        /**
         *
         * @param connection An SQL Connection.
         * @param methDBID The method database ID.
         * @param bdefDBID The behavior Definition object database ID.
         * @param parmName the parameter name.
         * @param parmDefaultValue A default value for the parameter.
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
                 String insertionStatement = "INSERT INTO MechDefaultParameter "
                 + "(METH_DBID, BMECH_DBID, DEFPARM_Name, DEFPARM_Default_Value, "
                 + "DEFPARM_Domain_Values, DEFPARM_Required_Flag, DEFPARM_Label, "
                 + "DEFPARM_Type) VALUES ('"
                 + methDBID + "', '" + bmechDBID + "', '"
                 + parmName + "', '" + parmDefaultValue + "', '"
                 + parmDomainValues + "', '"
                 + parmRequiredFlag + "', '" + parmLabel + "', '"
                 + parmType + "');";
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

package fedora.server.storage.replication;

/**
 * Title: DOReplicator.java
 * Description: Replicates Fedora object data. 
 * Copyright: Copyright (c) 2002
 * Company: 
 * @author Paul Charlton
 * @version 1.0
 */

import java.util.*;
import java.sql.*;
import java.io.*;

import fedora.server.errors.*;
import fedora.server.storage.*;
import fedora.server.storage.types.*;
import fedora.server.storage.replication.*;

/**
* 
* Description: Converts data read from the object reader interfaces and creates 
* the corresponding database rows.
*
* @version 1.0
*
*/
public class DOReplicator { 

    /**
    * 
    * Replicates a Fedora behavior definition object.
    *
    * @param bDefReader behavior definition reader
    *
    * @exception ReplicationException replication processing error
    * @exception ClassNotFoundException JDBC driver error
    * @exception SQLException JDBC, SQL error
    * @exception Exception General exeception (DBMS init code)
    */
    public void replicateBehaviorDefinitionObject(DefinitiveBDefReader bDefReader) throws ReplicationException, ClassNotFoundException, SQLException, Exception {
	Connection connection;
	DbmsConnection db;
	DBIDLookup dl;
	MethodDef behaviorDefs[];
	RowInsertion ri;
	String bDefDBID;
	String bDefPID;
	String bDefLabel;

	db = new DbmsConnection();
	connection = db.getConnection();

	// Insert Behavior Definition row
	bDefPID = bDefReader.GetObjectPID();
	bDefLabel = bDefReader.GetObjectLabel();
	ri = new RowInsertion();
    	ri.insertBehaviorDefinitionRow(connection, bDefPID, bDefLabel);

	// Insert Method rows
	dl = new DBIDLookup();
	bDefDBID = dl.lookupBehaviorDefinitionDBID(connection, bDefPID);
	if (bDefDBID == null)
		throw new ReplicationException("BehaviorDefinition row doesn't exist for PID: " + bDefPID);

	behaviorDefs = bDefReader.GetBehaviorMethods(null);
  	for (int i=0; i<behaviorDefs.length; ++i) {
		ri.insertMethodRow(connection, bDefDBID, behaviorDefs[i].methodName, behaviorDefs[i].methodLabel);
        }

	db.freeConnection(connection);
    }

    /**
    * 
    * Replicates a Fedora behavior mechanism object.
    *
    * @param bMechReader behavior mechanism reader
    *
    * @exception ReplicationException replication processing error
    * @exception ClassNotFoundException JDBC driver error
    * @exception SQLException JDBC, SQL error
    * @exception Exception General exeception (DBMS init code)
    */
    public void replicateBehaviorMechanismObject(DefinitiveBMechReader bMechReader) throws ReplicationException, ClassNotFoundException, SQLException, Exception {
	BMechDSBindSpec dsBindSpec;
	Connection connection;
	DbmsConnection db;
	DBIDLookup dl;
	MethodDef behaviorBindings[];
	MethodDefOperationBind behaviorBindingsEntry;
	RowInsertion ri;
	String bDefDBID;
	String bDefPID;
	String bMechDBID;
	String bMechPID;
	String bMechLabel;
	String dsBindingKeyDBID;
	String methodDBID;
	String ordinality_flag;
	String cardinality;

	db = new DbmsConnection();
	connection = db.getConnection();

	// Insert Behavior Mechanism row
	dsBindSpec = bMechReader.GetDSBindingSpec(null);
	bDefPID = dsBindSpec.bDefPID;

	dl = new DBIDLookup();
	bDefDBID = dl.lookupBehaviorDefinitionDBID(connection, bDefPID);
	if (bDefDBID == null)
		throw new ReplicationException("BehaviorDefinition row doesn't exist for PID: " + bDefPID);

	bMechPID = bMechReader.GetObjectPID();
	bMechLabel = bMechReader.GetObjectLabel();

	ri = new RowInsertion();
    	ri.insertBehaviorMechanismRow(connection, bDefDBID, bMechPID, bMechLabel);

	// Insert DataStreamBindingSpec rows
	bMechDBID = dl.lookupBehaviorMechanismDBID(connection, bMechPID);
	if (bMechDBID == null)
		throw new ReplicationException("BehaviorMechanism row doesn't exist for PID: " + bDefPID);

	for (int i=0; i<dsBindSpec.dsBindRules.length; ++i) {

		// Convert from type boolean to type String
		ordinality_flag = dsBindSpec.dsBindRules[i].ordinality ? "true" : "false";
		// Convert from type int to type String
		cardinality = Integer.toString(dsBindSpec.dsBindRules[i].maxNumBindings);

		ri.insertDataStreamBindingSpecRow(connection, 
			bMechDBID, 
			dsBindSpec.dsBindRules[i].bindingKeyName, 
			ordinality_flag, 
			cardinality, 
			dsBindSpec.dsBindRules[i].bindingLabel);

		// Insert DataStreamMIME rows
		dsBindingKeyDBID = 
			dl.lookupDataStreamBindingSpecDBID(connection, bMechDBID, 
				dsBindSpec.dsBindRules[i].bindingKeyName);
		if (dsBindingKeyDBID == null)
			throw new ReplicationException(
			"DataStreamBindingSpec row doesn't exist for bMechDBID: "
 				+ bMechDBID + ", binding key name: " + 
				dsBindSpec.dsBindRules[i].bindingKeyName);

		for (int j=0; j<dsBindSpec.dsBindRules[i].bindingMIMETypes.length; ++j) {
			ri.insertDataStreamMIMERow(connection, 
				dsBindingKeyDBID,
				dsBindSpec.dsBindRules[i].bindingMIMETypes[j]);
		}
	}

	behaviorBindings = bMechReader.GetBehaviorMethods(null);


	// Insert MechanismImpl rows

        for (int i=0; i<behaviorBindings.length; ++i) {
		behaviorBindingsEntry = (MethodDefOperationBind)behaviorBindings[i];

		if (!behaviorBindingsEntry.protocolType.equals("HTTP")) {

			// Debug statement
			// System.out.println("Ignoring non HTTP protocol: " + behaviorBindingsEntry.protocolType + "i=" + i);
			// For the time being, ignore bindings other than HTTP.
			continue;
		}

		methodDBID = dl.lookupMethodDBID(connection, bDefDBID, behaviorBindingsEntry.methodName);
		if (methodDBID == null)
			throw new ReplicationException("Method row doesn't exist for method name: " + behaviorBindingsEntry.methodName);

		for (int j=0; j<dsBindSpec.dsBindRules.length; ++j) {

			dsBindingKeyDBID = 
				dl.lookupDataStreamBindingSpecDBID(connection, bMechDBID, 
				dsBindSpec.dsBindRules[j].bindingKeyName);
			if (dsBindingKeyDBID == null)
				throw new ReplicationException(
				"DataStreamBindingSpec row doesn't exist for bMechDBID: " 				+ bMechDBID + ", binding key name: " + 
				dsBindSpec.dsBindRules[j].bindingKeyName);

			ri.insertMechanismImplRow(connection, 
				bMechDBID, 
				bDefDBID, 
				methodDBID, 
				dsBindingKeyDBID,
				"http", 
				"text/html", 
				behaviorBindingsEntry.serviceBindingAddress, 
				behaviorBindingsEntry.operationLocation, 
				"1");
		}
	}	

	db.freeConnection(connection);
    }

    /**
    * 
    * Replicates a Fedora data object.
    *
    * @param doReader data object reader
    *
    * @exception ReplicationException replication processing error
    * @exception ClassNotFoundException JDBC driver error
    * @exception SQLException JDBC, SQL error
    * @exception Exception General exeception (DBMS init code)
    */
    public void replicateDO(DefinitiveDOReader doReader) throws ReplicationException, ClassNotFoundException, SQLException, Exception {
	DSBindingMapAugmented[] allBindingMaps;
	Connection connection;
	DbmsConnection db;
	Disseminator disseminators[]; 
	DBIDLookup dl;
	RowInsertion ri;
	String bDefDBID;
	String bindingMapDBID;
	String bMechDBID;
	String dissDBID;
	String doDBID;
	String doPID;
	String doLabel;
	String dsBindingKeyDBID;
	int rc;

    	doPID = doReader.GetObjectPID();

	db = new DbmsConnection();
	connection = db.getConnection();

	// Insert Digital Object row
    	doPID = doReader.GetObjectPID();
	doLabel = doReader.GetObjectLabel();

	ri = new RowInsertion();
    	ri.insertDigitalObjectRow(connection, doPID, doLabel);

	dl = new DBIDLookup();
	doDBID = dl.lookupDigitalObjectDBID(connection, doPID);
	if (doDBID == null)
		throw new ReplicationException("DigitalObject row doesn't exist for PID: " + doPID);

	disseminators = doReader.GetDisseminators(null);
	for (int i=0; i<disseminators.length; ++i) {
		bDefDBID = dl.lookupBehaviorDefinitionDBID(connection, disseminators[i].bDefID);
		if (bDefDBID == null)
			throw new ReplicationException("BehaviorDefinition row doesn't exist for PID: " + disseminators[i].bDefID);

		bMechDBID = dl.lookupBehaviorMechanismDBID(connection,  disseminators[i].bMechID);
		if (bMechDBID == null)
			throw new ReplicationException("BehaviorMechanism row doesn't exist for PID: " + disseminators[i].bMechID);


		// Insert Disseminator row if it doesn't exist.
		dissDBID = dl.lookupDisseminatorDBID(connection, bDefDBID, bMechDBID, disseminators[i].dissID);
		if (dissDBID == null) {
			// Disseminator row doesn't exist, add it.
    			ri.insertDisseminatorRow(connection, bDefDBID, bMechDBID, 
				disseminators[i].dissID, disseminators[i].dissLabel);
			dissDBID = dl.lookupDisseminatorDBID(connection, bDefDBID, bMechDBID, disseminators[i].dissID);
			if (dissDBID == null) 
				throw new ReplicationException("Disseminator row doesn't exist for PID: " + disseminators[i].dissID);
		}

		// Insert DigitalObjectDissAssoc row
    		ri.insertDigitalObjectDissAssocRow(connection, doDBID, dissDBID);
	}

	allBindingMaps = doReader.GetDSBindingMaps(null);
	for (int i=0; i<allBindingMaps.length; ++i) {

		bMechDBID = dl.lookupBehaviorMechanismDBID(connection,  allBindingMaps[i].dsBindMechanismPID);
		if (bMechDBID == null)
			throw new ReplicationException("BehaviorMechanism row doesn't exist for PID: " + allBindingMaps[i].dsBindMechanismPID);

		// Insert DataStreamBindingMap row if it doesn't exist.
		bindingMapDBID = dl.lookupDataStreamBindingMapDBID(connection, bMechDBID, 
			allBindingMaps[i].dsBindMapID);
		if (bindingMapDBID == null) {
			// DataStreamBinding row doesn't exist, add it.
    			ri.insertDataStreamBindingMapRow(connection, bMechDBID, 
				allBindingMaps[i].dsBindMapID, 
				allBindingMaps[i].dsBindMapLabel);

			bindingMapDBID = dl.lookupDataStreamBindingMapDBID(connection, bMechDBID, allBindingMaps[i].dsBindMapID);
			if (bindingMapDBID == null) 
				throw new ReplicationException("lookupDataStreamBindingMapDBID row doesn't exist for bMechDBID: " + bMechDBID + ", dsBindingMapID: " + allBindingMaps[i].dsBindMapID);
		}

		for (int j=0; j<allBindingMaps[i].dsBindingsAugmented.length; ++j) {
			dsBindingKeyDBID = dl.lookupDataStreamBindingSpecDBID(connection, bMechDBID, allBindingMaps[i].dsBindingsAugmented[j].bindKeyName);
			if (dsBindingKeyDBID == null) 
				throw new ReplicationException("lookupDataStreamBindingDBID row doesn't exist for bMechDBID: " + bMechDBID + ", bindKeyName: " + allBindingMaps[i].dsBindingsAugmented[j].bindKeyName + "i=" + i + " j=" + j);

			// Insert DataStreamBinding row
    			ri.insertDataStreamBindingRow(connection, 
				doDBID, 
				dsBindingKeyDBID, 
				bindingMapDBID, 
				allBindingMaps[i].dsBindingsAugmented[j].seqNo, 
				allBindingMaps[i].dsBindingsAugmented[j].datastreamID,
				allBindingMaps[i].dsBindingsAugmented[j].bindLabel,
				allBindingMaps[i].dsBindingsAugmented[j].DSMIME,
				allBindingMaps[i].dsBindingsAugmented[j].DSLocation,
				"1");

		}
	}

	db.freeConnection(connection);
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
	String rplOpt;
	String pid;
	String usage = "Usage: java DOReplicator do|bd|bm pid";

    	if (args.length != 2) {
		System.out.println(usage);
		System.exit(1);
	}


    	if (!(args[0].equals("do") || args[0].equals("bd") || args[0].equals("bm"))) 
	{
		System.out.println(usage);
		System.exit(1);
	}
	rplOpt = args[0];
	pid = args[1];

	System.out.println("rplOpt=" + rplOpt + " pid=" + pid);

	if (rplOpt.equals("do")) {
		System.out.println("Calling DefinitiveDOReader with: " + pid);
    		DefinitiveDOReader doReader = new DefinitiveDOReader(pid);
		new DOReplicator().replicateDO(doReader);
	}
	else {
		if (rplOpt.equals("bd")) { 
			System.out.println("Calling DefinitiveBDefReader with: " + pid);
    			DefinitiveBDefReader bDefReader = new DefinitiveBDefReader(pid);
			new DOReplicator().replicateBehaviorDefinitionObject(bDefReader);
		}
		else {
			if (rplOpt.equals("bm")) {
				System.out.println("Calling DefinitiveBMechReader with: " + pid);
    				DefinitiveBMechReader bMechReader = new DefinitiveBMechReader(pid);
				new DOReplicator().replicateBehaviorMechanismObject(bMechReader);
			}
		}
	}
    }
}

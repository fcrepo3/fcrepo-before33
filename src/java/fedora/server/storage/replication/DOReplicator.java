/**
 * <p>Title: DOReplicator.java</p>
 * <p>Description: Replicates Fedora object data. 
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 * @author Paul Charlton
 * @version 1.0
 */

import java.util.*;
import java.sql.*;
import java.io.*;

import fedora.server.storage.*;
import fedora.server.storage.types.*;
import fedora.server.storage.replication.*;

class ReplicationException extends Exception {
       public ReplicationException() {}
       public ReplicationException(String msg) {
         super(msg);
       }
}

/**
* <p>
* Description: Converts data read from the object reader interfaces and creates 
* the corresponding database rows./p>
*
* @version 1.0
*
*/
public class DOReplicator { 

    /**
    * <p>
    * Replicates a Fedora behavior definition object.
    *
    * @param bDefReader behavior definition reader
    *
    * @exception ReplicationException replication processing error
    * @exception ClassNotFoundException JDBC driver error
    * @exception SQLException JDBC, SQL error
    */
    public void replicateBehaviorDefinitionObject(DefinitiveBDefReader bDefReader) throws ReplicationException, ClassNotFoundException, SQLException {
	DbmsConnection db;
	IDLookup idl;
	MethodDef behaviorDefs[];
	RowInsertion ri;
	String bDefDBID;
	String bDefPID;
	String bDefLabel;

	db = new DbmsConnection();
	db.connectDatabase();

	// Insert Behavior Definition row
	bDefPID = bDefReader.GetObjectPID();
	bDefLabel = bDefReader.GetObjectLabel();
	ri = new RowInsertion();
    	ri.insertBehaviorDefinitionRow(db, bDefPID, bDefLabel);

	// Insert Method rows
	idl = new IDLookup();
	bDefDBID = idl.lookupBehaviorDefinitionDBID(db, bDefPID);
	if (bDefDBID == null)
		throw new ReplicationException("BehaviorDefinition row doesn't exist for PID: " + bDefPID);

	behaviorDefs = bDefReader.GetBehaviorMethods(null);
  	for (int i=0; i<behaviorDefs.length; ++i) {
		ri.insertMethodRow(db, bDefDBID, behaviorDefs[i].methodName, behaviorDefs[i].methodLabel);
        }
    }

    /**
    * <p>
    * Replicates a Fedora behavior mechanism object.
    *
    * @param bMechReader behavior mechanism reader
    *
    * @exception ReplicationException replication processing error
    * @exception ClassNotFoundException JDBC driver error
    * @exception SQLException JDBC, SQL error
    */
    public void replicateBehaviorMechanismObject(DefinitiveBMechReader bMechReader) throws ReplicationException, ClassNotFoundException, SQLException {
	BMechDSBindSpec dsBindSpec;
	DbmsConnection db;
	IDLookup idl;
	MethodDef behaviorBindings[];
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
	db.connectDatabase();

	// Insert Behavior Mechanism row
	dsBindSpec = bMechReader.GetDSBindingSpec(null);
	bDefPID = dsBindSpec.bDefPID;

	idl = new IDLookup();
	bDefDBID = idl.lookupBehaviorDefinitionDBID(db, bDefPID);
	if (bDefDBID == null)
		throw new ReplicationException("BehaviorDefinition row doesn't exist for PID: " + bDefPID);

	bMechPID = bMechReader.GetObjectPID();
	bMechLabel = bMechReader.GetObjectLabel();

	ri = new RowInsertion();
    	ri.insertBehaviorMechanismRow(db, bDefDBID, bMechPID, bMechLabel);

	// Insert DataStreamBindingSpec rows
	bMechDBID = idl.lookupBehaviorMechanismDBID(db, bMechPID);
	if (bMechDBID == null)
		throw new ReplicationException("BehaviorMechanism row doesn't exist for PID: " + bDefPID);

	for (int i=0; i<dsBindSpec.dsBindRules.length; ++i) {
		// Convert from type boolean to type String
		ordinality_flag = dsBindSpec.dsBindRules[i].ordinality ? "Y" : "N";
		// Convert from type int to type String
		cardinality = Integer.toString(dsBindSpec.dsBindRules[i].maxNumBindings);

		ri.insertDataStreamBindingSpecRow(db, 
			bMechDBID, 
			dsBindSpec.dsBindRules[i].bindingKeyName, 
			ordinality_flag, 
			cardinality, 
			// For the time being, the first array element will
			// be used here.  This may be changed in a later release.
			dsBindSpec.dsBindRules[i].bindingMIMETypes[0], 
			dsBindSpec.dsBindRules[i].bindingLabel);
	}

	behaviorBindings = bMechReader.GetBehaviorMethods(null);

	// Insert MechanismImpl rows

// Bug in GetBehaviorMethods? Waiting on Sandy's response.
// Will probably change for loop to the following line then.
//         for (int i=0; i<behaviorBindings.length; ++i) {
for (int i=0; i<1; ++i) {

		methodDBID = idl.lookupMethodDBID(db, bDefDBID, behaviorBindings[i].methodName);
		if (methodDBID == null)
			throw new ReplicationException("Method row doesn't exist for method name: " + behaviorBindings[i].methodName);

		for (int j=0; j<dsBindSpec.dsBindRules.length; ++j) {

			dsBindingKeyDBID = 
				idl.lookupDataStreamBindingSpecDBID(db, bMechDBID, 
				dsBindSpec.dsBindRules[j].bindingKeyName);
			if (dsBindingKeyDBID == null)
				throw new ReplicationException(
				"DataStreamBindingSpec row doesn't exist for bMechDBID: " 				+ bMechDBID + ", binding key name: " + 
				dsBindSpec.dsBindRules[j].bindingKeyName);

			ri.insertMechanismImplRow(db, 
				bMechDBID, 
				bDefDBID, 
				methodDBID, 
				dsBindingKeyDBID,
				"http", 
				"text/html", 
				behaviorBindings[i].httpBindingAddress, 
				behaviorBindings[i].httpBindingOperationLocation, 
				"1");
		}
	}	

    }

    /**
    * <p>
    * Replicates a Fedora data object.
    *
    * @param doReader data object reader
    *
    * @exception ReplicationException replication processing error
    * @exception ClassNotFoundException JDBC driver error
    * @exception SQLException JDBC, SQL error
    */
    public void replicateDO(DefinitiveDOReader doReader) throws ReplicationException, ClassNotFoundException, SQLException {
	DSBindingMapAugmented[] allBindingMaps;
	DbmsConnection db;
	Disseminator disseminators[]; 
	IDLookup idl;
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
	db.connectDatabase();

	// Insert Digital Object row
    	doPID = doReader.GetObjectPID();
	doLabel = doReader.GetObjectLabel();

	ri = new RowInsertion();
    	ri.insertDigitalObjectRow(db, doPID, doLabel);

	idl = new IDLookup();
	doDBID = idl.lookupDigitalObjectDBID(db, doPID);
	if (doDBID == null)
		throw new ReplicationException("DigitalObject row doesn't exist for PID: " + doPID);

	disseminators = doReader.GetDisseminators(null);
	for (int i=0; i<disseminators.length; ++i) {
		bDefDBID = idl.lookupBehaviorDefinitionDBID(db, disseminators[i].bDefID);
		if (bDefDBID == null)
			throw new ReplicationException("BehaviorDefinition row doesn't exist for PID: " + disseminators[i].bDefID);

		bMechDBID = idl.lookupBehaviorMechanismDBID(db,  disseminators[i].bMechID);
		if (bMechDBID == null)
			throw new ReplicationException("BehaviorMechanism row doesn't exist for PID: " + disseminators[i].bMechID);


		// Insert Disseminator row if it doesn't exist.
		dissDBID = idl.lookupDisseminatorDBID(db, bDefDBID, bMechDBID, disseminators[i].dissID);
		if (dissDBID == null) {
			// Disseminator row doesn't exist, add it.
    			ri.insertDisseminatorRow(db, bDefDBID, bMechDBID, 
				disseminators[i].dissID, disseminators[i].dissLabel);
			dissDBID = idl.lookupDisseminatorDBID(db, bDefDBID, bMechDBID, disseminators[i].dissID);
			if (dissDBID == null) 
				throw new ReplicationException("Disseminator row doesn't exist for PID: " + disseminators[i].dissID);
		}

		// Insert DigitalObjectDissAssoc row
    		ri.insertDigitalObjectDissAssocRow(db, doDBID, dissDBID);
	}

	allBindingMaps = doReader.GetDSBindingMaps(null);
	for (int i=0; i<allBindingMaps.length; ++i) {

		bMechDBID = idl.lookupBehaviorMechanismDBID(db,  allBindingMaps[i].dsBindMechanismPID);
		if (bMechDBID == null)
			throw new ReplicationException("BehaviorMechanism row doesn't exist for PID: " + allBindingMaps[i].dsBindMechanismPID);

		// Insert DataStreamBindingMap row if it doesn't exist.
		bindingMapDBID = idl.lookupDataStreamBindingMapDBID(db, bMechDBID, 
			allBindingMaps[i].dsBindMapID);
		if (bindingMapDBID == null) {
			// DataStreamBinding row doesn't exist, add it.
    			ri.insertDataStreamBindingMapRow(db, bMechDBID, 
				allBindingMaps[i].dsBindMapID, 
				allBindingMaps[i].dsBindMapLabel);

			bindingMapDBID = idl.lookupDataStreamBindingMapDBID(db, bMechDBID, allBindingMaps[i].dsBindMapID);
			if (bindingMapDBID == null) 
				throw new ReplicationException("lookupDataStreamBindingMapDBID row doesn't exist for bMechDBID: " + bMechDBID + ", dsBindingMapID: " + allBindingMaps[i].dsBindMapID);
		}

		for (int j=0; j<allBindingMaps[i].dsBindingsAugmented.length; ++j) {
			dsBindingKeyDBID = idl.lookupDataStreamBindingSpecDBID(db, bMechDBID, allBindingMaps[i].dsBindingsAugmented[j].bindKeyName);
			if (dsBindingKeyDBID == null) 
				throw new ReplicationException("lookupDataStreamBindingDBID row doesn't exist for bMechDBID: " + bMechDBID + ", bindKeyName: " + allBindingMaps[i].dsBindingsAugmented[j].bindKeyName + "i=" + i + " j=" + j);

			// Insert DataStreamBinding row
    			ri.insertDataStreamBindingRow(db, 
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

	DeleteTest dt = new DeleteTest();
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

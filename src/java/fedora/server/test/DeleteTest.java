package fedora.server.test;

import java.util.*;
import java.sql.*;
import java.io.*;

import fedora.server.errors.*;
import fedora.server.storage.*;
import fedora.server.storage.types.*;
import fedora.server.storage.replication.*;

/**
 *
 * <p><b>Title:</b> DeleteTest.java</p>
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
 * <p>The entire file consists of original code.  Copyright © 2002, 2003 by The
 * Rector and Visitors of the University of Virginia and Cornell University.
 * All rights reserved.</p>
 *
 * -----------------------------------------------------------------------------
 *
 * @author Paul Charlton
 * @version 1.0
 */
public class DeleteTest {


    /**
    *
    * Deletes a Fedora database data object.
    *
    * @param pid Data object PID
    *
    * @exception ReplicationException replication processing error
    * @exception ClassNotFoundException JDBC driver error
    * @exception SQLException JDBC, SQL error
    * @exception Exception General exeception (DBMS init code)
    */
    public void deleteDataObject(String pid) throws ReplicationException, ClassNotFoundException, SQLException, Exception {
	Connection connection;
	DbmsConnection db;
	DBIDLookup dl;
	String doDBID;
	RowDeletion rd;

	db = new DbmsConnection();
	connection = db.getConnection();

	dl = new DBIDLookup();
	doDBID = dl.lookupDigitalObjectDBID(connection, pid);
	if (doDBID == null)
		throw new ReplicationException("DigitalObject row doesn't exist for PID: " + pid);

	rd = new RowDeletion();

	// Single row delete.
    	rd.deleteDigitalObjectRow(connection, doDBID);
	// Possibly multiple row deletes.
    	rd.deleteDataStreamBindingRow(connection, doDBID);
	// Possibly multiple row deletes.
    	rd.deleteDigitalObjectDissAssocRow(connection, doDBID);

	db.freeConnection(connection);
    }


    /**
    *
    * Deletes a Fedora database behavior definition object.
    *
    * @param pid Behavior definition PID
    *
    * @exception ReplicationException replication processing error
    * @exception ClassNotFoundException JDBC driver error
    * @exception SQLException JDBC, SQL error
    * @exception Exception General exeception (DBMS init code)
    */
    public void deleteBehaviorDefinition(String pid) throws ReplicationException, ClassNotFoundException, SQLException, Exception {
	Connection connection;
	DbmsConnection db;
	DBIDLookup dl;
	String doDBID;
	RowDeletion rd;
	String bDefDBID;
	String bDefPID;

	db = new DbmsConnection();
	connection = db.getConnection();

	dl = new DBIDLookup();
	bDefDBID = dl.lookupBehaviorDefinitionDBID(connection, pid);
	if (bDefDBID == null)
		throw new ReplicationException("BehaviorDefinition row doesn't exist for PID: " + pid);

	rd = new RowDeletion();

	// Single row delete.
    	rd.deleteBehaviorDefinitionRow(connection, bDefDBID);
	// Possibly multiple row deletes.
    	rd.deleteMethodRow(connection, bDefDBID);

	db.freeConnection(connection);
    }

    /**
    *
    * Deletes a Fedora database behavior mechanism object.
    *
    * @param pid Behavior mechanism PID
    *
    * @exception ReplicationException replication processing error
    * @exception ClassNotFoundException JDBC driver error
    * @exception SQLException JDBC, SQL error
    * @exception Exception General exeception (DBMS init code)
    */
    public void deleteBehaviorMechanism(String pid) throws ReplicationException, ClassNotFoundException, SQLException, Exception {
	Connection connection;
	DbmsConnection db;
	DBIDLookup dl;
	String doDBID;
	RowDeletion rd;
	String bMechDBID;
	String bMechPID;

	db = new DbmsConnection();
	connection = db.getConnection();

	dl = new DBIDLookup();
	bMechDBID = dl.lookupBehaviorMechanismDBID(connection, pid);
	if (bMechDBID == null)
		throw new ReplicationException("BehaviorMechanism row doesn't exist for PID: " + pid);

	rd = new RowDeletion();

	// Single row delete.
    	rd.deleteBehaviorMechanismRow(connection, bMechDBID);
	// Possibly multiple row deletes.
    	rd.deleteMechanismImplRow(connection, bMechDBID);
	// Possibly multiple row deletes.
    	rd.deleteDataStreamBindingMapRow(connection, bMechDBID);
	// Possibly multiple row deletes.  This call should preceed
	// the deleteDataStreamBindingSpecRow call.
    	rd.deleteDataStreamMIMERow(connection, bMechDBID);
	// Possibly multiple row deletes.
    	rd.deleteDataStreamBindingSpecRow(connection, bMechDBID);
	// Possibly multiple row deletes.  Note that Disseminator rows are
	// inserted from the Data Object, but are deleted by a specified
	// Behavior Mechanism PID.  See RowDeletion.java for more information.
    	rd.deleteDisseminatorRow(connection, bMechDBID);

	db.freeConnection(connection);
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
	String delOpt;
	String pid;
	String usage = "Usage: java DeleteTest do|bd|bm pid";

    	if (args.length != 2) {
		System.out.println(usage);
		System.exit(1);
	}


    	if (!(args[0].equals("do") || args[0].equals("bd") || args[0].equals("bm")))
	{
		System.out.println(usage);
		System.exit(1);
	}
	delOpt = args[0];
	pid = args[1];

	System.out.println("delOpt=" + delOpt + " pid=" + pid);

	DeleteTest dt = new DeleteTest();
	if (delOpt.equals("do"))
		dt.deleteDataObject(pid);
	else {
		if (delOpt.equals("bd"))
			dt.deleteBehaviorDefinition(pid);
		else {
			if (delOpt.equals("bm"))
				dt.deleteBehaviorMechanism(pid);
		}
	}
    }
}

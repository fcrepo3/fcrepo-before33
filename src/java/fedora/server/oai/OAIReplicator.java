package fedora.server.oai;

import java.util.*;
import java.sql.*;

import fedora.server.*;
import fedora.server.errors.*;
import fedora.server.storage.ConnectionPool;
import fedora.server.storage.DOReader;
import fedora.server.storage.types.*;
import fedora.server.utilities.DateUtility;
import fedora.server.utilities.DCFields;

/**
 * Writes updated information to the OAI-specific tables when objects have
 * been added, modified, or deleted.
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
 * @author cwilper@cs.cornell.edu
 * @version $Id$
 */
public class OAIReplicator
        extends StdoutLogging {

    private ConnectionPool m_pool;
    private HashMap m_formatDbIDs;

    protected OAIReplicator(ConnectionPool pool,
                         Logging logTarget) {
        super(logTarget);
        m_pool=pool;
    }

    /**
     * Get a connection from the pool with autocommit turned off.
     */
    private Connection beginTransaction()
            throws SQLException {
        Connection conn=m_pool.getConnection();
        conn.setAutoCommit(false);
        return conn;
    }

    /**
     * Commit or rollback a transaction, then return the connection to the pool.
     * Nothing is done if the connection is null.
     */
    private void endTransaction(Connection conn, boolean commit) {
        if (conn!=null) {
            try {
                if (commit) {
                    conn.commit();
                } else {
                    conn.rollback();
                }
                conn.setAutoCommit(true);
                m_pool.free(conn);
            } catch (Exception e) {
                logWarning(e.getClass().getName() + ": " + e.getMessage()
                        + " (encountered during OAIReplication.endTransaction)");
            }
        }
    }

    /**
     * Update the OAI information as the result of a createObject API call.
     */
    public void newObject(DigitalObject newObj)
            throws ServerException {
        logFiner("OAIReplication.newObject(" + newObj.getPid() + ") started.");
        Connection conn=null;
        Statement st=null;
        boolean commit=false;
        try {
            conn=beginTransaction();
            st=conn.createStatement();
            // Make or replace an Item row, throwing an exception if
            // a non-purged object is already using the itemID.
            String itemID=getItemID(newObj);
            int status=1;
            if (newObj.getState().equals("A")) status=2;
            long itemDbID=addOrReplaceItem(st,
                                           itemID,
                                           newObj.getPid(),
                                           status,
                                           newObj.getLastModDate().getTime());
            // Add each harvestable, active datatstream as a Record row.
            Iterator iter=newObj.datastreamIdIterator();
            while (iter.hasNext()) {
                String dsID=(String) iter.next();
                Datastream ds=getMostRecentDatastream(newObj.datastreams(dsID));
                if (ds.isHarvestable && ds.DSState.equals("A")) {
                    addRecord(st,
                              itemDbID,
                              dsID,
                              getFormatDbID(ds.DSFormatURI),
                              ds.DSCreateDT.getTime());
                }
            }
            commit=true;
        } catch (SQLException e) {
            throw new GeneralException("OAIReplicator.newObject failed due to SQLException: "
                    + e.getMessage(), e);
        } finally {
            try {
                if (st!=null) st.close();
            } catch (SQLException e) {
                throw new StorageDeviceException("Error with sql database. "
                        + e.getMessage());
            } finally {
                st=null;
            }
            endTransaction(conn, commit);
            logFiner("OAIReplication.newObject(" + newObj.getPid() + ") finished.");
        }
    }

    /**
     * Update the OAI information as the result of one of the following API-M calls.
     * modifyObject, addDatastream, modifyDatastreamByReference/Value,
     * setDatastreamState, setDatastreamHarvestable, purgeDatastream
     */
    public void modifiedObject(DOReader oldReader, DigitalObject newObj)
            throws ServerException {
        String pid=newObj.getPid();
        logFiner("OAIReplication.modifiedObject(" + pid + ") started.");
        long nowUTC=DateUtility.convertLocalDateToUTCDate(new java.util.Date()).getTime();
        Connection conn=null;
        Statement st=null;
        ResultSet results=null;
        ResultSet rs=null;
        boolean commit=false;
        try {
            conn=beginTransaction();
            st=conn.createStatement();
            // We need to account for all combinations of possible changes (including NONE).
            // modifyObject:
            //   OBJECT_STATE_CHANGE
            // addDatastream:
            //   NEW_DATASTREAM
            // modifyDatastreamByReference/Value:
            //   CHANGED_DC_DATASTREAM
            //   CHANGED_DATASTREAM_STATE
            //   CHANGED_DATASTREAM_HARVESTABLE
            // setDatastreamState
            //   CHANGED_DATASTREAM_STATE
            // setDatastreamHarvestable
            //   CHANGED_DATASTREAM_HARVESTABLE
            // purgedDatastream
            //   PURGED_DATASTREAM
            ItemDbIDGetter itemDbID=new ItemDbIDGetter(st, pid);
            String itemUpdates=null;
            // CHANGED_DC_DATASTREAM?
            // (checked first as it's most likely to generate an error)
            Datastream oldDC=oldReader.GetDatastream("DC", null);
            Datastream newDC=getMostRecentDatastream(newObj.datastreams("DC"));
            if (oldDC.DSCreateDT.getTime()!=newDC.DSCreateDT.getTime()) {
                logFinest("Detected modified DC datastream");
                String oldItemID=getItemID(oldDC, pid);
                String newItemID=getItemID(newDC, pid);
                // If the itemID changed:
                // If it's already in use by a different object:
                //   ..and it's a purged object
                //     - remove the purged item and records,
                //     - change the itemID in place (and give oai warning)
                //   ..and it's a non-purged object
                //     - throw exception
                // Else
                //   - change the itemID in place (and give oai warning)
                if (!newItemID.equals(oldItemID)) {
                    logFinest("Trying itemID change from " + oldItemID + " to " + newItemID);
                    results=logAndExecuteQuery(st,
                            "SELECT itemDbID, status, pid FROM oItem WHERE itemID='"
                            + newItemID + "' AND NOT itemDbID=" + itemDbID.get() + "");
                    if (results.next()) {
                        long status=results.getLong("status");
                        String otherPID=results.getString("pid");
                        if (status==0) {
                            logFinest("New itemID was used by a purged object, "
                                    + "so we can remove the old Item and associated Records.");
                            long oldDbID=results.getLong("itemDbID");
                            results.close();
                            results=null;
                            logAndExecuteUpdate(st, "DELETE FROM oItem WHERE itemDbID=" + oldDbID);
                            int rows=logAndExecuteUpdate(st, "DELETE FROM oRecord WHERE itemDbID=" + oldDbID);
                            if (rows>0) {
                                logWarning("Removed " + rows + " rows from oRecord because the itemID "
                                        + newItemID + " which was previously used by purged object "
                                        + otherPID + " is now being used by " + pid + ".  Certain "
                                        + "incremental harvesters may not realize the old records "
                                        + "have been deleted.");
                            }
                        } else {
                            throw new GeneralException("Couldn't change itemID to " + newItemID
                                    + " since that itemID belongs to object " + otherPID);
                        }
                    }
                    // we can just change the itemID in place
                    results.close();
                    results=null;
                    itemUpdates="itemID='" + newItemID + "'";
                    logWarning("Changing an itemID.  If any harvestable records existed for "
                        + "this object, harvesters won't see them with the old id anymore.");
                }
            }
            // OBJECT_STATE_CHANGE?
            String oState=oldReader.GetObjectState();
            String nState=newObj.getState();
            if (!oState.equals(nState)) {
                if (oState.equals("A")) {
                    if (itemUpdates==null) {
                        itemUpdates="status=1";
                    } else {
                        itemUpdates=itemUpdates + ", status=1";
                    }
                    itemUpdates=itemUpdates + ", vDate=" + nowUTC;
                    logFinest("Item status changed from visible to invisible.");
                } else {
                    if (nState.equals("A")) {
                        if (itemUpdates==null) {
                            itemUpdates="status=2";
                        } else {
                            itemUpdates=itemUpdates + ", status=2";
                        }
                        itemUpdates=itemUpdates + ", vDate=" + nowUTC;
                        logFinest("Item status changed from invisible to visible.");
                    }
                }
            }
            if (itemUpdates!=null) {
                logAndExecuteUpdate(st, "UPDATE oItem SET " + itemUpdates
                        + " WHERE itemDbID=" + itemDbID.get());
            }
            // Check for NEW_DATASTREAMS while checking for CHANGED_DATASTREAM_STATE/HARVESTABLE
            Iterator iter=newObj.datastreamIdIterator();
            while (iter.hasNext()) {
                String dsID=(String) iter.next();
                Datastream oldDS=oldReader.GetDatastream(dsID, null);
                Datastream newDS=getMostRecentDatastream(newObj.datastreams(dsID));
                if (oldDS==null) {
                    // NEW_DATASTREAM - insert (or update a purged one if needed)
                    if (newDS.isHarvestable && newDS.DSState.equals("A")) {
                        logFiner("Detected new, harvestable datastream: " + dsID);
                        addOrUpdateRecord(st,
                                           itemDbID.get(),
                                           dsID,
                                           getFormatDbID(newDS.DSFormatURI),
                                           newDS.DSCreateDT.getTime());
                    }
                } else {
                    //
                    // Do eligibility checks/updates
                    //
                    boolean wasntEligible=false;
                    if (oldDS.isHarvestable) {
                        if (oldDS.DSState.equals("A")) {
                            if (!newDS.isHarvestable || !newDS.DSState.equals("A")) {
                                // was eligible, but now shouldn't be
                                logFinest("Making " + dsID + " ineligible because it's either "
                                        + "no longer harvestable or has a non-active status.");
                                logAndExecuteUpdate(st, "UPDATE oRecord SET iDate="
                                        + nowUTC
                                        + " WHERE itemDbID=" + itemDbID.get()
                                        + " AND formatDbID=" + getFormatDbID(newDS.DSFormatURI));
                            }
                            // else eligibility didn't change
                        } else {
                            wasntEligible=true;
                        }
                    } else {
                        wasntEligible=true;
                    }
                    if (wasntEligible) {
                        // ... but is it eligible now?
                        if (newDS.isHarvestable && newDS.DSState.equals("A")) {
                            // YES, so add/update it
                            rs=logAndExecuteQuery(st, "SELECT itemDbID FROM "
                                    + "oRecord WHERE itemDbID="
                                    + itemDbID.get() + " AND formatDbID=" + getFormatDbID(newDS.DSFormatURI));
                            boolean wasEverEligible=rs.next();
                            rs.close();
                            rs=null;
                            if (!wasEverEligible) {
                                // it was never eligible, so add as new
                                logFinest("In this object, a Record with formatURI " + newDS.DSFormatURI
                                        + " was never eligible for harvest, but "
                                        + "now is.  Adding new Record row.");
                                addRecord(st,
                                          itemDbID.get(),
                                          dsID,
                                          getFormatDbID(newDS.DSFormatURI),
                                          nowUTC);
                            } else {
                                // it was previously eligible, so do update
                                logFinest("In this object, the Record with formatURI " + newDS.DSFormatURI
                                        + " was not eligible for harvest before, but "
                                        + "now is.  *Updating* Record row since at one point in the past"
                                        + " it was eligible.");
                                logAndExecuteUpdate(st, "UPDATE oRecord SET eDate=" +
                                        + nowUTC
                                        + " WHERE itemDbID=" + itemDbID.get()
                                        + " AND formatDbID=" + getFormatDbID(newDS.DSFormatURI));
                            }
                        }
                        // else eligibility didn't change
                    }
                    //
                    // Then do cDate update if its contents have changed and its currently eligible
                    //
                    if (oldDS.DSCreateDT.getTime()!=newDS.DSCreateDT.getTime()
                            && newDS.isHarvestable && newDS.DSState.equals("A")) {
                        // the row exists by now, because if it didn't exist before,
                        // it would have been created above after we found it didn't
                        // have an entry in the oRecord table.
                        logFinest("Updating Record " + dsID + "'s cDate because its "
                                + "contents have changed and it is currently eligible for harvest.");
                        logAndExecuteUpdate(st, "UPDATE oRecord SET cDate="
                                + newDS.DSCreateDT.getTime()
                                + " WHERE itemDbID=" + itemDbID.get()
                                + " AND repID='" + dsID + "'");
                    }
                }
            }
            // Finally, check for PURGED_DATASTREAMS
            String[] oldIDs=oldReader.ListDatastreamIDs(null);
            for (int i=0; i<oldIDs.length; i++) {
                if (newObj.datastreams(oldIDs[i]).size()==0) {
                    // detected a purged datastream
                    logFinest("Detected purged datastream (" + oldIDs[i] + ").  Setting record iDate (if it was previously eligible for harvest)...");
                    logAndExecuteUpdate(st, "UPDATE oRecord SET iDate=" + nowUTC
                                + " WHERE itemDbID=" + itemDbID.get()
                                + " AND repID='" + oldIDs[i] + "' AND iDate<eDate");
                }
            }
            commit=true;
        } catch (SQLException e) {
            throw new GeneralException("OAIReplicator.modifiedObject failed due to SQLException: "
                    + e.getMessage(), e);
        } finally {
            try {
                if (st!=null) st.close();
                if (results!=null) results.close();
                if (rs!=null) rs.close();
            } catch (SQLException e) {
                throw new StorageDeviceException("Error with sql database. "
                        + e.getMessage());
            } finally {
                st=null;
                results=null;
                rs=null;
            }
            endTransaction(conn, commit);
            logFiner("OAIReplication.modifiedObject(" + newObj.getPid() + ") finished.");
        }
    }

    /**
     * Update the OAI information as the result of a purgeObject API-M call.
     */
    public void purgedObject(String pid)
            throws ServerException {
        logFiner("OAIReplication.purgedObject(" + pid + ") started.");
        Connection conn=null;
        Statement st=null;
        ResultSet results=null;
        try {
            // don't bother using a transaction since we're only executing one update at the end
            conn=m_pool.getConnection();
            st=conn.createStatement();
            // change vDate only if the item was previously active
            String vDatePart="";
            results=logAndExecuteQuery(st, "SELECT status FROM oItem "
                                                   + "WHERE pid='" + pid + "'");
            if (results.next()) {
                if (results.getLong("status")==2) {
                    vDatePart=", vDate=" + DateUtility.convertLocalDateToUTCDate(
                            new java.util.Date()).getTime();
                }
                logAndExecuteUpdate(st, "UPDATE oItem SET status=0" + vDatePart
                                     + " WHERE pid='" + pid + "'");
            }
            results.close();
            results=null;
        } catch (SQLException e) {
            throw new GeneralException("OAIReplicator.purgedObject failed due to SQLException: "
                    + e.getMessage(), e);
        } finally {
            try {
                if (st!=null) st.close();
                if (results!=null) results.close();
                if (conn!=null) m_pool.free(conn);
            } catch (SQLException e) {
                throw new StorageDeviceException("Error with sql database. "
                        + e.getMessage());
            } finally {
                st=null;
                results=null;
            }
            logFiner("OAIReplication.purgedObject(" + pid + ") finished.");
        }
    }

    /**
     * Add or replace an Item in the oItem table and return its itemDbID.
     *
     * If an Item row exists with the given itemID:
     *   If the assoc obj has been purged, replace, and REMOVE assoc recs.
     *     (may leave incremental oai harvesters in the dark,
     *     but it's the best we can do)
     *   Else die "An object (ePID) already exists with itemID (itemID)"
     * If it doesn't:
     *   Add the row.
     */
    private long addOrReplaceItem(Statement st,
                                  String itemID,
                                  String pid,
                                  int status,
                                  long vDate)
            throws SQLException, GeneralException {
        ResultSet results=null;
        long itemDbID=0;
        try {
            results=logAndExecuteQuery(st, "SELECT itemDbID, pid, status "
                                               + "FROM oItem WHERE itemID='" + itemID + "'");
            if (results.next()) {
                // An object existed with that itemID
                itemDbID=results.getLong("itemDbID");
                int oldStatus=results.getInt("status");
                String oldPID=results.getString("pid");
                results.close();
                results=null;
                if (oldStatus==0) {
                    // It was purged, so we're going to replace it
                    logFinest("Object " + oldPID + " used this itemID ("
                              + itemID + "), but that object was purged, so we'll re-use it.");
                    logAndExecuteUpdate(st, "UPDATE oItem "
                                        + "SET pid='" + pid
                                        + "', status=" + status
                                        + ", vDate=" + vDate + " "
                                        + "WHERE itemID='" + itemID + "'");
                    // .. then remove all associated Records
                    int rows=logAndExecuteUpdate(st, "DELETE FROM oRecord WHERE itemDbID=" + itemDbID);
                    if (rows>0) {
                        logWarning("Removed " + rows + " rows from oRecord because the itemID "
                                   + itemID + " which was previously used by purged object "
                                   + oldPID + " is now being used by " + pid + ".  Certain "
                                   + "incremental harvesters may not realize the old records "
                                   + "have been deleted.");
                    }
                } else {
                    // It hasn't been purged, so throw an error
                    throw new GeneralException("Object " + oldPID + " already exists with OAI Item id "
                            + itemID + ".");
                }
            } else {
                results.close();
                results=null;
                // insert it as a new one
                logFinest("This itemID (" + itemID + ") has never been used, so we'll create a new Item row.");
                logAndExecuteUpdate(st, "INSERT INTO oItem (itemID, pid, status, vDate) "
                                    + "VALUES ('" + itemID + "', '" + pid + "', "
                                    + status + ", " + vDate + ")");
                results=logAndExecuteQuery(st, "SELECT itemDbID from oItem "
                        + "WHERE itemID='" + itemID + "'");
                results.next();
                itemDbID=results.getLong("itemDbID");
            }
        } catch (SQLException sqle) {
            throw sqle;
        } finally {
            try {
                if (results!=null) results.close();
            } catch (SQLException sqle) {
                throw sqle;
            } finally {
                results=null;
                return itemDbID;
            }
        }
    }

    private void addOrUpdateRecord(Statement st,
                                   long itemDbID,
                                   String repID,
                                   long formatDbID,
                                   long ceDate)
            throws SQLException {

/*
I think I need to add a check on insert, and REPLACE the record row if that happens
(because, if all has gone right, we can assume it could have only gotten to this point
if it was a "purge")

...and only do the replacement by DSID if the target format is the same as the previously existing one!

mod this method to use formatDbID.... ALSO, check all queries in here for repID,
as their assumptions might also need changing!

*/

        ResultSet results=null;
        try {
            results=logAndExecuteQuery(st, "SELECT itemDbID from oRecord "
                + "WHERE itemDbID=" + itemDbID + " AND repID='" + repID + "'");
            if (results.next()) {
                results.close();
                results=null;
                logFinest("Record " + repID + " existed for this object, but was previously deleted.  Updating it...");
                logAndExecuteUpdate(st, "UPDATE oRecord SET formatDbID=" + formatDbID
                                    + ", cDate=" + ceDate + ", eDate=" + ceDate + " "
                                    + "WHERE itemDbID=" + itemDbID + " AND repID='" + repID + "'");
            } else {
                results.close();
                results=null;
                logFinest("Record " + repID + " never existed before for this object.  Adding it...");
                addRecord(st, itemDbID, repID, formatDbID, ceDate);
            }
        } catch (SQLException sqle) {
            throw sqle;
        } finally {
            try {
                if (results!=null) results.close();
            } catch (SQLException sqle) {
                throw sqle;
            } finally {
                results=null;
            }

        }
    }

    private void addRecord(Statement st,
                           long itemDbID,
                           String repID,
                           long formatDbID,
                           long ceDate)
            throws SQLException {
        logAndExecuteUpdate(st, "INSERT INTO oRecord (itemDbID, repID, formatDbID, "
                          + "cDate, eDate, iDate) VALUES (" + itemDbID + ", '"
                          + repID + "', " + formatDbID + ", " + ceDate + ", "
                          + ceDate + ", 0)");
    }


    /**
     * Get the id for the given format from the format table, creating
     * it beforehand if it doesn't exist.
     */
    private long getFormatDbID(String uri)
            throws SQLException {

        if (m_formatDbIDs==null) {
            m_formatDbIDs=new HashMap();
            Connection conn=null;
            Statement st=null;
            ResultSet results=null;
            try {
                conn=m_pool.getConnection();
                st=conn.createStatement();
                results=logAndExecuteQuery(st, "SELECT * FROM format");
                while (results.next()) {
                    long fmtid=results.getLong("formatDbID");
                    String fmturi=results.getString("formatURI");
                    m_formatDbIDs.put(fmturi, new Long(fmtid));
                }
                results.close();
                results=null;
            } finally {
                try {
                    if (st!=null) st.close();
                    if (results!=null) results.close();
                    if (conn!=null) m_pool.free(conn);
                } catch (SQLException sqle) {
                    throw sqle;
                } finally {
                    st=null;
                    results=null;
                }
            }
        }
        Long formatDbID=(Long) m_formatDbIDs.get(uri);
        if (formatDbID!=null) {
            return formatDbID.longValue();
        }
        // if it doesn't exist yet, add it to the table (and the hash)
        Connection conn=null;
        Statement st=null;
        ResultSet results=null;
        try {
            conn=m_pool.getConnection();
            st=conn.createStatement();
            int count=logAndExecuteUpdate(st, "INSERT INTO format (formatURI) VALUES ('" +  uri + "')");
            results=logAndExecuteQuery(st, "SELECT formatDbID FROM format");
            results.next();
            long fmtid=results.getLong("formatDbID");
            m_formatDbIDs.put(uri, new Long(fmtid));
            return fmtid;
        } finally {
            try {
                if (st!=null) st.close();
                if (results!=null) results.close();
                if (conn!=null) m_pool.free(conn);
            } catch (SQLException sqle) {
                throw sqle;
            } finally {
                st=null;
                results=null;
            }
        }
    }

    private ResultSet logAndExecuteQuery(Statement statement, String sql)
                throws SQLException {
        logFinest("Executing query: " + sql);
        return statement.executeQuery(sql);
    }

    private int logAndExecuteUpdate(Statement statement, String sql)
            throws SQLException {
        logFinest("Executing update: " + sql);
        return statement.executeUpdate(sql);
    }

    /**
     * Get the appropriate oai identifer for the given object.
     * Prepending this with oai:local.domain: should yield something
     * like oai:oai-domain:demo:1
     */
    private String getItemID(DigitalObject obj)
           throws ServerException {
        Datastream currentDC=getMostRecentDatastream(obj.datastreams("DC"));
        return getItemID(currentDC, obj.getPid());
    }

    private String getItemID(Datastream currentDC, String pid)
            throws ServerException {
        List ids=new DCFields(currentDC.getContentStream()).identifiers();
        if (ids.size()==0) {
            return pid;
        }
        String itemID=((String) ids.get(0)).trim();
        if (itemID.length()==0 || itemID.indexOf(" ")!=-1) {
            throw new GeneralException("First dc:identifier must be a valid URI because when specified, it's used as the object's OAI Item identifier.");
        }
        return itemID;
    }



    /**
     * Get the most recent datastream in the list.
     */
    private Datastream getMostRecentDatastream(List list) {
        long biggest=0;
        Datastream mostRecent=null;
        for (int i=0; i<list.size(); i++) {
            Datastream ds=(Datastream) list.get(i);
            long createDT=ds.DSCreateDT.getTime();
            if (createDT>biggest) {
                biggest=createDT;
                mostRecent=ds;
            }
        }
        return mostRecent;
    }

    /**
     * Caches the itemDbID the first time it's asked for, and returns
     * the cached id subseqently.  Used to avoid doing a query for the id
     * if we don't need to.
     */
    private class ItemDbIDGetter {

        private boolean m_got;
        private long m_dbID;
        private Statement m_st;
        private String m_pid;

        public ItemDbIDGetter(Statement st, String pid) {
            m_st=st;
            m_pid=pid;
        }

        public void set(long dbID) {
            m_dbID=dbID;
            m_got=true;
        }

        public long get()
                throws SQLException {
            if (m_got) {
                return m_dbID;
            } else {
                ResultSet results=logAndExecuteQuery(m_st,
                        "SELECT itemDbID from oItem WHERE pid='" + m_pid + "'");
                results.next();
                set(results.getLong("itemDbID"));
                return m_dbID;
            }
        }
    }

}

package fedora.server.storage.replication;

import java.util.*;
import java.sql.*;
import java.io.*;

import fedora.server.errors.*;
import fedora.server.errors.*;
import fedora.server.storage.*;
import fedora.server.storage.types.*;
import fedora.server.utilities.SQLUtility;

import fedora.server.Module;
import fedora.server.Server;
import fedora.server.storage.ConnectionPoolManager;
import fedora.server.errors.ModuleInitializationException;

/**
 * A Module that replicates digital object information to the dissemination
 * database.
 * <p></p>
 * Converts data read from the object reader interfaces and creates or
 * updates the corresponding database rows in the dissemination database.
 *
 * FIXME: recast sqlexceptions as replicationexceptions here and in interface
 *
 * @author Paul Charlton, cwilper@cs.cornell.edu
 * @version 1.0
 */
public class DefaultDOReplicator
        extends Module
        implements DOReplicator {

    private ConnectionPool m_pool;
    private RowInsertion m_ri;
    private DBIDLookup m_dl;

    public DefaultDOReplicator(Map moduleParameters, Server server, String role)
            throws ModuleInitializationException {
        super(moduleParameters, server, role);
    }

    public void initModule() {
        m_ri=new RowInsertion();
        m_dl=new DBIDLookup();
    }

    public void postInitModule()
            throws ModuleInitializationException {
        try {
            ConnectionPoolManager mgr=(ConnectionPoolManager)
                    getServer().getModule(
                    "fedora.server.storage.ConnectionPoolManager");
            m_pool=mgr.getPool();
        } catch (ServerException se) {
            throw new ModuleInitializationException(
                    "Error getting default pool: " + se.getMessage(),
                    getRole());
        }
    }

    /**
     * If the object has already been replicated, update the components
     * and return true.  Otherwise, return false.
     *
     * @ids=select doDbID from do where doPID='demo:5'
     * if @ids.size=0, return false
     * else:
     *     foreach $id in @ids
     *         ds=reader.getDatastream(id, null)
     *         update dsBind set dsLabel='mylabel', dsLocation='' where dsID='$id'
     */
    private boolean updateComponents(DOReader reader)
            throws ReplicationException {
        Connection connection=null;
        Statement st=null;
        ResultSet results=null;
        boolean triedUpdate=false;
        boolean failed=false;
        try {
            connection=m_pool.getConnection();
            st=connection.createStatement();
            results=logAndExecuteQuery(st, "SELECT doDbID FROM do WHERE "
                    + "doPID='" + reader.GetObjectPID() + "'");
            if (!results.next()) {
                logFinest("DefaultDOReplication.updateComponents: Object is "
                        + "new; components dont need updating.");
                return false;
            }
            int doDbID=results.getInt("doDbID");
            results.close();
            results=logAndExecuteQuery(st, "SELECT dsID, dsLabel, dsLocation "
                    + "FROM dsBind WHERE doDbID=" + doDbID);
            ArrayList updates=new ArrayList();
            while (results.next()) {
                String dsID=results.getString("dsID");
                String dsLabel=results.getString("dsLabel");
                String dsLocation=results.getString("dsLocation");
                // compare the datastream to what's in the db...
                // if different, add to update list
                Datastream ds=reader.GetDatastream(dsID, null);
                if (!ds.DSLabel.equals(dsLabel)
                        || !ds.DSLocation.equals(dsLocation)) {
                    updates.add("UPDATE dsBind SET dsLabel='"
                            + SQLUtility.aposEscape(ds.DSLabel) + "', dsLocation='"
                            + SQLUtility.aposEscape(ds.DSLocation)
                            + "' WHERE dsID='" + dsID + "'");
                }
            }
            results.close();
            // do any required updates via a transaction
            if (updates.size()>0) {
                connection.setAutoCommit(false);
                triedUpdate=true;
                for (int i=0; i<updates.size(); i++) {
                    String update=(String) updates.get(i);
                    logAndExecuteUpdate(st, update);
                }
                connection.commit();
            } else {
                logFinest("No datastream labels or locations changed.");
            }
        } catch (SQLException sqle) {
            failed=true;
            throw new ReplicationException("An error has occurred during "
                + "Replication. The error was \" " + sqle.getClass().getName()
                + " \". The cause was \" " + sqle.getMessage());
        } catch (ServerException se) {
            failed=true;
            throw new ReplicationException("An error has occurred during "
                + "Replication. The error was \" " + se.getClass().getName()
                + " \". The cause was \" " + se.getMessage());
        } finally {
            if (connection!=null) {
                try {
                    if (triedUpdate && failed) connection.rollback();
                } catch (Throwable th) {
                    logWarning("While rolling back: " +  th.getClass().getName()
                            + ": " + th.getMessage());
                } finally {
                    try {
                        if (results != null) results.close();
                        if (st!=null) st.close();
                        connection.setAutoCommit(true);
                    } catch (SQLException sqle) {
                        logWarning("While cleaning up: " +  sqle.getClass().getName()
                            + ": " + sqle.getMessage());
                    } finally {
                        m_pool.free(connection);
                    }
                }
            }
        }
        return true;
    }

    /**
     * Replicates a Fedora behavior definition object.
     *
     * @param bDefReader behavior definition reader
     * @exception ReplicationException replication processing error
     * @exception SQLException JDBC, SQL error
     */
    public void replicate(BDefReader bDefReader)
            throws ReplicationException, SQLException {
        if (!updateComponents(bDefReader)) {
            Connection connection=null;
            try {
                MethodDef behaviorDefs[];
                String bDefDBID;
                String bDefPID;
                String bDefLabel;
                String methDBID;
                String methodName;
                String parmRequired;
                String[] parmDomainValues;
                connection = m_pool.getConnection();
                connection.setAutoCommit(false);

                // Insert Behavior Definition row
                bDefPID = bDefReader.GetObjectPID();
                bDefLabel = bDefReader.GetObjectLabel();
                m_ri.insertBehaviorDefinitionRow(connection, bDefPID, bDefLabel);

                // Insert method rows
                bDefDBID = m_dl.lookupBehaviorDefinitionDBID(connection, bDefPID);
                if (bDefDBID == null) {
                    throw new ReplicationException(
                        "BehaviorDefinition row doesn't exist for PID: "
                        + bDefPID);
                }
                behaviorDefs = bDefReader.getAbstractMethods(null);
                for (int i=0; i<behaviorDefs.length; ++i) {
                    m_ri.insertMethodRow(connection, bDefDBID,
                            behaviorDefs[i].methodName,
                            behaviorDefs[i].methodLabel);

                    // Insert method parm rows
                    methDBID =  m_dl.lookupMethodDBID(connection, bDefDBID,
                        behaviorDefs[i].methodName);
                    for (int j=0; j<behaviorDefs[i].methodParms.length; j++)
                    {
                      MethodParmDef[] methodParmDefs =
                      new MethodParmDef[behaviorDefs[i].methodParms.length];
                      methodParmDefs = behaviorDefs[i].methodParms;
                      parmRequired =
                               methodParmDefs[j].parmRequired ? "true" : "false";
                      parmDomainValues = methodParmDefs[j].parmDomainValues;
                      StringBuffer sb = new StringBuffer();
                      if (parmDomainValues != null && parmDomainValues.length > 0)
                      {
                        for (int k=0; k<parmDomainValues.length; k++)
                        {
                          if (k < parmDomainValues.length-1)
                          {
                            sb.append(parmDomainValues[k]+",");
                          } else
                          {
                            sb.append(parmDomainValues[k]);
                          }
                      }
                      } else
                      {
                        sb.append("null");
                      }
                      m_ri.insertMethodParmRow(connection, methDBID, bDefDBID,
                              methodParmDefs[j].parmName,
                              methodParmDefs[j].parmDefaultValue,
                              sb.toString(),
                              parmRequired,
                              methodParmDefs[j].parmLabel,
                              methodParmDefs[j].parmType);
                    }
                }
                connection.commit();
            } catch (ReplicationException re) {
                throw re;
            } catch (ServerException se) {
                throw new ReplicationException("Replication exception caused by "
                        + "ServerException - " + se.getMessage());
            } finally {
                if (connection!=null) {
                    try {
                        connection.rollback();
                    } catch (Throwable th) {
                        logWarning("While rolling back: " +  th.getClass().getName() + ": " + th.getMessage());
                    } finally {
                        connection.setAutoCommit(true);
                        m_pool.free(connection);
                    }
                }
            }
        }
    }

    /**
     * Replicates a Fedora behavior mechanism object.
     *
     * @param bMechReader behavior mechanism reader
     * @exception ReplicationException replication processing error
     * @exception SQLException JDBC, SQL error
     */
    public void replicate(BMechReader bMechReader)
            throws ReplicationException, SQLException {
        if (!updateComponents(bMechReader)) {
            Connection connection=null;
            try {
                BMechDSBindSpec dsBindSpec;
                MethodDefOperationBind behaviorBindings[];
                MethodDefOperationBind behaviorBindingsEntry;
                String bDefDBID;
                String bDefPID;
                String bMechDBID;
                String bMechPID;
                String bMechLabel;
                String dsBindingKeyDBID;
                String methodDBID;
                String ordinality_flag;
                String cardinality;
                String[] parmDomainValues;
                String parmRequired;

                connection = m_pool.getConnection();
                connection.setAutoCommit(false);

                // Insert Behavior Mechanism row
                dsBindSpec = bMechReader.getServiceDSInputSpec(null);
                bDefPID = dsBindSpec.bDefPID;

                bDefDBID = m_dl.lookupBehaviorDefinitionDBID(connection, bDefPID);
                if (bDefDBID == null) {
                    throw new ReplicationException("BehaviorDefinition row doesn't "
                            + "exist for PID: " + bDefPID);
                }

                bMechPID = bMechReader.GetObjectPID();
                bMechLabel = bMechReader.GetObjectLabel();

                m_ri.insertBehaviorMechanismRow(connection, bDefDBID, bMechPID,
                        bMechLabel);

                // Insert dsBindSpec rows
                bMechDBID = m_dl.lookupBehaviorMechanismDBID(connection, bMechPID);
                if (bMechDBID == null) {
                    throw new ReplicationException("BehaviorMechanism row doesn't "
                            + "exist for PID: " + bDefPID);
                }

                for (int i=0; i<dsBindSpec.dsBindRules.length; ++i) {

                  // Convert from type boolean to type String
                  ordinality_flag =
                           dsBindSpec.dsBindRules[i].ordinality ? "true" : "false";
                  // Convert from type int to type String
                  cardinality = Integer.toString(
                      dsBindSpec.dsBindRules[i].maxNumBindings);

                  m_ri.insertDataStreamBindingSpecRow(connection,
                      bMechDBID, dsBindSpec.dsBindRules[i].bindingKeyName,
                      ordinality_flag, cardinality,
                      dsBindSpec.dsBindRules[i].bindingLabel);

                  // Insert dsMIME rows
                  dsBindingKeyDBID =
                      m_dl.lookupDataStreamBindingSpecDBID(connection,
                      bMechDBID, dsBindSpec.dsBindRules[i].bindingKeyName);
                  if (dsBindingKeyDBID == null) {
                    throw new ReplicationException(
                        "dsBindSpec row doesn't exist for "
                        + "bMechDBID: " + bMechDBID
                        + ", binding key name: "
                        + dsBindSpec.dsBindRules[i].bindingKeyName);
                  }

                  for (int j=0;
                       j<dsBindSpec.dsBindRules[i].bindingMIMETypes.length;
                       ++j) {
                    m_ri.insertDataStreamMIMERow(connection,
                        dsBindingKeyDBID,
                        dsBindSpec.dsBindRules[i].bindingMIMETypes[j]);
                  }
                }

                // Insert mechImpl rows

                behaviorBindings = bMechReader.getServiceMethodBindings(null);

                for (int i=0; i<behaviorBindings.length; ++i) {
                    behaviorBindingsEntry =
                            (MethodDefOperationBind)behaviorBindings[i];

                    if (!behaviorBindingsEntry.protocolType.equals("HTTP")) {

                      // For the time being, ignore bindings other than HTTP.
                      continue;
                    }

                    // Insert mechDefParm rows
                    methodDBID = m_dl.lookupMethodDBID(connection, bDefDBID,
                            behaviorBindingsEntry.methodName);
                    if (methodDBID == null) {
                        throw new ReplicationException("Method row doesn't "
                               + "exist for method name: "
                               + behaviorBindingsEntry.methodName);
                    }
                    for (int j=0; j<behaviorBindings[i].methodParms.length; j++)
                    {
                      MethodParmDef[] methodParmDefs =
                          new MethodParmDef[behaviorBindings[i].methodParms.length];
                      methodParmDefs = behaviorBindings[i].methodParms;
                      //if (methodParmDefs[j].parmType.equalsIgnoreCase("fedora:defaultInputType"))
                      if (methodParmDefs[j].parmType.equalsIgnoreCase(MethodParmDef.DEFAULT_INPUT))
                      {
                      parmRequired =
                               methodParmDefs[j].parmRequired ? "true" : "false";
                      parmDomainValues = methodParmDefs[j].parmDomainValues;
                      StringBuffer sb = new StringBuffer();
                      if (parmDomainValues != null && parmDomainValues.length > 0)
                      {
                        for (int k=0; k<parmDomainValues.length; k++)
                        {
                          if (k < parmDomainValues.length-1)
                          {
                            sb.append(parmDomainValues[k]+",");
                          } else
                          {
                            sb.append(parmDomainValues[k]);
                          }
                      }
                      } else
                      {
                        if (sb.length() == 0) sb.append("null");
                      }

                      m_ri.insertMechDefaultMethodParmRow(connection, methodDBID, bMechDBID,
                              methodParmDefs[j].parmName,
                              methodParmDefs[j].parmDefaultValue,
                              sb.toString(),
                              parmRequired,
                              methodParmDefs[j].parmLabel,
                              methodParmDefs[j].parmType);
                      }
                    }
                    for (int j=0; j<dsBindSpec.dsBindRules.length; ++j) {
                        dsBindingKeyDBID =
                                m_dl.lookupDataStreamBindingSpecDBID(connection,
                                bMechDBID,
                                dsBindSpec.dsBindRules[j].bindingKeyName);
                        if (dsBindingKeyDBID == null) {
                                throw new ReplicationException(
                                        "dsBindSpec "
                                        + "row doesn't exist for bMechDBID: "
                                        + bMechDBID + ", binding key name: "
                                        + dsBindSpec.dsBindRules[j].bindingKeyName);
                        }
                        for (int k=0; k<behaviorBindingsEntry.dsBindingKeys.length;
                             k++)
                        {
                          // A row is added to the mechImpl table for each
                          // method with a different BindingKeyName. In cases where
                          // a single method may have multiple binding keys,
                          // multiple rows are added for each different
                          // BindingKeyName for that method.
                          if (behaviorBindingsEntry.dsBindingKeys[k].
                              equalsIgnoreCase(
                              dsBindSpec.dsBindRules[j].bindingKeyName))
                          {
                            m_ri.insertMechanismImplRow(connection, bMechDBID,
                                bDefDBID, methodDBID, dsBindingKeyDBID,
                                "http", "text/html",
                                behaviorBindingsEntry.serviceBindingAddress,
                                behaviorBindingsEntry.operationLocation, "1");
                          }
                        }
                    }
                }
                connection.commit();
            } catch (ReplicationException re) {
                re.printStackTrace();
                throw re;
            } catch (ServerException se) {
                se.printStackTrace();
                throw new ReplicationException(
                        "Replication exception caused by ServerException - "
                        + se.getMessage());
            } finally {
                if (connection!=null) {
                    try {
                        connection.rollback();
                    } catch (Throwable th) {
                        logWarning("While rolling back: " +  th.getClass().getName() + ": " + th.getMessage());
                    } finally {
                        connection.setAutoCommit(true);
                        m_pool.free(connection);
                    }
                }
            }
        }
    }

    /**
     * Replicates a Fedora data object.
     *
     * @param doReader data object reader
     * @exception ReplicationException replication processing error
     * @exception SQLException JDBC, SQL error
     */
    public void replicate(DOReader doReader)
            throws ReplicationException, SQLException {
        if (!updateComponents(doReader)) {
            Connection connection=null;
            try {
                DSBindingMapAugmented[] allBindingMaps;
                Disseminator disseminators[];
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

                connection = m_pool.getConnection();
                connection.setAutoCommit(false);
                // Insert Digital Object row
                doPID = doReader.GetObjectPID();
                doLabel = doReader.GetObjectLabel();

                m_ri.insertDigitalObjectRow(connection, doPID, doLabel);

                doDBID = m_dl.lookupDigitalObjectDBID(connection, doPID);
                if (doDBID == null) {
                    throw new ReplicationException("do row doesn't "
                            + "exist for PID: " + doPID);
                }

                disseminators = doReader.GetDisseminators(null);
                for (int i=0; i<disseminators.length; ++i) {
                    bDefDBID = m_dl.lookupBehaviorDefinitionDBID(connection,
                            disseminators[i].bDefID);
                    if (bDefDBID == null) {
                        throw new ReplicationException("BehaviorDefinition row "
                                + "doesn't exist for PID: "
                                + disseminators[i].bDefID);
                    }
                    bMechDBID = m_dl.lookupBehaviorMechanismDBID(connection,
                            disseminators[i].bMechID);
                    if (bMechDBID == null) {
                        throw new ReplicationException("BehaviorMechanism row "
                                + "doesn't exist for PID: "
                                + disseminators[i].bMechID);
                    }
                    // Insert Disseminator row if it doesn't exist.
                    dissDBID = m_dl.lookupDisseminatorDBID(connection, bDefDBID,
                            bMechDBID, disseminators[i].dissID);
                    if (dissDBID == null) {
                        // Disseminator row doesn't exist, add it.
                        m_ri.insertDisseminatorRow(connection, bDefDBID, bMechDBID,
                        disseminators[i].dissID, disseminators[i].dissLabel);
                        dissDBID = m_dl.lookupDisseminatorDBID(connection, bDefDBID,
                                bMechDBID, disseminators[i].dissID);
                        if (dissDBID == null) {
                            throw new ReplicationException("diss row "
                                    + "doesn't exist for PID: "
                                    + disseminators[i].dissID);
                        }
                    }
                    // Insert doDissAssoc row
                    m_ri.insertDigitalObjectDissAssocRow(connection, doDBID,
                            dissDBID);
                }
//                try{
                    allBindingMaps = doReader.GetDSBindingMaps(null);
                    for (int i=0; i<allBindingMaps.length; ++i) {
                        bMechDBID = m_dl.lookupBehaviorMechanismDBID(connection,
                                allBindingMaps[i].dsBindMechanismPID);
                        if (bMechDBID == null) {
                            throw new ReplicationException("BehaviorMechanism row "
                                    + "doesn't exist for PID: "
                                    + allBindingMaps[i].dsBindMechanismPID);
                        }

                        // Insert dsBindMap row if it doesn't exist.
                        bindingMapDBID = m_dl.lookupDataStreamBindingMapDBID(connection,
                                bMechDBID, allBindingMaps[i].dsBindMapID);
                        if (bindingMapDBID == null) {
                            // DataStreamBinding row doesn't exist, add it.
                            m_ri.insertDataStreamBindingMapRow(connection, bMechDBID,
                            allBindingMaps[i].dsBindMapID,
                            allBindingMaps[i].dsBindMapLabel);
                            bindingMapDBID = m_dl.lookupDataStreamBindingMapDBID(
                                    connection,bMechDBID,allBindingMaps[i].dsBindMapID);
                            if (bindingMapDBID == null) {
                                throw new ReplicationException(
                                        "lookupdsBindMapDBID row "
                                        + "doesn't exist for bMechDBID: " + bMechDBID
                                        + ", dsBindingMapID: "
                                        + allBindingMaps[i].dsBindMapID);
                            }
                        }

                        for (int j=0; j<allBindingMaps[i].dsBindingsAugmented.length;
                                ++j) {
                            dsBindingKeyDBID = m_dl.lookupDataStreamBindingSpecDBID(
                                    connection, bMechDBID,
                                    allBindingMaps[i].dsBindingsAugmented[j].
                                    bindKeyName);
                            if (dsBindingKeyDBID == null) {
                                throw new ReplicationException(
                                        "lookupDataStreamBindingDBID row doesn't "
                                        + "exist for bMechDBID: " + bMechDBID
                                        + ", bindKeyName: " + allBindingMaps[i].
                                        dsBindingsAugmented[j].bindKeyName + "i=" + i
                                        + " j=" + j);
                            }

                            // Insert DataStreamBinding row
                            m_ri.insertDataStreamBindingRow(connection, doDBID,
                                    dsBindingKeyDBID,
                                    bindingMapDBID,
                                    allBindingMaps[i].dsBindingsAugmented[j].seqNo,
                                    allBindingMaps[i].dsBindingsAugmented[j].
                                    datastreamID,
                                    allBindingMaps[i].dsBindingsAugmented[j].DSLabel,
                                    allBindingMaps[i].dsBindingsAugmented[j].DSMIME,
                                    allBindingMaps[i].dsBindingsAugmented[j].DSLocation,
                                    allBindingMaps[i].dsBindingsAugmented[j].DSControlGrp,
                                    allBindingMaps[i].dsBindingsAugmented[j].DSVersionID,
                                    "1");

                        }
                    }
//                    } catch(Exception e)
//                    {
//                      e.printStackTrace();
//                    }
                    connection.commit();
                } catch (ReplicationException re) {
                    re.printStackTrace();
                    throw new ReplicationException("An error has occurred during "
                        + "Replication. The error was \" " + re.getClass().getName()
                        + " \". The cause was \" " + re.getMessage() + " \"");
                } catch (ServerException se) {
                    se.printStackTrace();
                    throw new ReplicationException("An error has occurred during "
                        + "Replication. The error was \" " + se.getClass().getName()
                        + " \". The cause was \" " + se.getMessage());
                } finally {
                    if (connection!=null) {
                        try {
                            connection.rollback();
                        } catch (Throwable th) {
                            logWarning("While rolling back: " +  th.getClass().getName() + ": " + th.getMessage());
                        } finally {
                            connection.setAutoCommit(true);
                            m_pool.free(connection);
                        }
                    }
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
     * Gets a string suitable for a SQL WHERE clause, of the form
     * <b>x=y1 or x=y2 or x=y3</b>...etc, where x is the value from the
     * column, and y1 is composed of the integer values from the given set.
     * <p></p>
     * If the set doesn't contain any items, returns a condition that
     * always evaluates to false, <b>1=2</b>.
     */
    private String inIntegerSetWhereConditionString(String column,
            Set integers) {
        StringBuffer out=new StringBuffer();
        Iterator iter=integers.iterator();
        int n=0;
        while (iter.hasNext()) {
            if (n>0) {
                out.append(" OR ");
            }
            out.append(column);
            out.append('=');
            int i=((Integer) iter.next()).intValue();
            out.append(i);
            n++;
        }
        if (n>0) {
            return out.toString();
        } else {
            return "1=2";
        }
    }

    /**
     * Deletes all rows pertinent to the given behavior definition object,
     * if they exist.
     * <p></p>
     * Pseudocode:
     * <ul><pre>
     * $bDefDbID=SELECT bDefDbID FROM bDef WHERE bDefPID=$PID
     * DELETE FROM bDef,method,parm
     * WHERE bDefDbID=$bDefDbID
     * </pre></ul>
     *
     * @throws SQLException If something totally unexpected happened.
     */
    private void deleteBehaviorDefinition(Connection connection, String pid)
            throws SQLException {
        logFinest("Entered DefaultDOReplicator.deleteBehaviorDefinition");
        Statement st=null;
        ResultSet results=null;
        try {
		     st=connection.createStatement();
            //
            // READ
            //
            logFinest("Checking BehaviorDefinition table for " + pid + "...");
            results=logAndExecuteQuery(st, "SELECT bDefDbID FROM "
                    + "bDef WHERE bDefPID='" + pid + "'");
            if (!results.next()) {
                 // must not be a bdef...exit early
                 logFinest(pid + " wasn't found in BehaviorDefinition table..."
                         + "skipping deletion as such.");
                 return;
            }
            int dbid=results.getInt("bDefDbID");
            logFinest(pid + " was found in BehaviorDefinition table (DBID="
                    + dbid + ")");
            //
            // WRITE
            //
            int rowCount;
            logFinest("Attempting row deletion from BehaviorDefinition "
                    + "table...");
            rowCount=logAndExecuteUpdate(st, "DELETE FROM bDef "
                    + "WHERE bDefDbID=" + dbid);
            logFinest("Deleted " + rowCount + " row(s).");
            logFinest("Attempting row deletion from method table...");
            rowCount=logAndExecuteUpdate(st, "DELETE FROM method WHERE "
                    + "bDefDbID=" + dbid);
            logFinest("Deleted " + rowCount + " row(s).");
            logFinest("Attempting row deletion from parm table...");
            rowCount=logAndExecuteUpdate(st, "DELETE FROM parm WHERE "
                    + "bDefDbID=" + dbid);
            logFinest("Deleted " + rowCount + " row(s).");
        } finally {
            if (results != null) results.close();
            if (st!=null) st.close();
            logFinest("Exiting DefaultDOReplicator.deleteBehaviorDefinition");
        }
    }

    /**
     * Deletes all rows pertinent to the given behavior mechanism object,
     * if they exist.
     * <p></p>
     * Pseudocode:
     * <ul><pre>
     * $bMechDbID=SELECT bMechDbID
     * FROM bMech WHERE bMechPID=$PID
     * bMech
     * @BKEYIDS=SELECT dsBindKeyDbID
     * FROM dsBindSpec
     * WHERE bMechDbID=$bMechDbID
     * dsMIME WHERE dsBindKeyDbID in @BKEYIDS
     * mechImpl
     * </pre></ul>
     *
     * @throws SQLException If something totally unexpected happened.
     */
    private void deleteBehaviorMechanism(Connection connection, String pid)
            throws SQLException {
        logFinest("Entered DefaultDOReplicator.deleteBehaviorMechanism");
        Statement st=null;
        ResultSet results=null;
        try {
		     st=connection.createStatement();
            //
            // READ
            //
            logFinest("Checking bMech table for " + pid + "...");
            //results=logAndExecuteQuery(st, "SELECT bMechDbID, SMType_DBID "
            results=logAndExecuteQuery(st, "SELECT bMechDbID "
                    + "FROM bMech WHERE bMechPID='" + pid + "'");
            if (!results.next()) {
                 // must not be a bmech...exit early
                 logFinest(pid + " wasn't found in bMech table..."
                         + "skipping deletion as such.");
                 return;
            }
            int dbid=results.getInt("bMechDbID");
            //int smtype_dbid=results.getInt("bMechDbID");
            results.close();
            logFinest(pid + " was found in bMech table (DBID="
            //        + dbid + ", SMTYPE_DBID=" + smtype_dbid + ")");
                    + dbid);
            logFinest("Getting dsBindKeyDbID(s) from dsBindSpec "
                    + "table...");
            HashSet dsBindingKeyIds=new HashSet();
            results=logAndExecuteQuery(st, "SELECT dsBindKeyDbID from "
                    + "dsBindSpec WHERE bMechDbID=" + dbid);
            while (results.next()) {
                dsBindingKeyIds.add(new Integer(
                        results.getInt("dsBindKeyDbID")));
            }
            results.close();
            logFinest("Found " + dsBindingKeyIds.size()
                    + " dsBindKeyDbID(s).");
            //
            // WRITE
            //
            int rowCount;
            logFinest("Attempting row deletion from bMech table..");
            rowCount=logAndExecuteUpdate(st, "DELETE FROM bMech "
                    + "WHERE bMechDbID=" + dbid);
            logFinest("Deleted " + rowCount + " row(s).");
            logFinest("Attempting row deletion from dsBindSpec "
                    + "table...");
            rowCount=logAndExecuteUpdate(st, "DELETE FROM "
                    + "dsBindSpec WHERE bMechDbID=" + dbid);
            logFinest("Deleted " + rowCount + " row(s).");
            logFinest("Attempting row deletion from dsMIME table...");
            rowCount=logAndExecuteUpdate(st, "DELETE FROM dsMIME WHERE "
                    + inIntegerSetWhereConditionString("dsBindKeyDbID",
                    dsBindingKeyIds));
            logFinest("Deleted " + rowCount + " row(s).");
            //logFinest("Attempting row deletion from StructMapType table...");
            //rowCount=logAndExecuteUpdate(st, "DELETE FROM StructMapType WHERE "
            //        + "SMType_DBID=" + smtype_dbid);
            //logFinest("Deleted " + rowCount + " row(s).");
            logFinest("Attempting row deletion from dsBindMap table...");
            rowCount=logAndExecuteUpdate(st, "DELETE FROM dsBindMap WHERE "
                    + "bMechDbID=" + dbid);
            logFinest("Deleted " + rowCount + " row(s).");
            logFinest("Attempting row deletion from mechImpl table...");
            rowCount=logAndExecuteUpdate(st, "DELETE FROM mechImpl WHERE "
                    + "bMechDbID=" + dbid);
            logFinest("Deleted " + rowCount + " row(s).");
            logFinest("Attempting row deletion from mechDefParm table...");
            rowCount=logAndExecuteUpdate(st, "DELETE FROM mechDefParm "
                + "WHERE bMechDbID=" + dbid);
            logFinest("Deleted " + rowCount + " row(s).");

        } finally {
            if (results != null) results.close();
            if (st!=null)st.close();
            logFinest("Exiting DefaultDOReplicator.deleteBehaviorMechanism");
        }
    }

    /**
     * Deletes all rows pertinent to the given digital object (treated as a
     * regular data object) if they exist.
     * <p></p>
     * Pseudocode:
     * <ul><pre>
     * $doDbID=SELECT doDbID FROM do where doPID=$PID
     * @DISSIDS=SELECT dissDbID
     * FROM doDissAssoc WHERE doDbID=$doDbID
     * @BMAPIDS=SELECT dsBindMapDbID
     * FROM dsBind WHERE doDbID=$doDbID
     * do
     * doDissAssoc where $doDbID=doDbID
     * dsBind WHERE $doDbID=doDbID
     * diss WHERE dissDbID in @DISSIDS
     * dsBindMap WHERE dsBindMapDbID in @BMAPIDS
     * </pre></ul>
     *
     * @throws SQLException If something totally unexpected happened.
     */
    private void deleteDigitalObject(Connection connection, String pid)
            throws SQLException {
        logFinest("Entered DefaultDOReplicator.deleteDigitalObject");
        Statement st=null;
        ResultSet results=null;
        try {
		     st=connection.createStatement();
            //
            // READ
            //
            logFinest("Checking do table for " + pid + "...");
            results=logAndExecuteQuery(st, "SELECT doDbID FROM "
                    + "do WHERE doPID='" + pid + "'");
            if (!results.next()) {
                 // must not be a digitalobject...exit early
                 logFinest(pid + " wasn't found in do table..."
                         + "skipping deletion as such.");
                 return;
            }
            int dbid=results.getInt("doDbID");
            results.close();
            logFinest(pid + " was found in do table (DBID="
                    + dbid + ")");

            logFinest("Getting dissDbID(s) from doDissAssoc "
                    + "table...");
            HashSet dissIds=new HashSet();
            HashSet dissIdsInUse = new HashSet();
            results=logAndExecuteQuery(st, "SELECT dissDbID from "
                    + "doDissAssoc WHERE doDbID=" + dbid);
            while (results.next()) {
                dissIds.add(new Integer(results.getInt("dissDbID")));
            }
            results.close();
            HashSet bMechIds = new HashSet();
            logFinest("Getting dissDbID(s) from doDissAssoc "
                    + "table unique to this object...");
            Iterator iterator = dissIds.iterator();
            while (iterator.hasNext())
            {
              Integer id = (Integer)iterator.next();
              logFinest("Getting occurrences of dissDbID(s) in "
                    + "doDissAssoc table...");
              results=logAndExecuteQuery(st, "SELECT COUNT(*) from "
                    + "doDissAssoc WHERE dissDbID=" + id);
              while (results.next())
              {
                Integer i1 = new Integer(results.getInt("COUNT(*)"));
                if ( i1.intValue() > 1 )
                {
                  //dissIds.remove(id);
                  // A dissDbID that occurs more than once indicates that the
                  // disseminator is used by other objects. In this case, we
                  // do not want to remove the disseminator from the diss
                  // table so keep track of this dissDbID.
                  dissIdsInUse.add(id);
                } else
                {
                  ResultSet rs = null;
                  logFinest("Getting associated bMechDbID(s) that are unique "
                        + "for this object in diss table...");
                  rs=logAndExecuteQuery(st, "SELECT bMechDbID from "
                    + "diss WHERE dissDbID=" + id);
                  while (rs.next())
                  {
                    bMechIds.add(new Integer(rs.getInt("bMechDbID")));
                  }
                  rs.close();
                }
              }
              results.close();

            }
            iterator = dissIdsInUse.iterator();

            // Remove disseminator ids of those disseminators that were in
            // use by one or more other objects to prevent them from being
            // removed from the disseminator table in following code section.
            while (iterator.hasNext() )
            {
              Integer id = (Integer)iterator.next();
              dissIds.remove(id);
            }

            logFinest("Found " + dissIds.size() + " dissDbID(s).");
            logFinest("Getting bMechDbIDs matching dsBindMapDbID(s) from dsBind "
                    + "table...");
            logFinest("Getting dsBindMapDbID(s) from dsBind "
                    + "table...");
            //HashSet bmapIds=new HashSet();
            //results=logAndExecuteQuery(st, "SELECT dsBindMapDbID FROM "
            //        + "dsBind WHERE doDbID=" + dbid);
            //while (results.next()) {
            //    bmapIds.add(new Integer(results.getInt("dsBindMapDbID")));
            //}
            //results.close();
            //logFinest("Found " + bmapIds.size() + " dsBindMapDbID(s).");
            //
            // WRITE
            //
            int rowCount;
            logFinest("Attempting row deletion from do table...");
            rowCount=logAndExecuteUpdate(st, "DELETE FROM do "
                    + "WHERE doDbID=" + dbid);
            logFinest("Deleted " + rowCount + " row(s).");
            logFinest("Attempting row deletion from doDissAssoc "
                    + "table...");
            rowCount=logAndExecuteUpdate(st, "DELETE FROM "
                    + "doDissAssoc WHERE doDbID=" + dbid);
            logFinest("Deleted " + rowCount + " row(s).");
            logFinest("Attempting row deletion from dsBind table..");
            rowCount=logAndExecuteUpdate(st, "DELETE FROM dsBind "
                    + "WHERE doDbID=" + dbid);
            logFinest("Deleted " + rowCount + " row(s).");
            logFinest("Attempting row deletion from diss table...");
            rowCount=logAndExecuteUpdate(st, "DELETE FROM diss WHERE "
                    + inIntegerSetWhereConditionString("dissDbID", dissIds));
            logFinest("Deleted " + rowCount + " row(s).");
            logFinest("Attempting row deletion from dsBindMap "
                    + "table...");
            rowCount=logAndExecuteUpdate(st, "DELETE FROM dsBindMap "
                    + "WHERE " + inIntegerSetWhereConditionString(
            //        "dsBindMapDbID", bmapIds));
                      "bMechDbID", bMechIds));
            logFinest("Deleted " + rowCount + " row(s).");
        } finally {
            if (results != null) results.close();
            if (st!=null) st.close();
            logFinest("Exiting DefaultDOReplicator.deleteDigitalObject");
        }
    }

    /**
     * Removes a digital object from the dissemination database.
     * <p></p>
     * If the object is a behavior definition or mechanism, it's deleted
     * as such, and then an attempt is made to delete it as a regular
     * digital object as well.
     * <p></p>
     * Note that this does not do cascading check object dependencies at
     * all.  It is expected at this point that when this is called, any
     * referencial integrity issues have been ironed out or checked as
     * appropriate.
     * <p></p>
     * All deletions happen in a transaction.  If any database errors occur,
     * the change is rolled back.
     *
     * @param pid The pid of the object to delete.
     * @throws ReplicationException If the request couldn't be fulfilled for
     *         any reason.
     */
    public void delete(String pid)
            throws ReplicationException {
        logFinest("Entered DefaultDOReplicator.delete");
        Connection connection=null;
        try {
            connection = m_pool.getConnection();
            connection.setAutoCommit(false);
            deleteBehaviorDefinition(connection, pid);
            deleteBehaviorMechanism(connection, pid);
            deleteDigitalObject(connection, pid);
            connection.commit();
        } catch (SQLException sqle) {
            throw new ReplicationException("Error while replicator was trying "
                    + "to delete " + pid + ". " + sqle.getMessage());
        } finally {
            if (connection!=null) {
                try {
                    connection.rollback();
                    connection.setAutoCommit(true);
                    m_pool.free(connection);
                } catch (SQLException sqle) {}
            }
            logFinest("Exiting DefaultDOReplicator.delete");
        }
    }

}

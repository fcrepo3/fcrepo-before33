package fedora.server.storage.replication;

import java.util.*;
import java.sql.*;
import java.io.*;

import fedora.server.errors.*;
import fedora.server.errors.*;
import fedora.server.storage.*;
import fedora.server.storage.types.*;

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
                    "Error getting default pool: " + se.getMessage(), getRole());
        }
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
        Connection connection=null;
        try {
            MethodDef behaviorDefs[];
            String bDefDBID;
            String bDefPID;
            String bDefLabel;
            connection = m_pool.getConnection();
            connection.setAutoCommit(false);
            // Insert Behavior Definition row
            bDefPID = bDefReader.GetObjectPID();
            bDefLabel = bDefReader.GetObjectLabel();
            m_ri.insertBehaviorDefinitionRow(connection, bDefPID, bDefLabel);
            // Insert Method rows
            bDefDBID = m_dl.lookupBehaviorDefinitionDBID(connection, bDefPID);
            if (bDefDBID == null) {
                throw new ReplicationException("BehaviorDefinition row doesn't exist for PID: " + bDefPID);
            }
            behaviorDefs = bDefReader.GetBehaviorMethods(null);
            for (int i=0; i<behaviorDefs.length; ++i) {
                m_ri.insertMethodRow(connection, bDefDBID, behaviorDefs[i].methodName, behaviorDefs[i].methodLabel);
            }
            connection.commit();
        } catch (ReplicationException re) {
            throw re;
        } catch (ServerException se) {
            throw new ReplicationException("Replication exception caused by ServerException - " + se.getMessage());
        } finally {
            if (connection!=null) {
                connection.rollback();
                connection.setAutoCommit(true);
                m_pool.free(connection);
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
        Connection connection=null;
        try {
            BMechDSBindSpec dsBindSpec;
            MethodDef behaviorBindings[];
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

            connection = m_pool.getConnection();
            connection.setAutoCommit(false);

            // Insert Behavior Mechanism row
            dsBindSpec = bMechReader.GetDSBindingSpec(null);
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

            // Insert DataStreamBindingSpec rows
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
                cardinality = Integer.toString(dsBindSpec.dsBindRules[i].maxNumBindings);
    
                m_ri.insertDataStreamBindingSpecRow(connection, 
                        bMechDBID, dsBindSpec.dsBindRules[i].bindingKeyName, 
                        ordinality_flag, cardinality,
                        dsBindSpec.dsBindRules[i].bindingLabel);

                // Insert DataStreamMIME rows
                dsBindingKeyDBID = 
                        m_dl.lookupDataStreamBindingSpecDBID(connection, bMechDBID, 
                        dsBindSpec.dsBindRules[i].bindingKeyName);
                if (dsBindingKeyDBID == null) {
                        throw new ReplicationException(
                            "DataStreamBindingSpec row doesn't exist for bMechDBID: "
                            + bMechDBID + ", binding key name: " + 
                            dsBindSpec.dsBindRules[i].bindingKeyName);
                }

                for (int j=0; j<dsBindSpec.dsBindRules[i].bindingMIMETypes.length; 
                        ++j) {
                    m_ri.insertDataStreamMIMERow(connection, 
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

                methodDBID = m_dl.lookupMethodDBID(connection, bDefDBID, 
                        behaviorBindingsEntry.methodName);
                if (methodDBID == null) {
                    throw new ReplicationException("Method row doesn't exist for "
                           + "method name: " + behaviorBindingsEntry.methodName);
                }
            
                for (int j=0; j<dsBindSpec.dsBindRules.length; ++j) {
                    dsBindingKeyDBID =
                            m_dl.lookupDataStreamBindingSpecDBID(connection, bMechDBID, 
                            dsBindSpec.dsBindRules[j].bindingKeyName);
                    if (dsBindingKeyDBID == null) {
                            throw new ReplicationException("DataStreamBindingSpec "
                                    + "row doesn't exist for bMechDBID: "
                                    + bMechDBID + ", binding key name: " 
                                    + dsBindSpec.dsBindRules[j].bindingKeyName);
                    }
                    m_ri.insertMechanismImplRow(connection, bMechDBID, bDefDBID, 
                            methodDBID, dsBindingKeyDBID, "http", "text/html", 
                            behaviorBindingsEntry.serviceBindingAddress, 
                            behaviorBindingsEntry.operationLocation, "1");
                }
            }
            connection.commit();
        } catch (ReplicationException re) {
            throw re;
        } catch (ServerException se) {
            throw new ReplicationException("Replication exception caused by ServerException - " + se.getMessage());
        } finally {
            if (connection!=null) {
                connection.rollback();
                connection.setAutoCommit(true);
                m_pool.free(connection);
            }
        }
    }

    /**
    * 
    * Replicates a Fedora data object.
    *
    * @param doReader data object reader
    * @exception ReplicationException replication processing error
    * @exception SQLException JDBC, SQL error
    */
    public void replicate(DOReader doReader) throws ReplicationException, SQLException {
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
                throw new ReplicationException("DigitalObject row doesn't exist for PID: " + doPID);
            }

            disseminators = doReader.GetDisseminators(null);
            for (int i=0; i<disseminators.length; ++i) {
                bDefDBID = m_dl.lookupBehaviorDefinitionDBID(connection, disseminators[i].bDefID);
                if (bDefDBID == null) {
                    throw new ReplicationException("BehaviorDefinition row doesn't exist for PID: " + disseminators[i].bDefID);
                }
                bMechDBID = m_dl.lookupBehaviorMechanismDBID(connection,  disseminators[i].bMechID);
                if (bMechDBID == null) {
                    throw new ReplicationException("BehaviorMechanism row doesn't exist for PID: " + disseminators[i].bMechID);
                }

                // Insert Disseminator row if it doesn't exist.
                dissDBID = m_dl.lookupDisseminatorDBID(connection, bDefDBID, bMechDBID, disseminators[i].dissID);
                if (dissDBID == null) {
                    // Disseminator row doesn't exist, add it.
                    m_ri.insertDisseminatorRow(connection, bDefDBID, bMechDBID, 
                    disseminators[i].dissID, disseminators[i].dissLabel);
                    dissDBID = m_dl.lookupDisseminatorDBID(connection, bDefDBID, bMechDBID, disseminators[i].dissID);
                    if (dissDBID == null) {
                        throw new ReplicationException("Disseminator row doesn't exist for PID: " + disseminators[i].dissID);
                    }
                }

                // Insert DigitalObjectDissAssoc row
                m_ri.insertDigitalObjectDissAssocRow(connection, doDBID, dissDBID);
            }

            allBindingMaps = doReader.GetDSBindingMaps(null);
            for (int i=0; i<allBindingMaps.length; ++i) {
                bMechDBID = m_dl.lookupBehaviorMechanismDBID(connection,  allBindingMaps[i].dsBindMechanismPID);
                if (bMechDBID == null) {
                    throw new ReplicationException("BehaviorMechanism row doesn't exist for PID: " + allBindingMaps[i].dsBindMechanismPID);
                }
     
                // Insert DataStreamBindingMap row if it doesn't exist.
                bindingMapDBID = m_dl.lookupDataStreamBindingMapDBID(connection, bMechDBID, 
                        allBindingMaps[i].dsBindMapID);
                if (bindingMapDBID == null) {
                    // DataStreamBinding row doesn't exist, add it.
                    m_ri.insertDataStreamBindingMapRow(connection, bMechDBID, 
                    allBindingMaps[i].dsBindMapID, 
                    allBindingMaps[i].dsBindMapLabel);
                    bindingMapDBID = m_dl.lookupDataStreamBindingMapDBID(connection, bMechDBID, allBindingMaps[i].dsBindMapID);
                    if (bindingMapDBID == null) {
                        throw new ReplicationException("lookupDataStreamBindingMapDBID row doesn't exist for bMechDBID: " + bMechDBID + ", dsBindingMapID: " + allBindingMaps[i].dsBindMapID);
                    }
                }
 
                for (int j=0; j<allBindingMaps[i].dsBindingsAugmented.length; ++j) {
                    dsBindingKeyDBID = m_dl.lookupDataStreamBindingSpecDBID(connection, bMechDBID, allBindingMaps[i].dsBindingsAugmented[j].bindKeyName);
                    if (dsBindingKeyDBID == null) {
                        throw new ReplicationException("lookupDataStreamBindingDBID row doesn't exist for bMechDBID: " + bMechDBID + ", bindKeyName: " + allBindingMaps[i].dsBindingsAugmented[j].bindKeyName + "i=" + i + " j=" + j);
                    }
                    // Insert DataStreamBinding row
                    m_ri.insertDataStreamBindingRow(connection, doDBID, dsBindingKeyDBID, 
                            bindingMapDBID, 
                            allBindingMaps[i].dsBindingsAugmented[j].seqNo, 
                            allBindingMaps[i].dsBindingsAugmented[j].datastreamID,
                            allBindingMaps[i].dsBindingsAugmented[j].bindLabel,
                            allBindingMaps[i].dsBindingsAugmented[j].DSMIME,
                            allBindingMaps[i].dsBindingsAugmented[j].DSLocation, "1");
 
                }
            }
            connection.commit();
        } catch (ReplicationException re) {
            throw re;
        } catch (ServerException se) {
            throw new ReplicationException("Replication exception caused by ServerException - " + se.getMessage());
        } finally {
            if (connection!=null) {
                connection.rollback();
                connection.setAutoCommit(true);
                m_pool.free(connection);
            }
        }
    }
    
    public void delete(String pid) {
        
    }

}

package fedora.server.storage;

import fedora.server.Context;
import fedora.server.Module;
import fedora.server.ReadOnlyContext;
import fedora.server.Server;
import fedora.server.errors.ConnectionPoolNotFoundException;
import fedora.server.errors.ModuleInitializationException;
import fedora.server.errors.ObjectNotFoundException;
import fedora.server.errors.StorageException;
import fedora.server.errors.StorageDeviceException;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.SQLException;

/**
 * Provides access to digital object readers and writers.
 * <p></p>
 * Object states are: A,L,R,N,W,C,D, documented at:
 * http://www.fedora.info/documents/master-spec.html#_Toc11835716
 * <p></p>
 * object_state_registry<br>
 * DO_PID varchar(255) NOT NULL, State char(1) NOT NULL, LockingUser varchar(16)
 * <p></p>
 * This impl gets a connection from the connection pool and keeps a hold on
 * it until the Module is shut down.  Calls to query the table are synchronized
 * because they share the same connection.
 *
 * @author cwilper@cs.cornell.edu
 */
public class DefaultDOManager 
        extends DOManager {
        
    private String m_osrPoolName;
    private String m_osrTableName;
    private ConnectionPool m_connectionPool;
    private Connection m_connection;
        
    /**
     * Creates a new DefaultDOManager.
     */
    public DefaultDOManager(Map moduleParameters, Server server, String role)
            throws ModuleInitializationException {
        super(moduleParameters, server, role);
    }

    /**
     * Ensures that object_state_registry has been specified correctly.
     *
     * @throws ModuleInitializationException If it hasn't.
     */
    public void initModule()
            throws ModuleInitializationException {
        // object_state_registry parameter (required)
        String osr=getParameter("object_state_registry");
        if (osr==null) {
            throw new ModuleInitializationException("Parameter object_state_registry unspecified.", getRole());
        }
        int hashPos=osr.indexOf("#");
        if (hashPos==-1) {
            throw new ModuleInitializationException("Parameter object_state_registry must be in the form poolName#tableName... the hash mark is required.", getRole());
        }
        m_osrPoolName=osr.substring(0, hashPos);
        m_osrTableName=osr.substring(hashPos+1);
        getServer().logConfig("object_state_registry, poolName=" + m_osrPoolName + ", tableName=" + m_osrTableName);
        if (m_osrPoolName.length()==0) {
            getServer().logConfig("Parameter object_state_registry provides no poolName... will attempt to use default pool.");
        }
        if (m_osrTableName.length()==0) {
            throw new ModuleInitializationException("Parameter object_state_registry must be in the form poolName#tableName, where tableName is not empty.", getRole());
        }
        // TODO: read and set default_context_XXX variables from config
    }

    public void postInitModule()
            throws ModuleInitializationException {
        ConnectionPoolManager cpm=(ConnectionPoolManager) getServer().getModule("fedora.server.storage.ConnectionPoolManager");
        try {
            if (m_osrPoolName.length()==0) {
                m_connectionPool=cpm.getPool();
            } else {
                m_connectionPool=cpm.getPool(m_osrPoolName);
            }
        } catch (ConnectionPoolNotFoundException cpnfe) {
            throw new ModuleInitializationException("Couldn't get required connection pool...wasn't found", getRole());
        }
        try {
            m_connection=m_connectionPool.getConnection();
        } catch (SQLException sqle) {
            throw new ModuleInitializationException("Couldn't get required connection: " + sqle.getMessage(), getRole());
        }
        int numRows=0;
        try {
            // this simultaneously counts rows and checks if the table's there...
            // it would be better if it used database metadata to check for the
            // table instead... so these operations would be separate, but this works
            // for now...
            Statement selectCount=m_connection.createStatement();
            ResultSet results=selectCount.executeQuery("SELECT DO_PID FROM " 
                    + m_osrTableName);
            while (results.next()) {
                numRows++;
            }
            results.close();
        } catch (SQLException sqle) {
            getServer().logConfig("object_state_registry table count(*) query failed (message was: " + sqle.getMessage() + ")... attempting to create table because this might be the first run of this module.");
            try {
                m_connection.setAutoCommit(false);
                Statement createTable=m_connection.createStatement();
                createTable.executeUpdate("CREATE TABLE " + m_osrTableName 
                        + " (DO_PID varchar(255) NOT NULL, State char(1) NOT NULL, LockingUser varchar(16))");
                m_connection.commit();
                getServer().logConfig("Table created successfully.");
            } catch (java.sql.SQLException sqle2) {
                throw new ModuleInitializationException("Table creation failed:" + sqle2.getMessage(), getRole());
            }
        }
        getServer().logConfig("object_state_registry table (" + m_osrTableName + ") has " + numRows + " rows.");
        try {
            // set default commit behavior--auto
            m_connection.setAutoCommit(true);
        } catch (SQLException sqle) {
            throw new ModuleInitializationException("Couldn't set autocommit=true on the object_state_registry db connection.", getRole());
        }
    }
    
    public String[] getRequiredModuleRoles() {
        return new String[] {"fedora.server.storage.ConnectionPoolManager"};
    }

    /** pid will always be non-null, context will always be non-null */
    protected DOReader getReaderForContext(String pid, ReadOnlyContext context) {
        return null;
    }

    /** pid may be null, context will always be non-null */
    protected DOWriter getWriterForContext(String pid, ReadOnlyContext context) {
        if (pid==null) {
            
        }
        return null;
    }
    
    protected String[] getObjectPIDsForContext(String state, ReadOnlyContext context) 
            throws StorageDeviceException {
        String wherePredicate;
        if (state==null) {
            wherePredicate="";
        } else {
            wherePredicate=" WHERE State='" + state + "'";
        }
        try {
            synchronized (m_connection) {
                Statement selectCount=m_connection.createStatement();
                ResultSet results=selectCount.executeQuery("SELECT DO_PID FROM " 
                        + m_osrTableName + wherePredicate);
                ArrayList pids=new ArrayList();
                while (results.next()) {
                    pids.add(results.getString(1));
                }
                results.close();
                String[] out=new String[pids.size()];
                Iterator pidIter=pids.iterator();
                int i=0;
                while (pidIter.hasNext()) {
                    out[i++]=(String) pidIter.next();
                }
                return out;
            }
        } catch (SQLException sqle) {
            throw new StorageDeviceException("Problem querying db for object ids: " + sqle.getMessage());
        }
    }

    // meant to be called by DOReader and DOWriter instances... this
    // is just an artifact of how this DOManager is written... and
    // how DigitalObject has no current facility for holding this info
    // at the moment
    /**
     * Gets the name of the user who has locked the given object.
     * 
     * @param return The name of the user, or null if the object doesn't exist
     *               or isn't locked.
     */
    public String getLockingUser(String pid) 
            throws StorageDeviceException {
        String wherePredicate=" WHERE State='L' AND DO_PID='" + pid + "'";
        try {
            synchronized (m_connection) {
                Statement selectCount=m_connection.createStatement();
                ResultSet results=selectCount.executeQuery("SELECT LockingUser FROM " 
                        + m_osrTableName + wherePredicate);
                String out=null;
                while (results.next()) {
                    out=results.getString(1);
                }
                results.close();
                return out;
            }
        } catch (SQLException sqle) {
            throw new StorageDeviceException("Problem querying db for locking user: " + sqle.getMessage());
        }
    }

    // called internally when obj created, and by DOWriters
    public void updateObject(String pid, String state, String lockingUser) {
    }
    
    // called by a DOWriter
    public void dropObject(String pid) {
    }
    
    public void shutdownModule() {
        if (m_connectionPool!=null) {
            m_connectionPool.free(m_connection);
        }
    }
    
}
package fedora.server.storage;

import java.io.InputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.SQLException;

import fedora.server.Context;
import fedora.server.Module;
import fedora.server.ReadOnlyContext;
import fedora.server.Server;
import fedora.server.errors.ConnectionPoolNotFoundException;
import fedora.server.errors.InconsistentTableSpecException;
import fedora.server.errors.InvalidContextException;
import fedora.server.errors.ModuleInitializationException;
import fedora.server.errors.ObjectNotFoundException;
import fedora.server.errors.ServerException;
import fedora.server.errors.StorageException;
import fedora.server.errors.StorageDeviceException;
import fedora.server.utilities.TableCreatingConnection;
import fedora.server.utilities.TableSpec;

/**
 * Provides access to digital object readers and writers.
 *
 * @author cwilper@cs.cornell.edu
 */
public class DefaultDOManager 
        extends Module implements DOManager {
        
    private String m_storagePool;
    private String m_storageFormat;
    private String m_storageCharacterEncoding;
    
    private ConnectionPool m_connectionPool;
    private Connection m_connection;
    
    public static String DEFAULT_STATE="L";
        
    /**
     * Creates a new DefaultDOManager.
     */
    public DefaultDOManager(Map moduleParameters, Server server, String role)
            throws ModuleInitializationException {
        super(moduleParameters, server, role);
    }

    /**
     * Gets initial param values.
     */
    public void initModule() {
        // storagePool (optional, default=ConnectionPoolManager's default pool)
        m_storagePool=getParameter("storagePool");
        if (m_storagePool==null) {
            getServer().logConfig("Parameter storagePool "
                + "not given, will defer to ConnectionPoolManager's "
                + "default pool.");
        }
        // storageFormat (optional, default=DOTranslator's default format)
        m_storageFormat=getParameter("storageFormat");
        if (m_storageFormat==null) {
            getServer().logConfig("Parameter storageFormat "
                + "not given, will defer to DOTranslator's default format.");
        }
        // storageCharacterEncoding (optional, default=UTF-8)
        m_storageCharacterEncoding=getParameter("storageCharacterEncoding");
        if (m_storageCharacterEncoding==null) {
            getServer().logConfig("Parameter storage_character_encoding "
                + "not given, using UTF-8");
            m_storageCharacterEncoding="UTF-8";
        }
    }
    
    public void postInitModule() 
            throws ModuleInitializationException {
        ConnectionPoolManager cpm=(ConnectionPoolManager) getServer().getModule("fedora.server.storage.ConnectionPoolManager");
        if (cpm==null) {
            throw new ModuleInitializationException("ConnectionPoolManager not loaded.", getRole());
        }
        try {
            if (m_storagePool==null) {
                m_connectionPool=cpm.getPool();
            } else {
                m_connectionPool=cpm.getPool(m_storagePool);
            }
        } catch (ConnectionPoolNotFoundException cpnfe) {
            throw new ModuleInitializationException("Couldn't get required connection pool...wasn't found", getRole());
        }
        String dbSpec="fedora/server/storage/resources/DefaultDOManager.dbspec";
        InputStream specIn=this.getClass().getClassLoader().getResourceAsStream(
                dbSpec);
        if (specIn==null) {
            throw new ModuleInitializationException("Cannot find required "
                    + "resource: " + dbSpec, getRole());
        }
        List tSpecs=null;
        try {
            tSpecs=TableSpec.getTableSpecs(specIn);
        } catch (IOException ioe) {
            throw new ModuleInitializationException("Couldn't read dbspec :"
                    + ioe.getMessage(), getRole());
        } catch (InconsistentTableSpecException itse) {
            throw new ModuleInitializationException("Inconsistent table spec :"
                    + itse.getMessage(), getRole());
        }

        // Look at the database tables and construct a list of
        // TableSpec objects for tables that don't exist.
        ArrayList nonExisting=new ArrayList();
        Connection conn=null;
        try {
            conn=m_connectionPool.getConnection();
            DatabaseMetaData dbMeta=conn.getMetaData();
            Iterator tSpecIter=tSpecs.iterator();
            // Get a list of tables that don't exist, if any
            ResultSet r=dbMeta.getTables(null, null, "%", null);
            HashSet existingTableSet=new HashSet();
            while (r.next()) {
                existingTableSet.add(r.getString("TABLE_NAME"));
            }
            r.close();
            while (tSpecIter.hasNext()) {
                TableSpec spec=(TableSpec) tSpecIter.next();
                if (!existingTableSet.contains(spec.getName())) {
                    nonExisting.add(spec);
                }
            }
        } catch (SQLException sqle) {
            throw new ModuleInitializationException("Error while attempting to "
                    + "inspect database tables: " + sqle.getMessage(), 
                    getRole());
        } finally {
            if (conn!=null) {
                m_connectionPool.free(conn);
            }
        }
        
        if (nonExisting.size()>0) {
            Iterator nii=nonExisting.iterator();
            int i=0;
            StringBuffer msg=new StringBuffer();
            msg.append("One or more required tables did not exist (");
            while (nii.hasNext()) {
                if (i>0) {
                    msg.append(", ");
                }
                msg.append(((TableSpec) nii.next()).getName());
                i++;
            }
            msg.append(")");
            System.out.println(msg.toString());
            TableCreatingConnection tcConn=null;
            try {
                tcConn=m_connectionPool.getTableCreatingConnection();
                if (tcConn==null) {
                    throw new SQLException("Unable to construct CREATE TABLE "
                        + "statement(s) because there is no DDLConverter "
                        + "registered for this connection type.");
                }
            } catch (SQLException sqle) {
                throw new ModuleInitializationException("Error while attempting"
                    + " to create non-existing table(s): " + sqle.getMessage(), 
                    getRole());
            } finally {
                if (tcConn!=null) {
                    m_connectionPool.free(tcConn);
                }
            }
        }
/*        
        
        TableCreatingConnection connection=m_connectionPool.
                getTableCreatingConnection();
        if (connection==null) {
            boolean 
        }
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
        */
    }
    
    public String[] getRequiredModuleRoles() {
        return new String[] {"fedora.server.storage.ConnectionPoolManager"};
    }
    
    public String getStorageCharacterEncoding() {
        return m_storageCharacterEncoding;
    }

    /** pid will always be non-null, context will always be non-null */
    public DOReader getReader(Context context, String pid)
            throws ServerException {
        if (context.get("application").equals("apim")) {
            return null;
        } else if (context.get("application").equals("apia")) {
            return null;
        } else {
            throw new InvalidContextException("Error in context: 'application' must be 'apim' or 'apia'");
        }
    }
    
    public DisseminatingDOReader getDisseminatingReader(Context context, String pid) 
            throws ServerException {
        if (context.get("application").equals("??")) {
            return null;
        } else if (context.get("application").equals("??")) {
            return null;
        } else {
            throw new InvalidContextException("Error in context: 'application' must be ...??");
        }
    }

    public BMechReader getBMechReader(Context context, String pid)
            throws ServerException {
        if (context.get("application").equals("??")) {
            return null;
        } else if (context.get("application").equals("??")) {
            return null;
        } else {
            throw new InvalidContextException("Error in context: 'application' must be ...??");
        }
    }

    public BDefReader getBDefReader(Context context, String pid)
            throws ServerException {
        if (context.get("application").equals("??")) {
            return null;
        } else if (context.get("application").equals("??")) {
            return null;
        } else {
            throw new InvalidContextException("Error in context: 'application' must be ...??");
        }
    }

    /** nulls not allowed */
    public DOWriter getWriter(Context context, String pid)
            throws ServerException {
        if (context.get("application").equals("apim")) {
            return null;
        } else if (context.get("application").equals("apia")) {
            return null;
        } else {
            throw new InvalidContextException("Error in context: 'application' must be 'apim' or 'apia'");
        }
            // create a new, empty object, giving it a DEFAULT_STATE
//            DefinitiveDOWriter writer=new DefinitiveDOWriter(newPid, DEFAULT_STATE,
            
/*
    public DefinitiveDOWriter(String pid, TestStreamStorage storage, 
            TestStreamStorage tempStorage, StreamValidator validator,
            DODeserializer importDeserializer, DOSerializer storageSerializer,
            DODeserializer storageDeserializer, DOSerializer exportSerializer,
            InputStream initialContent, boolean useContentPid) 
*/

 //           );
    }
    
    /** nulls not allowed */
    public DOWriter newWriter(Context context, InputStream in, String format, String encoding, boolean newPid) 
            throws ServerException {
        if (context.get("application").equals("apim")) {
            return null;
        } else if (context.get("application").equals("apia")) {
            return null;
        } else {
            throw new InvalidContextException("Error in context: 'application' must be 'apim' or 'apia'");
        }
    }
    
    /** nulls not allowed */
    public DOWriter newWriter(Context context) 
            throws ServerException {
        if (context.get("application").equals("apim")) {
            return null;
        } else if (context.get("application").equals("apia")) {
            return null;
        } else {
            throw new InvalidContextException("Error in context: 'application' must be 'apim' or 'apia'");
        }
    }
    public String[] listObjectPIDs(Context context, String state) 
{return null;}/*            throws StorageDeviceException, InvalidContextException {
        if ( (!context.get("application").equals("apim")) 
                && (!context.get("application").equals("apia")) ) {
            throw new InvalidContextException("Error in context: 'application' must be 'apim' or 'apia'");
        }
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
    */

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
     /*
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
    } */

    // called internally when obj created, and by DOWriters
    public void updateObject(String pid, String state, String lockingUser) {
    }
    
    // called by a DOWriter
    public void dropObject(String pid) {
    }

}
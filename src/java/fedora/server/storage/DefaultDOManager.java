package fedora.server.storage;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
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
import fedora.server.errors.GeneralException;
import fedora.server.errors.InconsistentTableSpecException;
import fedora.server.errors.InvalidContextException;
import fedora.server.errors.MalformedPidException;
import fedora.server.errors.ModuleInitializationException;
import fedora.server.errors.ObjectExistsException;
import fedora.server.errors.ObjectLockedException;
import fedora.server.errors.ObjectNotFoundException;
import fedora.server.errors.ServerException;
import fedora.server.errors.StorageException;
import fedora.server.errors.StorageDeviceException;
import fedora.server.management.PIDGenerator;
import fedora.server.storage.lowlevel.FileSystemLowlevelStorage;
import fedora.server.storage.lowlevel.ILowlevelStorage;
import fedora.server.storage.types.BasicDigitalObject;
import fedora.server.storage.types.DigitalObject;
import fedora.server.utilities.SQLUtility;
import fedora.server.utilities.TableCreatingConnection;
import fedora.server.utilities.TableSpec;

/**
 * Provides access to digital object readers and writers.
 *
 * @author cwilper@cs.cornell.edu
 */
public class DefaultDOManager 
        extends Module implements DOManager {
        
    private String m_pidNamespace;
    private String m_storagePool;
    private String m_storageFormat;
    private String m_storageCharacterEncoding;
    private PIDGenerator m_pidGenerator;
    private DOTranslator m_translator;
    private ILowlevelStorage m_permanentStore;
    private ILowlevelStorage m_tempStore;
    
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
    public void initModule() 
            throws ModuleInitializationException {
        // pidNamespace (required, 1-17 chars, a-z, A-Z, 0-9 '-')
        m_pidNamespace=getParameter("pidNamespace");
        if (m_pidNamespace==null) {
            throw new ModuleInitializationException(
                    "pidNamespace parameter must be specified.", getRole());
        }
        if ( (m_pidNamespace.length() > 17) || (m_pidNamespace.length() < 1) ) {
            throw new ModuleInitializationException(
                    "pidNamespace parameter must be 1-17 chars long", getRole());
        }
        StringBuffer badChars=new StringBuffer();
        for (int i=0; i<m_pidNamespace.length(); i++) {
            char c=m_pidNamespace.charAt(i);
            boolean invalid=true;
            if (c>='0' && c<='9') {
                invalid=false;
            } else if (c>='a' && c<='z') {
                invalid=false;
            } else if (c>='A' && c<='Z') {
                invalid=false;
            } else if (c=='-') {
                invalid=false;
            }
            if (invalid) {
                badChars.append(c);
            }
        }
        if (badChars.toString().length()>0) {
            throw new ModuleInitializationException("pidNamespace contains "
                    + "invalid character(s) '" + badChars.toString() + "'", getRole());
        }
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
        // get ref to pidgenerator
        m_pidGenerator=(PIDGenerator) getServer().
                getModule("fedora.server.management.PIDGenerator");
        // get the permanent and temporary storage handles
        // m_permanentStore=FileSystemLowlevelStorage.getPermanentStore();
        // m_tempStore=FileSystemLowlevelStorage.getTempStore();
        // moved above to getPerm and getTemp (lazy instantiation) because of 
        // multi-instance problem due to s_server.getInstance occurring while another is running
        
        // get ref to translator and derive storageFormat default if not given
        m_translator=(DOTranslator) getServer().
                getModule("fedora.server.storage.DOTranslator");
        if (m_storageFormat==null) {
            m_storageFormat=m_translator.getDefaultFormat();
        }
        // now get the connectionpool
        ConnectionPoolManager cpm=(ConnectionPoolManager) getServer().
                getModule("fedora.server.storage.ConnectionPoolManager");
        if (cpm==null) {
            throw new ModuleInitializationException(
                    "ConnectionPoolManager not loaded.", getRole());
        }
        try {
            if (m_storagePool==null) {
                m_connectionPool=cpm.getPool();
            } else {
                m_connectionPool=cpm.getPool(m_storagePool);
            }
        } catch (ConnectionPoolNotFoundException cpnfe) {
            throw new ModuleInitializationException("Couldn't get required "
                    + "connection pool...wasn't found", getRole());
        }
        String dbSpec="fedora/server/storage/resources/DefaultDOManager.dbspec";
        InputStream specIn=this.getClass().getClassLoader().
                getResourceAsStream(dbSpec);
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
                existingTableSet.add(r.getString("TABLE_NAME").toLowerCase());
            }
            r.close();
            while (tSpecIter.hasNext()) {
                TableSpec spec=(TableSpec) tSpecIter.next();
                if (!existingTableSet.contains(spec.getName().toLowerCase())) {
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
            msg.append(" required table(s) did not exist (");
            while (nii.hasNext()) {
                if (i>0) {
                    msg.append(", ");
                }
                msg.append(((TableSpec) nii.next()).getName());
                i++;
            }
            msg.append(")");
            getServer().logConfig(i + msg.toString());
            TableCreatingConnection tcConn=null;
            try {
                tcConn=m_connectionPool.getTableCreatingConnection();
                if (tcConn==null) {
                    throw new SQLException("Unable to construct CREATE TABLE "
                        + "statement(s) because there is no DDLConverter "
                        + "registered for this connection type.");
                }
                nii=nonExisting.iterator();
                while (nii.hasNext()) {
                    TableSpec spec=(TableSpec) nii.next();
                    StringBuffer sqlCmds=new StringBuffer();
                    Iterator iter=tcConn.getDDLConverter().getDDL(spec).iterator();
                    while (iter.hasNext()) {
                        sqlCmds.append("\n");
                        sqlCmds.append((String) iter.next());
                        sqlCmds.append(";");
                    }
                    getServer().logConfig("Attempting to create nonexisting "
                            + "table '" + spec.getName() + "' with command(s): "
                            + sqlCmds.toString());
                    tcConn.createTable(spec);
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
    }

    public void releaseWriter(DOWriter writer) {
        writer.invalidate();
        // remove pid from tracked list...m_writers.remove(writer);
    }

    public ILowlevelStorage getPermanentStore() {
        return FileSystemLowlevelStorage.getPermanentStore();

//        return m_permanentStore;
    }
    
    public ILowlevelStorage getTempStore() {
        return FileSystemLowlevelStorage.getTempStore();
//        return m_tempStore;
    }
    
    public ConnectionPool getConnectionPool() {
        return m_connectionPool;
    }
    
    public String[] getRequiredModuleRoles() {
        return new String[] {
                "fedora.server.management.PIDGenerator",
                "fedora.server.storage.ConnectionPoolManager",
                "fedora.server.storage.DOTranslator" };
    }
    
    public String getStorageFormat() {
        return m_storageFormat;
    }

    public String getStorageCharacterEncoding() {
        return m_storageCharacterEncoding;
    }

    public DOTranslator getTranslator() {
        return m_translator;
    }

    /**
     * Tells whether the context indicates that cached objects are required.
     */
    private static boolean cachedObjectRequired(Context context) {
        String c=context.get("useCachedObject");
        if (c!=null && c.equalsIgnoreCase("true")) {
            return true;
        } else {
            return false;
        }
    }

    public DOReader getReader(Context context, String pid)
            throws ServerException {
        if (cachedObjectRequired(context)) {
            return new FastDOReader(pid);
        } else {
            return new DefinitiveDOReader(pid);
        }
    }
    
    public DisseminatingDOReader getDisseminatingReader(Context context, String pid) 
            throws ServerException {
        if (cachedObjectRequired(context)) {
            return new FastDOReader(pid);
        } else {
            throw new InvalidContextException("A DisseminatingDOReader is unavailable in a non-cached context.");
        }
    }

    public BMechReader getBMechReader(Context context, String pid)
            throws ServerException {
        if (cachedObjectRequired(context)) {
            throw new InvalidContextException("A BMechReader is unavailable in a cached context.");
        } else {
            return new DefinitiveBMechReader(pid);
        }
    }

    public BDefReader getBDefReader(Context context, String pid)
            throws ServerException {
        if (cachedObjectRequired(context)) {
            throw new InvalidContextException("A BDefReader is unavailable in a cached context.");
        } else {
            return new DefinitiveBDefReader(pid);
        }
    }

    protected void doUnlock(String pid, boolean commit) {
    }

    /**
     * Requests a lock on the object.
     *
     * If the lock is already owned by the user in the context, that's ok.
     *
     * @param context The current context.
     * @param pid The pid of the object.
     * @throws ObjectNotFoundException If the object does not exist. 
     * @throws ObjectLockedException If the object is already locked by someone else.
     */
    protected void obtainLock(Context context, String pid) 
            throws ObjectLockedException, ObjectNotFoundException, 
            StorageDeviceException, InvalidContextException {
        Connection conn=null;
        try {
            String query="SELECT LockingUser "
                       + "FROM ObjectRegistry "
                       + "WHERE DO_PID='" + pid + "'";
            conn=m_connectionPool.getConnection();
            Statement s=conn.createStatement();
            ResultSet results=s.executeQuery(query);
            if (!results.next()) {
                throw new ObjectNotFoundException("The requested object doesn't exist.");
            }
            String lockingUser=results.getString("LockingUser");
            if (lockingUser==null) {
                // get the lock
                s.executeUpdate("UPDATE ObjectRegistry SET LockingUser='" 
                    + getUserId(context) + "' WHERE DO_PID='" + pid + "'");
            }
            if (!lockingUser.equals(getUserId(context))) {
                throw new ObjectLockedException("The object is locked by " + lockingUser);
            }
            // if we got here, the lock is already owned by current user, ok
        } catch (SQLException sqle) {
            throw new StorageDeviceException("Unexpected error from SQL database: " + sqle.getMessage());
        } finally {
            if (conn!=null) {
                m_connectionPool.free(conn);
            }
        }
    }

    /**
     * Gets a writer on an an existing object.
     *
     * If the object is locked, it must be by the current user.
     * If the object is not locked, a lock is obtained automatically.
     *
     * The object must be locked by the user identified in the context.
     */
    public DOWriter getWriter(Context context, String pid)
            throws ServerException, ObjectLockedException {
        if (cachedObjectRequired(context)) {
            throw new InvalidContextException("A DOWriter is unavailable in a cached context.");
        } else {
            // ensure we've got a lock
            obtainLock(context, pid);
            
            // TODO: make sure there's no session lock on a writer for the pid
            
            BasicDigitalObject obj=new BasicDigitalObject();
            m_translator.deserialize(getPermanentStore().retrieve(pid), obj, 
                    m_storageFormat, m_storageCharacterEncoding);
            DOWriter w=new DefinitiveDOWriter(this, obj);
            // add to internal list...somehow..think...
            return w;
        }
    }

    /**
     * Gets a writer on a new, imported object.
     *
     * A new object is created in the system, locked by the current user.
     * The incoming stream must represent a valid object.
     */
    public DOWriter newWriter(Context context, InputStream in, String format, String encoding, boolean newPid) 
            throws ServerException {
        getServer().logFinest("Entered DefaultDOManager.newWriter(Context, InputStream, String, String, boolean)");
        if (cachedObjectRequired(context)) {
            throw new InvalidContextException("A DOWriter is unavailable in a cached context.");
        } else {
            // deserialize it first
            BasicDigitalObject obj=new BasicDigitalObject();
            m_translator.deserialize(in, obj, format, encoding);
            // do we need to generate a pid?
            if (newPid) {
               getServer().logFinest("Ingesting client wants a new PID.");
               // yes... so do that, then set it in the obj.
               String p=null;
               try {
                   p="urn:" + m_pidGenerator.generatePID(m_pidNamespace);
               } catch (Exception e) {
                   throw new GeneralException("Error generating PID, PIDGenerator returned unexpected error: (" 
                           + e.getClass().getName() + ") - " + e.getMessage());
               }
               getServer().logFiner("Generated PID: " + p);
               obj.setPid(p);
            } else {
               getServer().logFinest("Ingesting client wants to use existing PID.");
            }
            // now check the pid.. 1) it must be a valid pid and 2) it can't already exist
            
            
// FIXME: uncomment the following after lv0 test
//            assertValidPid(obj.getPid());

            if (objectExists(obj.getPid())) {
                throw new ObjectExistsException("The PID '" + obj.getPid() + "' already exists in the registry... the object can't be re-created.");
            }
            // make a record of it in the registry
            registerObject(obj.getPid(), getUserId(context));
            
            // serialize to disk, then validate.. if that's ok, go on.. else unregister it! 
            ByteArrayOutputStream out=new ByteArrayOutputStream();
            m_translator.serialize(obj, out, m_storageFormat, m_storageCharacterEncoding);
            ByteArrayInputStream newIn=new ByteArrayInputStream(out.toByteArray());
            getPermanentStore().add(obj.getPid(), newIn); 
            
             
            // then get the writer
            DOWriter w=new DefinitiveDOWriter(this, obj);
            // add to internal list...somehow..think...
            return w;
        }
    }

    /**
     * Gets a writer on a new, empty object.
     */
    public DOWriter newWriter(Context context) 
            throws ServerException {
        getServer().logFinest("Entered DefaultDOManager.newWriter(Context)");
        if (cachedObjectRequired(context)) {
            throw new InvalidContextException("A DOWriter is unavailable in a cached context.");
        } else {
            BasicDigitalObject obj=new BasicDigitalObject();
            getServer().logFinest("Creating object, need a new PID.");
            String p=null;
            try {
                p="urn:" + m_pidGenerator.generatePID(m_pidNamespace);
            } catch (Exception e) {
                throw new GeneralException("Error generating PID, PIDGenerator returned unexpected error: (" 
                        + e.getClass().getName() + ") - " + e.getMessage());
            }
            getServer().logFiner("Generated PID: " + p);
            obj.setPid(p);
// FIXME: uncomment the following after lv0 test
//          assertValidPid(obj.getPid());
            if (objectExists(obj.getPid())) {
                throw new ObjectExistsException("The PID '" + obj.getPid() + "' already exists in the registry... the object can't be re-created.");
            }
            // make a record of it in the registry
            registerObject(obj.getPid(), getUserId(context));
            
            // serialize to disk, then validate.. if that's ok, go on.. else unregister it! 
        }
        return null;
    }

    /**
     * Gets the userId property from the context... if it's not
     * populated, throws an InvalidContextException.
     */
    private String getUserId(Context context)
            throws InvalidContextException {
        String ret=context.get("userId");
        if (ret==null) {
            throw new InvalidContextException("The context identifies no userId, but a user must be identified for this operation.");
        }
        return ret;
    }
    
    /**
     * Throws an exception if the PID is invalid.
     * <pre>
     * Basically:
     * ----------
     * The implementation's limit for the namespace 
     * id is 17 characters.
     *
     * The limit for object id is 10 characters,
     * representing any decimal # between zero and
     * 2147483647 (2.14 billion)
     *
     * This does not necessarily mean a particular
     * installation can handle 2.14 billion objects.
     * The max number of objects is practically 
     * limited by:
     *   - disk storage limits
     *   - OS filesystem impl. limits
     *   - database used (max rows in a table, etc.)
     *
     * How prantical length limits were derived:
     * -----------------------------------------
     * The type for dbid's on objects in the db is int.
     *
     * MySQL and McKoi both impose a max of 2.14Billion (10
     * decimal digits) on INT. (for oracle it's higher, but
     * unknown).  Some dbs have a higher-prcision int type
     * (like bigint), but it's likely a limit in number of
     * rows would be reached before the int type is
     * exhausted.
     * 
     * So for PIDs, which use URN syntax, the NSS part (in
     * our case, a decimal number [see spec section
     * 8.3.1(3)]) can be *practically* be between 1 and 10
     * (decimal) digits.
     * 
     * Additionally, where PIDs are stored in the db, we
     * impose a max length of 32 chars.  
     * 
     * Given the urn-syntax-imposed 5 chars ('urn:' and ':'),
     * the storage system's int-type limit of 10 chars for
     * row ids, and the storage system's imposed limit of 32
     * chars for the total pid, this leaves 17 characters for
     * the namespace id.
     * 
     * urn:17maxChars-------:10maxChars
     * ^                              ^
     * |-------- 32 chars max --------|
     * </pre>
     */
    private void assertValidPid(String pid)
            throws MalformedPidException {
        if (pid.length()>32) {
            throw new MalformedPidException("Pid is too long.  Max total length is 32 chars.");
        }
        String[] parts=pid.split(":");
        if (parts.length!=3) {
            throw new MalformedPidException("Pid must have two ':' characters, as in urn:nsid:1234");
        }
        if (!parts[0].equalsIgnoreCase("urn")) {
            throw new MalformedPidException("Pids must use the urn scheme, as in urn:nsid:1234");
        }
        if (parts[1].length()>17) {
            throw new MalformedPidException("Namespace id part of pid must be less than 18 chars.");
        }
        if (parts[1].length()==0) {
            throw new MalformedPidException("Namespace id part of pid must be at least 1 char.");
        }
        // check for valid chars in namespace id part
        StringBuffer badChars=new StringBuffer();
        for (int i=0; i<parts[1].length(); i++) {
            char c=parts[1].charAt(i);
            boolean invalid=true;
            if (c>='0' && c<='9') {
                invalid=false;
            } else if (c>='a' && c<='z') {
                invalid=false;
            } else if (c>='A' && c<='Z') {
                invalid=false;
            } else if (c=='-') {
                invalid=false;
            }
            if (invalid) {
                badChars.append(c);
            }
        }
        if (badChars.toString().length()>0) {
            throw new MalformedPidException("Pid namespace id part contains "
                    + "invalid character(s) '" + badChars.toString() + "'");
        }
        if (parts[2].length()>10) {
            throw new MalformedPidException("Pid object id part must be "
                    + "less than 11 chars.");
        }
        if (parts[2].length()==0) {
            throw new MalformedPidException("Pid object id part must be "
                    + "at least 1 char.");
        }
        try {
            long lng=Long.parseLong(parts[2]);
            if (lng>2147483647) {
                throw new NumberFormatException("");
            }
            if (lng<0) {
                throw new NumberFormatException("");
            }
        } catch (NumberFormatException nfe) {
            throw new MalformedPidException("Pid object id part must be "
                    + "an integer between 0 and 2.147483647 billion.");
        }
    }
    
    /**
     * Checks the object registry for the given object.
     */
    public boolean objectExists(String pid) 
            throws StorageDeviceException {
        Connection conn=null;
        try {
            String query="SELECT DO_PID "
                       + "FROM ObjectRegistry "
                       + "WHERE DO_PID='" + pid + "'";
            conn=m_connectionPool.getConnection();
            Statement s=conn.createStatement();
            ResultSet results=s.executeQuery(query);
            return results.next(); // 'true' if match found, else 'false'
        } catch (SQLException sqle) {
            throw new StorageDeviceException("Unexpected error from SQL database: " + sqle.getMessage());
        } finally {
            if (conn!=null) {
                m_connectionPool.free(conn);
            }
        }
    }
    
    /**
     * Adds a new, locked object.
     */
    private void registerObject(String pid, String userId) 
            throws StorageDeviceException {
        Connection conn=null;
        try {
            String query="INSERT INTO ObjectRegistry (DO_PID, LockingUser) "
                       + "VALUES ('" + pid + "', '"+ userId +"')";
            conn=m_connectionPool.getConnection();
            Statement s=conn.createStatement();
            s.executeUpdate(query);
        } catch (SQLException sqle) {
            throw new StorageDeviceException("Unexpected error from SQL database: " + sqle.getMessage());
        } finally {
            if (conn!=null) {
                m_connectionPool.free(conn);
            }
        }
    }
    
    public String[] listObjectPIDs(Context context, String state) 
            throws StorageDeviceException {
        ArrayList pidList=new ArrayList();
        Connection conn=null;
        try {
            String query="SELECT DO_PID "
                       + "FROM ObjectRegistry "
                       + "WHERE SystemVersion > 0"; // <- ignore new,uncommitted
            conn=m_connectionPool.getConnection();
            Statement s=conn.createStatement();
            ResultSet results=s.executeQuery(query);
            while (results.next()) {
                pidList.add(results.getString("DO_PID"));
            }
            String[] ret=new String[pidList.size()];
            Iterator pidIter=pidList.iterator();
            int i=0;
            while (pidIter.hasNext()) {
                ret[i++]=(String) pidIter.next();
            }
            return ret;
        } catch (SQLException sqle) {
            throw new StorageDeviceException("Unexpected error from SQL database: " + sqle.getMessage());
        } finally {
            if (conn!=null) {
                m_connectionPool.free(conn);
            }
        }
    }

}
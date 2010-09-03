package fedora.server.utilities.rebuild;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import fedora.common.Constants;
import fedora.server.Context;
import fedora.server.ReadOnlyContext;
import fedora.server.Server;
import fedora.server.config.DatastoreConfiguration;
import fedora.server.config.ModuleConfiguration;
import fedora.server.config.ServerConfiguration;
import fedora.server.errors.InitializationException;
import fedora.server.errors.LowlevelStorageException;
import fedora.server.errors.ModuleInitializationException;
import fedora.server.errors.ObjectNotFoundException;
import fedora.server.errors.ServerException;
import fedora.server.errors.StorageDeviceException;
import fedora.server.management.PIDGenerator;
import fedora.server.search.FieldSearch;
import fedora.server.storage.BDefReader;
import fedora.server.storage.BMechReader;
import fedora.server.storage.ConnectionPool;
import fedora.server.storage.ConnectionPoolManager;
import fedora.server.storage.DOManager;
import fedora.server.storage.DOReader;
import fedora.server.storage.DOWriter;
import fedora.server.storage.lowlevel.ILowlevelStorage;
import fedora.server.storage.replication.DOReplicator;
import fedora.server.storage.types.Datastream;
import fedora.server.storage.types.DigitalObject;
import fedora.server.storage.types.Disseminator;
import fedora.server.utilities.SQLUtility;
import fedora.server.utilities.TableSpec;

/**
 * A Rebuilder for the SQL database.
 *
 * @@version $Id$
 */
public class SQLRebuilder implements Rebuilder {

    /** Logger for this class. */
    private static final Logger LOG = Logger.getLogger(
            Rebuilder.class.getName());

    private File m_serverDir;
    private ServerConfiguration m_serverConfig;
    private static Server s_server;
    private ConnectionPool m_connectionPool;
    private Connection m_connection;
    private Context m_context;

    private final String m_echoString = "Added PID";

    /**
     * Get a short phrase describing what the user can do with this rebuilder.
     */
    public String getAction() {
        return "Rebuild SQL database.";
    }

    /**
     * Returns true is the server _must_ be shut down for this
     * rebuilder to safely operate.
     */
    public boolean shouldStopServer()
    {
        return(true);
    }

    /**
     * Initialize the rebuilder, given the server configuration.
     *
     * @@returns a map of option names to plaintext descriptions.
     */
    public Map init(File serverDir,
                    ServerConfiguration serverConfig) {
        m_serverDir = serverDir;
        m_serverConfig = serverConfig;
        Map m = new HashMap();
        return m;
    }

    /**
     * Validate the provided options and perform any necessary startup tasks.
     */
    public void start(Map options) throws Exception
    {
        // This must be done before starting "RebuildServer"
        // rather than after, so any application caches
        // (in particular the hash map held by PIDGenerator)
        // don't get out of sync with the database.
        blankExistingTables( );

        try {
            s_server = RebuildServer.getRebuildInstance(
                    new File(Constants.FEDORA_HOME));
            // now get the connectionpool
            ConnectionPoolManager cpm=(ConnectionPoolManager) s_server.
                    getModule("fedora.server.storage.ConnectionPoolManager");
            if (cpm==null)
            {
                throw new ModuleInitializationException(
                        "ConnectionPoolManager not loaded.", "ConnectionPoolManager");
            }
            m_connectionPool = cpm.getPool();
            m_context = ReadOnlyContext.getContext("utility", "fedoraAdmin", "", /*null, */ReadOnlyContext.DO_OP);
            String registryClassTemp = s_server.getParameter("registry");
            String reason = "registry";

            ILowlevelStorage llstore = (ILowlevelStorage) s_server.
            getModule("fedora.server.storage.lowlevel.ILowlevelStorage");
            try
            {
                llstore.rebuildObject();
                llstore.rebuildDatastream();
            }
            catch (LowlevelStorageException e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        }
        catch (InitializationException ie)
        {
            LOG.error("Error initializing", ie);
            throw ie;
        }
    }


    public static List getExistingTables( Connection conn )
            throws SQLException
    {

        ArrayList existing=new ArrayList();
        DatabaseMetaData dbMeta=conn.getMetaData();
        ResultSet r = null;
        // Get a list of tables that don't exist, if any
        try
        {
            r = dbMeta.getTables(null, null, "%", null);
            HashSet existingTableSet=new HashSet();
            while (r.next())
            {
                existing.add(r.getString("TABLE_NAME"));
            }
            r.close();
            r = null;
        }
        catch (SQLException sqle)
        {
            throw new SQLException(sqle.getMessage());
        }
        finally
        {
            try {
                if (r != null) r.close();
            }
            catch (SQLException sqle2)
            {
                throw sqle2;
            }
            finally
            {
                r=null;
            }
        }
        return existing;
    }


    /**
     * Delete all rows from all Fedora-related tables (except the resource index
     * ones) that exist in the database.
     */
    private void blankExistingTables( )
    {
        Connection connection = null;
        try {
            connection = getDefaultConnection();
            List existingTables = getExistingTables(connection);
            List fedoraTables = getFedoraTables();
            for (int i = 0; i < existingTables.size(); i++)
            {
                String origTableName = existingTables.get(i).toString();
                String tableName = origTableName.toUpperCase();
                if (fedoraTables.contains(tableName) && !tableName.startsWith("RI"))
                {
                    System.out.println("Cleaning up table: " + origTableName);
                    try {
                        executeSql(connection, "DELETE FROM " + origTableName);
                    }
                    catch (LowlevelStorageException lle)
                    {
                        System.err.println(lle.getMessage());
                        System.err.flush();
                    }
                }
            }
        }
        catch (SQLException e)
        {
            throw new RuntimeException("DB error while blanking existing tables", e);
        }
        finally
        {
            try { connection.close(); } catch (Exception e) { }
        }
    }

    /**
     * Get the names of all Fedora tables listed in the server's dbSpec file.
     *
     * Names will be returned in ALL CAPS so that case-insensitive comparisons
     * can be done.
     */
    private List getFedoraTables() {
        try {
            String dbSpecLocation = "fedora/server/storage/resources/DefaultDOManager.dbspec";
            InputStream in = getClass().getClassLoader().getResourceAsStream(dbSpecLocation);
            List specs = TableSpec.getTableSpecs(in);
            ArrayList names = new ArrayList();
            for (int i = 0; i < specs.size(); i++) {
                TableSpec spec = (TableSpec) specs.get(i);
                names.add(spec.getName().toUpperCase());
            }
            return names;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Unexpected error reading dbspec file", e);
        }
    }

    public void executeSql(Connection connection, String sql )
           throws LowlevelStorageException
    {
        Statement statement = null;
        try {
            statement = connection.createStatement();
            if (statement.execute(sql))
            {
                throw new LowlevelStorageException(true, "sql returned query results for a nonquery");
            }
            int updateCount = statement.getUpdateCount();
        }
        catch (SQLException e1)
        {
            throw new LowlevelStorageException(true, "sql failurex (exec)", e1);
        }
        finally
        {
            try {
                if (statement != null) statement.close();
            }
            catch (Exception e2)
            { // purposely general to include uninstantiated statement, connection
                throw new LowlevelStorageException(true,"sql failure closing statement, connection, pool (exec)", e2);
            }
            finally
            {
                statement=null;
            }
        }
    }

    /**
     * Add the data of interest for the given object.
     */
    public void addObject(DigitalObject obj)
    {
        // CURRENT TIME:
        // Get the current time to use for created dates on object
        // and object components (if they are not already there).
        Date nowUTC=new Date();

        DOReplicator replicator=(DOReplicator) s_server.getModule("fedora.server.storage.replication.DOReplicator");
        DOManager manager=(DOManager) s_server.getModule("fedora.server.storage.DOManager");
        FieldSearch fieldSearch=(FieldSearch) s_server.getModule("fedora.server.search.FieldSearch");
        PIDGenerator pidGenerator=(PIDGenerator) s_server.getModule("fedora.server.management.PIDGenerator");

        // SET OBJECT PROPERTIES:
        LOG.debug("Rebuild: Setting object/component states and create dates if unset...");
        // set object state to "A" (Active) if not already set
        if (obj.getState()==null || obj.getState().equals("")) {
            obj.setState("A");
        }
        // set object create date to UTC if not already set
        if (obj.getCreateDate()==null || obj.getCreateDate().equals("")) {
            obj.setCreateDate(nowUTC);
        }
        // set object last modified date to UTC
        obj.setLastModDate(nowUTC);

        // SET OBJECT PROPERTIES:
        LOG.debug("Rebuild: Setting object/component states and create dates if unset...");
        // set object state to "A" (Active) if not already set
        if (obj.getState()==null || obj.getState().equals("")) {
            obj.setState("A");
        }
        // set object create date to UTC if not already set
        if (obj.getCreateDate()==null || obj.getCreateDate().equals("")) {
            obj.setCreateDate(nowUTC);
        }
        // set object last modified date to UTC
        obj.setLastModDate(nowUTC);

        // SET DATASTREAM PROPERTIES...
        Iterator dsIter=obj.datastreamIdIterator();
        while (dsIter.hasNext()) {
            List dsList=obj.datastreams((String) dsIter.next());
            for (int i=0; i<dsList.size(); i++) {
                Datastream ds=(Datastream) dsList.get(i);
                // Set create date to UTC if not already set
                if (ds.DSCreateDT==null || ds.DSCreateDT.equals("")) {
                    ds.DSCreateDT=nowUTC;
                }
                // Set state to "A" (Active) if not already set
                if (ds.DSState==null || ds.DSState.equals("")) {
                    ds.DSState="A";
                }
            }
        }
        // SET DISSEMINATOR PROPERTIES...
        Iterator dissIter=obj.disseminatorIdIterator();
        while (dissIter.hasNext()) {
            List dissList=obj.disseminators((String) dissIter.next());
            for (int i=0; i<dissList.size(); i++) {
                Disseminator diss=(Disseminator) dissList.get(i);
                // Set create date to UTC if not already set
                if (diss.dissCreateDT==null || diss.dissCreateDT.equals("")) {
                    diss.dissCreateDT=nowUTC;
                }
                // Set state to "A" (Active) if not already set
                if (diss.dissState==null || diss.dissState.equals("")) {
                    diss.dissState="A";
                }
            }
        }

        // GET DIGITAL OBJECT WRITER:
        // get an object writer configured with the DEFAULT export format
        LOG.debug("INGEST: Instantiating a SimpleDOWriter...");
        try {
            DOWriter w = manager.getWriter(Server.USE_DEFINITIVE_STORE, m_context, obj.getPid());
        }
        catch (ServerException se)
        {
        }

        // PID GENERATION:
        // have the system generate a PID if one was not provided
        LOG.debug("INGEST: Stream contained PID with retainable namespace-id... will use PID from stream.");
        try {
            pidGenerator.neverGeneratePID(obj.getPid());
        }
        catch (IOException e)
        {
            throw new RuntimeException("Error calling pidGenerator.neverGeneratePID(): " + e.getMessage(), e);
        }

        // REGISTRY:
        // at this point the object is valid, so make a record
        // of it in the digital object registry
        try {
            registerObject(obj.getPid(), obj.getFedoraObjectType(),
            obj.getOwnerId(), obj.getContentModelId(),
            obj.getCreateDate(), obj.getLastModDate());
        }
        catch (StorageDeviceException e)
        {}


        // REPLICATE:
        // add to replication jobs table and do replication to db
        LOG.debug("COMMIT: Adding replication job...");
        try {
            addReplicationJob(obj.getPid(), false);
        }
        catch (StorageDeviceException e)
        {
        }

        try {
            if (obj.getFedoraObjectType()==DigitalObject.FEDORA_BDEF_OBJECT)
            {
                LOG.info("COMMIT: Attempting replication as bdef object: " + obj.getPid());
                BDefReader reader = manager.getBDefReader(Server.USE_DEFINITIVE_STORE, m_context, obj.getPid());
                replicator.replicate(reader);
                LOG.info("COMMIT: Updating FieldSearch indexes...");
                fieldSearch.update(reader);
            }
            else if (obj.getFedoraObjectType()==DigitalObject.FEDORA_BMECH_OBJECT)
            {
                LOG.info("COMMIT: Attempting replication as bmech object: " + obj.getPid());
                BMechReader reader = manager.getBMechReader(Server.USE_DEFINITIVE_STORE, m_context, obj.getPid());
                replicator.replicate(reader);
                LOG.info("COMMIT: Updating FieldSearch indexes...");
                fieldSearch.update(reader);
            }
            else
            {
                LOG.info("COMMIT: Attempting replication as normal object: " + obj.getPid());
                DOReader reader = manager.getReader(Server.USE_DEFINITIVE_STORE, m_context, obj.getPid());
                replicator.replicate(reader);
                LOG.info("COMMIT: Updating FieldSearch indexes...");
                fieldSearch.update(reader);
            }
            // FIXME: also remove from temp storage if this is successful
            removeReplicationJob(obj.getPid());
        }
        catch (ServerException se)
        {
          System.out.println("Error while replicating: " + se.getClass().getName() + ": " + se.getMessage());
          se.printStackTrace();
        }
        catch (Throwable th)
        {
          System.out.println("Error while replicating: " + th.getClass().getName() + ": " + th.getMessage());
          th.printStackTrace();
        }
        System.out.println(m_echoString + ": " + obj.getPid());
    }

    /**
     * Add an entry to the replication jobs table.
     */
    private void addReplicationJob(String pid, boolean deleted)
            throws StorageDeviceException {
        Connection conn=null;
        String[] columns=new String[] {"doPID", "action"};
        String action="M";
        if (deleted) {
            action="D";
        }
        String[] values=new String[] {pid, action};
        try {
            conn=m_connectionPool.getConnection();
            SQLUtility.replaceInto(conn, "doRepJob", columns,
                    values, "doPID");
        } catch (SQLException sqle) {
            throw new StorageDeviceException("Error creating replication job: " + sqle.getMessage());
        } finally {
            if (conn!=null) {
                m_connectionPool.free(conn);
            }
        }
    }

    private void removeReplicationJob(String pid)
                  throws StorageDeviceException
    {
        Connection conn=null;
        Statement s=null;
        try {
            conn=m_connectionPool.getConnection();
            s=conn.createStatement();
            s.executeUpdate("DELETE FROM doRepJob "+ "WHERE doPID = '" + pid + "'");
        }
        catch (SQLException sqle)
        {
            throw new StorageDeviceException("Error removing entry from replication jobs table: " + sqle.getMessage());
        }
        finally
        {

            try {
                if (s!=null) s.close();
                if (conn!=null) m_connectionPool.free(conn);
            }
            catch (SQLException sqle)
            {
                throw new StorageDeviceException("Unexpected error from SQL database: " + sqle.getMessage());
            }
            finally
            {
                s=null;
            }
        }
    }


    /**
     * Adds a new object.
     */
    private void registerObject(String pid, int fedoraObjectType, String userId,
            String contentModelId, Date createDate, Date lastModDate)
            throws StorageDeviceException
    {
        // label field is not used (FCREPO-789)
        String theLabel="the label field is no longer used";

        // label or contentModelId may be null...set to blank if so
        String theContentModelId=contentModelId;
        if (theContentModelId==null) {
            theContentModelId="";
        }
        Connection conn=null;
        Statement s1=null;
        String foType="O";
        if (fedoraObjectType==DigitalObject.FEDORA_BDEF_OBJECT) {
            foType="D";
        }
        if (fedoraObjectType==DigitalObject.FEDORA_BMECH_OBJECT) {
            foType="M";
        }
        try
        {
            String query="INSERT INTO doRegistry (doPID, foType, "
                                                   + "ownerId, label, "
                                                   + "contentModelID) "
                       + "VALUES ('" + pid + "', '" + foType +"', '"
                                     + userId +"', '" + theLabel + "', '"
                                     + theContentModelId + "')";
            conn=m_connectionPool.getConnection();
            s1=conn.createStatement();
            s1.executeUpdate(query);
        }
        catch (SQLException sqle)
        {
            throw new StorageDeviceException("Unexpected error from SQL database while registering object: " + sqle.getMessage());
        }
        finally
        {
            try {
                if (s1!=null) s1.close();
             }
            catch (Exception sqle)
            {
                throw new StorageDeviceException("Unexpected error from SQL database while registering object: " + sqle.getMessage());
            }
            finally
            {
                s1=null;
            }
        }

        Statement s2 = null;
        ResultSet results = null;
        try {
            // REGISTRY:
            // update systemVersion in doRegistry (add one)
            LOG.debug("COMMIT: Updating registry...");
    //                conn=m_connectionPool.getConnection();
            String query="SELECT systemVersion "
                           + "FROM doRegistry "
                           + "WHERE doPID='" + pid + "'";
            s2 = conn.createStatement();
            results = s2.executeQuery(query);
            if (!results.next())
            {
                throw new ObjectNotFoundException("Error creating replication job: The requested object doesn't exist in the registry.");
            }
            int systemVersion=results.getInt("systemVersion");
            systemVersion++;
            Date now = new Date();
            s2.executeUpdate("UPDATE doRegistry SET systemVersion="
                    + systemVersion + " "
                    + "WHERE doPID='" + pid + "'");
        }
        catch (SQLException sqle)
        {
            throw new StorageDeviceException("Error creating replication job: " + sqle.getMessage());
        }
        catch (ObjectNotFoundException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        finally
        {
            try
            {
              if (results!=null) results.close();
              if (s2!= null) s2.close();
              if (conn!=null) m_connectionPool.free(conn);
            }
            catch (SQLException sqle)
            {
                throw new StorageDeviceException("Unexpected error from SQL database: " + sqle.getMessage());
            }
            finally
            {
                results=null;
                s2=null;
            }
        }
    }

    /**
     * Free up any system resources associated with rebuilding.
     */
    public void finish()
    {
        try {
            s_server.shutdown(null);
        } catch (Throwable th) {
            System.out.println("Error shutting down RebuildServer:");
            th.printStackTrace();
        }
    }

    /**
     * Gets a connection to the database specified in
     * connection pool module's "defaultPoolName" config value.
     *
     * This allows us to the connect to the database without
     * the server running.
     */
    private Connection getDefaultConnection() {
        ModuleConfiguration poolConfig = m_serverConfig.getModuleConfiguration(
                "fedora.server.storage.ConnectionPoolManager");
        String datastoreID = poolConfig.getParameter("defaultPoolName")
                .getValue();
        DatastoreConfiguration dbConfig = m_serverConfig
                .getDatastoreConfiguration(datastoreID);
        return getConnection(dbConfig.getParameter("jdbcDriverClass").getValue(),
                dbConfig.getParameter("jdbcURL").getValue(),
                dbConfig.getParameter("dbUsername").getValue(),
                dbConfig.getParameter("dbPassword").getValue());
    }

    private static Connection getConnection(String driverClass,
            String url, String username, String password) {
        try {
            Class.forName(driverClass);
            return DriverManager.getConnection(url, username, password);
        } catch (Exception e) {
            throw new RuntimeException("Error getting database connection", e);
        }
    }

}

package fedora.server.resourceIndex;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.Logger;

import fedora.server.errors.ResourceIndexException;

import fedora.server.storage.ConnectionPool;
import fedora.server.storage.BDefReader;
import fedora.server.storage.BMechReader;

import fedora.server.storage.types.MethodDef;
import fedora.server.storage.types.MethodDefOperationBind;

/**
 * A <code>MethodInfoStore</code> that uses a database for storage.
 *
 * <pre>
 * TABLE                 COLUMN        EXAMPLE VALUE
 * --------------------  ------------  ----------------------
 * riMethod              methodId      test:bdef1/methodName
 *                       bDefPid       test:bdef1
 *                       methodName    methodName
 *
 * riMethodPermutations  methodId      test:bdef1/methodName
 *                       permutation   methodName?arg1=val1
 *
 * riMethodImplBinding   methodImplId  test:bmech1/methodName
 *                       dsBindKey     KEY1
 * 
 * riMethodImpl          methodImplId  test:bmech1/methodName
 *                       bMechPid      test:bmech1
 *                       methodId      test:bdef1/methodName
 *
 * riMethodMimeType      methodImplId  test:bmech1/methodName
 *                       mimeType      text/plain
 * </pre>
 *
 * @author cwilper@cs.cornell.edu
 */
public class DatabaseMethodInfoStore implements MethodInfoStore {

    /////////////////////////////////
    // SQL for BDef-defined Tables //
    /////////////////////////////////

    private static final String INSERT_METHOD = "INSERT INTO riMethod"
            + " (methodId, bDefPid, methodName) VALUES (?, ?, ?)";

    private static final String DELETE_METHODS = "DELETE FROM riMethod"
            + " WHERE bDefPid = ?";

    private static final String INSERT_PERMUTATION = "INSERT INTO"
            + " riMethodPermutation (methodId, permutation) VALUES (?, ?)";

    private static final String DELETE_PERMUTATIONS = "DELETE FROM"
            + " riMethodPermutation WHERE methodId LIKE ?";

    //////////////////////////////////
    // SQL for BMech-defined Tables //
    //////////////////////////////////

    private static final String INSERT_METHOD_IMPL_BINDING = "INSERT INTO"
            + " riMethodImplBinding (methodImplId, dsBindKey) VALUES (?, ?)";

    private static final String DELETE_METHOD_IMPL_BINDINGS = "DELETE FROM"
            + " riMethodImplBinding WHERE methodImplId LIKE ?";

    private static final String INSERT_METHOD_IMPL = "INSERT INTO riMethodImpl"
            + " (methodImplId, bMechPid, methodId) VALUES (?, ?, ?)";

    private static final String DELETE_METHOD_IMPLS = "DELETE FROM"
            + " riMethodImpl WHERE methodImplId LIKE ?";

    private static final String INSERT_METHOD_MIME_TYPE = "INSERT INTO"
            + " riMethodMimeType (methodImplId, mimeType) VALUES (?, ?)";

    private static final String DELETE_METHOD_MIME_TYPES = "DELETE FROM"
            + " riMethodMimeType WHERE methodImplId LIKE ?";

    ////////////////////////////////////////////
    // SQL for getting MethodInfo for a BMech //
    ////////////////////////////////////////////

    private static final String SELECT_METHOD_IDS = "SELECT methodId"
            + " FROM riMethodImpl WHERE bMechPid = ?";

    private static final String SELECT_PERMUTATIONS = "SELECT permutation"
            + " FROM riMethodPermutation WHERE methodId = ?";

    private static final String SELECT_BINDINGS = "SELECT dsBindKey"
            + " FROM riMethodImplBinding WHERE methodImplId = ?";

    private static final String SELECT_MIME_TYPES = "SELECT mimeType"
            + " FROM riMethodMimeType WHERE methodImplId = ?";

    /** Logger for this class. */
    private static final Logger LOG = Logger.getLogger(
            DatabaseMethodInfoStore.class.getName());

    /** Connection pool this instance will use. */
    private ConnectionPool _pool;

    /** Whether method permutations should be stored. */
    private boolean _storePermutations;

    ////////////////////
    // Initialization //
    ////////////////////

    public DatabaseMethodInfoStore(ConnectionPool pool,
                                   boolean storePermutations) {
        _pool = pool;
        _storePermutations = storePermutations;
    }

    /////////////////////////////
    // MethodInfoStore Methods //
    /////////////////////////////

    /**
     * {@inheritDoc}
     */
    public synchronized void putBDefInfo(BDefReader reader)
            throws ResourceIndexException {
        Connection conn = null;
        try {
            String bDefPID = reader.GetObjectPID();
            conn = _pool.getConnection();
            deleteMethodDefs(conn, bDefPID);
            addMethodDefs(conn, bDefPID, reader.getAbstractMethods(null));
        } catch (Exception e) {
            throw new ResourceIndexException("Error putting bDef info", e);
        } finally {
            releaseConnection(conn);
        }
    }

    /**
     * {@inheritDoc}
     */
    public synchronized void deleteBDefInfo(String bDefPID)
            throws ResourceIndexException {
        Connection conn = null;
        try {
            conn = _pool.getConnection();
            deleteMethodDefs(conn, bDefPID);
        } catch (Exception e) {
            throw new ResourceIndexException("Error deleting bDef info", e);
        } finally {
            releaseConnection(conn);
        }
    }

    /**
     * {@inheritDoc}
     */
    public synchronized void putBMechInfo(BMechReader reader)
            throws ResourceIndexException {
        Connection conn = null;
        try {
            String bMechPID = reader.GetObjectPID();
            conn = _pool.getConnection();
            deleteMethodImpls(conn, bMechPID);
            addMethodImpls(conn, bMechPID, 
                    reader.getServiceDSInputSpec(null).bDefPID,
                    reader.getServiceMethodBindings(null));
        } catch (Exception e) {
            throw new ResourceIndexException("Error putting bMech info", e);
        } finally {
            releaseConnection(conn);
        }
    }

    /**
     * {@inheritDoc}
     */
    public synchronized void deleteBMechInfo(String bMechPID)
            throws ResourceIndexException {
        Connection conn = null;
        try {
            conn = _pool.getConnection();
            deleteMethodImpls(conn, bMechPID);
        } catch (Exception e) {
            throw new ResourceIndexException("Error deleting bMech info", e);
        } finally {
            releaseConnection(conn);
        }
    }

    ////////////////////////////////
    // MethodInfoProvider Methods //
    ////////////////////////////////

    /**
     * {@inheritDoc}
     */
    public Set<MethodInfo> getMethodInfo(String bMechPID)
            throws ResourceIndexException {
        Connection conn = null;
        try {
            Set<MethodInfo> methodInfo = new HashSet<MethodInfo>();
            conn = _pool.getConnection();
            Set<String> methodIds = getSet(conn, SELECT_METHOD_IDS, bMechPID);
            for (String methodId : methodIds) {
                String methodName = methodId.split("/")[1];
                String implId = bMechPID + "/" + methodName;
                methodInfo.add(
                        new MethodInfo(methodName,
                                getSet(conn, SELECT_BINDINGS, implId),
                                getSet(conn, SELECT_MIME_TYPES, implId),
                                getSet(conn, SELECT_PERMUTATIONS, methodId)));
            }
            return methodInfo;
        } catch (Exception e) {
            throw new ResourceIndexException("Error getting method info", e);
        } finally {
            releaseConnection(conn);
        }
    }

    /////////////////////
    // Private Methods //
    /////////////////////

    /**
     * Adds BDef-defined method and permutation information to the
     * database.
     */
    private void addMethodDefs(Connection conn, String bDefPID, 
            MethodDef[] methodDefs)
            throws Exception {
        PreparedStatement addMethod = null;
        PreparedStatement addPerm = null;
        try {
            addMethod = conn.prepareStatement(INSERT_METHOD);
            addPerm = conn.prepareStatement(INSERT_PERMUTATION);
            for (int i = 0; i < methodDefs.length; i++) {
                MethodDef def = methodDefs[i];
                String methodId = bDefPID + "/" + def.methodName;
                addMethod.setString(1, methodId);
                addMethod.setString(2, bDefPID);
                addMethod.setString(3, def.methodName);
                addMethod.addBatch();
                if (_storePermutations) {
                    Set<String> perms = new ParamDomainMap(def.methodName,
                            def.methodParms, true).getPermutations();
                    for (String perm : perms) {
                        addPerm.setString(1, methodId);
                        addPerm.setString(2, perm);
                        addPerm.addBatch();
                    }
                } else {
                    // NOTE: This seems counterintuitive, but it mimicks the
                    //       original implementation.  If we knew it wouldn't
                    //       cause problems, we could remove it.
                    addPerm.setString(1, methodId);
                    addPerm.setString(2, def.methodName);
                    addPerm.addBatch();
                }
            }
            addMethod.executeBatch();
            addPerm.executeBatch();
        } finally {
            closeStatement(addMethod);
            closeStatement(addPerm);
        }
    }

    /**
     * Removes BDef-defined method and permutation information from the 
     * database.
     */ 
    private static void deleteMethodDefs(Connection conn, String bDefPID)
            throws Exception {
        PreparedStatement delMethods = null;
        PreparedStatement delPerms = null;
        try {
            delMethods = conn.prepareStatement(DELETE_METHODS);
            delPerms = conn.prepareStatement(DELETE_PERMUTATIONS);

            delMethods.setString(1, bDefPID);
            delPerms.setString(1, bDefPID + "/%");

            delMethods.execute();
            delPerms.execute();
        } finally {
            closeStatement(delMethods);
            closeStatement(delPerms);
        }
    }

    /**
     * Adds BMech-defined method implementation information to the
     * database.
     */
    private void addMethodImpls(Connection conn, String bMechPID,
            String bDefPID, MethodDefOperationBind[] bindings)
            throws Exception {
        PreparedStatement addImpl = null;
        PreparedStatement addBinding = null;
        PreparedStatement addMime = null;
        try {
            addImpl = conn.prepareStatement(INSERT_METHOD_IMPL);
            addBinding = conn.prepareStatement(INSERT_METHOD_IMPL_BINDING);
            addMime = conn.prepareStatement(INSERT_METHOD_MIME_TYPE);
            for (MethodDefOperationBind binding : bindings) {
                String methodImplId = bMechPID + "/" + binding.methodName;
                String methodId = bDefPID + "/" + binding.methodName;
                addImpl.setString(1, methodImplId);
                addImpl.setString(2, bMechPID);
                addImpl.setString(3, methodId);
                addImpl.addBatch();
                addValues(addBinding, methodImplId, binding.dsBindingKeys);
                addValues(addMime, methodImplId, binding.outputMIMETypes);
            }
            addImpl.executeBatch();
            addBinding.executeBatch();
            addMime.executeBatch();
        } finally {
            closeStatement(addImpl);
            closeStatement(addBinding);
            closeStatement(addMime);
        }
    }

    /**
     * Removes BMech-defined method implementation information from the 
     * database.
     */ 
    private static void deleteMethodImpls(Connection conn, String bMechPID)
            throws Exception {
        PreparedStatement delBindings = null;
        PreparedStatement delImpls = null;
        PreparedStatement delMimes = null;
        try {
            delBindings = conn.prepareStatement(DELETE_METHOD_IMPL_BINDINGS);
            delImpls = conn.prepareStatement(DELETE_METHOD_IMPLS);
            delMimes = conn.prepareStatement(DELETE_METHOD_MIME_TYPES);

            delBindings.setString(1, bMechPID + "/%");
            delImpls.setString(1, bMechPID + "/%");
            delMimes.setString(1, bMechPID + "/%");

            delBindings.execute();
            delImpls.execute();
            delMimes.execute();
        } finally {
            closeStatement(delBindings);
            closeStatement(delImpls);
            closeStatement(delMimes);
        }
    }

    /**
     * Adds each value in the given array, if it's non-null.
     */
    private static void addValues(PreparedStatement statement, String id,
            String[] values) throws SQLException {
        if (values != null) {
            for (String value : values) {
                statement.setString(1, id);
                statement.setString(2, value);
                statement.addBatch();
            }
        }
    }

    /**
     * Executes the given one-parameter query and returns the resulting
     * set of strings.
     */
    private static Set<String> getSet(Connection conn, String sql, String val)
            throws SQLException {
        PreparedStatement statement = null;
        ResultSet results = null;
        try {
            statement = conn.prepareStatement(sql);
            statement.setString(1, val);
            results = statement.executeQuery();
            Set<String> set = new HashSet<String>();
            while (results.next()) {
                set.add(results.getString(1));
            }
            return set;
        } finally {
            closeResultSet(results);
            closeStatement(statement);
        }
    }

    /**
     * Releases a connection if it's non-null, logging a warning if there's
     * a problem.
     */
    private void releaseConnection(Connection conn) {
        if (conn != null) {
            try {
                _pool.free(conn);
            } catch (Exception e) {
                LOG.warn("Unable to release connection", e);
            }
        }
    }

    /**
     * Closes a statement if it's non-null, logging a warning if there's
     * a problem.
     */
    private static void closeStatement(Statement statement) {
        if (statement != null) {
            try {
                statement.close();
            } catch (SQLException e) {
                LOG.warn("Unable to close statement", e);
            }
        }
    }

    /**
     * Closes a result set if it's non-null, logging a warning if there's
     * a problem.
     */
     private static void closeResultSet(ResultSet results) {
        if (results != null) {
            try {
                results.close();
            } catch (SQLException e) {
                LOG.warn("Unable to close results", e);
            }
        }
    }

}
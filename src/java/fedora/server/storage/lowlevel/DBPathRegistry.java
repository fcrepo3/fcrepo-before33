package fedora.server.storage.lowlevel;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Map;

import fedora.server.errors.LowlevelStorageException;
import fedora.server.errors.LowlevelStorageInconsistencyException;
import fedora.server.errors.ObjectNotInLowlevelStorageException;
import fedora.server.storage.ConnectionPool;
import fedora.server.utilities.SQLUtility;

/**
 *
 * <p><b>Title:</b> DBPathRegistry.java</p>
 * <p><b>Description:</b> </p>
 *
 * @author wdn5e@virginia.edu
 * @version $Id$
 */
public class DBPathRegistry extends PathRegistry {
	private ConnectionPool connectionPool = null;
	private boolean backslashIsEscape;

	public DBPathRegistry(Map configuration) {
		super(configuration);
		connectionPool = (ConnectionPool)configuration.get("connectionPool");
		backslashIsEscape = Boolean.valueOf((String)configuration.get("backslashIsEscape")).booleanValue();
	}

	public String get(String pid) throws ObjectNotInLowlevelStorageException, LowlevelStorageInconsistencyException, LowlevelStorageException {
		String path = null;
		Connection connection = null;
		Statement statement = null;
                ResultSet rs = null;
		try {
			int paths = 0;
			connection = connectionPool.getConnection();
			statement = connection.createStatement();
			rs = statement.executeQuery("SELECT path FROM " + getRegistryName() + " WHERE token='" + pid + "'");
			for (; rs.next(); paths++) {
				path = rs.getString(1);
			}
			if (paths == 0) {
				throw new ObjectNotInLowlevelStorageException("no path in db registry for [" + pid + "]");
			}
			if (paths > 1) {
				throw new LowlevelStorageInconsistencyException("[" + pid + "] in db registry -multiple- times");
			}
			if ((path == null) || (path.length() == 0)) {
				throw new LowlevelStorageInconsistencyException("[" + pid + "] has -null- path in db registry");
			}
		} catch (SQLException e1) {
			throw new LowlevelStorageException(true,"sql failure (get)", e1);
		} finally {
			try {
                                if (rs!=null) rs.close();
				if (statement!=null) statement.close();
				if (connection!=null) connectionPool.free(connection);
			} catch (Exception e2) { // purposely general to include uninstantiated statement, connection
				throw new LowlevelStorageException(true,"sql failure closing statement, connection, pool (get)", e2);
			} finally {
                            rs=null;
                            statement=null;
                        }
		}
		return path;
	}

	public void executeSql (String sql) throws ObjectNotInLowlevelStorageException, LowlevelStorageInconsistencyException, LowlevelStorageException {
		Connection connection = null;
		Statement statement = null;
		try {
			connection = connectionPool.getConnection();
			statement = connection.createStatement();
			if (statement.execute(sql)) {
				throw new LowlevelStorageException(true,"sql returned query results for a nonquery");
			}
			int updateCount = statement.getUpdateCount();
			if (updateCount == 0) {
				throw new ObjectNotInLowlevelStorageException("-no- rows updated in db registry");
			}
			if (updateCount > 1) {
				throw new LowlevelStorageInconsistencyException("-multiple- rows updated in db registry");
			}
		} catch (SQLException e1) {
			throw new LowlevelStorageException(true,"sql failurex (exec)", e1);
		} finally {
			try {
				if (statement!=null) statement.close();
				if (connection!=null) connectionPool.free(connection);
			} catch (Exception e2) { // purposely general to include uninstantiated statement, connection
				throw new LowlevelStorageException(true,"sql failure closing statement, connection, pool (exec)", e2);
			} finally {
                            statement=null;
                        }
		}
	}

	public void put (String pid, String path)  throws ObjectNotInLowlevelStorageException, LowlevelStorageInconsistencyException, LowlevelStorageException  {
		if (backslashIsEscape) {
			StringBuffer buffer = new StringBuffer();
			String backslash = "\\"; //Java quotes will interpolate this as 1 backslash
			String escapedBackslash = "\\\\"; //Java quotes will interpolate these as 2 backslashes
			/* Escape each backspace so that DB will correctly record a single backspace,
			   instead of incorrectly escaping the following character.
			 */
			for (int i = 0; i < path.length(); i++) {
				String s = path.substring(i,i+1);
				buffer.append(s.equals(backslash) ? escapedBackslash : s);
			}
			path = buffer.toString();
		}
       Connection conn=null;
		try {
            conn=connectionPool.getConnection();
            SQLUtility.replaceInto(conn,
                    getRegistryName(), new String[] {"token", "path"},
                    new String[] {pid, path}, "token");
		} catch (SQLException e1) {
			throw new ObjectNotInLowlevelStorageException("put into db registry failed for [" + pid + "]", e1);
		} finally {
           if (conn!=null) connectionPool.free(conn);
       }
	}

	public void remove (String pid) throws ObjectNotInLowlevelStorageException, LowlevelStorageInconsistencyException, LowlevelStorageException {
		try {
			executeSql("DELETE FROM " + getRegistryName() + " WHERE " + getRegistryName() + ".token='" + pid + "'");
		} catch (ObjectNotInLowlevelStorageException e1) {
			throw new ObjectNotInLowlevelStorageException("[" + pid + "] not in db registry to delete", e1);
		} catch (LowlevelStorageInconsistencyException e2) {
			throw new LowlevelStorageInconsistencyException("[" + pid + "] deleted from db registry -multiple- times", e2);
		}
	}
	
	public void rebuild () throws LowlevelStorageException {
		int report = FULL_REPORT;
		try {
			executeSql("DELETE FROM " + getRegistryName());
		} catch (ObjectNotInLowlevelStorageException e1) {
		} catch (LowlevelStorageInconsistencyException e2) {
		}
		try {
			System.err.println("\nbegin rebuilding registry from files");
			traverseFiles(storeBases, REBUILD, false, report); // continues, ignoring bad files
			System.err.println("end rebuilding registry from files (ending normally)");
		} catch (Exception e) {
			if (report != NO_REPORT) {
				System.err.println("ending rebuild unsuccessfully: " + e.getMessage());
			}
			throw new LowlevelStorageException(true, "ending rebuild unsuccessfully", e); //<<====
		}
	}

	public void auditFiles () throws LowlevelStorageException {
		System.err.println("\nbegin audit:  files-against-registry");
		traverseFiles(storeBases, AUDIT_FILES, false, FULL_REPORT);
		System.err.println("end audit:  files-against-registry (ending normally)");
	}

	protected Enumeration keys() throws LowlevelStorageException, LowlevelStorageInconsistencyException {
		Hashtable hashtable = new Hashtable(); {
			ResultSet rs = null;
			Connection connection = null;
			Statement statement = null;
			try {
				connection = connectionPool.getConnection();
				statement = connection.createStatement();
				rs = statement.executeQuery("SELECT token FROM " + getRegistryName());
				while (rs.next()) {
					String pid = rs.getString(1);
					if ((null == pid) || (0 == pid.length())) {
						throw new LowlevelStorageInconsistencyException("null pid on enumeration");
					}
					hashtable.put(pid,"");
				}
			} catch (SQLException e1) {
				throw new LowlevelStorageException(true, "sql failure (enum)", e1);
			}
			finally {
				try {
                                        if (rs!=null) rs.close();
					if (statement!=null) statement.close();
					if (connection!=null) connectionPool.free(connection);
				} catch (Exception e2) { // purposely general to include uninstantiated statement, connection
					throw new LowlevelStorageException(true,"sql failure closing statement, connection, pool (enum)", e2);
				} finally {
                                    rs=null;
                                    statement=null;
                                }
			}
		}
		return hashtable.keys();
	}
}



package fedora.server.storage.lowlevel;
import java.io.File;
import java.util.Enumeration;
import java.util.Hashtable;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import fedora.server.storage.ConnectionPool;
import fedora.server.Server;
import fedora.server.errors.InitializationException;
import fedora.server.errors.LowlevelStorageException;
import fedora.server.errors.LowlevelStorageInconsistencyException;
import fedora.server.errors.ObjectNotInLowlevelStorageException;
class DBPathRegistry extends PathRegistry implements IPathRegistry {
	//private static final IPathAlgorithm pathAlgorithm = new CNullPathAlgorithm();
	private static Server s_server;
	static {
		 try {
		     s_server=Server.getInstance(new File(System.getProperty("fedora.home")));
		 } catch (InitializationException ie) {
		     System.err.println(ie.getMessage());
		 }
	}
	private ConnectionPool connectionPool = null;

	public DBPathRegistry() throws LowlevelStorageException{
		super();
		
		String username = s_server.getParameter("dbuser");
		if (username == null) {
			throw new LowlevelStorageException(true,"must configure dbuser");
		}
		String password = s_server.getParameter("dbpass");
		if (password == null) {
			throw new LowlevelStorageException(true,"must configure dbpass");
		}
		String url = s_server.getParameter("connect_string");
		if (url == null) {
			throw new LowlevelStorageException(true,"must configure connect_string");
		}
		int minConnections; {
			String minConnectionsString = s_server.getParameter("pool_min");
			if (minConnectionsString == null) {
				throw new LowlevelStorageException(true,"must configure pool_min");
			}
			minConnections = Integer.parseInt(minConnectionsString);
		}
		int maxConnections; {
			String maxConnectionsString = s_server.getParameter("pool_max");
			if (maxConnectionsString == null) {
				throw new LowlevelStorageException(true,"must configure pool_max");
			}
			maxConnections = Integer.parseInt(maxConnectionsString);
		}
		try {
			String driver = "org.gjt.mm.mysql.Driver";
      			connectionPool = new ConnectionPool(driver, url, username, password,
				minConnections, /*initConnections,*/ maxConnections, true);
		} catch (SQLException e) {
			throw new LowlevelStorageException(true,"sql pool init failure", e);
		}
	}
	
	public String get(String pid) throws ObjectNotInLowlevelStorageException, LowlevelStorageInconsistencyException, LowlevelStorageException {
		String path = null;
		Connection connection = null;
		Statement statement = null;
		try {
			ResultSet rs = null;
			int paths = 0;
			connection = connectionPool.getConnection();
			statement = connection.createStatement();
			rs = statement.executeQuery("SELECT Location FROM PIDRegistry WHERE PID='" + pid + "'");
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
				statement.close();
				connection.close();
				connectionPool.free(connection);
			} catch (Exception e2) { // purposely general to include uninstantiated statement, connection
				throw new LowlevelStorageException(true,"sql failure closing statement, connection, pool (get)", e2);
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
				statement.close();
				connection.close();
				connectionPool.free(connection);
			} catch (Exception e2) { // purposely general to include uninstantiated statement, connection
				throw new LowlevelStorageException(true,"sql failure closing statement, connection, pool (exec)", e2);
			}
		}
	}

	public void put (String pid, String path)  throws ObjectNotInLowlevelStorageException, LowlevelStorageInconsistencyException, LowlevelStorageException  {
		try {
			executeSql("REPLACE INTO PIDRegistry (PID, Location) VALUES ('" + pid + "','" + path + "')");
		} catch (ObjectNotInLowlevelStorageException e1) {
			throw new ObjectNotInLowlevelStorageException("put into db registry failed for [" + pid + "]", e1);
		} catch (LowlevelStorageInconsistencyException e2) {
			throw new LowlevelStorageInconsistencyException("[" + pid + "] put into db registry -multiple- times", e2);
		}
	}

	public void remove (String pid) throws ObjectNotInLowlevelStorageException, LowlevelStorageInconsistencyException, LowlevelStorageException {
		try {
			executeSql("DELETE FROM PIDRegistry WHERE PIDRegistry.PID='" + pid + "'");
		} catch (ObjectNotInLowlevelStorageException e1) {
			throw new ObjectNotInLowlevelStorageException("[" + pid + "] not in db registry to delete", e1);
		} catch (LowlevelStorageInconsistencyException e2) {
			throw new LowlevelStorageInconsistencyException("[" + pid + "] deleted from db registry -multiple- times", e2);
		}
	}

	public void auditFiles () throws LowlevelStorageException {
		System.err.println("\nbegin audit:  files-against-registry");
		traverseFiles(configuration.getStoreBases(), AUDIT_FILES, false, FULL_REPORT);
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
				rs = statement.executeQuery("SELECT PID FROM PIDRegistry");
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
					statement.close();
					connection.close();
					connectionPool.free(connection);
				} catch (Exception e2) { // purposely general to include uninstantiated statement, connection
					throw new LowlevelStorageException(true,"sql failure closing statement, connection, pool (enum)", e2);
				}
			}
		}
		return hashtable.keys();
	}

	public void rebuild () throws LowlevelStorageException {
		// to be done
	}

}



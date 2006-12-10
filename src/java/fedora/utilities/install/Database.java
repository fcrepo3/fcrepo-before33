package fedora.utilities.install;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import fedora.server.utilities.McKoiDDLConverter;
import fedora.server.utilities.TableCreatingConnection;
import fedora.server.utilities.TableSpec;
import fedora.utilities.DriverShim;
import fedora.utilities.FileUtils;
import fedora.utilities.Zip;

public class Database {
	private Distribution _dist;
	private InstallOptions _opts;
	private String _db;
	
	public Database(Distribution dist, InstallOptions opts) {
		_dist = dist;
		_opts = opts;
		_db = _opts.getValue(InstallOptions.DATABASE);
	}
	
	public void install() throws InstallationFailedException {
		if (_db.equals(InstallOptions.INCLUDED)) {
			installEmbeddedMcKoi();
    	}
		
		if (_opts.getBooleanValue(InstallOptions.DATABASE_UPDATE, false)) {
			updateDOTable();
		}
	}
	
	/**
	 * Fedora 2.2 renamed the 'do' table to 'dobj' (because 'do' is reserved in
	 * Postgresql, which is supported as of Fedora 2.2).
	 * 
	 * McKoi unfortunately doesn't support renaming tables, so we create the 
	 * new dobj table, copy, and then delete.
	 * 
	 * For reference, the CREATE TABLE command for McKoi should be:
		CREATE TABLE dobj (
			doDbID int(11) default UNIQUEKEY('dobj') NOT NULL,
			doPID varchar(64) default '' NOT NULL,
			doLabel varchar(255) default '',
			doState varchar(1) default 'I' NOT NULL,
			PRIMARY KEY (doDbID),
			UNIQUE (doPID)
		)
	 * @throws InstallationFailedException
	 */
	private void updateDOTable() throws InstallationFailedException {
		if (_db.equals(InstallOptions.INCLUDED)) {
    		return; // no need to update embedded
    	}
		try {
	    	DriverShim.loadAndRegister(getDriver(), _opts.getValue(InstallOptions.DATABASE_DRIVERCLASS));
			Connection conn = DriverManager.getConnection(
					_opts.getValue(InstallOptions.DATABASE_JDBCURL), 
					_opts.getValue(InstallOptions.DATABASE_USERNAME),
					_opts.getValue(InstallOptions.DATABASE_PASSWORD));
			DatabaseMetaData dmd = conn.getMetaData();
			ResultSet rs = dmd.getTables(null, null, "%", null);
			while (rs.next()) {
				if (rs.getString("TABLE_NAME").equals("do")) {
					// McKoi doesn't support RENAME TABLE
					if (_db.equals(InstallOptions.MCKOI)) {
						// create table using DDL
						List<TableSpec> specs = TableSpec.getTableSpecs(_dist.get(Distribution.DBSPEC));
						for (TableSpec spec : specs) {
							if (spec.getName().equals("dobj")) {
								TableCreatingConnection tcc = new TableCreatingConnection(conn, new McKoiDDLConverter());
								tcc.createTable(spec);
								Statement stmt = conn.createStatement();
								
								// copy all from do to dobj
								stmt.execute("INSERT INTO dobj SELECT * FROM do;");
								
								// delete old table
								stmt.execute("DROP TABLE do");
								stmt.close();
								break;
							}
						}
					} else {
						Statement stmt = conn.createStatement();
						stmt.execute("ALTER TABLE do RENAME TO dobj");
						System.out.println("Renamed table 'do' to 'dobj'.");
						stmt.close();
					}
					break;
				}
			}
			rs.close();
			conn.close();
    	} catch(Exception e) {
    		throw new InstallationFailedException(e.getMessage(), e);
    	}
	}
	
	private File getDriver() throws IOException {
		File driver = null;
    	if (_opts.getValue(InstallOptions.DATABASE_DRIVER).equals(InstallOptions.INCLUDED)) {
    		InputStream is;
    		boolean success = false;
    		if (_db.equals(InstallOptions.MCKOI)) {
	        	is = _dist.get(Distribution.JDBC_MCKOI);
	        	driver = new File(System.getProperty("java.io.tmpdir"), Distribution.JDBC_MCKOI);
	        	success = FileUtils.copy(is, new FileOutputStream(driver));
	        } else if (_db.equals(InstallOptions.MYSQL)) {
	        	is = _dist.get(Distribution.JDBC_MYSQL);
	        	driver = new File(System.getProperty("java.io.tmpdir"), Distribution.JDBC_MYSQL);
	        	success = FileUtils.copy(is, new FileOutputStream(driver));
	        } else if (_db.equals(InstallOptions.POSTGRESQL)) {
	        	is = _dist.get(Distribution.JDBC_POSTGRESQL);
	        	driver = new File(System.getProperty("java.io.tmpdir"), Distribution.JDBC_POSTGRESQL);
	        	success = FileUtils.copy(is, new FileOutputStream(driver));
	        }
    		if (!success) {
    			throw new IOException("Extraction of included JDBC driver failed.");
    		}
    	} else {
    		driver = new File(_opts.getValue(InstallOptions.DATABASE_DRIVER));
    	}
    	return driver;
	}
	
	private void installEmbeddedMcKoi() throws InstallationFailedException {
    	System.out.println("Installing embedded McKoi...");
    	
    	File fedoraHome = new File(_opts.getValue(InstallOptions.FEDORA_HOME));
    	try {
			Zip.unzip(_dist.get(Distribution.MCKOI), fedoraHome);
			File mckoiHome = new File(fedoraHome, Distribution.MCKOI_BASENAME);
			
			// Default is to create data and log dirs relative to JVM, not conf location
			File mckoiProps = new File(mckoiHome, "db.conf");
			Properties mckoiConf = FileUtils.loadProperties(mckoiProps);
			mckoiConf.setProperty("root_path", "configuration");
			mckoiConf.store(new FileOutputStream(mckoiProps), null);
			
			String container = _opts.getValue(InstallOptions.SERVLET_ENGINE);
			if (container.equals(InstallOptions.INCLUDED) || container.equals(InstallOptions.EXISTING_TOMCAT)) {
				File tomcatHome = new File(_opts.getValue(InstallOptions.TOMCAT_HOME));
				File mckoidbSrc = new File(mckoiHome, "mckoidb.jar");
				File mckoidbDest = new File(tomcatHome, "common/lib/mckoidb.jar");
				if (!FileUtils.copy(new FileInputStream(mckoidbSrc),
						new FileOutputStream(mckoidbDest))) {
					throw new InstallationFailedException("Copy to " + 
							mckoidbDest.getAbsolutePath() + " failed.");
				}
			}
    	} catch(IOException e) {
    		throw new InstallationFailedException(e.getMessage(), e);
    	}
    }
	
	private void test() throws Exception {
		/*
		DriverShim.loadAndRegister(getDriver(), _opts.getValue(InstallOptions.DATABASE_DRIVERCLASS));
		Connection conn = DriverManager.getConnection(
				_opts.getValue(InstallOptions.DATABASE_JDBCURL), 
				_opts.getValue(InstallOptions.DATABASE_USERNAME),
				_opts.getValue(InstallOptions.DATABASE_PASSWORD));
		List<TableSpec> specs = TableSpec.getTableSpecs(_dist.get(Distribution.DBSPEC));
		for (TableSpec spec : specs) {
			if (spec.getName().equals("dobj")) {
				TableCreatingConnection tcc = new TableCreatingConnection(conn, new McKoiDDLConverter());
				tcc.createTable(spec);
				break;
			}
		}
		*/
		
		
		DriverShim.loadAndRegister(getDriver(), _opts.getValue(InstallOptions.DATABASE_DRIVERCLASS));
		Connection conn = DriverManager.getConnection(
				_opts.getValue(InstallOptions.DATABASE_JDBCURL), 
				_opts.getValue(InstallOptions.DATABASE_USERNAME),
				_opts.getValue(InstallOptions.DATABASE_PASSWORD));
		DatabaseMetaData dmd = conn.getMetaData();
		
		dmd.getTables(null, null, "%", null);
		System.out.println("Successfully connected to " + dmd.getDatabaseProductName());
	}
	
	public static void main(String[] args) throws Exception {
		Map<Object, Object> map = new HashMap<Object, Object>();
		map.put(InstallOptions.DATABASE, InstallOptions.MCKOI);
		map.put(InstallOptions.DATABASE_DRIVER, InstallOptions.INCLUDED);
		map.put(InstallOptions.DATABASE_JDBCURL, "jdbc:mckoi://localhost:9157/");
		map.put(InstallOptions.DATABASE_DRIVERCLASS, "com.mckoi.JDBCDriver");
		
		//map.put(InstallOptions.DATABASE, InstallOptions.MYSQL);
		//map.put(InstallOptions.DATABASE_DRIVER, InstallOptions.INCLUDED);
		//map.put(InstallOptions.DATABASE_JDBCURL, "jdbc:mysql://localhost/fedora22?useUnicode=true&amp;characterEncoding=UTF-8&amp;autoReconnect=true");
		//map.put(InstallOptions.DATABASE_DRIVERCLASS, "com.mysql.jdbc.Driver");
		map.put(InstallOptions.DATABASE_USERNAME, "fedoraAdmin");
		map.put(InstallOptions.DATABASE_PASSWORD, "fedoraAdmin");
		
		Distribution dist = new ClassLoaderDistribution();
        InstallOptions opts = new InstallOptions(dist, map);
        Database db = new Database(dist, opts);
        db.test();
	}
}

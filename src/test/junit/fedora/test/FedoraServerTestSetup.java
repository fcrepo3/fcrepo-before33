package fedora.test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.Statement;
import java.util.Iterator;

import junit.extensions.TestSetup;
import junit.framework.Test;
import fedora.server.config.DatastoreConfiguration;
import fedora.server.config.ModuleConfiguration;
import fedora.server.config.ServerConfiguration;
import fedora.server.config.ServerConfigurationParser;
import fedora.server.storage.ConnectionPool;
import fedora.server.utilities.DDLConverter;
import fedora.server.utilities.SQLUtility;
import fedora.server.utilities.TableSpec;
import fedora.utilities.ExecUtility;

/**
 * A wrapper (decorator) for Test responsible for starting and stopping a 
 * Fedora server test fixture.
 * 
 * n.b. This class makes many assumptions about various filesystem locations.
 * 
 * @author Edwin Shin
 */
public class FedoraServerTestSetup 
  extends    TestSetup 
  implements FedoraTestConstants {

    private boolean doSetup;
    private File m_configDir;
    
    /**
     * @param test
     */
    public FedoraServerTestSetup(Test test) {
        super(test);
    }

    public FedoraServerTestSetup(Test test, String suiteClassName) {
        super(test);

        String testHome = System.getProperty(PROP_TEST_HOME);
        if (testHome == null) {
            throw new RuntimeException("Required system property not set: " 
                    + PROP_TEST_HOME);
        }

        m_configDir = new File(new File(testHome), 
                               suiteClassName.replaceAll("\\.", "/"));
    }

    public void setUp() throws Exception {
        doSetup = getSetup();
        
        if (doSetup) {
            // setup actions go here
            startServer();
        } else {
            System.out.println("    skipping setUp()");
        }
    }
    
    public void tearDown() throws Exception {
        if (doSetup) {
            System.setProperty(PROP_SETUP, "true");
            // tear down actions go here
            stopServer();
        } else {
            System.out.println("    skipping tearDown()");
        }
    }
    
    public static ServerConfiguration getServerConfiguration() throws Exception {
        return new ServerConfigurationParser(
                new FileInputStream(FCFG)).parse();
    }

    private boolean getSetup() {
        String setup = System.getProperty(PROP_SETUP);
        if (setup == null) {
            System.setProperty(PROP_SETUP, "false");
            return true;
        }
        return setup.equalsIgnoreCase("true");
    }
    
    private void startServer() throws Exception {
        System.out.println("+ doing setUp(): starting server...");

        swapConfigurationFiles();

        String cmd = FEDORA_HOME + "/server/bin/fedora-start";
        
        String osName = System.getProperty("os.name" );

        if (osName.startsWith("Windows")) {
            cmd = "cmd.exe /C " + cmd;
        }
        
        try {
	        Process cp = Runtime.getRuntime().exec(cmd, null);
	        String line;
            BufferedReader input = new BufferedReader(
                    new InputStreamReader(cp.getInputStream()));
            BufferedReader error = new BufferedReader(
                    new InputStreamReader(cp.getErrorStream()));
	        boolean done = false;
            while (!done)
            {            
                line = null;
                while (!input.ready() && !error.ready()) 
                {
    	            Thread.sleep(10);
                }
                if (error.ready())
                {
                    line = error.readLine();
                }
                else if (input.ready())
                {
                    line = input.readLine();
                }
                System.out.println(line);
	            // If there's a better way to do this, please go ahead
	            if ( line.equals("OK") ) break;
	        }
	        input.close();
            error.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    private void stopServer() throws Exception {
        System.out.println("- doing tearDown(): stopping server...");

        ExecUtility.execCommandLineUtility(FEDORA_HOME + "/server/bin/fedora-stop");
        dropDBTables();
        deleteStore();
        unswapConfigurationFiles();
    }

    private void swapConfigurationFiles() throws Exception {
        System.out.println("Swapping-in configuration files from " + m_configDir.getPath());
    }
    
    private void unswapConfigurationFiles() throws Exception {
        System.out.println("Replacing original configuration files...");
    }
    
    private void dropDBTables() throws Exception {
        ConnectionPool cPool = SQLUtility.getConnectionPool(getServerConfiguration());
        Connection conn = cPool.getConnection();
        Statement stmt = conn.createStatement();
        DDLConverter ddlConverter = getDDLConverter();
        try {
	        FileInputStream fis = new FileInputStream("src/dbspec/server/fedora/server/storage/resources/DefaultDOManager.dbspec");
	        Iterator tableSpecs = TableSpec.getTableSpecs(fis).iterator();	        
	        
	        TableSpec tableSpec;
	        Iterator commands;
	        String command;
	        while (tableSpecs.hasNext()) {
	            tableSpec = (TableSpec)tableSpecs.next();
	            commands = ddlConverter.getDDL(tableSpec).iterator();
	            while (commands.hasNext()) {
	                command = ddlConverter.getDropDDL((String)commands.next());
	                stmt.execute(command);
	            }
	        }
        } finally {
	        if (stmt != null) stmt.close();
	        if (conn != null) conn.close();
        }
    }
    
    private DDLConverter getDDLConverter() throws Exception {
        ServerConfiguration fcfg = getServerConfiguration();
        ModuleConfiguration mcfg = fcfg.getModuleConfiguration("fedora.server.storage.ConnectionPoolManager");
        String defaultPoolName = mcfg.getParameter("defaultPoolName").getValue();
        DatastoreConfiguration dcfg = fcfg.getDatastoreConfiguration(defaultPoolName);
        String ddlConverterClassName = dcfg.getParameter("ddlConverter").getValue();
        return (DDLConverter)Class.forName(ddlConverterClassName).newInstance();
    }
    
    private String getRIStoreLocation() throws Exception {
        ServerConfiguration fcfg = getServerConfiguration();
        ModuleConfiguration mcfg = fcfg.getModuleConfiguration("fedora.server.resourceIndex.ResourceIndex");
        String datastore = mcfg.getParameter("datastore").getValue();
        DatastoreConfiguration dcfg = fcfg.getDatastoreConfiguration(datastore);
        return dcfg.getParameter("path").getValue();
    }
    
    private void deleteStore() throws Exception {
        ServerConfiguration fcfg = getServerConfiguration();
        String[] dirs = {
            fcfg.getParameter("object_store_base").getValue(),
            fcfg.getParameter("temp_store_base").getValue(),
            fcfg.getParameter("datastream_store_base").getValue(),
            getRIStoreLocation()
        };
        for (int i = 0; i < dirs.length; i++) {
            deleteDirectory(dirs[i]);
        }
    }
    
    protected boolean deleteDirectory(String directory) {
        boolean result = false;

        if (directory != null) {
            File file = new File(directory);
            if (file.exists() && file.isDirectory()) {
                // 1. delete content of directory:
                File[] files = file.listFiles();
                result = true; //init result flag
                int count = files.length;
                for (int i = 0; i < count; i++) { //for each file:
                    File f = files[i];
                    if (f.isFile()) {
                        result = result && f.delete();
                    } else if (f.isDirectory()) {
                        result = result && deleteDirectory(f.getAbsolutePath());
                    }
                }//next file

                file.delete(); //finally delete (empty) input directory
            }//else: input directory does not exist or is not a directory
        }//else: no input value

        return result;
    }//deleteDirectory()
}

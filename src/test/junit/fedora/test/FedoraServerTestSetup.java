package fedora.test;

import java.io.*;
import java.sql.Connection;
import java.sql.Statement;
import java.util.*;

import junit.extensions.TestSetup;
import junit.framework.Test;
import fedora.server.config.Configuration;
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
    static ByteArrayOutputStream sbOut = null;
    static ByteArrayOutputStream sbErr = null;    
    
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

		System.out.println("Using suite configuration package: " + suiteClassName + "CFG");
        m_configDir = new File(new File(testHome), 
                               (suiteClassName.replaceAll("\\.", "/")) + "CFG");
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
   
    /**
     * If this instance started the server, shut it down (and signal that it
     * can be started again by setting PROP_SETUP to true).
     */
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

    /**
     * Tell whether the Fedora server should be started.
     *
     * If PROP_SETUP is undefined or true, set it to false then return true.
     * Else return false.
     */
    private boolean getSetup() {
        String setup = System.getProperty(PROP_SETUP);
        if (setup == null || setup.equalsIgnoreCase("true")) {
            System.setProperty(PROP_SETUP, "false");
            return true;
        } else {
            return false;
        }
    }
    
    private void startServer() throws Exception {
        System.out.println("+ doing setUp(): starting server...");
		System.out.println("Suite configuration package is: " + m_configDir);

        if (m_configDir != null) swapConfigurationFiles();

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
        if (m_configDir != null) unswapConfigurationFiles();
    }

    private void backupPolicies() throws Exception {
		System.out.println("Backing up poliices...");
        ServerConfiguration config = new ServerConfigurationParser(new FileInputStream(FCFG_SRC)).parse();
        Configuration authzConfig = config.getModuleConfiguration("fedora.server.security.Authorization");
        backupDir(authzConfig.getParameter("REPOSITORY-POLICIES-DIRECTORY").getValue());
        backupDir(authzConfig.getParameter("OBJECT-POLICIES-DIRECTORY").getValue());
        backupDir(authzConfig.getParameter("SURROGATE-POLICIES-DIRECTORY").getValue());
        backupDir(authzConfig.getParameter("REPOSITORY-POLICY-GUITOOL-POLICIES-DIRECTORY").getValue());
    }

    private void restorePolicies() throws Exception {
        ServerConfiguration config = new ServerConfigurationParser(new FileInputStream(FCFG_SRC)).parse();
        Configuration authzConfig = config.getModuleConfiguration("fedora.server.security.Authorization");
        restoreDir(authzConfig.getParameter("REPOSITORY-POLICIES-DIRECTORY").getValue());
        restoreDir(authzConfig.getParameter("OBJECT-POLICIES-DIRECTORY").getValue());
        restoreDir(authzConfig.getParameter("SURROGATE-POLICIES-DIRECTORY").getValue());
        restoreDir(authzConfig.getParameter("REPOSITORY-POLICY-GUITOOL-POLICIES-DIRECTORY").getValue());
    }

    private void backupDir(String dirName) throws Exception {

        // make sure the original exists
        File origDir = new File(dirName);
        if (!origDir.exists()) {
            throw new IOException(dirName + " does not exist!  To remedy, run Fedora with default configuration first!!");
        }

        // prepare the new directory
        File newDir = new File(dirName + "_ORIG");
        deleteDirectory(newDir.getPath());
        newDir.mkdirs();

        // copy all files from origDir to newDir
        File[] sourceFiles = origDir.listFiles();
        for (int i = 0; i < sourceFiles.length; i++) {
            if (!sourceFiles[i].isDirectory() && sourceFiles[i].canRead()) {
                copy(sourceFiles[i], new File(newDir, sourceFiles[i].getName()));
            }
        }
    }

    private void restoreDir(String dirName) throws Exception {
        
        // clear out the active directory
        File activeDir = new File(dirName);
        deleteDirectory(activeDir.getPath());
        activeDir.mkdirs();

        // copy all files from dirName + "_ORIG" to the active directory
        File[] sourceFiles = new File(dirName + "_ORIG").listFiles();
        for (int i = 0; i < sourceFiles.length; i++) {
            copy(sourceFiles[i], new File(activeDir, sourceFiles[i].getName()));
        }
    }

    private void swapConfigurationFiles() throws Exception {
        System.out.println("Swapping-in configuration files from " + m_configDir.getPath());

        //
        // back up the contents of the policy directories
        //
        backupPolicies();
        //
        // fcfg.properties
        //
        File fcfgPropertiesFile = new File(m_configDir, "fcfg.properties");
        if (fcfgPropertiesFile.exists()) {
            System.out.println("fcfg.properties FOUND. Overriding fedora.fcfg...");

            // apply overrides in memory
            FileInputStream fis = new FileInputStream(FCFG_SRC);
            ServerConfigurationParser scp = new ServerConfigurationParser(fis);
            ServerConfiguration config = scp.parse();
            Properties overrides = new Properties();
            overrides.load(new FileInputStream(fcfgPropertiesFile));
            config.applyProperties(overrides);

            // back the old one up
            File fcfg = new File(FCFG);
            backup(fcfg);

            // serialize to fedora.fcfg
            OutputStream out = new FileOutputStream(fcfg);
            config.serialize(out);

        } else {
            System.out.println("fcfg.properties not found, will use default fedora.fcfg");
        }

        //
        // other configuration files
        //
        swapIn(JAAS, new File(JAAS_PATH));
        swapIn(TOMCAT_USERS_TEMPLATE, new File(TOMCAT_USERS_TEMPLATE_PATH));
        swapIn(WEB_XML, new File(WEB_XML_PATH));

    }

    private void swapIn(String name, File activeConfig) throws Exception {
        File newConfig = new File(m_configDir, name);       
        System.out.println("SWAP IN FILE: " + newConfig.getAbsolutePath());
		System.out.println("ACTIVE FILE: " + activeConfig.getAbsolutePath());
        if (newConfig.exists()) {
            System.out.println("Override found for " + name + ", swapping in...");
            backup(activeConfig);
            copy(newConfig, activeConfig);
        } else {
            System.out.println("No override for " + name + ", will use default.");
        }
    }

    private static void backup(File source) throws Exception {
        File backup = new File(source.getPath() + ".bak");
        backup.delete();
        source.renameTo(backup);
    }

    public static void copy(File source, File dest) throws Exception {
        FileInputStream in = new FileInputStream(source);
        FileOutputStream out = new FileOutputStream(dest);
        int c;

        while ((c = in.read()) != -1)
           out.write(c);

        in.close();
        out.close();
    }
    
    private void unswapConfigurationFiles() throws Exception {
        System.out.println("Replacing original configuration files...");

        //
        // restore the backed-up policy directories
        //
        restorePolicies();

        // 
        // restore from .baks of all config files
        //
        restore("fcfg.properties", new File(FCFG));
        restore(JAAS, new File(JAAS_PATH));
        restore(TOMCAT_USERS_TEMPLATE, new File(TOMCAT_USERS_TEMPLATE_PATH));
        restore(WEB_XML, new File(WEB_XML_PATH));
    }

    private void restore(String name, File activeConfig) throws Exception {
        File newConfig = new File(m_configDir, name);
        if (newConfig.exists()) {
            System.out.println(name + " was an override. Restoring original...");
            File backup = new File(activeConfig.getPath() + ".bak");
            activeConfig.delete();
            backup.renameTo(activeConfig);
        } else {
            System.out.println(name + " was not an override; no need to restore.");
        }
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

    public static void execute(String cmd) 
    {
        if (sbOut != null && sbErr != null)
        {
            sbOut.reset();
            sbErr.reset();
            ExecUtility.execCommandLineUtility(FEDORA_HOME + cmd, sbOut, sbErr);
        }
        else
        {
            ExecUtility.execCommandLineUtility(FEDORA_HOME + cmd);
        }
    }
    
}

package fedora.test;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Constructor;
import java.sql.Connection;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

import junit.extensions.TestSetup;
import junit.framework.Test;
import fedora.server.config.Configuration;
import fedora.server.config.DatastoreConfiguration;
import fedora.server.config.ModuleConfiguration;
import fedora.server.config.Parameter;
import fedora.server.config.ServerConfiguration;
import fedora.server.config.ServerConfigurationParser;
import fedora.server.storage.ConnectionPool;
import fedora.server.storage.lowlevel.DefaultLowlevelStorage;
import fedora.server.storage.lowlevel.FileSystem;
import fedora.server.utilities.DDLConverter;
import fedora.server.utilities.SQLUtility;
import fedora.server.utilities.TableSpec;
import fedora.utilities.ExecUtility;
import fedora.utilities.FileUtils;

/**
 * This should be refactored as a kind of FedoraTestCase, perhaps one whose setup
 * and/or teardown are responsible for purging the repository
 * 
 * @author Edwin Shin
 */
public class FedoraServerTestSetup 
  extends    TestSetup 
  implements FedoraTestConstants {

    private File m_configDir;
    static ByteArrayOutputStream sbOut = null;
    static ByteArrayOutputStream sbErr = null;    
    static FileSystem filesystem;
    
    /**
     * @param test
     */
    public FedoraServerTestSetup(Test test) {
        super(test);
    }

    public FedoraServerTestSetup(Test test, String suiteClassName) {
        super(test);

		//System.out.println("Using suite configuration package: " + suiteClassName + "CFG");
        //m_configDir = new File(new File(testHome), 
        //                       (suiteClassName.replaceAll("\\.", "/")) + "CFG");
    }

    public void setUp() throws Exception {

    }
   
    /**
     * If this instance started the server, shut it down (and signal that it
     * can be started again by setting PROP_SETUP to true).
     */
    public void tearDown() throws Exception {

    }

    private void backupPolicies() throws Exception {
		System.out.println("Backing up poliices...");
        ServerConfiguration config = new ServerConfigurationParser(new FileInputStream(FCFG)).parse();
        Configuration authzConfig = config.getModuleConfiguration("fedora.server.security.Authorization");
        backupDir(authzConfig.getParameter("REPOSITORY-POLICIES-DIRECTORY").getValue(true), null);
		backupDir(authzConfig.getParameter("SURROGATE-POLICIES-DIRECTORY").getValue(true), null);;
    }

    private void restorePolicies() throws Exception {
        ServerConfiguration config = new ServerConfigurationParser(new FileInputStream(FCFG)).parse();
        Configuration authzConfig = config.getModuleConfiguration("fedora.server.security.Authorization");
        restoreDir(authzConfig.getParameter("REPOSITORY-POLICIES-DIRECTORY").getValue(true), null);
		restoreDir(authzConfig.getParameter("SURROGATE-POLICIES-DIRECTORY").getValue(true), null);
    }

    private void restoreDir(String toDirName, String sourceDirName) throws Exception {
        
        // clear out the active directory
        File activeDir = new File(toDirName);
        FileUtils.delete(activeDir);
        activeDir.mkdirs();

        File fromDir = null;
        File[] sourceFiles = null;
        if (sourceDirName == null){
        	fromDir = new File(toDirName + "_ORIG");
			sourceFiles = fromDir.listFiles();
        	
        } else {
			fromDir = new File(sourceDirName);
			sourceFiles = fromDir.listFiles();
        	
        }
        //File[] sourceFiles = new File(toDirName + "_ORIG").listFiles();
        
		// copy all files from sourceDirName + "_ORIG" to the active directory        
        for (int i = 0; i < sourceFiles.length; i++) {
           
            //SDP
			//copy(sourceFiles[i], new File(activeDir, sourceFiles[i].getName()));
			if (!sourceFiles[i].isDirectory() && sourceFiles[i].canRead()) {
				copy(sourceFiles[i], new File(activeDir, sourceFiles[i].getName()));
				sourceFiles[i].delete();
			}
			// SDP: recurse directories
			else if (sourceFiles[i].isDirectory()){
				restoreDir(activeDir.getAbsolutePath() + File.separator + sourceFiles[i].getName(),
						   fromDir.getAbsolutePath() + File.separator + sourceFiles[i].getName());
			}
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
            FileInputStream fis = new FileInputStream(FCFG);
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
    
    
    
    private static String getRIStoreLocation() throws Exception {
        ServerConfiguration fcfg = getServerConfiguration();
        ModuleConfiguration mcfg = fcfg.getModuleConfiguration("fedora.server.resourceIndex.ResourceIndex");
        String datastore = mcfg.getParameter("datastore").getValue();
        DatastoreConfiguration dcfg = fcfg.getDatastoreConfiguration(datastore);
        return dcfg.getParameter("path").getValue(true);
    }


    public static void execute(String cmd) {
        if (sbOut != null && sbErr != null) {
            sbOut.reset();
            sbErr.reset();
            ExecUtility.execCommandLineUtility(FEDORA_HOME + cmd, sbOut, sbErr);
        } else {
            ExecUtility.execCommandLineUtility(FEDORA_HOME + cmd);
        }
    }
}

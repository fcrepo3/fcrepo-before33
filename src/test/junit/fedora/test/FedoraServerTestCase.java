package fedora.test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Constructor;
import java.sql.Connection;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import fedora.client.FedoraClient;
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
import fedora.utilities.FileUtils;

/**
 * Base class for JUnit tests that assume a running Fedora instance.
 * 
 * 
 * @author Edwin Shin
 */
public abstract class FedoraServerTestCase extends FedoraTestCase {

    private File m_configDir;
    public static String ssl = "http";
    
    public FedoraServerTestCase() {
        super();
    }
    
    public FedoraServerTestCase(String name) {
        super(name);
    }

    public static void main(String[] args) {
        junit.textui.TestRunner.run(FedoraServerTestCase.class);
    }

    public void setUp() throws Exception {

    }

    public void tearDown() throws Exception {

    }
   
    public static ServerConfiguration getServerConfiguration() {
        try {
        	return new ServerConfigurationParser(
                    new FileInputStream(FCFG)).parse();
        } catch(Exception e) {
            fail(e.getMessage());
            return null;
        }        
    }
    
    public static String getBaseURL() {
        return getProtocol() + "://" + getHost() + ":" + getPort() + "/fedora";  
    }
    
    public static String getHost() {
        return getServerConfiguration().getParameter("fedoraServerHost").getValue();
    }
    
    public static String getPort() {
        String port=null;
        if(getProtocol().equals("http")) {
            port = getServerConfiguration().getParameter("fedoraServerPort").getValue();
        } else {
            port = getServerConfiguration().getParameter("fedoraRedirectPort").getValue();
        }
        return port;
    }

    // hack to dynamically set protocol based on settings in beSecurity
    // Settings for fedoraInternalCall-1 should have callSSL=true when server is secure    
    public static String getProtocol() {
        BufferedReader br = null;
        try {
            br = new BufferedReader(new InputStreamReader(new FileInputStream(BESECURITY)));
            String line = null;
            while ((line = br.readLine()) != null) {
                if(line.indexOf("role=\"fedoraInternalCall-1\"") > 0    &&
                        line.indexOf("callSSL=\"true\"") > 0) {
                        ssl = "https";
                        break;
                }
            }
        } catch (Exception e) {
        	System.out.println("fedora.home: " + FEDORA_HOME);
            fail("beSecurity file Not found: "+BESECURITY.getAbsolutePath());
        } finally {
            try {
                if( br!=null ) {
                    br.close();
                    br=null;
                }
            } catch (Exception e) {
                System.out.println("Unable to close BufferdReader");
            }
        }
        return ssl;
    }
    
    public static String getUsername() {
        return getServerConfiguration().getParameter("adminUsername").getValue();
    }
    
    public static String getPassword() {
        return getServerConfiguration().getParameter("adminPassword").getValue();
    }

    public void usePolicies(String dirName) throws Exception {
        File policyBaseDir = new File(m_configDir, dirName);
        System.out.println("Using policies from " + policyBaseDir.getPath());

        // currently just blows away existing policies and replaces them with
        // whatever's in dirName

        System.out.println("Replacing policies...");
        replacePolicies(new File(policyBaseDir, "repository-policies"), 
                        "REPOSITORY-POLICIES-DIRECTORY");
		replacePolicies(new File(policyBaseDir, "surrogate-policies"), 
						"SURROGATE-POLICIES-DIRECTORY");
        File backendSecurityReplacement = new File(dirName, "beSecurity.xml");
        if (backendSecurityReplacement.exists()) {
            File currentBackendSecurity = BESECURITY;
            currentBackendSecurity.delete();
            FileUtils.copy(backendSecurityReplacement, currentBackendSecurity);
        }
       
        System.out.println("Telling server to reload policies...");
        FedoraClient client = new FedoraClient(getBaseURL(), getUsername(), getPassword());
        client.reloadPolicies();
    }
    
    private void replacePolicies(File fromDir, String toDirProp) throws Exception {
        Configuration config = getServerConfiguration().getModuleConfiguration("fedora.server.security.Authorization");
        Parameter p = config.getParameter(toDirProp);
        File toDir = new File(p.getValue(p.getIsFilePath()));
        if (toDir.exists()) {
			// clear out the active policy directory
            FileUtils.delete(toDir);
        	
        	// copy junit config policy files into active directory
            if (fromDir.exists()) {
                FileUtils.copy(fromDir, toDir);
            }
        }
    }
    
    public static FedoraClient getFedoraClient() throws Exception {
    	return new FedoraClient(getBaseURL(), getUsername(), getPassword());
    }
    
    /**
     * Heavy handed way to delete all of Fedora file storage locations
     * @throws Exception
     */
    public static void deleteStores() throws Exception {
    	ServerConfiguration fcfg = getServerConfiguration();
        ModuleConfiguration mcfg = fcfg.getModuleConfiguration("fedora.server.storage.lowlevel.ILowlevelStorage");
    	Iterator params = mcfg.getParameters().iterator();
    	Map storeConfig = new HashMap();
    	while (params.hasNext()) {
    		Parameter param = (Parameter)params.next();
    		storeConfig.put(param.getName(), param.getValue(param.getIsFilePath()));
    	}
    	
    	Object[] parameters = new Object[] {storeConfig};
		Class[] parameterTypes = new Class[] {Map.class};
    	ClassLoader loader = ClassLoader.getSystemClassLoader();
    	Class fsClass = loader.loadClass((String)storeConfig.get(DefaultLowlevelStorage.FILESYSTEM));
		Constructor constructor = fsClass.getConstructor(parameterTypes);
		FileSystem filesystem = (FileSystem) constructor.newInstance(parameters);
		filesystem.deleteDirectory((String)storeConfig.get(DefaultLowlevelStorage.OBJECT_STORE_BASE));
		filesystem.deleteDirectory((String)storeConfig.get(DefaultLowlevelStorage.DATASTREAM_STORE_BASE));
		//FileUtils.deleteDirectory(getRIStoreLocation());		
    }
    
    public static void dropDBTables() throws Exception {
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
	                command = ddlConverter.getDeleteDDL((String)commands.next());
	                stmt.execute(command);
	            }
	        }
        } finally {
	        if (stmt != null) stmt.close();
	        if (conn != null) conn.close();
        }
    }
    
    private static DDLConverter getDDLConverter() throws Exception {
        ServerConfiguration fcfg = getServerConfiguration();
        ModuleConfiguration mcfg = fcfg.getModuleConfiguration("fedora.server.storage.ConnectionPoolManager");
        String defaultPoolName = mcfg.getParameter("defaultPoolName").getValue();
        DatastoreConfiguration dcfg = fcfg.getDatastoreConfiguration(defaultPoolName);
        String ddlConverterClassName = dcfg.getParameter("ddlConverter").getValue();
        return (DDLConverter)Class.forName(ddlConverterClassName).newInstance();
    }
}

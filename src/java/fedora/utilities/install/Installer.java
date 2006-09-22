package fedora.utilities.install;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Properties;

import org.dom4j.DocumentException;

import fedora.server.config.ServerConfiguration;
import fedora.server.config.ServerConfigurationParser;
import fedora.server.security.BESecurityConfig;
import fedora.server.security.DefaultRoleConfig;
import fedora.utilities.FileUtils;
import fedora.utilities.Zip;
import fedora.utilities.install.container.Container;
import fedora.utilities.install.webxml.WebXML;

public class Installer {
    private Distribution _dist;
    private InstallOptions _opts;
    
    private File fedoraHome;
    private File installDir;
    
    public Installer(Distribution dist,
                     InstallOptions opts) {
        _dist = dist;
        _opts = opts;
        fedoraHome = new File(_opts.getValue(InstallOptions.FEDORA_HOME));
        installDir = new File(fedoraHome, "install" + File.separator);
    }

    /**
     * Install the distribution based on the options.
     */
    public void install() throws InstallationFailedException {        
    	installFedoraHome();
    	
    	Container container = Container.getContainer(_dist, _opts);
    	container.install();
		container.deploy(buildWAR());
		if (_opts.getBooleanValue(InstallOptions.DEPLOY_LOCAL_SERVICES, true)) {
			deployLocalService(container, Distribution.FOP_WAR);
			deployLocalService(container, Distribution.IMAGEMANIP_WAR);
			deployLocalService(container, Distribution.SAXON_WAR);
		}
		if (_opts.getValue(InstallOptions.DATABASE).equals(InstallOptions.EMBEDDED_MCKOI)) {
			installEmbeddedMcKoi();
		}
		System.out.println("Installation complete.");
    }
    
    private void installFedoraHome() throws InstallationFailedException {
		if (!fedoraHome.exists() && !fedoraHome.mkdirs()) {
			throw new InstallationFailedException("Unable to create FEDORA_HOME: " + fedoraHome.getAbsolutePath());
		}
		if (!fedoraHome.isDirectory()) {
			throw new InstallationFailedException(fedoraHome.getAbsolutePath() + " is not a directory");
		}
		
		installDir.mkdirs();
		
		System.out.println("Preparing FEDORA_HOME...");
		try {
			Zip.unzip(_dist.get(Distribution.FEDORA_HOME), fedoraHome);
		} catch (IOException e) {
			throw new InstallationFailedException(e.getMessage(), e);
		}
		
		installFCFG();
		installBESecurity();
    }
    
    private File buildWAR() throws InstallationFailedException {
    	System.out.println("Preparing fedora.war...");
		// build a staging area in FEDORA_HOME
    	try {
			File warStage = new File(installDir, "fedorawar" + File.separator);
			warStage.mkdirs();
			Zip.unzip(_dist.get(Distribution.FEDORA_WAR), warStage);
			
			// modify web.xml
			System.out.println("Processing web.xml");
	        File distWebXML = new File(warStage, "WEB-INF/web.xml");
	        WebXML webXML = new WebXML(distWebXML, _opts);
	        webXML.setSecurityConstraints();	        
	        webXML.write(distWebXML.getAbsolutePath());
	        File fedoraWar = new File(installDir, Distribution.FEDORA_WAR);
	        Zip.zip(fedoraWar, warStage.listFiles());
	        return fedoraWar;

    	} catch (FileNotFoundException e) {
			throw new InstallationFailedException(e.getMessage(), e);
    	} catch(IOException e) {
    		throw new InstallationFailedException(e.getMessage(), e);
    	} catch (DocumentException e) {
    		throw new InstallationFailedException(e.getMessage(), e);
		}
    }
    
    private void installFCFG() throws InstallationFailedException {
    	System.out.println("\tInstalling fedora.fcfg");
    	File fcfgBase = new File(fedoraHome, "server/fedora-internal-use/config/fedora-base.fcfg");
    	File fcfg = new File(fedoraHome, "server/config/fedora.fcfg");
        
        Properties props = new Properties();
        if (_opts.getValue(InstallOptions.FEDORA_ADMIN_PASS) != null) {
        	props.put("server.adminPassword", _opts.getValue(InstallOptions.FEDORA_ADMIN_PASS));
        }
        if (_opts.getValue(InstallOptions.TOMCAT_HTTP_PORT) != null) {
        	props.put("server.fedoraServerPort", _opts.getValue(InstallOptions.TOMCAT_HTTP_PORT));
        }
        if (_opts.getValue(InstallOptions.TOMCAT_SHUTDOWN_PORT) != null) {
        	props.put("server.fedoraShutdownPort", _opts.getValue(InstallOptions.TOMCAT_SHUTDOWN_PORT));
        }
        if (_opts.getValue(InstallOptions.TOMCAT_SSL_PORT) != null) {
        	props.put("server.fedoraRedirectPort", _opts.getValue(InstallOptions.TOMCAT_SSL_PORT));
        }
        String database = _opts.getValue(InstallOptions.DATABASE);
        if (database.equals(InstallOptions.BUNDLED_MCKOI) || database.equals(InstallOptions.MCKOI)) {
        	setDatabasePools(props, "localMcKoiPool");
        } else if (database.equals(InstallOptions.BUNDLED_MYSQL) || database.equals(InstallOptions.MYSQL)) {
        	setDatabasePools(props, "localMySQLPool");
        } else if (database.equals(InstallOptions.EMBEDDED_MCKOI)) {
        	setDatabasePools(props, "localMcKoiPool");
        	props.put("datastore.localMcKoiPool.jdbcURL", "jdbc:mckoi:local://" + fedoraHome.getAbsolutePath() + 
        			"/" + Distribution.MCKOI_BASENAME +"/db.conf?create_or_boot=true");
        } else if (database.equals("oracle")) {
        	setDatabasePools(props, "localOracle9iPool");
        } else {
        	throw new InstallationFailedException("unable to configure for unknown database: " + database);
        }
        if (_opts.getBooleanValue(InstallOptions.XACML_ENABLED, true)) {
        	props.put("module.fedora.server.security.Authorization.ENFORCE-MODE", "enforce-policies");
        } else {
        	props.put("module.fedora.server.security.Authorization.ENFORCE-MODE", "permit-all-requests");
        }
        
        props.put("module.fedora.server.access.Access.doMediateDatastreams", _opts.getValue(InstallOptions.APIA_AUTH_REQUIRED));
        
        // FIXME any others?
        
        try {
	        FileInputStream fis = new FileInputStream(fcfgBase);
	        ServerConfiguration config = new ServerConfigurationParser(fis).parse();
	        config.applyProperties(props);
	        config.serialize(new FileOutputStream(fcfg));
        } catch(IOException e) {
    		throw new InstallationFailedException(e.getMessage(), e);
    	}
    }
    
    private void installBESecurity() throws InstallationFailedException {
    	System.out.println("\tInstalling beSecurity");
    	File beSecurity = new File(fedoraHome, "/server/config/beSecurity.xml");
    	boolean apiaAuth = _opts.getBooleanValue(InstallOptions.APIA_AUTH_REQUIRED, false);
    	boolean apiaSSL = _opts.getBooleanValue(InstallOptions.APIA_SSL_REQUIRED, false);
    	//boolean apimSSL = _opts.getBooleanValue(InstallOptions.APIM_SSL_REQUIRED, false);
    	
    	PrintWriter pwriter;
		try {
			pwriter = new PrintWriter(beSecurity);
		} catch (FileNotFoundException e) {
			throw new InstallationFailedException(e.getMessage(), e);
		}
    	BESecurityConfig becfg = new BESecurityConfig();

    	becfg.setDefaultConfig(new DefaultRoleConfig());
    	becfg.setInternalBasicAuth(new Boolean(apiaAuth));
    	becfg.setInternalIPList(new String[] {"127.0.0.1"});
    	becfg.setInternalPassword("changeme");
    	becfg.setInternalSSL(new Boolean(apiaSSL));
    	becfg.setInternalUsername("fedoraIntCallUser");
    	becfg.write(true, true, pwriter);
    	pwriter.close();
    }
    
    private void installEmbeddedMcKoi() throws InstallationFailedException {
    	System.out.println("Installing embedded McKoi...");
    	try {
			Zip.unzip(_dist.get(Distribution.MCKOI), fedoraHome);
			File mckoiHome = new File(fedoraHome, Distribution.MCKOI_BASENAME);
			
			// Default is to create data and log dirs relative to JVM, not conf location
			File mckoiProps = new File(mckoiHome, "db.conf");
			Properties mckoiConf = FileUtils.loadProperties(mckoiProps);
			mckoiConf.setProperty("root_path", "configuration");
			mckoiConf.store(new FileOutputStream(mckoiProps), null);
			
			String container = _opts.getValue(InstallOptions.SERVLET_ENGINE);
			if (container.equals(InstallOptions.BUNDLED_TOMCAT)) {
				File tomcatHome = new File(_opts.getValue(InstallOptions.TOMCAT_HOME));
				FileUtils.copy(new FileInputStream(new File(mckoiHome, "mckoidb.jar")),
						new FileOutputStream(new File(tomcatHome, "common/lib/mckoidb.jar")));
			}
    	} catch(IOException e) {
    		throw new InstallationFailedException(e.getMessage(), e);
    	}
    }
    
    private void deployLocalService(Container container, String filename) throws InstallationFailedException {
    	try {
			File war = new File(installDir, filename);
			FileUtils.copy(_dist.get(filename), new FileOutputStream(war));
			container.deploy(war);
		} catch (IOException e) {
			throw new InstallationFailedException(e.getMessage(), e);
		}
    }
    
    private void setDatabasePools(Properties props, String poolName) {
    	props.put("module.fedora.server.storage.DOManager.storagePool", poolName);
    	props.put("module.fedora.server.search.FieldSearch.connectionPool", poolName);
    	props.put("module.fedora.server.storage.ConnectionPoolManager.poolNames", poolName);
    	props.put("module.fedora.server.storage.ConnectionPoolManager.defaultPoolName", poolName);
    }
    
    /**
     * Command-line entry point.
     */
    public static void main(String[] args) {

        try {
            Distribution dist = new ClassLoaderDistribution();
            InstallOptions opts = null;

            if (args.length == 0) {
                opts = new InstallOptions(dist.isBundled());
            } else if (args.length == 1) {
                Properties props = FileUtils.loadProperties(new File(args[0]));
                opts = new InstallOptions(props, dist.isBundled());
            } else {
                System.err.println("ERROR: Too many arguments.");
                System.err.println("Usage: java -jar fedora-install.jar [options-file]");
                System.exit(1);
            }

            new Installer(dist, opts).install();

        } catch (Exception e) {
            printException(e);
            System.exit(1);
        }
    }

    /**
     * Print a message appropriate for the given exception
     * in as human-readable way as possible.
     */
    private static void printException(Exception e) {

        if (e instanceof InstallationCancelledException) {
            System.out.println("Installation cancelled.");
            return;
        }

        boolean recognized = false;
        String msg = "ERROR: ";
        if (e instanceof InstallationFailedException) {
            msg += "Installation failed: " + e.getMessage();
            recognized = true;
        } else if (e instanceof OptionValidationException) {
            OptionValidationException ove = (OptionValidationException) e;
            msg += "Bad value for '" + ove.getOptionId() + "': " + e.getMessage();
            recognized = true;
        }
        
        if (recognized) {
            System.err.println(msg);
            if (e.getCause() != null) {
                System.err.println("Caused by: ");
                e.getCause().printStackTrace(System.err);
            }
        } else {
            System.err.println(msg + "Unexpected error; installation aborted.");
            e.printStackTrace();
        }
    }
}

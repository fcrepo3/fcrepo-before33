package fedora.utilities.install;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.Properties;

import org.apache.log4j.PropertyConfigurator;

import fedora.server.config.ServerConfiguration;
import fedora.server.config.ServerConfigurationParser;
import fedora.server.security.BESecurityConfig;
import fedora.server.security.DefaultRoleConfig;
import fedora.server.security.servletfilters.xmluserfile.FedoraUsers;
import fedora.server.security.servletfilters.xmluserfile.User;
import fedora.utilities.ExecUtility;
import fedora.utilities.FileUtils;
import fedora.utilities.Zip;
import fedora.utilities.install.container.Container;
import fedora.utilities.install.container.FedoraWebXML;

public class Installer {
	static {
		//send all log4j (WARN only) output to STDOUT
		Properties props = new Properties();
		props.setProperty("log4j.appender.STDOUT", "org.apache.log4j.ConsoleAppender");
		props.setProperty("log4j.rootLogger", "WARN, STDOUT");
		PropertyConfigurator.configure(props);
		
		//tell commons-logging to use log4j
		final String pfx = "org.apache.commons.logging.";
		if (System.getProperty(pfx + "LogFactory") == null) {
			System.setProperty(pfx + "LogFactory", pfx + "impl.Log4jFactory");
			System.setProperty(pfx + "Log", pfx + "impl.Log4JLogger");
		}
	}
	
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
			deployLocalService(container, Distribution.DEMO_WAR);
		}
		if (_opts.getValue(InstallOptions.DATABASE).equals(InstallOptions.INCLUDED)) {
			installEmbeddedMcKoi();
		}
		
		// Write out the install options used to a properties file in the install directory
		try {
			OutputStream out = new FileOutputStream(new File(installDir, "install.properties"));
			_opts.dump(out);
			out.close();
		} catch (Exception e) {
			throw new InstallationFailedException(e.getMessage(), e);
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
            setScriptsExecutable(new File(fedoraHome, "client/bin"));
            setScriptsExecutable(new File(fedoraHome, "server/bin"));
		} catch (IOException e) {
			throw new InstallationFailedException(e.getMessage(), e);
		}
		
		installFCFG();
		installFedoraUsers();
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
	        FedoraWebXML webXML = new FedoraWebXML(distWebXML.getAbsolutePath(), _opts);
	        Writer outputWriter = new BufferedWriter(new FileWriter(distWebXML));
	        webXML.write(outputWriter);
	        outputWriter.close();

	        File fedoraWar = new File(installDir, Distribution.FEDORA_WAR);
	        Zip.zip(fedoraWar, warStage.listFiles());
	        return fedoraWar;

    	} catch (FileNotFoundException e) {
			throw new InstallationFailedException(e.getMessage(), e);
    	} catch(IOException e) {
    		throw new InstallationFailedException(e.getMessage(), e);
    	}
    }
    
    private void installFCFG() throws InstallationFailedException {
    	System.out.println("\tInstalling fedora.fcfg");
    	File fcfgBase = new File(fedoraHome, "server/fedora-internal-use/config/fedora-base.fcfg");
    	File fcfg = new File(fedoraHome, "server/config/fedora.fcfg");
        
        Properties props = new Properties();
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
        String dbPoolName = "";
        String backslashIsEscape = "true";
        if (database.equals(InstallOptions.MCKOI) || database.equals(InstallOptions.INCLUDED)) {
        	dbPoolName = "localMcKoiPool";
        	backslashIsEscape = "false";
        } else if (database.equals(InstallOptions.MYSQL)) {
        	dbPoolName = "localMySQLPool";
        } else if (database.equals(InstallOptions.ORACLE)) {
        	dbPoolName = "localOraclePool";
        	backslashIsEscape = "false";
        } else if (database.equals(InstallOptions.POSTGRESQL)) {
        	dbPoolName = "localPostgresqlPool";
        } else {
        	throw new InstallationFailedException("unable to configure for unknown database: " + database);
        }
        props.put("module.fedora.server.storage.DOManager.storagePool", dbPoolName);
    	props.put("module.fedora.server.search.FieldSearch.connectionPool", dbPoolName);
    	props.put("module.fedora.server.storage.ConnectionPoolManager.poolNames", dbPoolName);
    	props.put("module.fedora.server.storage.ConnectionPoolManager.defaultPoolName", dbPoolName);
    	props.put("module.fedora.server.storage.lowlevel.ILowlevelStorage.backslash_is_escape", backslashIsEscape);
        props.put("datastore." + dbPoolName + ".jdbcURL", _opts.getValue(InstallOptions.DATABASE_JDBCURL));
        props.put("datastore." + dbPoolName + ".dbUsername", _opts.getValue(InstallOptions.DATABASE_USERNAME));
        props.put("datastore." + dbPoolName + ".dbPassword", _opts.getValue(InstallOptions.DATABASE_PASSWORD));
        props.put("datastore." + dbPoolName + ".jdbcDriverClass", _opts.getValue(InstallOptions.DATABASE_DRIVERCLASS));
        
        if (_opts.getBooleanValue(InstallOptions.XACML_ENABLED, true)) {
        	props.put("module.fedora.server.security.Authorization.ENFORCE-MODE", "enforce-policies");
        } else {
        	props.put("module.fedora.server.security.Authorization.ENFORCE-MODE", "permit-all-requests");
        }
        
        props.put("module.fedora.server.access.Access.doMediateDatastreams", _opts.getValue(InstallOptions.APIA_AUTH_REQUIRED));
        
        try {
	        FileInputStream fis = new FileInputStream(fcfgBase);
	        ServerConfiguration config = new ServerConfigurationParser(fis).parse();
	        config.applyProperties(props);
	        config.serialize(new FileOutputStream(fcfg));
        } catch(IOException e) {
    		throw new InstallationFailedException(e.getMessage(), e);
    	}
    }
    
    private void installFedoraUsers() throws InstallationFailedException {
    	FedoraUsers fu = FedoraUsers.getInstance();
    	for (User user : fu.getUsers()) {
			if (user.getName().equals("fedoraAdmin")) {
				user.setPassword(_opts.getValue(InstallOptions.FEDORA_ADMIN_PASS));
			}
		}
    	
    	try {
    		Writer outputWriter = new BufferedWriter(new FileWriter(FedoraUsers.fedoraUsersXML));
			fu.write(outputWriter);
			outputWriter.close();
		} catch (IOException e) {
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
			pwriter = new PrintWriter(new FileOutputStream(beSecurity));
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
    
    private void deployLocalService(Container container, String filename) throws InstallationFailedException {
    	try {
			File war = new File(installDir, filename);
			if (!FileUtils.copy(_dist.get(filename), new FileOutputStream(war))) {
				throw new InstallationFailedException("Copy to " + 
	        			war.getAbsolutePath() + " failed.");
			}
			container.deploy(war);
		} catch (IOException e) {
			throw new InstallationFailedException(e.getMessage(), e);
		}
    }

	/**
     * Make scripts (ending with .sh) executable on *nix systems.
     */
    public static void setScriptsExecutable(File dir) {
		String os = System.getProperty("os.name");
		if (os != null && !os.startsWith("Windows")) {
			FileFilter filter = FileUtils.getSuffixFileFilter(".sh");
			setExecutable(dir, filter);
		}
    }
    
    private static void setExecutable(File dir, FileFilter filter) {
    	File[] files;
    	if (filter != null) {
    		files = dir.listFiles(filter);
    	} else {
    		files = dir.listFiles();
    	}
		for (int i = 0; i < files.length; i++) {
			ExecUtility.exec("chmod +x " + files[i].getAbsolutePath());
		}
    }
    
    /**
     * Command-line entry point.
     */
    public static void main(String[] args) {

        try {
            Distribution dist = new ClassLoaderDistribution();
            InstallOptions opts = null;

            if (args.length == 0) {
                opts = new InstallOptions(dist);
            } else if (args.length == 1) {
                Properties props = FileUtils.loadProperties(new File(args[0]));
                opts = new InstallOptions(dist, props);
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

package fedora.utilities.install.container;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import fedora.utilities.FileUtils;
import fedora.utilities.install.Distribution;
import fedora.utilities.install.InstallOptions;
import fedora.utilities.install.InstallationFailedException;

public abstract class Tomcat extends Container {
	private File tomcatHome;
	private File webapps;
	private File conf;
	private File common_lib;
	
	public Tomcat(Distribution dist, InstallOptions options) {
		super(dist, options);
		tomcatHome = new File(getOptions().getValue(InstallOptions.TOMCAT_HOME));
		webapps = new File(tomcatHome, "webapps" + File.separator);
		conf = new File(tomcatHome, "conf" + File.separator);
		common_lib = new File(tomcatHome, "common" + File.separator + "lib" + File.separator);
	}

	public void deploy(File war) throws InstallationFailedException {
		System.out.println("Deploying " + war.getName() + "...");
        File dest = new File(webapps, war.getName());
        if (!FileUtils.copy(war, dest)) {
			throw new InstallationFailedException("Deploy failed: unable to copy " + 
					war.getAbsolutePath() + " to " + dest.getAbsolutePath());
		}
	}

	public void install() throws InstallationFailedException {
		installTomcat();
		installServerXML();
		installKeystore();
		installJDBCDriver();
	}
	
	protected abstract void installTomcat() throws InstallationFailedException;
	
	protected abstract void installServerXML() throws InstallationFailedException;
	
	protected abstract void installKeystore() throws InstallationFailedException;
	
	protected void installJDBCDriver() throws InstallationFailedException {
		String database = getOptions().getValue(InstallOptions.DATABASE);
        InputStream is;
        File driver = null;
        boolean success = true;
        try {
	        if (database.equals(InstallOptions.MCKOI)) {
	        	is = getDist().get(Distribution.JDBC_MCKOI);
	        	driver = new File(getCommonLib(), Distribution.JDBC_MCKOI);
	        	success = FileUtils.copy(is, new FileOutputStream(driver));
	        } else if (database.equals(InstallOptions.MYSQL)) {
	        	is = getDist().get(Distribution.JDBC_MYSQL);
	        	driver = new File(getCommonLib(), Distribution.JDBC_MYSQL);
	        	success = FileUtils.copy(is, new FileOutputStream(driver));
	        }
	        if (!success) {
	        	throw new InstallationFailedException("Copy to " + 
	        			driver.getAbsolutePath() + " failed.");
	        }
        } catch (IOException e) {
        	throw new InstallationFailedException(e.getMessage(), e);
		}
	}
	
	protected final File getTomcatHome() {
		return tomcatHome;
	}
	
	protected final File getWebapps() {
		return webapps;
	}
	
	protected final File getConf() {
		return conf;
	}
	
	protected final File getCommonLib() {
		return common_lib;
	}
}

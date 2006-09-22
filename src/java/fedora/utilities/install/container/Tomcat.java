package fedora.utilities.install.container;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.dom4j.DocumentException;

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
        try {
			FileUtils.copy(war, dest);
		} catch (IOException e) {
			throw new InstallationFailedException(e.getMessage(), e);
		}
	}

	public void install() throws InstallationFailedException {
		installTomcat();
		installServerXML();
		installKeystore();
		installJAASConfig();
		installJDBCDriver();
		installTomcatUsersXML();
	}
	
	protected abstract void installTomcat() throws InstallationFailedException;
	
	protected abstract void installServerXML() throws InstallationFailedException;
	
	protected abstract void installKeystore() throws InstallationFailedException;
	
	protected abstract void installJAASConfig() throws InstallationFailedException;
	
	protected void installJDBCDriver() throws InstallationFailedException {
		String database = getOptions().getValue(InstallOptions.DATABASE);
        InputStream is;
        File driver;
        try {
	        if (database.equals(InstallOptions.BUNDLED_MCKOI)) {
	        	is = getDist().get(Distribution.JDBC_MCKOI);
	        	driver = new File(getCommonLib(), Distribution.JDBC_MCKOI);
	        	FileUtils.copy(is, new FileOutputStream(driver));
	        } else if (database.equals(InstallOptions.BUNDLED_MYSQL)) {
	        	is = getDist().get(Distribution.JDBC_MYSQL);
	        	driver = new File(getCommonLib(), Distribution.JDBC_MYSQL);
	        	FileUtils.copy(is, new FileOutputStream(driver));
	        }
        } catch (IOException e) {
        	throw new InstallationFailedException(e.getMessage(), e);
		}
	}
	
	protected void installTomcatUsersXML() throws InstallationFailedException {
		File distTomcatUsersXML = new File(conf, "tomcat-users.xml");
    	String fedoraAdminPass = getOptions().getValue(InstallOptions.FEDORA_ADMIN_PASS);
        TomcatUsersXML tomcatUsersXML;
		try {
			tomcatUsersXML = new TomcatUsersXML(distTomcatUsersXML);
			tomcatUsersXML.setFedoraAdminPassword(fedoraAdminPass);
	        tomcatUsersXML.write(distTomcatUsersXML.getAbsolutePath());
		} catch (DocumentException e) {
			throw new InstallationFailedException(e.getMessage(), e);
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

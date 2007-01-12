package fedora.utilities.install.container;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.dom4j.DocumentException;

import fedora.utilities.FileUtils;
import fedora.utilities.Zip;
import fedora.utilities.install.Distribution;
import fedora.utilities.install.FedoraHome;
import fedora.utilities.install.InstallOptions;
import fedora.utilities.install.InstallationFailedException;

public class BundledTomcat extends Tomcat {
	public BundledTomcat(Distribution dist, InstallOptions options) {
		super(dist, options);
	}
	
	public void install() throws InstallationFailedException {
		super.install();
		installJDBCDriver();
	}
	
	protected void installTomcat() throws InstallationFailedException {
		System.out.println("Installing Tomcat...");
		try {
			Zip.unzip(getDist().get(Distribution.TOMCAT), 
					System.getProperty("java.io.tmpdir"));
		} catch (IOException e) {
			throw new InstallationFailedException(e.getMessage(), e);
		}
		File f = new File(System.getProperty("java.io.tmpdir"), Distribution.TOMCAT_BASENAME);
		if (!FileUtils.move(f, getTomcatHome())) {
			throw new InstallationFailedException("Move to " + 
					getTomcatHome().getAbsolutePath() + " failed.");
		}
        FedoraHome.setScriptsExecutable(new File(getTomcatHome(), "bin"));
	}
	
	protected void installServerXML() throws InstallationFailedException {
		try {
	        File distServerXML = new File(getConf(), "server.xml");
	        TomcatServerXML serverXML = new TomcatServerXML(distServerXML, getOptions());
	        serverXML.update();
	        serverXML.write(distServerXML.getAbsolutePath());
		} catch (IOException e) {
			throw new InstallationFailedException(e.getMessage(), e);
		} catch (DocumentException e) {
			throw new InstallationFailedException(e.getMessage(), e);
		}
	}
	
	protected void installIncludedKeystore() throws InstallationFailedException {
		String keystoreFile = getOptions().getValue(InstallOptions.KEYSTORE_FILE);
		if (keystoreFile == null || !keystoreFile.equals(InstallOptions.INCLUDED)) {
			// nothing to do
			return;
		}
		try {
			InputStream is = getDist().get(Distribution.KEYSTORE);
			File keystore = getIncludedKeystore();

	        if (!FileUtils.copy(is, new FileOutputStream(keystore))) {
	        	throw new InstallationFailedException("Copy to " + 
	        			keystore.getAbsolutePath() + " failed.");
	        }
		} catch (IOException e) {
			throw new InstallationFailedException(e.getMessage(), e);
		}
	}
	
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
	        } else if (database.equals(InstallOptions.POSTGRESQL)) {
	        	is = getDist().get(Distribution.JDBC_POSTGRESQL);
	        	driver = new File(getCommonLib(), Distribution.JDBC_POSTGRESQL);
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
}

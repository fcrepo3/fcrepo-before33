package fedora.utilities.install.container;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.dom4j.DocumentException;

import fedora.utilities.FileUtils;
import fedora.utilities.install.Distribution;
import fedora.utilities.install.InstallOptions;
import fedora.utilities.install.InstallationFailedException;

public class ExistingTomcat extends Tomcat {
	private File installDir;

	public ExistingTomcat(Distribution dist, InstallOptions options) {
		super(dist, options);
		installDir = new File(getOptions().getValue(InstallOptions.FEDORA_HOME) + 
	    		File.separator + "install" + File.separator);
	}

	protected void installTomcat() throws InstallationFailedException {
		// nothing to do
	}

	protected void installServerXML() throws InstallationFailedException {
		try {
	        File distServerXML = new File(getConf(), "server.xml");
	        TomcatServerXML serverXML = new TomcatServerXML(distServerXML, getOptions());
	        serverXML.update();
	        
	        File example = new File(installDir, "server.xml");
	        serverXML.write(example.getAbsolutePath());
	        System.out.println("Will not overwrite existing " + 
	        		distServerXML.getAbsolutePath() + ".\n" +
	        		"Wrote example server.xml to: \n\t" +
	        		example.getAbsolutePath());
		} catch (IOException e) {
			throw new InstallationFailedException(e.getMessage(), e);
		} catch (DocumentException e) {
			throw new InstallationFailedException(e.getMessage(), e);
		}
	}

	protected void installKeystore() throws InstallationFailedException {
		String keystoreFile = getOptions().getValue(InstallOptions.KEYSTORE_FILE);
		if (keystoreFile == null) {
			// nothing to do
			return;
		}
		try {
			InputStream is;
			File keystore = new File(getConf(), Distribution.KEYSTORE);
	        if (keystoreFile.equals("default")) {
	        	is = getDist().get(Distribution.KEYSTORE);
	        } else {
	        	is = new FileInputStream(keystoreFile);
	        }
	        if (keystore.exists()) {
	        	System.out.println("Will not overwrite existing " + keystore.getAbsolutePath() + ".");
	        	keystore = new File(installDir, Distribution.KEYSTORE);
	        	System.out.println("Wrote example to: \n\t" +
	        			keystore.getAbsolutePath());
	        }
	        if (!FileUtils.copy(is, new FileOutputStream(keystore))) {
	        	throw new InstallationFailedException("Copy to " + 
	        			keystore.getAbsolutePath() + " failed.");
	        }
		} catch (IOException e) {
			throw new InstallationFailedException(e.getMessage(), e);
		}
	}

	protected void installJAASConfig() throws InstallationFailedException {
		try {
			InputStream is = getDist().get(Distribution.JAAS_CONFIG);
	        File jaasConfig = new File(getConf(), Distribution.JAAS_CONFIG);
	        if (jaasConfig.exists()) {
	        	File example = new File(installDir, Distribution.JAAS_CONFIG);
	        	System.out.println("Will not overwrite existing " + 
	        			jaasConfig.getAbsolutePath() + ".\n" + 
	        			"Wrote example to: \n\t" + example.getAbsolutePath());
	        }
	        if (!FileUtils.copy(is, new FileOutputStream(jaasConfig))) {
	        	throw new InstallationFailedException("Copy to " + 
	        			jaasConfig.getAbsolutePath() + " failed.");
	        }
	        System.out.println("Before starting Tomcat, please ensure that JAVA_OPTS points to the location of " +
	        		"jaas.config, e.g.: \n\t" +
	        		"export JAVA_OPTS=\"-Djava.security.auth.login.config=" + 
	        		jaasConfig.getAbsolutePath() + "\".");
		} catch (IOException e) {
			throw new InstallationFailedException(e.getMessage(), e);
		}
	}

}

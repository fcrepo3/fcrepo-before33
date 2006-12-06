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
	        if (keystoreFile.equals(InstallOptions.INCLUDED)) {
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
}

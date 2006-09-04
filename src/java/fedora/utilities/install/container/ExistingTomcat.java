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
		// TODO present instructions for JAAS Realm, SSL
	}

	protected void installKeystore() throws InstallationFailedException {
		// nothing to do
	}

	protected void installJAASConfig() throws InstallationFailedException {
		 try {
			InputStream is = getDist().get(Distribution.JAAS_CONFIG);
			File jaasConfig = new File(installDir, Distribution.JAAS_CONFIG);
			FileUtils.copy(is, new FileOutputStream(jaasConfig));
			System.out.println("Wrote example jaas.config to: \n\t" +
        			jaasConfig.getAbsolutePath());
		} catch (IOException e) {
			throw new InstallationFailedException(e.getMessage(), e);
		}
	}

}

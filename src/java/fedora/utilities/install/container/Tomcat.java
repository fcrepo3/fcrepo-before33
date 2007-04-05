/* The contents of this file are subject to the license and copyright terms
 * detailed in the license directory at the root of the source tree (also 
 * available online at http://www.fedora.info/license/).
 */

package fedora.utilities.install.container;

import java.io.File;

import fedora.utilities.FileUtils;
import fedora.utilities.install.Distribution;
import fedora.utilities.install.InstallOptions;
import fedora.utilities.install.InstallationFailedException;

public abstract class Tomcat extends Container {
	public static final String CONF = "conf";
	public static final String KEYSTORE = "keystore";
	private File tomcatHome;
	private File webapps;
	private File conf;
	private File common_lib;
	
	/**
	 * Target location of the included keystore file.
	 */
	private File includedKeystore;
	
	Tomcat(Distribution dist, InstallOptions options) {
		super(dist, options);
		tomcatHome = new File(getOptions().getValue(InstallOptions.TOMCAT_HOME));
		webapps = new File(tomcatHome, "webapps" + File.separator);
		conf = new File(tomcatHome, CONF + File.separator);
		common_lib = new File(tomcatHome, "common" + File.separator + "lib" + File.separator);
		includedKeystore = new File(conf, KEYSTORE);
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
		installIncludedKeystore();
	}
	
	protected abstract void installTomcat() throws InstallationFailedException;
	
	protected abstract void installServerXML() throws InstallationFailedException;
	
	protected abstract void installIncludedKeystore() throws InstallationFailedException;
	
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
	
	protected final File getIncludedKeystore() {
		return includedKeystore;
	}
}

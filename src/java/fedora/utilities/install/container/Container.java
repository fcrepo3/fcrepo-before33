/**
 * 
 */
package fedora.utilities.install.container;

import java.io.File;

import fedora.utilities.install.Distribution;
import fedora.utilities.install.InstallOptions;
import fedora.utilities.install.InstallationFailedException;

/**
 * @author Edwin Shin
 *
 */
public abstract class Container {
	private Distribution dist;
	private InstallOptions options;
	
	public Container(Distribution dist, InstallOptions options) {
		this.dist = dist;
		this.options = options;
	}
	
	public static Container getContainer(Distribution dist, InstallOptions options) {
		String servletEngine = options.getValue(InstallOptions.SERVLET_ENGINE);
		if (servletEngine.equals(InstallOptions.BUNDLED_TOMCAT)) {
			return new BundledTomcat(dist, options);
		} else if (servletEngine.equals(InstallOptions.EXISTING_TOMCAT)) {
			return new ExistingTomcat(dist, options);
		} else {
			return new DefaultContainer(dist, options);
		}
	}
	
	public abstract void deploy(File war) throws InstallationFailedException;
	
	public abstract void install() throws InstallationFailedException;
	
	protected final Distribution getDist() {
		return dist;
	}
	
	protected final InstallOptions getOptions() {
		return options;
	}
}

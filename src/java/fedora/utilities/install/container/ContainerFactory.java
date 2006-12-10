package fedora.utilities.install.container;

import fedora.utilities.install.Distribution;
import fedora.utilities.install.InstallOptions;

/**
 * A static factory that returns a Container depending on InstallOptions
 *
 */
public class ContainerFactory {
	private ContainerFactory() {}
	
	public static Container getContainer(Distribution dist, InstallOptions options) {
		String servletEngine = options.getValue(InstallOptions.SERVLET_ENGINE);
		if (servletEngine.equals(InstallOptions.INCLUDED)) {
			return new BundledTomcat(dist, options);
		} else if (servletEngine.equals(InstallOptions.EXISTING_TOMCAT)) {
			return new ExistingTomcat(dist, options);
		} else {
			return new DefaultContainer(dist, options);
		}
	}
}

package fedora.utilities.install.container;

import fedora.utilities.install.Distribution;
import fedora.utilities.install.InstallOptions;
import fedora.utilities.install.InstallationFailedException;
import fedora.utilities.install.Installer;

public class ExistingTomcat50 extends ExistingTomcat {
	
	public ExistingTomcat50(Distribution dist, InstallOptions options) {
		super(dist, options);
	}
	
	public void install() throws InstallationFailedException {
		super.install();
		Installer.installJDBCDriver(getDist(), getOptions(), getCommonLib());
	}
}

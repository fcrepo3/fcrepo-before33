package fedora.utilities.install.container;

import java.io.File;

import fedora.utilities.install.Distribution;
import fedora.utilities.install.InstallOptions;

public class DefaultContainer extends Container {

	public DefaultContainer(Distribution dist, InstallOptions options) {
		super(dist, options);
		// TODO Auto-generated constructor stub
	}

	public void deploy(File war) {
		System.out.println("WARNING: Unable to deploy to this container.");
		System.out.println(war.getAbsolutePath() + " must be manually deployed.");
	}

	public void install() {
		System.out.println("Nothing to install for this container.");
	}
}

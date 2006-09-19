package fedora.utilities.install.webxml;

import java.io.File;

import fedora.utilities.install.InstallOptions;

public class WebXMLOptions {
	public boolean apiaAuth;
	public boolean apiaSSL;
	public boolean apimSSL;
	public File fedoraHome;
	
	public WebXMLOptions(InstallOptions installOptions) {
		apiaAuth = installOptions.getBooleanValue(InstallOptions.APIA_AUTH_REQUIRED, false);
		apiaSSL = installOptions.getBooleanValue(InstallOptions.APIA_SSL_REQUIRED, false);
		apimSSL = installOptions.getBooleanValue(InstallOptions.APIM_SSL_REQUIRED, false);
		fedoraHome = new File(installOptions.getValue(InstallOptions.FEDORA_HOME));
	}
}

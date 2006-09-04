package fedora.utilities.install.webxml;

import fedora.utilities.install.InstallOptions;

public class WebXMLOptions {
	public boolean apiaAuth;
	public boolean apiaSSL;
	public boolean apimSSL;
	
	public WebXMLOptions(InstallOptions installOptions) {
		apiaAuth = installOptions.getBooleanValue(InstallOptions.APIA_AUTH_REQUIRED, false);
		apiaSSL = installOptions.getBooleanValue(InstallOptions.APIA_SSL_REQUIRED, false);
		apimSSL = installOptions.getBooleanValue(InstallOptions.APIM_SSL_REQUIRED, false);
	}
}

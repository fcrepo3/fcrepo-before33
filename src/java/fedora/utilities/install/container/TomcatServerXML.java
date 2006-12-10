package fedora.utilities.install.container;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import org.dom4j.DocumentException;
import org.dom4j.Element;

import fedora.utilities.XMLDocument;
import fedora.utilities.install.InstallOptions;

public class TomcatServerXML extends XMLDocument {	
	private InstallOptions options;
	
	public TomcatServerXML(File serverXML, InstallOptions installOptions) throws FileNotFoundException, DocumentException {
		this(new FileInputStream(serverXML), installOptions);
	}
	
	public TomcatServerXML(InputStream serverXML, InstallOptions installOptions) throws FileNotFoundException, DocumentException {
		super(serverXML);
		options = installOptions;
	}
	
	public void update() {
		setHTTPPort();
		setShutdownPort();
		setSSLPort();
	}
	
	public void setHTTPPort() {
		// Note this very significant assumption: this xpath will select exactly one connector
		Element httpConnector = (Element)getDocument().selectSingleNode("/Server/Service[@name='Catalina']/Connector[not(@scheme='https' or contains(@protocol, 'AJP'))]");
		httpConnector.addAttribute("port", options.getValue(InstallOptions.TOMCAT_HTTP_PORT));	
		httpConnector.addAttribute("enableLookups", "true"); // supports client dns/fqdn in xacml authz policies
	}
	
	public void setShutdownPort() {
		Element server = (Element)getDocument().selectSingleNode("/Server[@shutdown and @port]");
		server.addAttribute("port", options.getValue(InstallOptions.TOMCAT_SHUTDOWN_PORT));	
	}
	
	public void setSSLPort() {
		Element httpsConnector = (Element)getDocument().selectSingleNode("/Server/Service[@name='Catalina']/Connector[@scheme='https' and not(contains(@protocol, 'AJP'))]");
		if (options.getBooleanValue(InstallOptions.SSL_AVAILABLE, true)) {
			if (httpsConnector == null) {
				Element service = (Element)getDocument().selectSingleNode("/Server/Service[@name='Catalina']");
				httpsConnector = service.addElement("Connector");
				httpsConnector.addAttribute("maxThreads", "150");
				httpsConnector.addAttribute("minSpareThreads", "25");
				httpsConnector.addAttribute("maxSpareThreads", "75");
				httpsConnector.addAttribute("disableUploadTimeout", "true");
				httpsConnector.addAttribute("acceptCount", "100");
				httpsConnector.addAttribute("debug", "0");
				httpsConnector.addAttribute("scheme", "https");
				httpsConnector.addAttribute("secure", "true");
				httpsConnector.addAttribute("clientAuth", "false");
				httpsConnector.addAttribute("sslProtocol", "TLS");
			}
			httpsConnector.addAttribute("port", options.getValue(InstallOptions.TOMCAT_SSL_PORT));
			httpsConnector.addAttribute("enableLookups", "true"); // supports client dns/fqdn in xacml authz policies
			//String keystore = options.getValue(InstallOptions.KEYSTORE_FILE);
			String keystore = "conf/keystore";
			httpsConnector.addAttribute("keystoreFile", keystore);
		} else if (httpsConnector != null) {
			httpsConnector.getParent().remove(httpsConnector);
		}
	}
}

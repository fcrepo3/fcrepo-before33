package fedora.server.utilities;

import java.io.File;
import java.net.HttpURLConnection;
import java.util.Properties;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.log4j.Logger;

import org.apache.commons.httpclient.methods.GetMethod;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;

import fedora.common.HttpClient;
import fedora.server.errors.GeneralException;

public class ServerUtility {

    /** Logger for this class. */
    private static final Logger LOG = Logger.getLogger(
            ServerUtility.class.getName());

    public static final String FEDORA_SERVER_HOST = "fedoraServerHost";
    public static final String FEDORA_SERVER_PORT = "fedoraServerPort";
    public static final String FEDORA_REDIRECT_PORT = "fedoraRedirectPort";
    public static final String FEDORA_SHUTDOWN_PORT = "fedoraShutdownPort";
    public static final String ADMIN_USERNAME_KEY = "adminUsername";
    public static final String ADMIN_PASSWORD_KEY = "adminPassword";
    public static final String BACKEND_USERNAME_KEY = "backendUsername";
    public static final String BACKEND_PASSWORD_KEY = "backendPassword";    
    
    private static final Properties readServerProperties(boolean httpRequired, boolean httpsRequired) 
    	throws Exception {
       	Properties properties = new Properties();    	
        String fedoraHome=System.getProperty("fedora.home");
        if (fedoraHome==null) {
        	throw new Exception("ERROR: fedora.home system property not set.");            	
        }        	
        File fedoraHomeDir=new File(fedoraHome);
        File fcfgFile=new File(fedoraHomeDir, "server/config/fedora.fcfg");
        DocumentBuilderFactory factory=DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        DocumentBuilder builder=factory.newDocumentBuilder();
        Element rootElement=builder.parse(fcfgFile).getDocumentElement();
        NodeList params=rootElement.getElementsByTagName("param");
        for (int i=0; i<params.getLength(); i++) {
            Node nameNode=params.item(i).getAttributes().getNamedItem("name");
            Node valueNode=params.item(i).getAttributes().getNamedItem("value");
    		if (FEDORA_SERVER_PORT.equals(nameNode.getNodeValue())
            ||  FEDORA_REDIRECT_PORT.equals(nameNode.getNodeValue())
            ||  FEDORA_SHUTDOWN_PORT.equals(nameNode.getNodeValue())			
            ||  FEDORA_SERVER_HOST.equals(nameNode.getNodeValue())
            ||  ADMIN_USERNAME_KEY.equals(nameNode.getNodeValue())
            ||  ADMIN_PASSWORD_KEY.equals(nameNode.getNodeValue())
            ||  BACKEND_USERNAME_KEY.equals(nameNode.getNodeValue())
            ||  BACKEND_PASSWORD_KEY.equals(nameNode.getNodeValue())) {			
        		properties.put(nameNode.getNodeValue(),valueNode.getNodeValue());            			
    		}
        }
        if ((! properties.containsKey(FEDORA_SERVER_HOST))) {
        	throw new Exception("fedora.fcfg missing " + "http host");            	
        }                
        if (httpRequired && ! properties.containsKey(FEDORA_SERVER_PORT)) {
        	throw new Exception("fedora.fcfg missing " + "http port");        	
        }
        if (httpsRequired && ! properties.containsKey(FEDORA_REDIRECT_PORT)) {
        	throw new Exception("fedora.fcfg missing " + "http port");            	
        }
        if (! properties.containsKey(FEDORA_SHUTDOWN_PORT)) {
        	throw new Exception("fedora.fcfg missing " + "shutdown port");            	
        }        
        if ((! properties.containsKey(ADMIN_USERNAME_KEY))) {
        	throw new Exception("fedora.fcfg missing " + "admin user");            	
        }
        if ((! properties.containsKey(ADMIN_PASSWORD_KEY))) {
        	throw new Exception("fedora.fcfg missing " + "admin passwd");            	
        }            
    	return properties;
    }

    public static final Properties getServerProperties() {
    	return serverProperties;
    }

    private static Properties serverProperties = null;
    static {
    	try {
			serverProperties = readServerProperties(false, false);
		} catch (Exception e) {
            LOG.warn("problem during static read of server properties", e);
		}
    }

    public static final String HTTP = "http";
    public static final String HTTPS = "https";
    
    public static final String getPort(String protocol) throws GeneralException {
    	String port = null;
    	if (! HTTP.equals(protocol) && ! HTTPS.equals(protocol)) {
    		throw new GeneralException("bad protocol parm = " + protocol);    	
    	}
    	LOG.debug("serverProperties = "+serverProperties);
    	LOG.debug("HTTP.equals(protocol) = "+HTTP.equals(protocol));    	
    	LOG.debug("HTTPS.equals(protocol) = "+HTTPS.equals(protocol));    	
    	LOG.debug("FEDORA_SERVER_PORT = "+FEDORA_SERVER_PORT);    	
    	LOG.debug("FEDORA_REDIRECT_PORT = "+FEDORA_REDIRECT_PORT); 
    	LOG.debug("serverProperties.containsKey(FEDORA_SERVER_PORT) = "+serverProperties.containsKey(FEDORA_SERVER_PORT));    	
    	LOG.debug("serverProperties.containsKey(FEDORA_REDIRECT_PORT) = "+serverProperties.containsKey(FEDORA_REDIRECT_PORT));    	
    	if (HTTP.equals(protocol) && serverProperties.containsKey(FEDORA_SERVER_PORT)) {	
    		port = (String) serverProperties.get(FEDORA_SERVER_PORT);
    	} else if (HTTPS.equals(protocol) && serverProperties.containsKey(FEDORA_REDIRECT_PORT)) {	
    		port = (String) serverProperties.get(FEDORA_REDIRECT_PORT);
    	} else {
    		throw new GeneralException("specified port not configured");    		
    	}
    	LOG.debug(protocol+"=>"+port);
    	return port;
    }
    
    public static final ProtocolPort getProtocolPort(String preferredProtocol, String fallbackProtocol) throws GeneralException {
    	String protocol = null;
    	String port = getPort(preferredProtocol);
    	if (port != null) {
    		protocol = preferredProtocol;
    	} else if (fallbackProtocol != null) {
    		port = getPort(fallbackProtocol);
        	if (port != null) {
        		protocol = fallbackProtocol;
        	}
    	}    	
    	LOG.debug("protocol="+protocol+"port="+port);
    	return new ProtocolPort(protocol, port);
    }
    
    public static boolean pingServletContainer(String path, int secondsTimeout, int maxConnectionAttemptsPerUrl) throws GeneralException {
        boolean pingsOk = false;
        HttpClient client = null;
        try {
        	LOG.debug("getServerProperties()="+getServerProperties());
        	ProtocolPort protocolPort = getProtocolPort(HTTP, HTTPS);
    		LOG.debug("protocolPort="+protocolPort);
    		LOG.debug("protocolPort.getProtocol()="+protocolPort.getProtocol());
    		LOG.debug("protocolPort.getPort()="+protocolPort.getPort());
    		LOG.debug("serverProperties.get(FEDORA_SERVER_HOST)="+serverProperties.get(FEDORA_SERVER_HOST));
    		LOG.debug("path="+path);    		
        	client = new HttpClient(protocolPort.getProtocol(), (String) getServerProperties().get(FEDORA_SERVER_HOST), protocolPort.getPort(), path);
    		LOG.debug("client="+client);
        	GetMethod getMethod = client.doNoAuthnGet(1000 * secondsTimeout, 25, maxConnectionAttemptsPerUrl);
    		LOG.debug("getMethod="+getMethod);
    		LOG.debug("getMethod.getStatusCode()="+getMethod.getStatusCode());    		
        	pingsOk = (getMethod.getStatusCode() == java.net.HttpURLConnection.HTTP_OK);
    		LOG.debug("pingsOk="+pingsOk);
        } catch (Exception e) {			
        	throw new GeneralException("op failure pinging fedora server", e);
		} finally {
			HttpClient.thisUseFinished();
		}
        return pingsOk;    	
    }
    
	public static final int MAX_CONNECTION_ATTEMPTS_PER_URL = 15;   
    public static boolean pingServletContainerStartup(String path, int secondsTimeout) throws GeneralException {
    	return pingServletContainer(path, secondsTimeout, MAX_CONNECTION_ATTEMPTS_PER_URL);
    }
    
    public static boolean pingServletContainerRunning(String path, int secondsTimeout) throws GeneralException {
    	return pingServletContainer(path, secondsTimeout, 1);
    }

    public static final int shutdown (String protocol, String optionalUsername, String optionalPassword) throws Exception {
        return serverAction(SHUTDOWN, protocol, null, null);
    }
    
    public static final int reloadPolicies (String protocol, String optionalUsername, String optionalPassword) throws Exception {
        return serverAction(RELOAD_POLICIES, protocol, optionalUsername, optionalPassword);
    }
    
    private static final int serverAction (String action, String protocol, String optionalUsername, String optionalPassword) throws Exception {
    	HttpClient client = null;
    	int statusCode = -1;
    	try {
	   		LOG.debug("SC:call HttpClient()...");
	  		client = new HttpClient(protocol, 
	  				ServerUtility.getServerProperties().getProperty(ServerUtility.FEDORA_SERVER_HOST), 
	  				ServerUtility.getServerProperties().getProperty( "http".equals(protocol) ? ServerUtility.FEDORA_SERVER_PORT : ServerUtility.FEDORA_REDIRECT_PORT),
	  				"/fedora/management/control?action=" + action
	  				);
	   		LOG.debug("...SC:call HttpClient()"); 
	   		LOG.debug("SC:call HttpClient.doAuthnGet()...");        		
	  		GetMethod getMethod = client.doAuthnGet(20000, 25,
	  			(optionalUsername == null) ? ServerUtility.getServerProperties().getProperty(ServerUtility.ADMIN_USERNAME_KEY) : optionalUsername,
	  			(optionalPassword == null) ? ServerUtility.getServerProperties().getProperty(ServerUtility.ADMIN_PASSWORD_KEY) : optionalPassword, 
	  			ServerUtility.MAX_CONNECTION_ATTEMPTS_PER_URL
	  		);
	   		LOG.debug("...SC:call HttpClient.doAuthnGet()");		      		
	   		LOG.debug("SC:call HttpClient.getLineResponse()...");
   			statusCode = getMethod.getStatusCode();
    	} finally {
			HttpClient.thisUseFinished();
    	}
   		return statusCode;
    }
    
    private static final String STARTUP = "startup";
    private static final String SHUTDOWN = "shutdown";
    private static final String STATUS = "status";
    private static final String RELOAD_POLICIES = "reloadPolicies";
    
    private static final String USAGE = "USAGE for ServerController.main(): startup|shutdown|status [http|https] [username] [passwd]";

    public static final int CONTINUE = 100;
 
    private static final String httpCodeString(int code) {
    	String string;
		switch (code) {
			case CONTINUE:
				string = "CONTINUE";
				break;		
			case HttpURLConnection.HTTP_OK:
				string = "OK";
				break;
			case HttpURLConnection.HTTP_BAD_REQUEST:
				string = "BAD REQUEST";
				break;				
			case HttpURLConnection.HTTP_UNAUTHORIZED:
				string = "UNAUTHORIZED";
				break;
			case HttpURLConnection.HTTP_FORBIDDEN:
				string = "FORBIDDEN";
				break;
			case HttpURLConnection.HTTP_UNAVAILABLE:
				string = "UNAVAILABLE";
				break;        			
			default:
				string = "SERVER RESPONSE WAS " + code;
    	}
    	return string;
    }

	private static final String serverControllerResponseString(int code) {
		String string;
		switch (code) {
			case CONTINUE:
				string = "AUTHORIZATION PERMITTED";
				break;		
			case HttpURLConnection.HTTP_OK:
				string = httpCodeString(code);
				break;				
			case HttpURLConnection.HTTP_UNAUTHORIZED:
				string = "AUTHENTICATION REQUIRED";
				break;
			case HttpURLConnection.HTTP_FORBIDDEN:
				string = "AUTHORIZATION DENIED";
				break;
			default:
				string = "ERROR";
		}
		return string;
	}

	private static final String serverStatusResponseString(int code) {
		String string;
		switch (code) {
			case HttpURLConnection.HTTP_OK:
				string = "RUNNING";
				break;
			case HttpURLConnection.HTTP_UNAVAILABLE:
				string = "STOPPED";
				break;        			
			default:
				string = serverControllerResponseString(code);
		}
		return string;
	}
	
    public static void main(String[] args) throws Exception {
        if (args.length < 1) {
        	throw new Exception(USAGE);
        }
        String action = args[0];
        String protocol = args.length > 1 ? args[1] : "http";        
        if (! "http".equals(protocol) && ! "https".equals(protocol)) {
        	throw new Exception(USAGE);
        }
        String optionalUsername = null;
        String optionalPassword = null;            
        if (args.length > 2) {
        	if (args.length == 3) {
            	throw new Exception(USAGE);
        	}
        	optionalUsername = args[2];
        	optionalPassword = args[3];
        }
        if ((STARTUP.equals(action))
        ||  (STATUS.equals(action))
        ||  (SHUTDOWN.equals(action))
        ||  (RELOAD_POLICIES.equals(action))) {
        	int code = serverAction(action, protocol, optionalUsername, optionalPassword);
        	String line = STATUS.equals(action) ? serverStatusResponseString(code) 
        			: serverControllerResponseString(code);
        	System.out.println(line);
        } else {
        	throw new Exception(USAGE);            	
        }
    }    
   
    public static boolean isURLFedoraServer(String url) {
        boolean isFedoraLocalService = false;
        String fedoraServerHost = (String) serverProperties.get(FEDORA_SERVER_HOST);
        String fedoraServerPort = (String) serverProperties.get(FEDORA_SERVER_PORT);
        String fedoraServerRedirectPort = (String) serverProperties.get(FEDORA_REDIRECT_PORT);
        
        // Check for URLs that are callbacks to the Fedora server
        if (url.startsWith("http://"+fedoraServerHost+":"+fedoraServerPort+"/fedora/") ||
            url.startsWith("http://"+fedoraServerHost+"/fedora/") ||   
            url.startsWith("https://"+fedoraServerHost+":"+fedoraServerRedirectPort+"/fedora/") ||
            url.startsWith("https://"+fedoraServerHost+"/fedora/") ) {
            LOG.debug("******************URL was Fedora-to-Fedora callback: "+url);
            return true;
        } else {
            LOG.debug("******************URL was Non-Fedora callback: "+url);
            return false;
        }
            
    }    
    
}

package fedora.common.policy;

public class HttpRequestNamespace extends XacmlNamespace {
	
	// Properties
	public final XacmlName MESSAGE_PROTOCOL;
	
	public final XacmlName PROTOCOL;
	public final XacmlName SCHEME;
	public final XacmlName SECURITY;
	public final XacmlName AUTHTYPE;
	public final XacmlName METHOD;	
	public final XacmlName SESSION_ENCODING;	
	public final XacmlName SESSION_STATUS;		
	public final XacmlName CONTENT_LENGTH;
	public final XacmlName CONTENT_TYPE;
	public final XacmlName CLIENT_FQDN;
	public final XacmlName CLIENT_IP_ADDRESS;	
	public final XacmlName SERVER_FQDN;
	public final XacmlName SERVER_IP_ADDRESS;	
	public final XacmlName SERVER_PORT;			

	// Values of MESSAGE_PROTOCOL	 
	public final XacmlName SOAP;
	public final XacmlName REST;
	
	// Values of SECURITY	 
	public final XacmlName SECURE;
	public final XacmlName INSECURE;
	
    private HttpRequestNamespace(XacmlNamespace parent, String localName) {
    	super(parent, localName);

    	// Properties
    	MESSAGE_PROTOCOL = addName(new XacmlName(this, "message-protocol"));
    	
    	PROTOCOL = addName(new XacmlName(this, "protocol"));
    	SCHEME = addName(new XacmlName(this, "scheme"));
    	SECURITY = addName(new XacmlName(this, "security"));
    	AUTHTYPE = addName(new XacmlName(this, "authtype"));
    	METHOD = addName(new XacmlName(this, "method"));	
    	SESSION_ENCODING = addName(new XacmlName(this, "session-encoding"));	
    	SESSION_STATUS = addName(new XacmlName(this, "session-status"));		
    	CONTENT_LENGTH = addName(new XacmlName(this, "content-length"));
    	CONTENT_TYPE = addName(new XacmlName(this, "content-type"));
    	CLIENT_FQDN = addName(new XacmlName(this, "client-fqdn"));
    	CLIENT_IP_ADDRESS = addName(new XacmlName(this, "client-ip-address"));	
    	SERVER_FQDN = addName(new XacmlName(this, "server-fqdn"));
    	SERVER_IP_ADDRESS = addName(new XacmlName(this, "server-ip-address"));	
    	SERVER_PORT = addName(new XacmlName(this, "server-port"));

    	// Values of MESSAGE_PROTOCOL	 
    	SOAP               = addName(new XacmlName(this, "soap"));
    	REST               = addName(new XacmlName(this, "rest"));    	
    	
    	// Values of SECURITY	 
    	SECURE               = addName(new XacmlName(this, "secure"));
    	INSECURE               = addName(new XacmlName(this, "insecure"));     	
    }

	public static HttpRequestNamespace onlyInstance = new HttpRequestNamespace(EnvironmentNamespace.getInstance(), "http-request");
	
	public static final HttpRequestNamespace getInstance() {
		return onlyInstance;
	}


}

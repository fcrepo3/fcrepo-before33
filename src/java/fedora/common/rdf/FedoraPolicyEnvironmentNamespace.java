package fedora.common.rdf;

import fedora.common.Constants;

/**
 *
 * -----------------------------------------------------------------------------
 *
 * <p><b>License and Copyright: </b>The contents of this file are subject to the
 * Mozilla Public License Version 1.1 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License
 * at <a href="http://www.mozilla.org/MPL">http://www.mozilla.org/MPL/.</a></p>
 *
 * <p>Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License for
 * the specific language governing rights and limitations under the License.</p>
 *
 * <p>The entire file consists of original code.  Copyright &copy; 2002-2005 by The
 * Rector and Visitors of the University of Virginia and Cornell University.
 * All rights reserved.</p>
 *
 * -----------------------------------------------------------------------------
 *
 */
public class FedoraPolicyEnvironmentNamespace extends RDFNamespace {

	// Properties
	public final RDFName REQUEST_MESSAGE_PROTOCOL;
	
	public final RDFName CURRENT_DATE_TIME;
	public final RDFName CURRENT_DATE;
	public final RDFName CURRENT_TIME;	
	public final RDFName REQUEST_PROTOCOL;
	public final RDFName REQUEST_SCHEME;
	public final RDFName REQUEST_SECURITY;
	public final RDFName REQUEST_AUTHTYPE;
	public final RDFName REQUEST_METHOD;	
	public final RDFName REQUEST_SESSION_ENCODING;	
	public final RDFName REQUEST_SESSION_STATUS;		
	public final RDFName REQUEST_CONTENT_LENGTH;
	public final RDFName REQUEST_CONTENT_TYPE;
	public final RDFName REQUEST_CLIENT_FQDN;
	public final RDFName REQUEST_CLIENT_IP_ADDRESS;	
	public final RDFName REQUEST_SERVER_FQDN;
	public final RDFName REQUEST_SERVER_IP_ADDRESS;	
	public final RDFName REQUEST_SERVER_PORT;	

	// Values of MESSAGE_PROTOCOL	 
	public final RDFName SOAP;
	public final RDFName REST;


    public FedoraPolicyEnvironmentNamespace() {

        this.uri = Constants.FEDORA_SYSTEM_DEF_URI + "/policy-environment#";

        // Properties
    	this.REQUEST_MESSAGE_PROTOCOL = new RDFName(this, "request-message-protocol");
    	
    	this.CURRENT_DATE_TIME = new RDFName(this, "current-date-time");
    	this.CURRENT_DATE = new RDFName(this, "current-date");
    	this.CURRENT_TIME = new RDFName(this, "current-time");	
    	this.REQUEST_PROTOCOL = new RDFName(this, "request-protocol");
    	this.REQUEST_SCHEME = new RDFName(this, "request-scheme");
    	this.REQUEST_SECURITY = new RDFName(this, "request-security");
    	this.REQUEST_AUTHTYPE = new RDFName(this, "request-authtype");
    	this.REQUEST_METHOD = new RDFName(this, "request-method");	
    	this.REQUEST_SESSION_ENCODING = new RDFName(this, "request-session-encoding");	
    	this.REQUEST_SESSION_STATUS = new RDFName(this, "request-session-status");		
    	this.REQUEST_CONTENT_LENGTH = new RDFName(this, "request-content-length");
    	this.REQUEST_CONTENT_TYPE = new RDFName(this, "request-content-type");
    	this.REQUEST_CLIENT_FQDN = new RDFName(this, "request-client-fqdn");
    	this.REQUEST_CLIENT_IP_ADDRESS = new RDFName(this, "request-client-ip-address");	
    	this.REQUEST_SERVER_FQDN = new RDFName(this, "request-server-fqdn");
    	this.REQUEST_SERVER_IP_ADDRESS = new RDFName(this, "request-server-ip-address");	
    	this.REQUEST_SERVER_PORT = new RDFName(this, "request-server-port");	


    	// Values of MESSAGE_PROTOCOL	 
    	this.SOAP               = new RDFName(this, "soap");
    	this.REST               = new RDFName(this, "rest");

    }

}

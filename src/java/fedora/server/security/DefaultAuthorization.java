package fedora.server.security;


import java.util.Date;
import java.util.Hashtable;
import java.util.Map;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import fedora.common.Constants;
import fedora.server.Context;
import fedora.server.Module;
import fedora.server.MultiValueMap;
import fedora.server.Server;
import fedora.server.errors.AuthzOperationalException;
import fedora.server.errors.ModuleInitializationException;
import fedora.server.errors.NotAuthorizedException;
import fedora.server.storage.DOManager;

/**
 *
 * <p><b>Title: </b>DefaultAccess.java</p>
 *
 * <p><b>Description: </b>The Access Module, providing support for the Fedora
 * Access subsystem.</p>
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
 * <p>The entire file consists of original code.  Copyright &copy; 2002-2004 by The
 * Rector and Visitors of the University of Virginia and Cornell University.
 * All rights reserved.</p>
 *
 * -----------------------------------------------------------------------------
 *
 * @author rlw@virginia.edu
 * @version $Id$
 */
public class DefaultAuthorization extends Module implements Authorization {

  private PolicyEnforcementPoint xacmlPep; // = XACMLPep.getInstance();
  
	boolean enforceListObjectInFieldSearchResults = true;
	boolean enforceListObjectInResourceIndexResults = true;


	

	/*
	private final URI ACTION_NEW_STATE_URI;
	private final URI ACTION_DATASTREAM_NEW_STATE_URI;
	private final URI ACTION_DISSEMINATOR_NEW_STATE_URI;
	

	//private final URI RESOURCE_PID_URI;	
	private final URI RESOURCE_AS_OF_DATE_URI;
	//private final URI RESOURCE_STATE_URI;
	private final URI RESOURCE_DATASTREAM_ID_URI;	
	//private final URI RESOURCE_DATASTREAM_NAMESPACE_URI;	
	private final URI RESOURCE_DATASTREAM_STATE_URI;
	private final URI RESOURCE_DATASTREAM_LOCATION_URI;
	private final URI RESOURCE_DATASTREAM_CONTROL_GROUP_URI;
	private final URI RESOURCE_BDEF_PID_URI;
	private final URI RESOURCE_BDEF_NAMESPACE_URI;
	private final URI RESOURCE_BMECH_PID_URI;	
	private final URI RESOURCE_BMECH_NAMESPACE_URI;
	private final URI RESOURCE_DISSEMINATOR_PID_URI;
	private final URI RESOURCE_DISSEMINATOR_NAMESPACE_URI;
	private final URI RESOURCE_DISSEMINATOR_STATE_URI;
	private final URI RESOURCE_DISSEMINATOR_METHOD_URI;
	//private final URI RESOURCE_DISSEMINATED_PID_URI;
	//private final URI RESOURCE_DISSEMINATED_NAMESPACE_URI;
	private final URI RESOURCE_N_NEW_PIDS_URI;
	

	private final URI ENVIRONMENT_CURRENT_DATETIME_URI;
	private final URI ENVIRONMENT_CURRENT_DATE_URI;
	private final URI ENVIRONMENT_CURRENT_TIME_URI;
	private final URI ENVIRONMENT_REQUEST_PROTOCOL_URI;
	private final URI ENVIRONMENT_REQUEST_SCHEME_URI;
	private final URI ENVIRONMENT_REQUEST_SECURITY_URI;
	private final URI ENVIRONMENT_REQUEST_AUTHTYPE_URI;
	private final URI ENVIRONMENT_REQUEST_METHOD_URI;	
	private final URI ENVIRONMENT_REQUEST_SESSION_ENCODING_URI;	
	private final URI ENVIRONMENT_REQUEST_SESSION_STATUS_URI;		
	private final URI ENVIRONMENT_REQUEST_CONTENT_LENGTH_URI;
	private final URI ENVIRONMENT_REQUEST_CONTENT_TYPE_URI;
	private final URI ENVIRONMENT_REQUEST_SOAP_OR_REST_URI;
	private final URI ENVIRONMENT_CLIENT_FQDN_URI;
	private final URI ENVIRONMENT_CLIENT_IP_URI;
	private final URI ENVIRONMENT_SERVER_FQDN_URI;
	private final URI ENVIRONMENT_SERVER_IP_URI;	
	private final URI ENVIRONMENT_SERVER_PORT_URI;		
	//private final URI RESOURCE_STATE_URI; 
	 */
	
	private String repositoryPoliciesDirectory = ""; //"/fedora-repository-policies";
	private String objectPoliciesDirectory = ""; //"/fedora-object-policies";
	private String combiningAlgorithm = ""; //"com.sun.xacml.combine.OrderedDenyOverridesPolicyAlg";
	private String enforceMode = "";

	private final String REPOSITORY_POLICIES_DIRECTORY = "REPOSITORY-POLICIES-DIRECTORY";
	private final String OBJECT_POLICIES_DIRECTORY = "OBJECT-POLICIES-DIRECTORY";
	private final String COMBINING_ALGORITHM = "XACML-COMBINING-ALGORITHM";
	private final String ENFORCE_MODE = "ENFORCE-MODE";

	
  /**
   * <p>Creates and initializes the Access Module. When the server is starting
   * up, this is invoked as part of the initialization process.</p>
   *
   * @param moduleParameters A pre-loaded Map of name-value pairs comprising
   *        the intended configuration of this Module.
   * @param server The <code>Server</code> instance.
   * @param role The role this module fulfills, a java class name.
   * @throws ModuleInitializationException If initilization values are
   *         invalid or initialization fails for some other reason.
   */
  public DefaultAuthorization(Map moduleParameters, Server server, String role)
          throws ModuleInitializationException
  {
    super(moduleParameters, server, role);
    System.err.println("log4j.configuration=" + System.getProperty("log4j.configuration"));
	String serverHome = null;
    try {
		serverHome = server.getHomeDir().getCanonicalPath() + File.separator;
	} catch (IOException e1) {
		throw new ModuleInitializationException("couldn't get server home", role, e1);
	}
    
    if (moduleParameters.containsKey(REPOSITORY_POLICIES_DIRECTORY)) {
    	repositoryPoliciesDirectory = 
    		((String) moduleParameters.get(REPOSITORY_POLICIES_DIRECTORY)).startsWith(File.separator) ? "" : serverHome 
		+ (String) moduleParameters.get(REPOSITORY_POLICIES_DIRECTORY);
    	System.err.println("repositoryPoliciesDirectory=" + repositoryPoliciesDirectory);
    }
    if (moduleParameters.containsKey(OBJECT_POLICIES_DIRECTORY)) {
    	objectPoliciesDirectory =
    		((String) moduleParameters.get(OBJECT_POLICIES_DIRECTORY)).startsWith(File.separator) ? "" : serverHome 
    	+ (String) moduleParameters.get(OBJECT_POLICIES_DIRECTORY);
    	System.err.println("objectPoliciesDirectory=" + objectPoliciesDirectory);
    }
    if (moduleParameters.containsKey(COMBINING_ALGORITHM)) {
    	combiningAlgorithm = (String) moduleParameters.get(COMBINING_ALGORITHM);
    }
    if (moduleParameters.containsKey(ENFORCE_MODE)) {
    	enforceMode = (String) moduleParameters.get(ENFORCE_MODE);
    }
 /*
	try {
		ACTION_NEW_STATE_URI = new URI(ACTION_NEW_STATE_URI_STRING);		
		ACTION_DATASTREAM_NEW_STATE_URI = new URI(ACTION_DATASTREAM_NEW_STATE_URI_STRING);		
		ACTION_DISSEMINATOR_NEW_STATE_URI = new URI(ACTION_DISSEMINATOR_NEW_STATE_URI_STRING);	

		//RESOURCE_ID_URI = new URI(RESOURCE_ID_URI_STRING);		
		//RESOURCE_NAMESPACE_URI = new URI(RESOURCE_NAMESPACE_URI_STRING);
		RESOURCE_AS_OF_DATE_URI = new URI(RESOURCE_AS_OF_DATE_URI_STRING);
		//RESOURCE_STATE_URI = new URI(RESOURCE_STATE_URI_STRING);
		
		RESOURCE_DATASTREAM_ID_URI = new URI(Constants.POLICY_RESOURCE.DATASTREAM_ID.uri);
		//RESOURCE_DATASTREAM_NAMESPACE_URI = new URI(RESOURCE_DATASTREAM_NAMESPACE_URI_STRING);
		RESOURCE_DATASTREAM_STATE_URI = new URI(Constants.POLICY_RESOURCE.DATASTREAM_ID.uri);
		RESOURCE_DATASTREAM_LOCATION_URI = new URI(RESOURCE_DATASTREAM_LOCATION_URI_STRING);
		RESOURCE_DATASTREAM_CONTROL_GROUP_URI = new URI(RESOURCE_DATASTREAM_CONTROL_GROUP_URI_STRING);

		RESOURCE_BDEF_PID_URI = new URI(RESOURCE_BDEF_PID_URI_STRING);
		RESOURCE_BDEF_NAMESPACE_URI = new URI(RESOURCE_BDEF_NAMESPACE_URI_STRING);

		RESOURCE_BMECH_PID_URI = new URI(RESOURCE_BMECH_PID_URI_STRING);
		RESOURCE_BMECH_NAMESPACE_URI = new URI(RESOURCE_BMECH_NAMESPACE_URI_STRING);
		
		RESOURCE_DISSEMINATOR_PID_URI = new URI(RESOURCE_DISSEMINATOR_PID_URI_STRING);		
		RESOURCE_DISSEMINATOR_NAMESPACE_URI = new URI(RESOURCE_DISSEMINATOR_NAMESPACE_URI_STRING);		
		RESOURCE_DISSEMINATOR_STATE_URI = new URI(RESOURCE_DISSEMINATOR_STATE_URI_STRING);		
		RESOURCE_DISSEMINATOR_METHOD_URI = new URI(RESOURCE_DISSEMINATOR_METHOD_URI_STRING);	
		//RESOURCE_DISSEMINATED_PID_URI = new URI(RESOURCE_DISSEMINATED_PID_URI_STRING);		
		//RESOURCE_DISSEMINATED_NAMESPACE_URI = new URI(RESOURCE_DISSEMINATED_NAMESPACE_URI_STRING);		
		
		//RESOURCE_PID_URI = new URI(RESOURCE_PID_URI_STRING);

		RESOURCE_N_NEW_PIDS_URI = new URI(RESOURCE_N_NEW_PIDS_URI_STRING);

		ENVIRONMENT_CURRENT_DATETIME_URI = new URI(Constants.POLICY_ENVIRONMENT.CURRENT_DATE_TIME.uri);
		ENVIRONMENT_CURRENT_DATE_URI = new URI(Constants.POLICY_ENVIRONMENT.CURRENT_DATE.uri);
		ENVIRONMENT_CURRENT_TIME_URI = new URI(Constants.POLICY_ENVIRONMENT.CURRENT_TIME.uri);		
		ENVIRONMENT_REQUEST_PROTOCOL_URI = new URI(Constants.POLICY_ENVIRONMENT.REQUEST_PROTOCOL.uri);
		ENVIRONMENT_REQUEST_SCHEME_URI = new URI(Constants.POLICY_ENVIRONMENT.REQUEST_SCHEME.uri);
		ENVIRONMENT_REQUEST_SECURITY_URI = new URI(Constants.POLICY_ENVIRONMENT.REQUEST_SECURITY.uri);
		ENVIRONMENT_REQUEST_AUTHTYPE_URI = new URI(Constants.POLICY_ENVIRONMENT.REQUEST_AUTHTYPE.uri);
		ENVIRONMENT_REQUEST_METHOD_URI = new URI(Constants.POLICY_ENVIRONMENT.REQUEST_METHOD.uri);	
		ENVIRONMENT_REQUEST_SESSION_ENCODING_URI = new URI(Constants.POLICY_ENVIRONMENT.REQUEST_SESSION_ENCODING.uri);	
		ENVIRONMENT_REQUEST_SESSION_STATUS_URI = new URI(Constants.POLICY_ENVIRONMENT.REQUEST_SESSION_STATUS.uri);		
		ENVIRONMENT_REQUEST_CONTENT_LENGTH_URI = new URI(Constants.POLICY_ENVIRONMENT.REQUEST_CONTENT_LENGTH.uri);
		ENVIRONMENT_REQUEST_CONTENT_TYPE_URI = new URI(Constants.POLICY_ENVIRONMENT.REQUEST_CONTENT_TYPE.uri);
		ENVIRONMENT_REQUEST_SOAP_OR_REST_URI = new URI(Constants.POLICY_ENVIRONMENT.REQUEST_MESSAGE_PROTOCOL.uri);
		ENVIRONMENT_CLIENT_FQDN_URI = new URI(Constants.POLICY_ENVIRONMENT.REQUEST_CLIENT_FQDN.uri);		
		ENVIRONMENT_CLIENT_IP_URI = new URI(Constants.POLICY_ENVIRONMENT.REQUEST_CLIENT_IP_ADDRESS.uri);
		ENVIRONMENT_SERVER_FQDN_URI = new URI(Constants.POLICY_ENVIRONMENT.REQUEST_SERVER_FQDN.uri);
		ENVIRONMENT_SERVER_IP_URI = new URI(Constants.POLICY_ENVIRONMENT.REQUEST_SERVER_IP_ADDRESS.uri);	
		ENVIRONMENT_SERVER_PORT_URI = new URI(Constants.POLICY_ENVIRONMENT.REQUEST_SERVER_PORT.uri);	
		//RESOURCE_STATE_URI = new URI(RESOURCE_STATE);

	} catch (URISyntaxException e) {
    	System.err.println("ERROR ON MAKING URIs");
		throw new ModuleInitializationException("couldn't make URIs", getRole(), e);
	}
	*/
  }

  
  /**
   * <p>Initializes the module.</p>
   *
   * @throws ModuleInitializationException If the module cannot be initialized.
   */
  public void initModule() throws ModuleInitializationException {
  }

  public void postInitModule() throws ModuleInitializationException {
    DOManager m_manager = (DOManager) getServer().getModule("fedora.server.storage.DOManager");
    if (m_manager == null) {
      throw new ModuleInitializationException("Can't get a DOManager from Server.getModule", getRole());
    }
    try {
        xacmlPep = PolicyEnforcementPoint.getInstance();
        xacmlPep.initPep(enforceMode, combiningAlgorithm, repositoryPoliciesDirectory, objectPoliciesDirectory, m_manager);
    } catch (Throwable e1) {
    	ModuleInitializationException e2 = new ModuleInitializationException(e1.getMessage(), getRole());
    	throw e2;
    }
  }

	//private static final boolean enforceResourceIndexList = true;
  	//private static final boolean enforceSearchList = true;
  
	private final String extractNamespace(String pid) {
		String namespace = "";
		int colonPosition = pid.indexOf(':');
		if (-1 < colonPosition) {
			namespace = pid.substring( 0, colonPosition);
		}
		return namespace;
	}
	

	
	// private final Set wrapResources(String resourceId) {} no such method :. all resource attributes in context
	
	public final void enforceAddDatastream(Context context, String pid, String dsId, String dsLocation, String controlGroup, String dsState) 
	throws NotAuthorizedException {
		String target = Constants.POLICY_ACTION.ADD_DATASTREAM.uri;
		log("enforcing " + target);
		context.setActionAttributes(null);
		MultiValueMap resourceAttributes = new MultiValueMap();
		String name = "";
		try {
			name = resourceAttributes.setReturn(Constants.MODEL.DATASTREAM_STATE.uri, dsState);
			name = resourceAttributes.setReturn(Constants.POLICY_RESOURCE.DATASTREAM_ID.uri, dsId);
			name = resourceAttributes.setReturn(Constants.POLICY_RESOURCE.DATASTREAM_LOCATION.uri, dsLocation);
			name = resourceAttributes.setReturn(Constants.POLICY_RESOURCE.CONTROL_GROUP.uri, controlGroup);
		} catch (Exception e) {
			context.setResourceAttributes(null);		
			throw new AuthzOperationalException(target + " couldn't set " + name, e);	
		}
		context.setResourceAttributes(resourceAttributes);
		xacmlPep.enforce(context.getSubjectValue(SUBJECT_ID_URI_STRING), target, Constants.POLICY_ACTION.APIM.uri, pid, extractNamespace(pid), context);
	}
	
	public final void enforceAddDisseminator(Context context, String pid) 
	throws NotAuthorizedException { 
		String target = Constants.POLICY_ACTION.ADD_DISSEMINATOR.uri;
		log("enforcing " + target);
		context.setActionAttributes(null);
		context.setResourceAttributes(null);
		xacmlPep.enforce(context.getSubjectValue(SUBJECT_ID_URI_STRING), target, Constants.POLICY_ACTION.APIM.uri, pid, extractNamespace(pid), context);
	}
		
	public final void enforceExportObject(Context context, String pid) 
	throws NotAuthorizedException { 
		String target = Constants.POLICY_ACTION.EXPORT_OBJECT.uri;
		log("enforcing " + target);
		context.setActionAttributes(null);
		context.setResourceAttributes(null);
		xacmlPep.enforce(context.getSubjectValue(SUBJECT_ID_URI_STRING), target, Constants.POLICY_ACTION.APIM.uri, pid, extractNamespace(pid), context);
	}			
	
	
	public final void enforceGetDisseminatorHistory(Context context, String pid, String disseminatorId) 
	throws NotAuthorizedException {
		String target = Constants.POLICY_ACTION.GET_DISSEMINATOR_HISTORY.uri;
		log("enforcing " + target);
		context.setActionAttributes(null);
		MultiValueMap resourceAttributes = new MultiValueMap();
		String name = "";
		try {
			name = resourceAttributes.setReturn(Constants.POLICY_RESOURCE.DISSEMINATOR_PID.uri, disseminatorId);	
			name = resourceAttributes.setReturn(Constants.POLICY_RESOURCE.DISSEMINATOR_NAMESPACE.uri, extractNamespace(disseminatorId));	
		} catch (Exception e) {
			context.setResourceAttributes(null);		
			throw new AuthzOperationalException(target + " couldn't set " + name, e);	
		}
		context.setResourceAttributes(resourceAttributes);
		xacmlPep.enforce(context.getSubjectValue(SUBJECT_ID_URI_STRING), target, Constants.POLICY_ACTION.APIM.uri, pid, extractNamespace(pid), context);
	}	

	public final void enforceGetNextPid(Context context, String namespace, int nNewPids) 
	throws NotAuthorizedException {
		String target = Constants.POLICY_ACTION.GET_NEXT_PID.uri;	
		log("enforcing " + target);
		context.setActionAttributes(null);
		MultiValueMap resourceAttributes = new MultiValueMap();
		String name = "";
		try {
			String nNewPidsAsString = Integer.toString(nNewPids);
			name = resourceAttributes.setReturn(Constants.POLICY_ACTION.N_NEW_PIDS.uri, nNewPidsAsString);
		} catch (Exception e) {
			context.setResourceAttributes(null);		
			throw new AuthzOperationalException(target + " couldn't set " + name, e);	
		}
		context.setResourceAttributes(resourceAttributes);
		xacmlPep.enforce(context.getSubjectValue(SUBJECT_ID_URI_STRING), target, Constants.POLICY_ACTION.APIM.uri, "", namespace, context);
	}	

	public final void enforceGetDatastream(Context context, String pid, String datastreamId) 
	throws NotAuthorizedException { 
		String target = Constants.POLICY_ACTION.GET_DATASTREAM.uri;
		log("enforcing " + target);
		context.setActionAttributes(null);
		MultiValueMap resourceAttributes = new MultiValueMap();
		String name = "";
		try {
			name = resourceAttributes.setReturn(Constants.POLICY_RESOURCE.DATASTREAM_ID.uri, datastreamId);
		} catch (Exception e) {
			context.setResourceAttributes(null);		
			throw new AuthzOperationalException(target + " couldn't set " + name, e);	
		}
		context.setResourceAttributes(resourceAttributes);
		xacmlPep.enforce(context.getSubjectValue(SUBJECT_ID_URI_STRING), target, Constants.POLICY_ACTION.APIM.uri, pid, extractNamespace(pid), context);
	}
	
	public final void enforceGetDatastreamHistory(Context context, String pid, String datastreamId) 
	throws NotAuthorizedException {
		String target = Constants.POLICY_ACTION.GET_DATASTREAM_HISTORY.uri;		
		log("enforcing " + target);
		context.setActionAttributes(null);
		MultiValueMap resourceAttributes = new MultiValueMap();
		String name = "";
		try {
			name = resourceAttributes.setReturn(Constants.POLICY_RESOURCE.DATASTREAM_ID.uri, datastreamId);
		} catch (Exception e) {
			context.setResourceAttributes(null);		
			throw new AuthzOperationalException(target + " couldn't set " + name, e);	
		}
		context.setResourceAttributes(resourceAttributes);
		xacmlPep.enforce(context.getSubjectValue(SUBJECT_ID_URI_STRING), target, Constants.POLICY_ACTION.APIM.uri, pid, extractNamespace(pid), context);
	}	
	
	private final String ensureDate (Date date, Context context) throws AuthzOperationalException {
		if (date == null) {
			date = context.now();
		}
		String dateAsString;
		try {
			dateAsString = dateAsString(date);
		} catch (Throwable t) {
			throw new AuthzOperationalException("couldn't make date a string", t);
		}
		return dateAsString;
	}
	
	public final void enforceGetDatastreams(Context context, String pid, Date asOfDate, String datastreamState) 
	throws NotAuthorizedException { 
		String target = Constants.POLICY_ACTION.GET_DATASTREAMS.uri;
		log("enforcing " + target);
		context.setActionAttributes(null);
		MultiValueMap resourceAttributes = new MultiValueMap();
        System.err.println("in enforceGetDatastreams");
		String name = ""; 
		try {
	        System.err.println("in enforceGetDatastreams, before setting datastreamState=" + datastreamState);
	        name = resourceAttributes.setReturn(Constants.MODEL.DATASTREAM_STATE.uri, datastreamState);	
	        System.err.println("in enforceGetDatastreams, before setting asOfDateAsString");
	        name = resourceAttributes.setReturn(Constants.POLICY_RESOURCE.AS_OF_DATE.uri, ensureDate(asOfDate, context));
		    System.err.println("in enforceGetDatastreams, after setting asOfDateAsString");
		} catch (Exception e) {
			context.setResourceAttributes(null);		
			throw new AuthzOperationalException(target + " couldn't set " + name, e);	
		}
        System.err.println("in enforceGetDatastreams, before setting resourceAttributes");
		context.setResourceAttributes(resourceAttributes);
        System.err.println("in enforceGetDatastreams, after setting resourceAttributes");
		xacmlPep.enforce(context.getSubjectValue(SUBJECT_ID_URI_STRING), target, Constants.POLICY_ACTION.APIM.uri, pid, extractNamespace(pid), context);
        System.err.println("in enforceGetDatastreams, after calling global enforce");
	}	

	public final void enforceGetDisseminator(Context context, String pid, String disseminatorPid, Date asOfDate) 
	throws NotAuthorizedException { 
		String target = Constants.POLICY_ACTION.GET_DISSEMINATOR.uri;
		log("enforcing " + target);
		context.setActionAttributes(null);
		MultiValueMap resourceAttributes = new MultiValueMap();
		String name = "";
		try {
			name = resourceAttributes.setReturn(RESOURCE_DISSEMINATOR_PID_URI_STRING, disseminatorPid);	
			name = resourceAttributes.setReturn(RESOURCE_DISSEMINATOR_NAMESPACE_URI_STRING, extractNamespace(disseminatorPid));
			name = resourceAttributes.setReturn(RESOURCE_AS_OF_DATE_URI_STRING, ensureDate(asOfDate, context));	
		} catch (Exception e) {
			context.setResourceAttributes(null);		
			throw new AuthzOperationalException(target + " couldn't set " + name, e);	
		}
		context.setResourceAttributes(resourceAttributes);		
		xacmlPep.enforce(context.getSubjectValue(SUBJECT_ID_URI_STRING), target, Constants.POLICY_ACTION.APIM.uri, pid, extractNamespace(pid), context);
	}	
	
	public final void enforceGetDisseminators(Context context, String pid, Date asOfDate, String disseminatorState) 
	throws NotAuthorizedException { 
		String target = Constants.POLICY_ACTION.GET_DISSEMINATORS.uri;
		log("enforcing " + target);
		context.setActionAttributes(null);
		MultiValueMap resourceAttributes = new MultiValueMap();
		String name = "";
		try {
			name = resourceAttributes.setReturn(Constants.POLICY_RESOURCE.DISSEMINATOR_STATE.uri, disseminatorState);	
			name = resourceAttributes.setReturn(Constants.POLICY_RESOURCE.AS_OF_DATE.uri, ensureDate(asOfDate, context));	
		} catch (Exception e) {
			context.setResourceAttributes(null);		
			throw new AuthzOperationalException(target + " couldn't set " + name, e);	
		}
		context.setResourceAttributes(resourceAttributes);	
		xacmlPep.enforce(context.getSubjectValue(SUBJECT_ID_URI_STRING), target, Constants.POLICY_ACTION.APIM.uri, pid, extractNamespace(pid), context);
	}
	
	public final void enforceGetObjectProperties(Context context, String pid) 
	throws NotAuthorizedException {
		String target = Constants.POLICY_ACTION.GET_OBJECT_PROPERTIES.uri;
		log("enforcing " + target);
		context.setActionAttributes(null);
		context.setResourceAttributes(null);
		xacmlPep.enforce(context.getSubjectValue(SUBJECT_ID_URI_STRING), target, Constants.POLICY_ACTION.APIM.uri, pid, extractNamespace(pid), context);
	}	
	
	public final void enforceGetObjectXML(Context context, String pid) 
	throws NotAuthorizedException {
		String target = Constants.POLICY_ACTION.GET_OBJECT_XML.uri;
		log("enforcing " + target);
		context.setActionAttributes(null);
		context.setResourceAttributes(null);
		xacmlPep.enforce(context.getSubjectValue(SUBJECT_ID_URI_STRING), target, Constants.POLICY_ACTION.APIM.uri, pid, extractNamespace(pid), context);
	}		
	
	public final void enforceIngestObject(Context context, String pid) 
	throws NotAuthorizedException {
		String target = Constants.POLICY_ACTION.INGEST_OBJECT.uri;
		log("enforcing " + target);
		context.setActionAttributes(null);
		context.setResourceAttributes(null);
		xacmlPep.enforce(context.getSubjectValue(SUBJECT_ID_URI_STRING), target, Constants.POLICY_ACTION.APIM.uri, pid, extractNamespace(pid), context);
	}
	
	public final void enforceListObjectInFieldSearchResults(Context context, String pid) 
	throws NotAuthorizedException {
		String target = Constants.POLICY_ACTION.LIST_OBJECT_IN_FIELD_SEARCH_RESULTS.uri;
		log("enforcing " + target);
		if (enforceListObjectInFieldSearchResults) {
			context.setActionAttributes(null);
			context.setResourceAttributes(null);
			xacmlPep.enforce(context.getSubjectValue(SUBJECT_ID_URI_STRING), target, Constants.POLICY_ACTION.APIA.uri, pid, extractNamespace(pid), context);
		}
	}	
	
	public final void enforceListObjectInResourceIndexResults(Context context, String pid) 
	throws NotAuthorizedException {
		String target = Constants.POLICY_ACTION.LIST_OBJECT_IN_RESOURCE_INDEX_RESULTS.uri;
		log("enforcing " + target);
		if (enforceListObjectInResourceIndexResults) {
			context.setActionAttributes(null);
			context.setResourceAttributes(null);
			xacmlPep.enforce(context.getSubjectValue(SUBJECT_ID_URI_STRING), target, Constants.POLICY_ACTION.APIA.uri, pid, extractNamespace(pid), context);
		}
	}

	public final void enforceModifyDatastreamByReference(Context context, String pid, String datastreamId, String datastreamNewLocation, String datastreamNewState) 
	throws NotAuthorizedException {
		String target = Constants.POLICY_ACTION.MODIFY_DATASTREAM_BY_REFERENCE.uri;
		log("enforcing " + target);
		context.setActionAttributes(null);
		MultiValueMap resourceAttributes = new MultiValueMap();
		String name = "";
		try {
			name = resourceAttributes.setReturn(Constants.POLICY_RESOURCE.DATASTREAM_ID.uri, datastreamId);
			name = resourceAttributes.setReturn(Constants.POLICY_ACTION.DATASTREAM_NEW_LOCATION.uri, datastreamNewLocation);			
			name = resourceAttributes.setReturn(Constants.POLICY_ACTION.DATASTREAM_NEW_STATE.uri, datastreamNewState);	
		} catch (Exception e) {
			context.setResourceAttributes(null);		
			throw new AuthzOperationalException(target + " couldn't set " + name, e);	
		}
		context.setResourceAttributes(resourceAttributes);	
		xacmlPep.enforce(context.getSubjectValue(SUBJECT_ID_URI_STRING), target, Constants.POLICY_ACTION.APIM.uri, pid, extractNamespace(pid), context);
	}
	
	public final void enforceModifyDatastreamByValue(Context context, String pid, String datastreamId, String newDatastreamState) 
	throws NotAuthorizedException { 
		String target = Constants.POLICY_ACTION.MODIFY_DATASTREAM_BY_VALUE.uri;
		log("enforcing " + target);
		context.setActionAttributes(null);
		MultiValueMap resourceAttributes = new MultiValueMap();
		String name = "";
		try {
			name = resourceAttributes.setReturn(Constants.POLICY_RESOURCE.DATASTREAM_ID.uri, datastreamId);
			name = resourceAttributes.setReturn(Constants.POLICY_ACTION.DATASTREAM_NEW_STATE.uri, newDatastreamState);
		} catch (Exception e) {
			context.setResourceAttributes(null);		
			throw new AuthzOperationalException(target + " couldn't set " + name, e);	
		}
		context.setResourceAttributes(resourceAttributes);	
		xacmlPep.enforce(context.getSubjectValue(SUBJECT_ID_URI_STRING), target, Constants.POLICY_ACTION.APIM.uri, pid, extractNamespace(pid), context);
	}	

	public final void enforceModifyDisseminator(Context context, String pid, String disseminatorPid, String bmechNewPid, String disseminatorNewState) 
	throws NotAuthorizedException {
		String target = Constants.POLICY_ACTION.MODIFY_DISSEMINATOR.uri;
		log("enforcing " + target);
		context.setActionAttributes(null);
		MultiValueMap resourceAttributes = new MultiValueMap();
		String name = "";
		try {
			name = resourceAttributes.setReturn(Constants.POLICY_RESOURCE.DISSEMINATOR_PID.uri, disseminatorPid);
			name = resourceAttributes.setReturn(Constants.POLICY_RESOURCE.DISSEMINATOR_NAMESPACE.uri, extractNamespace(disseminatorPid));
			name = resourceAttributes.setReturn(Constants.POLICY_ACTION.BMECH_NEW_PID.uri, bmechNewPid);	
			name = resourceAttributes.setReturn(Constants.POLICY_ACTION.BMECH_NEW_NAMESPACE.uri, extractNamespace(bmechNewPid));		
			name = resourceAttributes.setReturn(Constants.POLICY_ACTION.DISSEMINATOR_NEW_STATE.uri, extractNamespace(disseminatorNewState));		
		} catch (Exception e) {
			context.setResourceAttributes(null);		
			throw new AuthzOperationalException(target + " couldn't set " + name, e);	
		}
		context.setResourceAttributes(resourceAttributes);	
		xacmlPep.enforce(context.getSubjectValue(SUBJECT_ID_URI_STRING), target, Constants.POLICY_ACTION.APIM.uri, pid, extractNamespace(pid), context);
	}	
	
	public final void enforceModifyObject(Context context, String pid, String objectNewState) 
	throws NotAuthorizedException {
		String target = Constants.POLICY_ACTION.MODIFY_OBJECT.uri;
		log("enforcing " + target);
		context.setActionAttributes(null);
		MultiValueMap resourceAttributes = new MultiValueMap();
		String name = "";
		try {
			name = resourceAttributes.setReturn(Constants.POLICY_ACTION.OBJECT_NEW_STATE.uri, objectNewState);	
		} catch (Exception e) {
			context.setResourceAttributes(null);		
			throw new AuthzOperationalException(target + " couldn't set " + name, e);	
		}
		context.setResourceAttributes(resourceAttributes);	
		xacmlPep.enforce(context.getSubjectValue(SUBJECT_ID_URI_STRING), target, Constants.POLICY_ACTION.APIM.uri, pid, extractNamespace(pid), context);
	}

	public final void enforcePurgeDatastream(Context context, String pid, String datastreamId) 
	throws NotAuthorizedException {
		String target = Constants.POLICY_ACTION.PURGE_DATASTREAM.uri;
		log("enforcing " + target);
		context.setActionAttributes(null);
		MultiValueMap resourceAttributes = new MultiValueMap();
		String name = "";
		try {
			name = resourceAttributes.setReturn(Constants.POLICY_RESOURCE.DATASTREAM_ID.uri, datastreamId);	
		} catch (Exception e) {
			context.setResourceAttributes(null);		
			throw new AuthzOperationalException(target + " couldn't set " + name, e);	
		}
		context.setResourceAttributes(resourceAttributes);	
		xacmlPep.enforce(context.getSubjectValue(SUBJECT_ID_URI_STRING), target, Constants.POLICY_ACTION.APIM.uri, pid, extractNamespace(pid), context);
	}

	public final void enforcePurgeDisseminator(Context context, String pid, String disseminatorId) 
	throws NotAuthorizedException {
		String target = Constants.POLICY_ACTION.PURGE_DISSEMINATOR.uri;
		log("enforcing " + target);
		context.setActionAttributes(null);
		MultiValueMap resourceAttributes = new MultiValueMap();
		String name = "";
		try {
			name = resourceAttributes.setReturn(Constants.POLICY_RESOURCE.DISSEMINATOR_ID.uri, disseminatorId);	
		} catch (Exception e) {
			context.setResourceAttributes(null);		
			throw new AuthzOperationalException(target + " couldn't set " + name, e);	
		}
		context.setResourceAttributes(resourceAttributes);	
		xacmlPep.enforce(context.getSubjectValue(SUBJECT_ID_URI_STRING), target, Constants.POLICY_ACTION.APIM.uri, pid, extractNamespace(pid), context);
	}	
	
	public final void enforcePurgeObject(Context context, String pid) 
	throws NotAuthorizedException {
		String target = Constants.POLICY_ACTION.PURGE_OBJECT.uri;
		log("enforcing " + target);
		context.setActionAttributes(null);
		context.setResourceAttributes(null);
		xacmlPep.enforce(context.getSubjectValue(SUBJECT_ID_URI_STRING), target, Constants.POLICY_ACTION.APIM.uri, pid, extractNamespace(pid), context);
	}	

	public final void enforceSetDatastreamState(Context context, String pid, String datastreamId, String datastreamNewState) 
	throws NotAuthorizedException {
		String target = Constants.POLICY_ACTION.SET_DATASTREAM_STATE.uri;
		log("enforcing " + target);
		context.setActionAttributes(null);
		MultiValueMap resourceAttributes = new MultiValueMap();
		String name = "";
		try {
			name = resourceAttributes.setReturn(Constants.POLICY_RESOURCE.DATASTREAM_ID.uri, datastreamId);	
			name = resourceAttributes.setReturn(Constants.POLICY_ACTION.DATASTREAM_NEW_STATE.uri, datastreamNewState);	
		} catch (Exception e) {
			context.setResourceAttributes(null);		
			throw new AuthzOperationalException(target + " couldn't set " + name, e);	
		}
		context.setResourceAttributes(resourceAttributes);	
		xacmlPep.enforce(context.getSubjectValue(SUBJECT_ID_URI_STRING), target, Constants.POLICY_ACTION.APIM.uri, pid, extractNamespace(pid), context);
	}
	
	public final void enforceSetDisseminatorState(Context context, String pid, String disseminatorId, String disseminatorNewState) 
	throws NotAuthorizedException {
		String target = Constants.POLICY_ACTION.SET_DISSEMINATOR_STATE.uri;
		log("enforcing " + target);
		context.setActionAttributes(null);
		MultiValueMap resourceAttributes = new MultiValueMap();
		String name = "";
		try {
			name = resourceAttributes.setReturn(Constants.POLICY_RESOURCE.DISSEMINATOR_ID.uri, disseminatorId);	
			name = resourceAttributes.setReturn(Constants.POLICY_RESOURCE.DISSEMINATOR_NAMESPACE.uri, extractNamespace(disseminatorId));			
			name = resourceAttributes.setReturn(Constants.POLICY_ACTION.DISSEMINATOR_NEW_STATE.uri, disseminatorNewState);	
		} catch (Exception e) {
			context.setResourceAttributes(null);		
			throw new AuthzOperationalException(target + " couldn't set " + name, e);	
		}
		context.setResourceAttributes(resourceAttributes);	
		xacmlPep.enforce(context.getSubjectValue(SUBJECT_ID_URI_STRING), target, Constants.POLICY_ACTION.APIM.uri, pid, extractNamespace(pid), context);
	}
	
	public void enforceDescribeRepository(Context context) 
	throws NotAuthorizedException {
		String target = Constants.POLICY_ACTION.DESCRIBE_REPOSITORY.uri;
		log("enforcing " + target);
		context.setActionAttributes(null);
		context.setResourceAttributes(null);
		xacmlPep.enforce(context.getSubjectValue(SUBJECT_ID_URI_STRING), target, Constants.POLICY_ACTION.APIA.uri, "", "", context);
	}

	public void enforceFindObjects(Context context) 
	throws NotAuthorizedException {
		String target = Constants.POLICY_ACTION.FIND_OBJECTS.uri;
		log("enforcing " + target);
		context.setActionAttributes(null);
		context.setResourceAttributes(null);
		System.err.println("enforceFindObjects, subject (from context)=" + context.getSubjectValue(SUBJECT_ID_URI_STRING));
		xacmlPep.enforce(context.getSubjectValue(SUBJECT_ID_URI_STRING), target, Constants.POLICY_ACTION.APIA.uri, "", "", context);
	}
	
	public void enforceRIFindObjects(Context context) 
	throws NotAuthorizedException {
		String target = Constants.POLICY_ACTION.RI_FIND_OBJECTS.uri;
		log("enforcing " + target);
		context.setActionAttributes(null);
		context.setResourceAttributes(null);
		xacmlPep.enforce(context.getSubjectValue(SUBJECT_ID_URI_STRING), target, Constants.POLICY_ACTION.APIA.uri, "", "", context);
	}

	public void enforceGetDatastreamDissemination(Context context, String pid, String datastreamId, Date asOfDate) 
	throws NotAuthorizedException {
		String target = Constants.POLICY_ACTION.GET_DATASTREAM_DISSEMINATION.uri;
		log("enforcing " + target);
		context.setActionAttributes(null);
		MultiValueMap resourceAttributes = new MultiValueMap();
		String name = "";
		try {
			name = resourceAttributes.setReturn(Constants.POLICY_RESOURCE.DATASTREAM_ID.uri, datastreamId);	
			name = resourceAttributes.setReturn(RESOURCE_AS_OF_DATE_URI_STRING, ensureDate(asOfDate, context));	
		} catch (Exception e) {
			context.setResourceAttributes(null);		
			throw new AuthzOperationalException(target + " couldn't set " + name, e);	
		}
		context.setResourceAttributes(resourceAttributes);			
		xacmlPep.enforce(context.getSubjectValue(SUBJECT_ID_URI_STRING), target, Constants.POLICY_ACTION.APIA.uri, pid, extractNamespace(pid), context);
	}
	
	public void enforceGetDissemination(Context context, String pid, String bDefPid, String methodName, Date asOfDate) 
	throws NotAuthorizedException {
		String target = Constants.POLICY_ACTION.GET_DISSEMINATION.uri;
		log("enforcing " + target);
		context.setActionAttributes(null);
		MultiValueMap resourceAttributes = new MultiValueMap();
		String name = "";
		try {
			name = resourceAttributes.setReturn(Constants.POLICY_ACTION.BDEF_PID.uri, bDefPid);	
			name = resourceAttributes.setReturn(Constants.POLICY_ACTION.BDEF_NAMESPACE.uri, extractNamespace(bDefPid));	
			name = resourceAttributes.setReturn(Constants.POLICY_ACTION.DISSEMINATOR_METHOD.uri, methodName);
			name = resourceAttributes.setReturn(RESOURCE_AS_OF_DATE_URI_STRING, ensureDate(asOfDate, context));	
		} catch (Exception e) {
			context.setResourceAttributes(null);		
			throw new AuthzOperationalException(target + " couldn't set " + name, e);	
		}
		context.setResourceAttributes(resourceAttributes);			
		xacmlPep.enforce(context.getSubjectValue(SUBJECT_ID_URI_STRING), target, Constants.POLICY_ACTION.APIA.uri, pid, extractNamespace(pid), context);
	}

	public void enforceGetObjectHistory(Context context, String pid) 
	throws NotAuthorizedException {
		String target = Constants.POLICY_ACTION.GET_OBJECT_HISTORY.uri;
		log("enforcing " + target);
		context.setActionAttributes(null);
		context.setResourceAttributes(null);
		xacmlPep.enforce(context.getSubjectValue(SUBJECT_ID_URI_STRING), target, Constants.POLICY_ACTION.APIA.uri, pid, extractNamespace(pid), context);
	}

	public void enforceGetObjectProfile(Context context, String pid, Date asOfDate) 
	throws NotAuthorizedException {
		String target = Constants.POLICY_ACTION.GET_OBJECT_PROFILE.uri;
		log("enforcing " + target);
		context.setActionAttributes(null);
		MultiValueMap resourceAttributes = new MultiValueMap();
		String name = "";
		try {
			name = resourceAttributes.setReturn(Constants.POLICY_RESOURCE.AS_OF_DATE.uri, ensureDate(asOfDate, context));
		} catch (Exception e) {
			context.setResourceAttributes(null);		
			throw new AuthzOperationalException(target + " couldn't set " + name, e);	
		}
		context.setResourceAttributes(resourceAttributes);
		xacmlPep.enforce(context.getSubjectValue(SUBJECT_ID_URI_STRING), target, Constants.POLICY_ACTION.APIA.uri, pid, extractNamespace(pid), context);
	}

	public void enforceListDatastreams(Context context, String pid, Date asOfDate) 
	throws NotAuthorizedException {
		String target = Constants.POLICY_ACTION.LIST_DATASTREAMS.uri;
		log("enforcing " + target);
		context.setActionAttributes(null);
		MultiValueMap resourceAttributes = new MultiValueMap();
		String name = "";
		try {
			name = resourceAttributes.setReturn(RESOURCE_AS_OF_DATE_URI_STRING, ensureDate(asOfDate, context));				
		} catch (Exception e) {
			context.setResourceAttributes(null);		
			throw new AuthzOperationalException(target + " couldn't set " + name, e);	
		}
		context.setResourceAttributes(resourceAttributes);
		xacmlPep.enforce(context.getSubjectValue(SUBJECT_ID_URI_STRING), target, Constants.POLICY_ACTION.APIA.uri, pid, extractNamespace(pid), context);
	}

	public void enforceListMethods(Context context, String pid, Date asOfDate) 
	throws NotAuthorizedException {
		String target = Constants.POLICY_ACTION.LIST_METHODS.uri;
		log("enforcing " + target);
		context.setActionAttributes(null);
		MultiValueMap resourceAttributes = new MultiValueMap();
		String name = "";
		try {
			name = resourceAttributes.setReturn(RESOURCE_AS_OF_DATE_URI_STRING, ensureDate(asOfDate, context));				
		} catch (Exception e) {
			context.setResourceAttributes(null);		
			throw new AuthzOperationalException(target + " couldn't set " + name, e);	
		}
		context.setResourceAttributes(resourceAttributes);
		xacmlPep.enforce(context.getSubjectValue(SUBJECT_ID_URI_STRING), target, Constants.POLICY_ACTION.APIA.uri, pid, extractNamespace(pid), context);
	}

	  private static final String pad(int n, int length) throws Exception {
	  	String asString = Integer.toString(n);
	  	if (asString.length() > length) {
	  		throw new Exception("value as string is too long");
	  	}
	  	StringBuffer padding = new StringBuffer();
	  	for (int i=0; i<(length - asString.length()); i++) {
	  		padding.append('0');
	  	}
	  	return padding + asString; 
	  }
	  
	  public static final String dateAsString (Date date) throws Exception {
	  	 //2003-12-13T18:30:02Z
	  	StringBuffer temp = new StringBuffer();
	  	try {
	  	temp.append(pad(1900 + date.getYear(),4));
	  	temp.append('-');
	  	temp.append(pad(1 + date.getMonth(),2));
	  	temp.append('-');
	  	temp.append(pad(date.getDate(),2));
	  	temp.append('T');
	  	temp.append(pad(date.getHours(),2));
	  	temp.append(':');
	  	temp.append(pad(date.getMinutes(),2));
	  	temp.append(':');
	  	temp.append(pad(date.getSeconds(),2));
	  	temp.append('Z'); //<<<<<<<<<<<<XXXXXXXXXXX!!!!!!!!!!!!!!!!!
	  	} catch (Exception e) {
	  		System.err.println("exception in dateAsString " + temp.toString());
	  		System.err.println(e + " " + e.getMessage());
	  		throw e;
	  	}
	  	return temp.toString();
	  }
	  
	  private static final void putAsOfDate (Hashtable resourceAttributes, Date asOfDate) throws Exception {
	  	resourceAttributes.put("asOfDate", dateAsString(asOfDate));
	  }
	  
	  private boolean log = false;
	  
	  private final void log(String msg) {
	  	if (log) {
		  	System.err.println(msg);	  		
	  	}
	  }

	  private final String logged(String msg) {
	  	log(msg);
	  	return msg;
	  }

	
}

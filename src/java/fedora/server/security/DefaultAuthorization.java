package fedora.server.security;


import java.util.Date;
import java.util.Hashtable;
import java.util.Map;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
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
 
	try {
		ACTION_NEW_STATE_URI = new URI(ACTION_NEW_STATE_URI_STRING);		
		ACTION_DATASTREAM_NEW_STATE_URI = new URI(ACTION_DATASTREAM_NEW_STATE_URI_STRING);		
		ACTION_DISSEMINATOR_NEW_STATE_URI = new URI(ACTION_DISSEMINATOR_NEW_STATE_URI_STRING);	

		//RESOURCE_ID_URI = new URI(RESOURCE_ID_URI_STRING);		
		//RESOURCE_NAMESPACE_URI = new URI(RESOURCE_NAMESPACE_URI_STRING);
		RESOURCE_AS_OF_DATE_URI = new URI(RESOURCE_AS_OF_DATE_URI_STRING);
		//RESOURCE_STATE_URI = new URI(RESOURCE_STATE_URI_STRING);
		
		RESOURCE_DATASTREAM_ID_URI = new URI(RESOURCE_DATASTREAM_ID_URI_STRING);
		//RESOURCE_DATASTREAM_NAMESPACE_URI = new URI(RESOURCE_DATASTREAM_NAMESPACE_URI_STRING);
		RESOURCE_DATASTREAM_STATE_URI = new URI(RESOURCE_DATASTREAM_STATE_URI_STRING);
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

		ENVIRONMENT_CURRENT_DATETIME_URI = new URI(ENVIRONMENT_CURRENT_DATETIME_URI_STRING);
		ENVIRONMENT_CURRENT_DATE_URI = new URI(ENVIRONMENT_CURRENT_DATE_URI_STRING);
		ENVIRONMENT_CURRENT_TIME_URI = new URI(ENVIRONMENT_CURRENT_TIME_URI_STRING);		
		ENVIRONMENT_REQUEST_PROTOCOL_URI = new URI(ENVIRONMENT_REQUEST_PROTOCOL_URI_STRING);
		ENVIRONMENT_REQUEST_SCHEME_URI = new URI(ENVIRONMENT_REQUEST_SCHEME_URI_STRING);
		ENVIRONMENT_REQUEST_SECURITY_URI = new URI(ENVIRONMENT_REQUEST_SECURITY_URI_STRING);
		ENVIRONMENT_REQUEST_AUTHTYPE_URI = new URI(ENVIRONMENT_REQUEST_AUTHTYPE_URI_STRING);
		ENVIRONMENT_REQUEST_METHOD_URI = new URI(ENVIRONMENT_REQUEST_METHOD_URI_STRING);	
		ENVIRONMENT_REQUEST_SESSION_ENCODING_URI = new URI(ENVIRONMENT_REQUEST_SESSION_ENCODING_URI_STRING);	
		ENVIRONMENT_REQUEST_SESSION_STATUS_URI = new URI(ENVIRONMENT_REQUEST_SESSION_STATUS_URI_STRING);		
		ENVIRONMENT_REQUEST_CONTENT_LENGTH_URI = new URI(ENVIRONMENT_REQUEST_CONTENT_LENGTH_URI_STRING);
		ENVIRONMENT_REQUEST_CONTENT_TYPE_URI = new URI(ENVIRONMENT_REQUEST_CONTENT_TYPE_URI_STRING);
		ENVIRONMENT_REQUEST_SOAP_OR_REST_URI = new URI(ENVIRONMENT_REQUEST_MESSAGE_PROTOCOL_URI_STRING);
		ENVIRONMENT_CLIENT_FQDN_URI = new URI(ENVIRONMENT_CLIENT_FQDN_URI_STRING);		
		ENVIRONMENT_CLIENT_IP_URI = new URI(ENVIRONMENT_CLIENT_IP_URI_STRING);
		ENVIRONMENT_SERVER_FQDN_URI = new URI(ENVIRONMENT_SERVER_FQDN_URI_STRING);
		ENVIRONMENT_SERVER_IP_URI = new URI(ENVIRONMENT_SERVER_IP_URI_STRING);	
		ENVIRONMENT_SERVER_PORT_URI = new URI(ENVIRONMENT_SERVER_PORT_URI_STRING);	
		//RESOURCE_STATE_URI = new URI(RESOURCE_STATE);

	} catch (URISyntaxException e) {
		throw new ModuleInitializationException("couldn't make URIs", getRole(), e);
	}
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

  /*
  public XACMLPep getPep() throws ServerException {
  	return xacmlPep;
  }
  */
 
	//private static final boolean enforceResourceIndexList = true;
  	//private static final boolean enforceSearchList = true;

  /*
	private static final String[] parseRole (String role) {
		//System.out.println("parseRole() "+role);
		String[] parts = null;
		if ((role == null) || (role.length() == 0)) {
			//System.out.println("(role == null) || (role.length() == 0)");
		} else {
			int i = role.indexOf('=');
			if (i == 0) {
				//System.out.println("i==0");
			} else {
				parts = new String[2];	
				if (i < 0) {
					parts[0] = role;
					parts[1] = "";			
				} else {
					parts[0] = role.substring(0,i);
					//System.out.println("parts[0]="+parts[0]);
					if (i == (role.length()-1)) {
						parts[1] = "";
						//System.out.println("parts[1] set to \"\"");
					} else {
						parts[1] = role.substring(i+1);
						//System.out.println("parts[1]="+parts[1]);
					}
				}
				//System.out.println("parts[0]="+parts[0]+"parts[1]="+parts[1]);
			}
		}
		return parts; 
	}
	
	private static final String[] NO_ROLES = new String[0];
	*/
  
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
		context.setActionAttributes(null);
		MultiValueMap resourceAttributes = new MultiValueMap();
		try {
			//resourceAttributes.set(RESOURCE_ID_URI_STRING, pid);	
			//resourceAttributes.set(RESOURCE_NAMESPACE_URI_STRING, extractNamespace(pid));	
			resourceAttributes.set(RESOURCE_DATASTREAM_ID_URI_STRING, dsId);
			resourceAttributes.set(RESOURCE_DATASTREAM_LOCATION_URI_STRING, dsLocation);
			resourceAttributes.set(RESOURCE_DATASTREAM_CONTROL_GROUP_URI_STRING, controlGroup);
			resourceAttributes.set(RESOURCE_DATASTREAM_STATE_URI_STRING, dsState);
		} catch (Exception e) {
			context.setResourceAttributes(null);		
			throw new AuthzOperationalException("enforceX could not complete", e);	
		}
		context.setResourceAttributes(resourceAttributes);
		xacmlPep.enforce(context.getSubjectValue(SUBJECT_ID_URI_STRING), ACTION_ID_VALUE_ADD_DATASTREAM, ACTION_API_VALUE_APIM, pid, extractNamespace(pid), context);
	}
	
	public final void enforceAddDisseminator(Context context, String pid) 
	throws NotAuthorizedException { 
		context.setActionAttributes(null);
		context.setResourceAttributes(null);
		/*
		MultiValueMap resourceAttributes = new MultiValueMap();
		try {
			resourceAttributes.set(RESOURCE_ID_URI_STRING, pid);	
			resourceAttributes.set(RESOURCE_NAMESPACE_URI_STRING, extractNamespace(pid));	
		} catch (Exception e) {
			context.setResourceAttributes(null);		
			throw new AuthzOperationalException("enforceX could not complete", e);	
		}
		context.setResourceAttributes(resourceAttributes);
		*/
		xacmlPep.enforce(context.getSubjectValue(SUBJECT_ID_URI_STRING), ACTION_ID_VALUE_ADD_DISSEMINATOR, ACTION_API_VALUE_APIM, pid, extractNamespace(pid), context);
	}
		
	public final void enforceExportObject(Context context, String pid) 
	throws NotAuthorizedException { 
		context.setActionAttributes(null);
		context.setResourceAttributes(null);
		/*
		MultiValueMap resourceAttributes = new MultiValueMap();
		try {
			resourceAttributes.set(RESOURCE_ID_URI_STRING, pid);	
			resourceAttributes.set(RESOURCE_NAMESPACE_URI_STRING, extractNamespace(pid));	
		} catch (Exception e) {
			context.setResourceAttributes(null);		
			throw new AuthzOperationalException("enforceX could not complete", e);	
		}
		context.setResourceAttributes(resourceAttributes);
		*/
		xacmlPep.enforce(context.getSubjectValue(SUBJECT_ID_URI_STRING), ACTION_ID_VALUE_EXPORT_OBJECT, ACTION_API_VALUE_APIM, pid, extractNamespace(pid), context);
	}			
	
	
	public final void enforceGetDisseminatorHistory(Context context, String pid, String disseminatorId) 
	throws NotAuthorizedException { 
		context.setActionAttributes(null);
		MultiValueMap resourceAttributes = new MultiValueMap();
		try {
			//resourceAttributes.set(RESOURCE_ID_URI_STRING, pid);	
			//resourceAttributes.set(RESOURCE_NAMESPACE_URI_STRING, extractNamespace(pid));	
			resourceAttributes.set(RESOURCE_DISSEMINATOR_PID_URI_STRING, disseminatorId);	
			resourceAttributes.set(RESOURCE_DISSEMINATOR_NAMESPACE_URI_STRING, extractNamespace(disseminatorId));	
		} catch (Exception e) {
			context.setResourceAttributes(null);		
			throw new AuthzOperationalException("enforceX could not complete", e);	
		}
		context.setResourceAttributes(resourceAttributes);
		xacmlPep.enforce(context.getSubjectValue(SUBJECT_ID_URI_STRING), ACTION_ID_VALUE_GET_DISSEMINATOR_HISTORY, ACTION_API_VALUE_APIM, pid, extractNamespace(pid), context);
	}	

	public final void enforceGetNextPid(Context context, String namespace, int nNewPids) 
	throws NotAuthorizedException { 
		context.setActionAttributes(null);
		MultiValueMap resourceAttributes = new MultiValueMap();
		try {	
			//resourceAttributes.set(RESOURCE_NAMESPACE_URI_STRING, namespace);	
			String nNewPidsAsString = Integer.toString(nNewPids);
			resourceAttributes.set(RESOURCE_N_NEW_PIDS_URI_STRING, nNewPidsAsString);		
		} catch (Exception e) {
			context.setResourceAttributes(null);		
			throw new AuthzOperationalException("enforceX could not complete", e);	
		}
		context.setResourceAttributes(resourceAttributes);
		xacmlPep.enforce(context.getSubjectValue(SUBJECT_ID_URI_STRING), ACTION_ID_VALUE_GET_NEXT_PID, ACTION_API_VALUE_APIM, "", namespace, context);
	}	

	public final void enforceGetDatastream(Context context, String pid, String datastreamId) 
	throws NotAuthorizedException { 
		context.setActionAttributes(null);
		MultiValueMap resourceAttributes = new MultiValueMap();
		try {
			//resourceAttributes.set(RESOURCE_ID_URI_STRING, pid);	
			//resourceAttributes.set(RESOURCE_NAMESPACE_URI_STRING, extractNamespace(pid));	
			resourceAttributes.set(RESOURCE_DATASTREAM_ID_URI_STRING, datastreamId);		
		} catch (Exception e) {
			context.setResourceAttributes(null);		
			throw new AuthzOperationalException("enforceX could not complete", e);	
		}
		context.setResourceAttributes(resourceAttributes);
		xacmlPep.enforce(context.getSubjectValue(SUBJECT_ID_URI_STRING), ACTION_ID_VALUE_GET_DATASTREAM, ACTION_API_VALUE_APIM, pid, extractNamespace(pid), context);
	}
	
	public final void enforceGetDatastreamHistory(Context context, String pid, String datastreamId) 
	throws NotAuthorizedException { 
		context.setActionAttributes(null);
		MultiValueMap resourceAttributes = new MultiValueMap();
		try {
			//resourceAttributes.set(RESOURCE_ID_URI_STRING, pid);	
			//resourceAttributes.set(RESOURCE_NAMESPACE_URI_STRING, extractNamespace(pid));	
			resourceAttributes.set(RESOURCE_DATASTREAM_ID_URI_STRING, datastreamId);		
		} catch (Exception e) {
			context.setResourceAttributes(null);		
			throw new AuthzOperationalException("enforceX could not complete", e);	
		}
		context.setResourceAttributes(resourceAttributes);
		xacmlPep.enforce(context.getSubjectValue(SUBJECT_ID_URI_STRING), ACTION_ID_VALUE_GET_DATASTREAM_HISTORY, ACTION_API_VALUE_APIM, pid, extractNamespace(pid), context);
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
		String asOfDateAsString = ensureDate(asOfDate, context);
		context.setActionAttributes(null);
		MultiValueMap resourceAttributes = new MultiValueMap();
		try {
			//resourceAttributes.set(RESOURCE_ID_URI_STRING, pid);	
			//resourceAttributes.set(RESOURCE_NAMESPACE_URI_STRING, extractNamespace(pid));	
			resourceAttributes.set(RESOURCE_DATASTREAM_STATE_URI_STRING, datastreamState);	
			resourceAttributes.set(RESOURCE_AS_OF_DATE_URI_STRING, asOfDateAsString);	
		} catch (Exception e) {
			context.setResourceAttributes(null);		
			throw new AuthzOperationalException("enforceX could not complete", e);	
		}
		context.setResourceAttributes(resourceAttributes);
		xacmlPep.enforce(context.getSubjectValue(SUBJECT_ID_URI_STRING), ACTION_ID_VALUE_GET_DATASTREAMS, ACTION_API_VALUE_APIM, pid, extractNamespace(pid), context);
	}	

	public final void enforceGetDisseminator(Context context, String pid, String disseminatorId, Date asOfDate) 
	throws NotAuthorizedException { 
		String asOfDateAsString = ensureDate(asOfDate, context);
		context.setActionAttributes(null);
		MultiValueMap resourceAttributes = new MultiValueMap();
		try {
			//resourceAttributes.set(RESOURCE_ID_URI_STRING, pid);	
			//resourceAttributes.set(RESOURCE_NAMESPACE_URI_STRING, extractNamespace(pid));	
			resourceAttributes.set(RESOURCE_DISSEMINATOR_PID_URI_STRING, disseminatorId);	
			resourceAttributes.set(RESOURCE_DISSEMINATOR_NAMESPACE_URI_STRING, extractNamespace(disseminatorId));
			resourceAttributes.set(RESOURCE_AS_OF_DATE_URI_STRING, asOfDateAsString);	
		} catch (Exception e) {
			context.setResourceAttributes(null);		
			throw new AuthzOperationalException("enforceX could not complete", e);	
		}
		context.setResourceAttributes(resourceAttributes);		
		xacmlPep.enforce(context.getSubjectValue(SUBJECT_ID_URI_STRING), ACTION_ID_VALUE_GET_DISSEMINATOR, ACTION_API_VALUE_APIM, pid, extractNamespace(pid), context);
	}	
	
	public final void enforceGetDisseminators(Context context, String pid, Date asOfDate, String disseminatorState) 
	throws NotAuthorizedException { 
		String asOfDateAsString = ensureDate(asOfDate, context);
		context.setActionAttributes(null);
		MultiValueMap resourceAttributes = new MultiValueMap();
		try {
			//resourceAttributes.set(RESOURCE_ID_URI_STRING, pid);	
			//resourceAttributes.set(RESOURCE_NAMESPACE_URI_STRING, extractNamespace(pid));	
			resourceAttributes.set(RESOURCE_DISSEMINATOR_STATE_URI_STRING, disseminatorState);	
			resourceAttributes.set(RESOURCE_AS_OF_DATE_URI_STRING, asOfDateAsString);	
		} catch (Exception e) {
			context.setResourceAttributes(null);		
			throw new AuthzOperationalException("enforceX could not complete", e);	
		}
		context.setResourceAttributes(resourceAttributes);	
		xacmlPep.enforce(context.getSubjectValue(SUBJECT_ID_URI_STRING), ACTION_ID_VALUE_GET_DISSEMINATORS, ACTION_API_VALUE_APIM, pid, extractNamespace(pid), context);
	}
	
	public final void enforceGetObjectProperties(Context context, String pid) 
	throws NotAuthorizedException { 
		context.setActionAttributes(null);
		context.setResourceAttributes(null);
		/*
		MultiValueMap resourceAttributes = new MultiValueMap();
		try {
			resourceAttributes.set(RESOURCE_ID_URI_STRING, pid);	
			resourceAttributes.set(RESOURCE_NAMESPACE_URI_STRING, extractNamespace(pid));	
		} catch (Exception e) {
			context.setResourceAttributes(null);		
			throw new AuthzOperationalException("enforceX could not complete", e);	
		}
		context.setResourceAttributes(resourceAttributes);
		*/
		xacmlPep.enforce(context.getSubjectValue(SUBJECT_ID_URI_STRING), ACTION_ID_VALUE_GET_OBJECT_PROPERTIES, ACTION_API_VALUE_APIM, pid, extractNamespace(pid), context);
	}	
	
	public final void enforceGetObjectXML(Context context, String pid) 
	throws NotAuthorizedException { 
		context.setActionAttributes(null);
		context.setResourceAttributes(null);
		/*
		MultiValueMap resourceAttributes = new MultiValueMap();
		try {
			resourceAttributes.set(RESOURCE_ID_URI_STRING, pid);	
			resourceAttributes.set(RESOURCE_NAMESPACE_URI_STRING, extractNamespace(pid));	
		} catch (Exception e) {
			context.setResourceAttributes(null);		
			throw new AuthzOperationalException("enforceX could not complete", e);	
		}
		context.setResourceAttributes(resourceAttributes);
		*/
		xacmlPep.enforce(context.getSubjectValue(SUBJECT_ID_URI_STRING), ACTION_ID_VALUE_GET_OBJECT_XML, ACTION_API_VALUE_APIM, pid, extractNamespace(pid), context);
	}		
	
	public final void enforceIngestObject(Context context, String pid) 
	throws NotAuthorizedException { 
		context.setActionAttributes(null);
		context.setResourceAttributes(null);
		/*
		MultiValueMap resourceAttributes = new MultiValueMap();
		try {
			resourceAttributes.set(RESOURCE_ID_URI_STRING, pid);	
			resourceAttributes.set(RESOURCE_NAMESPACE_URI_STRING, extractNamespace(pid));	
		} catch (Exception e) {
			context.setResourceAttributes(null);		
			throw new AuthzOperationalException("enforceX could not complete", e);	
		}
		context.setResourceAttributes(resourceAttributes);
		*/
		xacmlPep.enforce(context.getSubjectValue(SUBJECT_ID_URI_STRING), ACTION_ID_VALUE_INGEST_OBJECT, ACTION_API_VALUE_APIM, pid, extractNamespace(pid), context);
	}
	
	public final void enforceListObjectInFieldSearchResults(Context context, String pid) 
	throws NotAuthorizedException {
		if (enforceListObjectInFieldSearchResults) {
			context.setActionAttributes(null);
			context.setResourceAttributes(null);
			/*
			MultiValueMap resourceAttributes = new MultiValueMap();
			try {
				resourceAttributes.set(RESOURCE_ID_URI_STRING, pid);	
				resourceAttributes.set(RESOURCE_NAMESPACE_URI_STRING, extractNamespace(pid));	
			} catch (Exception e) {
				context.setResourceAttributes(null);		
				throw new AuthzOperationalException("enforceX could not complete", e);	
			}
			context.setResourceAttributes(resourceAttributes);
			*/
			xacmlPep.enforce(context.getSubjectValue(SUBJECT_ID_URI_STRING), ACTION_ID_VALUE_LIST_OBJECT_IN_FIELD_SEARCH_RESULTS, ACTION_API_VALUE_APIA, pid, extractNamespace(pid), context);
		}
	}	
	
	public final void enforceListObjectInResourceIndexResults(Context context, String pid) 
	throws NotAuthorizedException {
		if (enforceListObjectInResourceIndexResults) {
			context.setActionAttributes(null);
			context.setResourceAttributes(null);
			/*
			MultiValueMap resourceAttributes = new MultiValueMap();
			try {
				resourceAttributes.set(RESOURCE_ID_URI_STRING, pid);	
				resourceAttributes.set(RESOURCE_NAMESPACE_URI_STRING, extractNamespace(pid));	
			} catch (Exception e) {
				context.setResourceAttributes(null);		
				throw new AuthzOperationalException("enforceX could not complete", e);	
			}
			context.setResourceAttributes(resourceAttributes);
			*/
			xacmlPep.enforce(context.getSubjectValue(SUBJECT_ID_URI_STRING), ACTION_ID_VALUE_LIST_OBJECT_IN_RESOURCE_INDEX_RESULTS, ACTION_API_VALUE_APIA, pid, extractNamespace(pid), context);
		}
	}

	public final void enforceModifyDatastreamByReference(Context context, String pid, String datastreamId, String datastreamLocation, String datastreamState) 
	throws NotAuthorizedException { 
		context.setActionAttributes(null);
		MultiValueMap resourceAttributes = new MultiValueMap();
		try {
			//resourceAttributes.set(RESOURCE_ID_URI_STRING, pid);	
			//resourceAttributes.set(RESOURCE_NAMESPACE_URI_STRING, extractNamespace(pid));
			resourceAttributes.set(RESOURCE_DATASTREAM_ID_URI_STRING, datastreamId);
			resourceAttributes.set(RESOURCE_DATASTREAM_LOCATION_URI_STRING, datastreamLocation);			
			resourceAttributes.set(ACTION_DATASTREAM_NEW_STATE_URI_STRING, datastreamState);	
		} catch (Exception e) {
			context.setResourceAttributes(null);		
			throw new AuthzOperationalException("enforceX could not complete", e);	
		}
		context.setResourceAttributes(resourceAttributes);	
		xacmlPep.enforce(context.getSubjectValue(SUBJECT_ID_URI_STRING), ACTION_ID_VALUE_MODIFY_DATASTREAM_BY_REFERENCE, ACTION_API_VALUE_APIM, pid, extractNamespace(pid), context);
	}
	
	public final void enforceModifyDatastreamByValue(Context context, String pid, String datastreamId, String datastreamState) 
	throws NotAuthorizedException { 
		context.setActionAttributes(null);
		MultiValueMap resourceAttributes = new MultiValueMap();
		try {
			//resourceAttributes.set(RESOURCE_ID_URI_STRING, pid);	
			//resourceAttributes.set(RESOURCE_NAMESPACE_URI_STRING, extractNamespace(pid));	
			resourceAttributes.set(RESOURCE_DATASTREAM_ID_URI_STRING, datastreamId);
			resourceAttributes.set(ACTION_DATASTREAM_NEW_STATE_URI_STRING, datastreamState);	
		} catch (Exception e) {
			context.setResourceAttributes(null);		
			throw new AuthzOperationalException("enforceX could not complete", e);	
		}
		context.setResourceAttributes(resourceAttributes);	
		xacmlPep.enforce(context.getSubjectValue(SUBJECT_ID_URI_STRING), ACTION_ID_VALUE_MODIFY_DATASTREAM_BY_VALUE, ACTION_API_VALUE_APIM, pid, extractNamespace(pid), context);
	}	

	public final void enforceModifyDisseminator(Context context, String pid, String disseminatorId, String bmechPid, String disseminatorState) 
	throws NotAuthorizedException { 
		context.setActionAttributes(null);
		MultiValueMap resourceAttributes = new MultiValueMap();
		try {
			//resourceAttributes.set(RESOURCE_ID_URI_STRING, pid);	
			//resourceAttributes.set(RESOURCE_NAMESPACE_URI_STRING, extractNamespace(pid));	
			resourceAttributes.set(RESOURCE_DISSEMINATOR_PID_URI_STRING, disseminatorId);
			resourceAttributes.set(RESOURCE_DISSEMINATOR_NAMESPACE_URI_STRING, extractNamespace(disseminatorId));
			resourceAttributes.set(RESOURCE_BMECH_PID_URI_STRING, bmechPid);	
			resourceAttributes.set(RESOURCE_BMECH_NAMESPACE_URI_STRING, extractNamespace(bmechPid));		
			resourceAttributes.set(ACTION_DISSEMINATOR_NEW_STATE_URI_STRING, extractNamespace(disseminatorState));		
		} catch (Exception e) {
			context.setResourceAttributes(null);		
			throw new AuthzOperationalException("enforceX could not complete", e);	
		}
		context.setResourceAttributes(resourceAttributes);	
		xacmlPep.enforce(context.getSubjectValue(SUBJECT_ID_URI_STRING), ACTION_ID_VALUE_MODIFY_DISSEMINATOR, ACTION_API_VALUE_APIM, pid, extractNamespace(pid), context);
	}	
	
	public final void enforceModifyObject(Context context, String pid, String objectState) 
	throws NotAuthorizedException { 
		context.setActionAttributes(null);
		MultiValueMap resourceAttributes = new MultiValueMap();
		try {
			//resourceAttributes.set(RESOURCE_ID_URI_STRING, pid);	
			//resourceAttributes.set(RESOURCE_NAMESPACE_URI_STRING, extractNamespace(pid));	
			resourceAttributes.set(RESOURCE_OBJECT_STATE_URI_STRING, objectState);	
		} catch (Exception e) {
			context.setResourceAttributes(null);		
			throw new AuthzOperationalException("enforceX could not complete", e);	
		}
		context.setResourceAttributes(resourceAttributes);	
		xacmlPep.enforce(context.getSubjectValue(SUBJECT_ID_URI_STRING), ACTION_ID_VALUE_MODIFY_OBJECT, ACTION_API_VALUE_APIM, pid, extractNamespace(pid), context);
	}

	public final void enforcePurgeDatastream(Context context, String pid, String datastreamId) 
	throws NotAuthorizedException { 
		context.setActionAttributes(null);
		MultiValueMap resourceAttributes = new MultiValueMap();
		try {
			//resourceAttributes.set(RESOURCE_ID_URI_STRING, pid);	
			//resourceAttributes.set(RESOURCE_NAMESPACE_URI_STRING, extractNamespace(pid));	
			resourceAttributes.set(RESOURCE_DATASTREAM_ID_URI_STRING, datastreamId);	
		} catch (Exception e) {
			context.setResourceAttributes(null);		
			throw new AuthzOperationalException("enforceX could not complete", e);	
		}
		context.setResourceAttributes(resourceAttributes);	
		xacmlPep.enforce(context.getSubjectValue(SUBJECT_ID_URI_STRING), ACTION_ID_VALUE_PURGE_DATASTREAM, ACTION_API_VALUE_APIM, pid, extractNamespace(pid), context);
	}

	public final void enforcePurgeDisseminator(Context context, String pid, String disseminatorId) 
	throws NotAuthorizedException { 
		context.setActionAttributes(null);
		MultiValueMap resourceAttributes = new MultiValueMap();
		try {
			//resourceAttributes.set(RESOURCE_ID_URI_STRING, pid);	
			//resourceAttributes.set(RESOURCE_NAMESPACE_URI_STRING, extractNamespace(pid));	
			resourceAttributes.set(RESOURCE_DISSEMINATOR_PID_URI_STRING, disseminatorId);	
		} catch (Exception e) {
			context.setResourceAttributes(null);		
			throw new AuthzOperationalException("enforceX could not complete", e);	
		}
		context.setResourceAttributes(resourceAttributes);	
		xacmlPep.enforce(context.getSubjectValue(SUBJECT_ID_URI_STRING), ACTION_ID_VALUE_PURGE_DISSEMINATOR, ACTION_API_VALUE_APIM, pid, extractNamespace(pid), context);
	}	
	
	public final void enforcePurgeObject(Context context, String pid) 
	throws NotAuthorizedException { 
		context.setActionAttributes(null);
		context.setResourceAttributes(null);
		/*
		MultiValueMap resourceAttributes = new MultiValueMap();
		try {
			resourceAttributes.set(RESOURCE_ID_URI_STRING, pid);	
			resourceAttributes.set(RESOURCE_NAMESPACE_URI_STRING, extractNamespace(pid));	
		} catch (Exception e) {
			context.setResourceAttributes(null);		
			throw new AuthzOperationalException("enforceX could not complete", e);	
		}
		context.setResourceAttributes(resourceAttributes);
		*/
		xacmlPep.enforce(context.getSubjectValue(SUBJECT_ID_URI_STRING), ACTION_ID_VALUE_PURGE_OBJECT, ACTION_API_VALUE_APIM, pid, extractNamespace(pid), context);
	}	

	public final void enforceSetDatastreamState(Context context, String pid, String datastreamId, String datastreamNewState) 
	throws NotAuthorizedException { 
		context.setActionAttributes(null);
		MultiValueMap resourceAttributes = new MultiValueMap();
		try {
			//resourceAttributes.set(RESOURCE_ID_URI_STRING, pid);	
			//resourceAttributes.set(RESOURCE_NAMESPACE_URI_STRING, extractNamespace(pid));	
			resourceAttributes.set(RESOURCE_DATASTREAM_ID_URI_STRING, datastreamId);	
			resourceAttributes.set(ACTION_DATASTREAM_NEW_STATE_URI_STRING, datastreamNewState);	
		} catch (Exception e) {
			context.setResourceAttributes(null);		
			throw new AuthzOperationalException("enforceX could not complete", e);	
		}
		context.setResourceAttributes(resourceAttributes);	
		xacmlPep.enforce(context.getSubjectValue(SUBJECT_ID_URI_STRING), ACTION_ID_VALUE_SET_DATASTREAM_STATE, ACTION_API_VALUE_APIM, pid, extractNamespace(pid), context);
	}
	
	public final void enforceSetDisseminatorState(Context context, String pid, String disseminatorId, String disseminatorNewState) 
	throws NotAuthorizedException { 
		context.setActionAttributes(null);
		MultiValueMap resourceAttributes = new MultiValueMap();
		try {
			//resourceAttributes.set(RESOURCE_ID_URI_STRING, pid);	
			//resourceAttributes.set(RESOURCE_NAMESPACE_URI_STRING, extractNamespace(pid));
			resourceAttributes.set(RESOURCE_DISSEMINATOR_PID_URI_STRING, disseminatorId);	
			resourceAttributes.set(RESOURCE_DISSEMINATOR_NAMESPACE_URI_STRING, extractNamespace(disseminatorId));			
			resourceAttributes.set(ACTION_DISSEMINATOR_NEW_STATE_URI_STRING, disseminatorNewState);	
		} catch (Exception e) {
			context.setResourceAttributes(null);		
			throw new AuthzOperationalException("enforceX could not complete", e);	
		}
		context.setResourceAttributes(resourceAttributes);	
		xacmlPep.enforce(context.getSubjectValue(SUBJECT_ID_URI_STRING), ACTION_ID_VALUE_SET_DISSEMINATOR_STATE, ACTION_API_VALUE_APIM, pid, extractNamespace(pid), context);
	}
	
	public void enforceDescribeRepository(Context context) 
	throws NotAuthorizedException {
		context.setActionAttributes(null);
		context.setResourceAttributes(null);
		xacmlPep.enforce(context.getSubjectValue(SUBJECT_ID_URI_STRING), ACTION_ID_VALUE_DESCRIBE_REPOSITORY, ACTION_API_VALUE_APIA, "", "", context);
	}

	public void enforceFindObjects(Context context) 
	throws NotAuthorizedException {
		context.setActionAttributes(null);
		context.setResourceAttributes(null);
		System.err.println("enforceFindObjects, subject (from context)=" + context.getSubjectValue(SUBJECT_ID_URI_STRING));
		xacmlPep.enforce(context.getSubjectValue(SUBJECT_ID_URI_STRING), ACTION_ID_VALUE_FIND_OBJECTS, ACTION_API_VALUE_APIA, "", "", context);
	}
	
	public void enforceRIFindObjects(Context context) 
	throws NotAuthorizedException {
		context.setActionAttributes(null);
		context.setResourceAttributes(null);
		xacmlPep.enforce(context.getSubjectValue(SUBJECT_ID_URI_STRING), ACTION_ID_VALUE_FIND_OBJECTS, ACTION_API_VALUE_APIA, "", "", context);
	}

	public void enforceGetDatastreamDissemination(Context context, String pid, String datastreamId, Date asOfDate) 
	throws NotAuthorizedException {
		String asOfDateAsString = ensureDate(asOfDate, context);
		context.setActionAttributes(null);
		MultiValueMap resourceAttributes = new MultiValueMap();
		try {
			//resourceAttributes.set(RESOURCE_ID_URI_STRING, pid);	
			//resourceAttributes.set(RESOURCE_NAMESPACE_URI_STRING, extractNamespace(pid));	
			resourceAttributes.set(RESOURCE_DATASTREAM_ID_URI_STRING, datastreamId);	
			resourceAttributes.set(RESOURCE_AS_OF_DATE_URI_STRING, asOfDateAsString);	
		} catch (Exception e) {
			context.setResourceAttributes(null);		
			throw new AuthzOperationalException("enforceX could not complete", e);	
		}
		context.setResourceAttributes(resourceAttributes);			
		xacmlPep.enforce(context.getSubjectValue(SUBJECT_ID_URI_STRING), ACTION_ID_VALUE_GET_DATASTREAM_DISSEMINATION, ACTION_API_VALUE_APIA, pid, extractNamespace(pid), context);
	}
	
	public void enforceGetDissemination(Context context, String pid, String bDefPID, String methodName, Date asOfDate) 
	throws NotAuthorizedException {
		String asOfDateAsString = ensureDate(asOfDate, context);
		context.setActionAttributes(null);
		MultiValueMap resourceAttributes = new MultiValueMap();
		try {
			//resourceAttributes.set(RESOURCE_ID_URI_STRING, pid);	
			//resourceAttributes.set(RESOURCE_NAMESPACE_URI_STRING, extractNamespace(pid));	
			resourceAttributes.set(RESOURCE_BDEF_PID_URI_STRING, bDefPID);	
			resourceAttributes.set(RESOURCE_BDEF_NAMESPACE_URI_STRING, extractNamespace(bDefPID));	
			resourceAttributes.set(RESOURCE_DISSEMINATOR_METHOD_URI_STRING, methodName);
			resourceAttributes.set(RESOURCE_AS_OF_DATE_URI_STRING, asOfDateAsString);	
		} catch (Exception e) {
			context.setResourceAttributes(null);		
			throw new AuthzOperationalException("enforceX could not complete", e);	
		}
		context.setResourceAttributes(resourceAttributes);			
		xacmlPep.enforce(context.getSubjectValue(SUBJECT_ID_URI_STRING), ACTION_ID_VALUE_GET_DISSEMINATION, ACTION_API_VALUE_APIA, pid, extractNamespace(pid), context);
	}

	public void enforceGetObjectHistory(Context context, String pid) 
	throws NotAuthorizedException {
		context.setActionAttributes(null);
		context.setResourceAttributes(null);
		/*
		MultiValueMap resourceAttributes = new MultiValueMap();
		try {
			resourceAttributes.set(RESOURCE_ID_URI_STRING, pid);	
			resourceAttributes.set(RESOURCE_NAMESPACE_URI_STRING, extractNamespace(pid));	
		} catch (Exception e) {
			context.setResourceAttributes(null);		
			throw new AuthzOperationalException("enforceX could not complete", e);	
		}
		context.setResourceAttributes(resourceAttributes);
		*/
		xacmlPep.enforce(context.getSubjectValue(SUBJECT_ID_URI_STRING), ACTION_ID_VALUE_GET_OBJECT_HISTORY, ACTION_API_VALUE_APIA, pid, extractNamespace(pid), context);
	}

	public void enforceGetObjectProfile(Context context, String pid, Date asOfDate) 
	throws NotAuthorizedException {
		String asOfDateAsString = ensureDate(asOfDate, context);
		context.setActionAttributes(null);
		MultiValueMap resourceAttributes = new MultiValueMap();
		try {
			//resourceAttributes.set(RESOURCE_ID_URI_STRING, pid);	
			//resourceAttributes.set(RESOURCE_NAMESPACE_URI_STRING, extractNamespace(pid));	
			resourceAttributes.set(RESOURCE_AS_OF_DATE_URI_STRING, asOfDateAsString);	
		} catch (Exception e) {
			context.setResourceAttributes(null);		
			throw new AuthzOperationalException("enforceX could not complete", e);	
		}
		context.setResourceAttributes(resourceAttributes);
		xacmlPep.enforce(context.getSubjectValue(SUBJECT_ID_URI_STRING), ACTION_ID_VALUE_GET_OBJECT_PROFILE, ACTION_API_VALUE_APIA, pid, extractNamespace(pid), context);
	}

	public void enforceListDatastreams(Context context, String pid, Date asOfDate) 
	throws NotAuthorizedException {
		String asOfDateAsString = ensureDate(asOfDate, context);
		context.setActionAttributes(null);
		MultiValueMap resourceAttributes = new MultiValueMap();
		try {
			//resourceAttributes.set(RESOURCE_ID_URI_STRING, pid);	
			//resourceAttributes.set(RESOURCE_NAMESPACE_URI_STRING, extractNamespace(pid));	
			resourceAttributes.set(RESOURCE_AS_OF_DATE_URI_STRING, asOfDateAsString);	
		} catch (Exception e) {
			context.setResourceAttributes(null);		
			throw new AuthzOperationalException("enforceX could not complete", e);	
		}
		context.setResourceAttributes(resourceAttributes);
		xacmlPep.enforce(context.getSubjectValue(SUBJECT_ID_URI_STRING), ACTION_ID_VALUE_LIST_DATASTREAMS, ACTION_API_VALUE_APIA, pid, extractNamespace(pid), context);
	}

	public void enforceListMethods(Context context, String pid, Date asOfDate) 
	throws NotAuthorizedException {
		String asOfDateAsString = ensureDate(asOfDate, context);
		context.setActionAttributes(null);
		MultiValueMap resourceAttributes = new MultiValueMap();
		try {
			//resourceAttributes.set(RESOURCE_ID_URI_STRING, pid);	
			//resourceAttributes.set(RESOURCE_NAMESPACE_URI_STRING, extractNamespace(pid));	
			resourceAttributes.set(RESOURCE_AS_OF_DATE_URI_STRING, asOfDateAsString);	
		} catch (Exception e) {
			context.setResourceAttributes(null);		
			throw new AuthzOperationalException("enforceX could not complete", e);	
		}
		context.setResourceAttributes(resourceAttributes);
		xacmlPep.enforce(context.getSubjectValue(SUBJECT_ID_URI_STRING), ACTION_ID_VALUE_LIST_METHODS, ACTION_API_VALUE_APIA, pid, extractNamespace(pid), context);
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

	
}

package fedora.server.security;

import java.util.Date;
import fedora.server.Context;
import fedora.server.errors.NotAuthorizedException;
import fedora.server.storage.types.Property;
import fedora.server.utilities.DateUtility;


public interface Authorization {
	
	//"info:fedora/fedora-system:def/fType"
	//"info:fedora/fedora-system:def/cModel"
	//"info:fedora/fedora-system:def/label"
	//"info:fedora/fedora-system:def/state"
	//"info:fedora/fedora-system:def/owner"
	//"info:fedora/fedora-system:def/cDate"
	//"info:fedora/fedora-system:def/mDate"


	/*
	public void enforceResourceIndexVisibility(Context context, String pid) 
	throws NotAuthorizedException;

	public void enforceSearchVisibility(Context context, String pid) 
	throws NotAuthorizedException;
	*/

	//subject
	public static final String SUBJECT_CATEGORY = "urn:oasis:names:tc:xacml:1.0:subject";
	public static final String SUBJECT_CATEGORY_ACCESS = "urn:oasis:names:tc:xacml:1.0:subject-category:access-subject";
	public static final String SUBJECT_ID_URI_STRING = "urn:oasis:names:tc:xacml:1.0:subject:subject-id";
                                     
	//action	
	public static final String ACTION_CATEGORY = "urn:oasis:names:tc:xacml:1.0:action";
	public static final String ACTION_CATEGORY_ACCESS = "urn:oasis:names:tc:xacml:1.0:action-category:access-action";
	public static final String ACTION_ID_URI_STRING = "urn:oasis:names:tc:xacml:1.0:action:action-id";
	public static final String ACTION_ID_VALUE_ADD_DATASTREAM = "addDatastream";	
	public static final String ACTION_ID_VALUE_ADD_DISSEMINATOR = "addDisseminator";	
	public static final String ACTION_ID_VALUE_EXPORT_OBJECT = "exportObject";	
	public static final String ACTION_ID_VALUE_GET_DATASTREAM = "getDatastream";	
	public static final String ACTION_ID_VALUE_GET_DATASTREAM_HISTORY = "getDatastreamHistory";	
	public static final String ACTION_ID_VALUE_GET_DATASTREAMS = "getDatastreams";	
	public static final String ACTION_ID_VALUE_GET_DISSEMINATOR = "getDisseminator";
	public static final String ACTION_ID_VALUE_GET_DISSEMINATORS = "getDisseminators";	
	public static final String ACTION_ID_VALUE_GET_DISSEMINATOR_HISTORY = "getDisseminatorHistory";	
	public static final String ACTION_ID_VALUE_GET_NEXT_PID = "getNextPid";
	public static final String ACTION_ID_VALUE_GET_OBJECT_PROPERTIES = "getObjectProperties";	
	public static final String ACTION_ID_VALUE_GET_OBJECT_XML = "getObjectXML";	
	public static final String ACTION_ID_VALUE_INGEST_OBJECT = "ingestObject";
	public static final String ACTION_ID_VALUE_MODIFY_DATASTREAM_BY_REFERENCE = "modifyDatastreamByReference";	
	public static final String ACTION_ID_VALUE_MODIFY_DATASTREAM_BY_VALUE = "modifyDatastreamByValue";
	public static final String ACTION_ID_VALUE_MODIFY_DISSEMINATOR = "modifyDisseminator";		
	public static final String ACTION_ID_VALUE_MODIFY_OBJECT = "modifyObject";
	public static final String ACTION_ID_VALUE_PURGE_OBJECT = "purgeObject";
	public static final String ACTION_ID_VALUE_PURGE_DATASTREAM = "purgeDatastream";
	public static final String ACTION_ID_VALUE_PURGE_DISSEMINATOR = "purgeDisseminator";	
	public static final String ACTION_ID_VALUE_SET_DATASTREAM_STATE = "setDatastreamState";	
	public static final String ACTION_ID_VALUE_SET_DISSEMINATOR_STATE = "setDisseminatorState";	
	public static final String ACTION_ID_VALUE_DESCRIBE_REPOSITORY = "describeRepository";	
	public static final String ACTION_ID_VALUE_FIND_OBJECTS = "findObjects";	
	public static final String ACTION_ID_VALUE_GET_DATASTREAM_DISSEMINATION = "getDatastreamDissemination";	
	public static final String ACTION_ID_VALUE_GET_DISSEMINATION = "getDissemination";	
	public static final String ACTION_ID_VALUE_GET_OBJECT_HISTORY = "getObjectHistory";	
	public static final String ACTION_ID_VALUE_GET_OBJECT_PROFILE = "getObjectProfile";	
	public static final String ACTION_ID_VALUE_LIST_DATASTREAMS = "listDatastreams";	
	public static final String ACTION_ID_VALUE_LIST_METHODS = "listMethods";		
	public static final String ACTION_ID_VALUE_LIST_OBJECT_IN_FIELD_SEARCH_RESULTS = "listObjectInFieldSearchResults";
	public static final String ACTION_ID_VALUE_LIST_OBJECT_IN_RESOURCE_INDEX_RESULTS = "listObjectInResourceIndexResults";
	
	public static final String ACTION_API_URI_STRING = "urn:oasis:names:tc:xacml:1.0:action:fedora-api";
	public static final String ACTION_API_VALUE_APIM = "apim";
	public static final String ACTION_API_VALUE_APIA = "apia";
	
	public static final String ACTION_CONTEXT_URI_STRING = "urn:oasis:names:tc:xacml:1.0:action:context";


	public static final String ACTION_NEW_STATE_URI_STRING = "urn:oasis:names:tc:xacml:1.0:action:fedora-newState";	
	public static final String ACTION_DATASTREAM_NEW_STATE_URI_STRING = "urn:oasis:names:tc:xacml:1.0:action:fedora-datastreamNewState";
	public static final String ACTION_DISSEMINATOR_NEW_STATE_URI_STRING = "urn:oasis:names:tc:xacml:1.0:action:fedora-disseminatorNewState";

	//resource
	public static final String RESOURCE_CATEGORY = "urn:oasis:names:tc:xacml:1.0:resource";
	public static final String RESOURCE_CATEGORY_ACCESS = "urn:oasis:names:tc:xacml:1.0:resource-category:access-resource";
	public static final String RESOURCE_ID_URI_STRING = "urn:oasis:names:tc:xacml:1.0:resource:resource-id";
	//public static final String RESOURCE_PID_URI_STRING = "urn:oasis:names:tc:xacml:1.0:resource:fedora-pid";
	public static final String RESOURCE_NAMESPACE_URI_STRING = "urn:oasis:names:tc:xacml:1.0:resource:fedora-namespace";
	public static final String RESOURCE_AS_OF_DATE_URI_STRING = "urn:oasis:names:tc:xacml:1.0:resource:fedora-asOfDate";
	
	public static final String RESOURCE_OBJECT_STATE_URI_STRING = "urn:oasis:names:tc:xacml:1.0:resource:fedora-object-state";
	public static final String RESOURCE_DATASTREAM_ID_URI_STRING = "urn:oasis:names:tc:xacml:1.0:resource:fedora-datastream-id";	
	//public static final String RESOURCE_DATASTREAM_NAMESPACE_URI_STRING = "urn:oasis:names:tc:xacml:1.0:resource:fedora-datastreamNamespace";	
	public static final String RESOURCE_DATASTREAM_STATE_URI_STRING = "urn:oasis:names:tc:xacml:1.0:resource:fedora-datastream-state";	
	public static final String RESOURCE_DATASTREAM_LOCATION_URI_STRING = "urn:oasis:names:tc:xacml:1.0:resource:fedora-datastreamLocation";
	public static final String RESOURCE_DATASTREAM_CONTROL_GROUP_URI_STRING = "urn:oasis:names:tc:xacml:1.0:resource:fedora-datastreamControlGroup";
	public static final String RESOURCE_BDEF_PID_URI_STRING = "urn:oasis:names:tc:xacml:1.0:resource:fedora-bdefPid";
	public static final String RESOURCE_BDEF_NAMESPACE_URI_STRING = "urn:oasis:names:tc:xacml:1.0:resource:fedora-bdefNamespace";
	public static final String RESOURCE_BMECH_PID_URI_STRING = "urn:oasis:names:tc:xacml:1.0:resource:fedora-bmechPid";
	public static final String RESOURCE_BMECH_NAMESPACE_URI_STRING = "urn:oasis:names:tc:xacml:1.0:resource:fedora-bmechNamespace";
	public static final String RESOURCE_DISSEMINATOR_PID_URI_STRING = "urn:oasis:names:tc:xacml:1.0:resource:fedora-disseminatorId";
	public static final String RESOURCE_DISSEMINATOR_NAMESPACE_URI_STRING = "urn:oasis:names:tc:xacml:1.0:resource:fedora-disseminatorNamespace";	
	public static final String RESOURCE_DISSEMINATOR_STATE_URI_STRING = "urn:oasis:names:tc:xacml:1.0:resource:fedora-disseminatorState";
	public static final String RESOURCE_DISSEMINATOR_METHOD_URI_STRING = "urn:oasis:names:tc:xacml:1.0:resource:fedora-disseminatorMethod";
	public static final String RESOURCE_DISSEMINATED_PID_URI_STRING = "urn:oasis:names:tc:xacml:1.0:resource:fedora-disseminatedPid";
	public static final String RESOURCE_DISSEMINATED_NAMESPACE_URI_STRING = "urn:oasis:names:tc:xacml:1.0:resource:fedora-disseminatedNamespace";
	public static final String RESOURCE_N_NEW_PIDS_URI_STRING = "urn:oasis:names:tc:xacml:1.0:resource:fedora-nNewPids";
	
	//environment
	public static final String ENVIRONMENT_CATEGORY = "urn:oasis:names:tc:xacml:1.0:environment";
	public static final String ENVIRONMENT_CATEGORY_ACCESS = "urn:oasis:names:tc:xacml:1.0:environment-category:access-environment";
	public static final String ENVIRONMENT_CURRENT_DATETIME_URI_STRING = "urn:oasis:names:tc:xacml:1.0:environment:current-dateTime";
	public static final String ENVIRONMENT_CURRENT_DATE_URI_STRING = "urn:oasis:names:tc:xacml:1.0:environment:current-date";
	public static final String ENVIRONMENT_CURRENT_TIME_URI_STRING = "urn:oasis:names:tc:xacml:1.0:environment:current-time";	
	public static final String ENVIRONMENT_REQUEST_PROTOCOL_URI_STRING = "urn:oasis:names:tc:xacml:1.0:environment:request-protocol";
	public static final String ENVIRONMENT_REQUEST_SCHEME_URI_STRING = "urn:oasis:names:tc:xacml:1.0:environment:request-scheme";
	public static final String ENVIRONMENT_REQUEST_SECURITY_URI_STRING = "urn:oasis:names:tc:xacml:1.0:environment:request-security";
	public static final String ENVIRONMENT_REQUEST_AUTHTYPE_URI_STRING = "urn:oasis:names:tc:xacml:1.0:environment:request-authtype";
	public static final String ENVIRONMENT_REQUEST_METHOD_URI_STRING = "urn:oasis:names:tc:xacml:1.0:environment:request-method";	
	public static final String ENVIRONMENT_REQUEST_SESSION_ENCODING_URI_STRING = "urn:oasis:names:tc:xacml:1.0:environment:request-session-encoding";	
	public static final String ENVIRONMENT_REQUEST_SESSION_STATUS_URI_STRING = "urn:oasis:names:tc:xacml:1.0:environment:request-session-status";		
	public static final String ENVIRONMENT_REQUEST_CONTENT_LENGTH_URI_STRING = "urn:oasis:names:tc:xacml:1.0:environment:request-content-length";
	public static final String ENVIRONMENT_REQUEST_CONTENT_TYPE_URI_STRING = "urn:oasis:names:tc:xacml:1.0:environment:request-content-type";
	public static final String ENVIRONMENT_REQUEST_SOAP_OR_REST_URI_STRING = "urn:oasis:names:tc:xacml:1.0:environment:request-soap-or-rest";
	public static final String ENVIRONMENT_CLIENT_FQDN_URI_STRING = "urn:oasis:names:tc:xacml:1.0:environment:client-fqdn";
	public static final String ENVIRONMENT_CLIENT_IP_URI_STRING = "urn:oasis:names:tc:xacml:1.0:environment:client-ip";	
	public static final String ENVIRONMENT_SERVER_FQDN_URI_STRING = "urn:oasis:names:tc:xacml:1.0:environment:server-fqdn";
	public static final String ENVIRONMENT_SERVER_IP_URI_STRING = "urn:oasis:names:tc:xacml:1.0:environment:server-ip";	
	public static final String ENVIRONMENT_SERVER_PORT_URI_STRING = "urn:oasis:names:tc:xacml:1.0:environment:server-port";	
	
	public static final String ENVIRONMENT_REQUEST_SOAP_OR_REST_SOAP = "soap";
	public static final String ENVIRONMENT_REQUEST_SOAP_OR_REST_REST = "rest";

  	


	//APIM 

	public void enforceAddDatastream(Context context, String pid, String dsId, String dsLocation, String controlGroup, String dsState) 
	throws NotAuthorizedException;

	public void enforceAddDisseminator(Context context, String pid) 
	throws NotAuthorizedException;
	
	public void enforceExportObject(Context context, String pid) 
	throws NotAuthorizedException;

	public void enforceGetDatastream(Context context, String pid, String datastreamId) 
	throws NotAuthorizedException;

	public void enforceGetDatastreamHistory(Context context, String pid, String datastreamId) 
	throws NotAuthorizedException;

	public void enforceGetDatastreams(Context context, String pid, Date asOfDate, String state) 
	throws NotAuthorizedException;
	
	public void enforceGetDisseminator(Context context, String pid, String disseminatorId, Date asOfDateTime) 
	throws NotAuthorizedException;
	
	public void enforceGetDisseminators(Context context, String pid, Date asOfDate, String disseminatorState) 
	throws NotAuthorizedException;

	public void enforceGetDisseminatorHistory(Context context, String pid, String disseminatorPid) 
	throws NotAuthorizedException;
	
	public void enforceGetNextPid(Context context, String namespace, int nNewPids) 
	throws NotAuthorizedException; 
	
	public void enforceGetObjectProperties(Context context, String pid) 
	throws NotAuthorizedException;
	
	public void enforceGetObjectXML(Context context, String pid) 
	throws NotAuthorizedException;

	public void enforceIngestObject(Context context, String pid) 
	throws NotAuthorizedException;
	
	public void enforceListObjectInFieldSearchResults(Context context, String pid) 
	throws NotAuthorizedException;
	
	public void enforceListObjectInResourceIndexResults(Context context, String pid) 
	throws NotAuthorizedException;

	public void enforceModifyDatastreamByReference(Context context, String pid, String datastreamId, String datastreamLocation, String datastreamState) 
	throws NotAuthorizedException;
	
	public void enforceModifyDatastreamByValue(Context context, String pid, String datastreamId, String datastreamState) 
	throws NotAuthorizedException;
	
	public void enforceModifyDisseminator(Context context, String pid, String disseminatorId, String mechanismPid, String disseminatorState) 
	throws NotAuthorizedException;
	
	public void enforceModifyObject(Context context, String pid, String objectState) 
	throws NotAuthorizedException;
	
	public void enforcePurgeDatastream(Context context, String pid, String datastreamId) 
	throws NotAuthorizedException;

	public void enforcePurgeDisseminator(Context context, String pid, String disseminatorId) 
	throws NotAuthorizedException;

	public void enforcePurgeObject(Context context, String pid) 
	throws NotAuthorizedException;

	public void enforceSetDatastreamState(Context context, String pid, String datastreamId, String datastreamNewState) 
	throws NotAuthorizedException;

	public void enforceSetDisseminatorState(Context context, String pid, String disseminatorId, String disseminatorNewState) 
	throws NotAuthorizedException;

	//APIA

	public void enforceDescribeRepository(Context context) 
	throws NotAuthorizedException;

	public void enforceFindObjects(Context context) 
	throws NotAuthorizedException;
	
	public void enforceRIFindObjects(Context context) 
	throws NotAuthorizedException;

	public void enforceGetDatastreamDissemination(Context context, String pid, String datastreamId, Date asOfDate) 
	throws NotAuthorizedException;
	
	public void enforceGetDissemination(Context context, String pid, String bDefPID, String methodName, Date asOfDate) 
	throws NotAuthorizedException;

	public void enforceGetObjectHistory(Context context, String pid) 
	throws NotAuthorizedException;

	public void enforceGetObjectProfile(Context context, String pid, Date asOfDate) 
	throws NotAuthorizedException;

	public void enforceListDatastreams(Context context, String pid, Date asOfDate) 
	throws NotAuthorizedException;

	public void enforceListMethods(Context context, String pid, Date ofAsDate) 
	throws NotAuthorizedException;

}

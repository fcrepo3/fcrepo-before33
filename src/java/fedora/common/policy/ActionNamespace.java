package fedora.common.policy;

import com.sun.xacml.attr.AnyURIAttribute;
import com.sun.xacml.attr.IntegerAttribute;
import com.sun.xacml.attr.StringAttribute;

import fedora.common.Constants;

public class ActionNamespace extends XacmlNamespace { 
	
	// Properties
	public final XacmlName ID;	
	public final XacmlName API;
	public final XacmlName CONTEXT_ID;
	public final XacmlName OBJECT_NEW_STATE;
	public final XacmlName DATASTREAM_CONTROL_GROUP;	
	public final XacmlName DISSEMINATOR_NEW_STATE;
	public final XacmlName BMECH_NEW_PID;
	public final XacmlName BMECH_NEW_NAMESPACE;
	public final XacmlName N_NEW_PIDS;
	public final XacmlName BDEF_PID;
	public final XacmlName BDEF_NAMESPACE;
	public final XacmlName DISSEMINATOR_METHOD;
	public final XacmlName USER_REPRESENTED;

    // Values of API
	public final XacmlName APIM;
	public final XacmlName APIA;
	
    // Values of urn:oasis:names:tc:xacml:1.0:action:action-id    
	public final XacmlName ADD_DATASTREAM;	
	public final XacmlName ADD_DISSEMINATOR;	
	public final XacmlName ADMIN_PING;		
	public final XacmlName EXPORT_OBJECT;	
	public final XacmlName GET_DATASTREAM;	
	public final XacmlName GET_DATASTREAM_HISTORY;	
	public final XacmlName GET_DATASTREAMS;	
	public final XacmlName GET_DISSEMINATOR;
	public final XacmlName GET_DISSEMINATORS;	
	public final XacmlName GET_DISSEMINATOR_HISTORY;	
	public final XacmlName GET_NEXT_PID;
	public final XacmlName GET_OBJECT_PROPERTIES;	
	public final XacmlName GET_OBJECT_XML;	
	public final XacmlName INGEST_OBJECT;
	public final XacmlName MODIFY_DATASTREAM_BY_REFERENCE;	
	public final XacmlName MODIFY_DATASTREAM_BY_VALUE;
	public final XacmlName MODIFY_DISSEMINATOR;		
	public final XacmlName MODIFY_OBJECT;
	public final XacmlName PURGE_OBJECT;
	public final XacmlName PURGE_DATASTREAM;
	public final XacmlName PURGE_DISSEMINATOR;	
	public final XacmlName SET_DATASTREAM_STATE;	
	public final XacmlName SET_DISSEMINATOR_STATE;	
	public final XacmlName DESCRIBE_REPOSITORY;	
	public final XacmlName FIND_OBJECTS;
	public final XacmlName RI_FIND_OBJECTS;	
	public final XacmlName GET_DATASTREAM_DISSEMINATION;	
	public final XacmlName GET_DISSEMINATION;	
	public final XacmlName GET_OBJECT_HISTORY;	
	public final XacmlName GET_OBJECT_PROFILE;	
	public final XacmlName LIST_DATASTREAMS;	
	public final XacmlName LIST_METHODS;		
	public final XacmlName LIST_OBJECT_IN_FIELD_SEARCH_RESULTS;
	public final XacmlName LIST_OBJECT_IN_RESOURCE_INDEX_RESULTS;
	public final XacmlName SURROGATE_PING;
	public final XacmlName SERVER_SHUTDOWN;
	public final XacmlName SERVER_STATUS;
	public final XacmlName OAI;	
	public final XacmlName FORMAT_URI;	
	public final XacmlName EXPORT_CONTEXT;	
	public final XacmlName ENCODING;	
	public final XacmlName DATASTREAM_NEW_MIME_TYPE;	
	public final XacmlName DATASTREAM_NEW_FORMAT_URI;	
	public final XacmlName DATASTREAM_NEW_LOCATION;	
	public final XacmlName DATASTREAM_NEW_STATE;	
	

    private ActionNamespace(XacmlNamespace parent, String localName) {
    	super(parent, localName);
        //this.uri = Constants.FEDORA_SYSTEM_DEF_URI + "action:";

        // Properties
    	ID = addName(new XacmlName(this, "id", StringAttribute.identifier));
    	API = addName(new XacmlName(this, "api", StringAttribute.identifier));
    	CONTEXT_ID = addName(new XacmlName(this, "context-id", StringAttribute.identifier));
    	OBJECT_NEW_STATE = addName(new XacmlName(this, "objectNewState", StringAttribute.identifier));
    	DATASTREAM_CONTROL_GROUP = addName(new XacmlName(this, "datastreamControlGroup", StringAttribute.identifier));
    	DATASTREAM_NEW_STATE = addName(new XacmlName(this, "datastreamNewState", StringAttribute.identifier));
    	DATASTREAM_NEW_LOCATION = addName(new XacmlName(this, "datastreamNewLocation", AnyURIAttribute.identifier));
       	DISSEMINATOR_NEW_STATE = addName(new XacmlName(this, "disseminatorNewState", StringAttribute.identifier));    	
    	BMECH_NEW_PID = addName(new XacmlName(this, "bmechNewPid", StringAttribute.identifier));
    	BMECH_NEW_NAMESPACE = addName(new XacmlName(this, "bmechNewNamespace", StringAttribute.identifier));
    	N_NEW_PIDS = addName(new XacmlName(this, "nNewPids", IntegerAttribute.identifier));
    	BDEF_PID = addName(new XacmlName(this, "bdefPid", StringAttribute.identifier));
    	BDEF_NAMESPACE = addName(new XacmlName(this, "bdefNamespace", StringAttribute.identifier));
    	DISSEMINATOR_METHOD = addName(new XacmlName(this, "disseminatorMethod", StringAttribute.identifier));    	
    	USER_REPRESENTED = addName(new XacmlName(this, "subjectRepresented", StringAttribute.identifier));    	
    	FORMAT_URI = addName(new XacmlName(this, "formatUri", AnyURIAttribute.identifier));
    	EXPORT_CONTEXT = addName(new XacmlName(this, "exportContext", StringAttribute.identifier));    	
    	ENCODING = addName(new XacmlName(this, "encoding", StringAttribute.identifier));    	
    	DATASTREAM_NEW_MIME_TYPE = addName(new XacmlName(this, "datastreamNewMimeType", StringAttribute.identifier));    	
    	DATASTREAM_NEW_FORMAT_URI = addName(new XacmlName(this, "datastreamNewFormatUri", AnyURIAttribute.identifier));    	
    	    	
    	// Values of CONTEXT_ID are sequential numerals, hence not enumerated here.
    	
        // Values of API
    	APIM               = addName(new XacmlName(this, "apim"));
    	APIA               = addName(new XacmlName(this, "apia"));

        // Values of urn:oasis:names:tc:xacml:1.0:action:action-id    
    	// derived from respective Java methods in Access.java or Management.java

    	ADD_DATASTREAM               = addName(new XacmlName(this, "addDatastream"));	
    	ADD_DISSEMINATOR               = addName(new XacmlName(this, "addDisseminator"));	
    	ADMIN_PING               = addName(new XacmlName(this, "adminPing"));    	
    	EXPORT_OBJECT               = addName(new XacmlName(this, "exportObject"));	
    	GET_DATASTREAM               = addName(new XacmlName(this, "getDatastream"));	
    	GET_DATASTREAM_HISTORY               = addName(new XacmlName(this, "getDatastreamHistory"));	
    	GET_DATASTREAMS               = addName(new XacmlName(this, "getDatastreams"));	
    	GET_DISSEMINATOR               = addName(new XacmlName(this, "getDisseminator"));
    	GET_DISSEMINATORS               = addName(new XacmlName(this, "getDisseminators"));	
    	GET_DISSEMINATOR_HISTORY               = addName(new XacmlName(this, "getDisseminatorHistory"));	
    	GET_NEXT_PID               = addName(new XacmlName(this, "getNextPid"));
    	GET_OBJECT_PROPERTIES               = addName(new XacmlName(this, "getObjectProperties"));	
    	GET_OBJECT_XML               = addName(new XacmlName(this, "getObjectXML"));	
    	INGEST_OBJECT               = addName(new XacmlName(this, "ingestObject"));
    	MODIFY_DATASTREAM_BY_REFERENCE               = addName(new XacmlName(this, "modifyDatastreamByReference"));	
    	MODIFY_DATASTREAM_BY_VALUE               = addName(new XacmlName(this, "modifyDatastreamByValue"));
    	MODIFY_DISSEMINATOR               = addName(new XacmlName(this, "modifyDisseminator"));		
    	MODIFY_OBJECT               = addName(new XacmlName(this, "modifyObject"));
    	PURGE_OBJECT               = addName(new XacmlName(this, "purgeObject"));
    	PURGE_DATASTREAM               = addName(new XacmlName(this, "purgeDatastream"));
    	PURGE_DISSEMINATOR               = addName(new XacmlName(this, "purgeDisseminator"));	
    	SET_DATASTREAM_STATE               = addName(new XacmlName(this, "setDatastreamState"));	
    	SET_DISSEMINATOR_STATE               = addName(new XacmlName(this, "setDisseminatorState"));	
    	DESCRIBE_REPOSITORY               = addName(new XacmlName(this, "describeRepository"));	
    	FIND_OBJECTS               = addName(new XacmlName(this, "findObjects"));	
    	RI_FIND_OBJECTS               = addName(new XacmlName(this, "riFindObjects"));	
    	GET_DATASTREAM_DISSEMINATION               = addName(new XacmlName(this, "getDatastreamDissemination"));	
    	GET_DISSEMINATION               = addName(new XacmlName(this, "getDissemination"));	
    	GET_OBJECT_HISTORY               = addName(new XacmlName(this, "getObjectHistory"));	
    	GET_OBJECT_PROFILE               = addName(new XacmlName(this, "getObjectProfile"));	
    	LIST_DATASTREAMS               = addName(new XacmlName(this, "listDatastreams"));	
    	LIST_METHODS               = addName(new XacmlName(this, "listMethods"));		
    	LIST_OBJECT_IN_FIELD_SEARCH_RESULTS               = addName(new XacmlName(this, "listObjectInFieldSearchResults"));
    	LIST_OBJECT_IN_RESOURCE_INDEX_RESULTS               = addName(new XacmlName(this, "listObjectInResourceIndexResults"));
    	SURROGATE_PING = addName(new XacmlName(this, "actAsSurrogateFor"));
    	SERVER_SHUTDOWN = addName(new XacmlName(this, "serverShutdown")); 
    	SERVER_STATUS = addName(new XacmlName(this, "serverStatus"));    
    	OAI = addName(new XacmlName(this, "oai"));     	
    }

	public static ActionNamespace onlyInstance = new ActionNamespace(Release2_1Namespace.getInstance(), "action");
	
	public static final ActionNamespace getInstance() {
		return onlyInstance;
	}

}

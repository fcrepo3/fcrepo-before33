package fedora.common.policy;

public class ActionNamespace extends XacmlNamespace { 
	
	// Properties
	public final XacmlName ID;	
	public final XacmlName API;
	public final XacmlName CONTEXT_ID;
	public final XacmlName OBJECT_NEW_STATE;
	public final XacmlName DATASTREAM_CONTROL_GROUP;	
	public final XacmlName DATASTREAM_NEW_STATE;
	public final XacmlName DATASTREAM_NEW_LOCATION;
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

    private ActionNamespace(XacmlNamespace parent, String localName) {
    	super(parent, localName);
        //this.uri = Constants.FEDORA_SYSTEM_DEF_URI + "action:";

        // Properties
    	ID = addName(new XacmlName(this, "id"));
    	API = addName(new XacmlName(this, "api"));
    	CONTEXT_ID = addName(new XacmlName(this, "context-id"));
    	OBJECT_NEW_STATE = addName(new XacmlName(this, "objectNewState"));
    	DATASTREAM_CONTROL_GROUP = addName(new XacmlName(this, "datastreamControlGroup"));
    	DATASTREAM_NEW_STATE = addName(new XacmlName(this, "datastreamNewState"));
    	DATASTREAM_NEW_LOCATION = addName(new XacmlName(this, "datastreamNewLocation"));
       	DISSEMINATOR_NEW_STATE = addName(new XacmlName(this, "disseminatorNewState"));    	
    	BMECH_NEW_PID = addName(new XacmlName(this, "bmechNewPid"));
    	BMECH_NEW_NAMESPACE = addName(new XacmlName(this, "bmechNewNamespace"));
    	N_NEW_PIDS = addName(new XacmlName(this, "nNewPids"));
    	BDEF_PID = addName(new XacmlName(this, "bdefPid"));
    	BDEF_NAMESPACE = addName(new XacmlName(this, "bdefNamespace"));
    	DISSEMINATOR_METHOD = addName(new XacmlName(this, "disseminatorMethod"));    	
    	USER_REPRESENTED = addName(new XacmlName(this, "subjectRepresented"));    	
    	
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

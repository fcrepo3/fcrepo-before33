package fedora.server.resourceIndex;

import fedora.server.errors.ServerException;
import fedora.server.errors.ResourceIndexException;
import fedora.server.storage.types.*;

/**
 * ResourceIndex is the interface to the Fedora Resource Index. The Fedora Resource
 * Index contains and mediates access to information about Fedora objects that other 
 * services may require.
 * 
 * @author Edwin Shin
 */
public interface ResourceIndex {
    public static final int INDEX_LEVEL_OBJECT_FIELDS = 1;
    public static final int INDEX_LEVEL_DISSEMINATIONS = 2;
    public static final int INDEX_LEVEL_DEPENDENCIES = 3;
    
	public static final String RDF_TYPE_URI = "http://www.w3.org/1999/02/22-rdf-syntax-ns#type";
	public static final String BDEF_RDF_TYPE_URI = "http://www.fedora.info/definitions/ontology#bdef";
	public static final String BMECH_RDF_TYPE_URI = "http://www.fedora.info/definitions/ontology#bmech";
	public static final String DATA_OBJECT_RDF_TYPE_URI = "http://www.fedora.info/definitions/ontology#dataobject";
	
	public static final String HAS_REPRESENTATION_URI = "http://www.fedora.info/definitions/ontology#hasRepresentation"; // ds, diss, bdefpid/methodname
	
	public static final String OWNER_ID_URI = "http://www.fedora.info/definitions/ontology#ownerID";
	public static final String CONTENT_MODEL_ID_URI = "http://www.fedora.info/definitions/ontology#contentModelID";
	public static final String LABEL_URI = "http://www.fedora.info/definitions/ontology#label";
	
	public static final String IMPLEMENTS_BDEF_URI = "http://www.fedora.info/definitions/ontology#implementsBDef";
	public static final String USES_BMECH_URI = "http://www.fedora.info/definitions/ontology#usesBMech";
	public static final String DEFINES_METHOD_URI = "http://www.fedora.info/definitions/ontology#definesMethod";
	
	public static final String DISSEMINATION_DIRECT_URI = "http://www.fedora.info/definitions/ontology#direct";
	public static final String DATE_CREATED_URI = "http://www.fedora.info/definitions/ontology#dateCreated";
	public static final String DATE_LAST_MODIFIED_URI = "http://www.fedora.info/definitions/ontology#dateLastModified";
	public static final String DISSEMINATION_MEDIA_TYPE_URI = "http://www.fedora.info/definitions/ontology#media-type";
	public static final String STATE_URI = "http://www.fedora.info/definitions/ontology#state";
	public static final String STATE_ACTIVE_URI = "http://www.fedora.info/definitions/ontology#active";
	public static final String STATE_INACTIVE_URI = "http://www.fedora.info/definitions/ontology#inactive";
	public static final String DISSEMINATION_TYPE_URI = "http://www.fedora.info/definitions/ontology#dissType";
	public static final String DISSEMINATION_VOLATILE_URI = "http://www.fedora.info/definitions/ontology#volatile";
	
	public static final String IS_MEMBER_OF = "http://www.fedora.info/definitions/ontology#isMemberOf";
	
	public static final String OAI_ITEM_ID = "http://www.openarchives.org/OAI/2.0/itemID";
	
	
	public int getIndexLevel();
	
	public RIResultIterator executeQuery(RIQuery query) throws ResourceIndexException ;
	
	public void addDigitalObject(DigitalObject digitalObject) throws ResourceIndexException;
	
	public void addDatastream(DigitalObject digitalObject, String datastreamID) throws ResourceIndexException;
	
	public void addDisseminator(DigitalObject digitalObject, String disseminatorID) throws ResourceIndexException;
	
	public void modifyDigitalObject(DigitalObject digitalObject) throws ResourceIndexException;
	
	/*
	 * TODO not clear if modify component requests should take the form of
	 *     modifyX(DigitalObject do, String componentID)
	 * 
	 * TODO should delete component requests just be
	 *     delete(String doPID, String componentID) ?
	 */
	
	public void modifyDatastream(Datastream ds) ;
	
	public void modifyDisseminator(Disseminator diss) ;
	
	public void deleteDigitalObject(String pid) ;
	
	public void deleteDatastream(Datastream ds) ;
	
	public void deleteDisseminator(Disseminator diss) ;
}




























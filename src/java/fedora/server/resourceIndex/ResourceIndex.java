package fedora.server.resourceIndex;

import org.trippi.RDFFormat;
import org.trippi.TriplestoreReader;

import fedora.server.errors.ResourceIndexException;
import fedora.server.storage.types.*;

import java.io.OutputStream;

/**
 * ResourceIndex is the interface to the Fedora Resource Index. The Fedora Resource
 * Index contains and mediates access to information about Fedora objects that other 
 * services may require.
 * 
 * @author Edwin Shin
 */
public interface ResourceIndex extends TriplestoreReader {
    // Index Levels
    public static final int INDEX_LEVEL_OFF = 0;
    public static final int INDEX_LEVEL_OBJECT_FIELDS = 1;
    public static final int INDEX_LEVEL_DISSEMINATIONS = 2;
    public static final int INDEX_LEVEL_DEPENDENCIES = 3;
    
    // RDF Namespaces
    public static final String NS_DC        = "http://purl.org/dc/elements/1.1/";
    public static final String NS_FEDORA    = "http://www.fedora.info/definitions/ontology#";
    public static final String NS_RDF       = "http://www.w3.org/1999/02/22-rdf-syntax-ns#";
    
	public static final String RDF_TYPE                = NS_RDF + "type";
	
	public static final String FEDORA_BDEF             = NS_FEDORA + "bdef";
    public static final String FEDORA_BMECH            = NS_FEDORA + "bmech";
	public static final String FEDORA_CMODEL           = NS_FEDORA + "contentModelID";
    public static final String FEDORA_DATAOBJECT       = NS_FEDORA + "dataobject";
    public static final String FEDORA_DATE_CREATED     = NS_FEDORA + "dateCreated";
    public static final String FEDORA_DATE_MODIFIED    = NS_FEDORA + "dateLastModified";
    public static final String FEDORA_DEFINES_METHOD   = NS_FEDORA + "definesMethod";
    public static final String FEDORA_DIRECT           = NS_FEDORA + "direct";
    public static final String FEDORA_DISS_TYPE        = NS_FEDORA + "dissType";
    public static final String FEDORA_IMPLEMENTS       = NS_FEDORA + "implementsBDef";
	public static final String FEDORA_LABEL            = NS_FEDORA + "label";
    public static final String FEDORA_MEDIATYPE        = NS_FEDORA + "media-type";
    public static final String FEDORA_OWNER_ID         = NS_FEDORA + "ownerID";
    public static final String FEDORA_REPRESENTATION   = NS_FEDORA + "hasRepresentation";
    public static final String FEDORA_STATE            = NS_FEDORA + "state";
    public static final String FEDORA_STATE_ACTIVE     = NS_FEDORA + "active";
    public static final String FEDORA_STATE_INACTIVE   = NS_FEDORA + "inactive";
    public static final String FEDORA_USES_BMECH       = NS_FEDORA + "usesBMech";
    public static final String FEDORA_VOLATILE         = NS_FEDORA + "volatile";
	
	public int getIndexLevel();
	
	public void addDigitalObject(DigitalObject digitalObject) throws ResourceIndexException;
	
	public void addDatastream(DigitalObject digitalObject, String datastreamID) throws ResourceIndexException;
	
	public void addDisseminator(DigitalObject digitalObject, String disseminatorID) throws ResourceIndexException;
	
	public void modifyDigitalObject(DigitalObject digitalObject) throws ResourceIndexException;
	
	public void modifyDatastream(DigitalObject digitalObject, String datastreamID) throws ResourceIndexException;
	
	public void modifyDisseminator(DigitalObject digitalObject, String disseminatorID) throws ResourceIndexException;
	
	public void deleteDigitalObject(DigitalObject digitalObject) throws ResourceIndexException;
	
	public void deleteDatastream(DigitalObject digitalObject, String datastreamID) throws ResourceIndexException;
	
	public void deleteDisseminator(DigitalObject digitalObject, String disseminatorID) throws ResourceIndexException;

	public void export(OutputStream out, RDFFormat format) throws ResourceIndexException;
}




























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
    public static final int INDEX_LEVEL_OFF             = 0;
    public static final int INDEX_LEVEL_ON              = 1;
    public static final int INDEX_LEVEL_PERMUTATIONS    = 2;
    
    // RDF Namespaces
    public static final String NS_DC            = "http://purl.org/dc/elements/1.1/";
    public static final String NS_FEDORA        = "info:fedora/";
    public static final String NS_FEDORA_ONT    = NS_FEDORA + "fedora-system:def/ontology#";
    public static final String NS_RDF           = "http://www.w3.org/1999/02/22-rdf-syntax-ns#";
    
	public static final String RDF_TYPE                = NS_RDF + "type";
	
	public static final String FEDORA_BDEF             = NS_FEDORA_ONT + "bdef";
    public static final String FEDORA_BMECH            = NS_FEDORA_ONT + "bmech";
	public static final String FEDORA_CMODEL           = NS_FEDORA_ONT + "contentModelID";
    public static final String FEDORA_DATAOBJECT       = NS_FEDORA_ONT + "dataobject";
    public static final String FEDORA_DATE_CREATED     = NS_FEDORA_ONT + "dateCreated";
    public static final String FEDORA_DATE_MODIFIED    = NS_FEDORA_ONT + "dateLastModified";
    public static final String FEDORA_DEFINES_METHOD   = NS_FEDORA_ONT + "definesMethod";
    public static final String FEDORA_DIRECT           = NS_FEDORA_ONT + "direct";
    public static final String FEDORA_DISS_TYPE        = NS_FEDORA_ONT + "dissType";
    public static final String FEDORA_DS_ALT_ID        = NS_FEDORA_ONT + "dsAltID";
    public static final String FEDORA_IMPLEMENTS       = NS_FEDORA_ONT + "implementsBDef";
	public static final String FEDORA_LABEL            = NS_FEDORA_ONT + "label";
    public static final String FEDORA_MEDIATYPE        = NS_FEDORA_ONT + "media-type";
    public static final String FEDORA_OWNER_ID         = NS_FEDORA_ONT + "ownerID";
    public static final String FEDORA_REPRESENTATION   = NS_FEDORA_ONT + "hasRepresentation";
    public static final String FEDORA_STATE            = NS_FEDORA_ONT + "state";
    public static final String FEDORA_STATE_ACTIVE     = NS_FEDORA_ONT + "active";
    public static final String FEDORA_STATE_INACTIVE   = NS_FEDORA_ONT + "inactive";
    public static final String FEDORA_USES_BMECH       = NS_FEDORA_ONT + "usesBMech";
    public static final String FEDORA_VOLATILE         = NS_FEDORA_ONT + "volatile";
	
	public int getIndexLevel();
	
	public void addDigitalObject(DigitalObject digitalObject) throws ResourceIndexException;
	
	public void modifyDigitalObject(DigitalObject digitalObject) throws ResourceIndexException;
	
	public void deleteDigitalObject(DigitalObject digitalObject) throws ResourceIndexException;
	
	public void export(OutputStream out, RDFFormat format) throws ResourceIndexException;
    
    /*
	public void addDatastream(DigitalObject digitalObject, String datastreamID) throws ResourceIndexException;
	public void addDisseminator(DigitalObject digitalObject, String disseminatorID) throws ResourceIndexException;
	public void modifyDatastream(DigitalObject digitalObject, String datastreamID) throws ResourceIndexException;
	public void modifyDisseminator(DigitalObject digitalObject, String disseminatorID) throws ResourceIndexException;
	public void deleteDatastream(DigitalObject digitalObject, String datastreamID) throws ResourceIndexException;
	public void deleteDisseminator(DigitalObject digitalObject, String disseminatorID) throws ResourceIndexException;
	*/
}




























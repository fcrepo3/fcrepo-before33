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
    /**
     * At this level, the ResourceIndex will not index anything.
     */
    public static final int INDEX_LEVEL_OFF             = 0;
    
    /**
     * At this level the ResourceIndex will index:
     *  object properties
     *  datastreams
     *  disseminators
     */
    public static final int INDEX_LEVEL_ON              = 1;
    
    /**
     * Equivalent to INDEX_LEVEL_ON plus the indexing of the various permutations
     * of a representation (i.e. dissemination) given by a fixed parameter list.
     * 
     * For example, consider the method, getImage, which requires two parameters,
     * border and format. The range of border's values is on or off; and the 
     * range of format's values is jpg or png.
     * 
     * There are four possible permutations:
     *  1) getImage?border=on&format=jpg
     *  2) getImage?border=on&format=png
     *  3) getImage?border=off&format=jpg
     *  4) getImage?border=off&format=png
     * 
     * At INDEX_LEVEL_ON, only the method name, getImage, would be indexed, and
     * none of the permutations.
     */
    public static final int INDEX_LEVEL_PERMUTATIONS    = 2;
    
    // RDF Namespaces
    public static final String NS_DC            = "http://purl.org/dc/elements/1.1/";
    public static final String NS_FEDORA        = "info:fedora/";
    public static final String NS_FEDORA_MODEL  = NS_FEDORA + "fedora-system:def/model#";
    public static final String NS_FEDORA_VIEW   = NS_FEDORA + "fedora-system:def/view#";
    public static final String NS_RDF           = "http://www.w3.org/1999/02/22-rdf-syntax-ns#";
    public static final String NS_XSD           = "http://www.w3.org/2001/XMLSchema#";
    
	public static final String RDF_TYPE                = NS_RDF + "type";
    public static final String XSD_DATETIME            = NS_XSD + "dateTime";
	
    //public static final String MODEL_ALT_ID            = NS_FEDORA_MODEL + "alternateIdentifier";
	public static final String MODEL_CMODEL            = NS_FEDORA_MODEL + "contentModel";
    public static final String MODEL_DATE_CREATED      = NS_FEDORA_MODEL + "createdDate";
    public static final String MODEL_DEFINES_METHOD    = NS_FEDORA_MODEL + "definesMethod";
    public static final String MODEL_IMPLEMENTS        = NS_FEDORA_MODEL + "implementsBDef";
	public static final String MODEL_LABEL             = NS_FEDORA_MODEL + "label";
    public static final String MODEL_OWNER             = NS_FEDORA_MODEL + "owner";
    public static final String MODEL_STATE             = NS_FEDORA_MODEL + "state";
    public static final String MODEL_USES_BMECH        = NS_FEDORA_MODEL + "usesBMech";
    
    public static final String MODEL_ACTIVE            = NS_FEDORA_MODEL + "Active";
    public static final String MODEL_DELETED           = NS_FEDORA_MODEL + "Deleted";
    public static final String MODEL_INACTIVE          = NS_FEDORA_MODEL + "Inactive";
    public static final String MODEL_BDEF              = NS_FEDORA_MODEL + "BDefObject";
    public static final String MODEL_BMECH             = NS_FEDORA_MODEL + "BMechObject";
    public static final String MODEL_DATAOBJECT        = NS_FEDORA_MODEL + "DataObject";
    
    public static final String VIEW_DATE_MODIFIED      = NS_FEDORA_VIEW + "lastModifiedDate";
    //public static final String VIEW_DEPENDS             = NS_FEDORA_VIEW + "dependsOn";
    public static final String VIEW_DIRECT             = NS_FEDORA_VIEW + "isDirect";
    public static final String VIEW_DISSEMINATES       = NS_FEDORA_VIEW + "disseminates";
    public static final String VIEW_MEDIATYPE          = NS_FEDORA_VIEW + "media-type";
    public static final String VIEW_TYPE               = NS_FEDORA_VIEW + "viewType";
    public static final String VIEW_VOLATILE           = NS_FEDORA_VIEW + "isVolatile";
	
    /**
     * Returns the index level of the ResourceIndex.
     * Possible index levels are:
     *  0 = off
     *  1 = on (default)
     *  2 = on plus indexing of permutations of disseminations that have
     *      fixed parameters.
     * @return Current index level of the ResourceIndex
     */
	public int getIndexLevel();
	
    /**
     * Adds a Fedora digital object to the ResourceIndex.
     * @param digitalObject The Fedora digital object to add to the ResourceIndex
     * @throws ResourceIndexException
     */
	public void addDigitalObject(DigitalObject digitalObject) throws ResourceIndexException;
	
    /**
     * Modifies a Fedora digital object in the ResourceIndex
     * @param digitalObject The Fedora digital object to modify in the ResourceIndex
     * @throws ResourceIndexException
     */
	public void modifyDigitalObject(DigitalObject digitalObject) throws ResourceIndexException;
	
    /**
     * Removes a Fedora digital object from the ResourceIndex.
     * @param digitalObject The Fedora digital object to remove from the ResourceIndex
     * @throws ResourceIndexException
     */
	public void deleteDigitalObject(DigitalObject digitalObject) throws ResourceIndexException;
	
    /**
     * Forces the ResourceIndex to write any buffered changes.
     * This method does not need to be used in normal usage, as the ResourceIndex
     * will write out its buffer periodically.
     * @throws ResourceIndexException
     */
    public void commit() throws ResourceIndexException;
    
    /**
     * Write a serialized representation of the ResourceIndex in the specified 
     * format.
     * @param out The output stream to which the RDF is written
     * @param format Desired format of the output (e.g. RDF_XML, TURTLE, N_TRIPLESs, etc.)
     * @throws ResourceIndexException
     */
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




























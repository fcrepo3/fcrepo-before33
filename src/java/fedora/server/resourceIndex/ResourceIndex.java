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
     * The ResourceIndex will not index anything.
     */
    public static final int INDEX_LEVEL_OFF             = 0;
    
    /**
     * The ResourceIndex will index:
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




























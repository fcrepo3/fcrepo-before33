package fedora.server.resourceIndex;

import org.trippi.RDFFormat;
import org.trippi.TriplestoreReader;

import fedora.common.Constants;
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
public interface ResourceIndex extends Constants, TriplestoreReader {
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




























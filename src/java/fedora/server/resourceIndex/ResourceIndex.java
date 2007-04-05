/* The contents of this file are subject to the license and copyright terms
 * detailed in the license directory at the root of the source tree (also 
 * available online at http://www.fedora.info/license/).
 */

package fedora.server.resourceIndex;

import java.io.OutputStream;

import org.trippi.RDFFormat;
import org.trippi.TriplestoreWriter;

import fedora.server.errors.ResourceIndexException;

import fedora.server.storage.BDefReader;
import fedora.server.storage.BMechReader;
import fedora.server.storage.DOReader;

/**
 * The main interface to the Fedora Resource Index.
 *
 * The Resource Index (RI) provides read/write access to an RDF representation
 * of all objects in the Fedora repository.  The information stored in the RI 
 * is derived solely from information stored within the digital objects.
 *
 * @author Edwin Shin
 * @author cwilper@cs.cornell.edu
 */
public interface ResourceIndex extends TriplestoreWriter {

    /**
     * At this level, the ResourceIndex will not index anything.
     */
    public static final int INDEX_LEVEL_OFF             = 0;
    
    /**
     * At this level the ResourceIndex will index:
     *  object properties
     *  datastreams
     *  intra-object dependencies
     */
    public static final int INDEX_LEVEL_ON              = 1;
    
    /**
     * Equivalent to INDEX_LEVEL_ON plus the indexing of the various 
     * permutations of a representation (i.e. dissemination) given by a fixed
     * parameter list.
     * 
     * For example, consider the method, getImage, which requires two 
     * parameters, border and format. The range of border's values is on or 
     * off; and the range of format's values is jpg or png.
     * 
     * There are four possible permutations:
     *  1) getImage?border=on&format=jpg
     *  2) getImage?border=on&format=png
     *  3) getImage?border=off&format=jpg
     *  4) getImage?border=off&format=png
     * 
     * At INDEX_LEVEL_ON, only the method name, getImage, would be indexed,
     * and none of the permutations.
     */
    public static final int INDEX_LEVEL_PERMUTATIONS    = 2;
    
    /**
     * Gets the index level of the ResourceIndex.
     *
     * @return the current index level of the RI, which is either 
     *         INDEX_LEVEL_OFF, INDEX_LEVEL_ON, or INDEX_LEVEL_PERMUTATIONS.
     */
	int getIndexLevel();

    /**
     * Adds a behavior definition object.
     *
     * @param reader the behavior definition to add.
     * @throws ResourceIndexException if the operation fails for any reason.
     */
    void addBDefObject(BDefReader reader)
            throws ResourceIndexException;

    /**
     * Adds a behavior mechanism object.
     *
     * @param reader the behavior definition to add.
     * @throws ResourceIndexException if the operation fails for any reason.
     */
    void addBMechObject(BMechReader reader)
            throws ResourceIndexException;

    /**
     * Adds a data object.
     *
     * @param reader the data object to add.
     * @throws ResourceIndexException if the operation fails for any reason.
     */
    void addDataObject(DOReader reader)
            throws ResourceIndexException;

    /**
     * Modifies a behavior definition object.
     *
     * @param oldReader the original behavior definition.
     * @param newReader the modified behavior definition.
     * @throws ResourceIndexException if the operation fails for any reason.
     */
    void modifyBDefObject(BDefReader oldReader, BDefReader newReader)
            throws ResourceIndexException;

    /**
     * Modifies a behavior mechanism object.
     *
     * @param oldReader the original behavior mechanism.
     * @param newReader the modified behavior mechanism.
     * @throws ResourceIndexException if the operation fails for any reason.
     */
    void modifyBMechObject(BMechReader oldReader, BMechReader newReader)
            throws ResourceIndexException;

    /**
     * Modifies a data object.
     *
     * @param oldReader the original data object.
     * @param newReader the modified data object.
     * @throws ResourceIndexException if the operation fails for any reason.
     */
    void modifyDataObject(DOReader oldReader, DOReader newReader)
            throws ResourceIndexException;

    /**
     * Deletes a behavior definition object.
     *
     * @param oldReader the original behavior definition.
     */
    void deleteBDefObject(BDefReader oldReader)
            throws ResourceIndexException;

    /**
     * Deletes a behavior definition object.
     *
     * @param oldReader the original behavior mechanism.
     */
    void deleteBMechObject(BMechReader oldReader)
            throws ResourceIndexException;

    /**
     * Deletes a behavior definition object.
     *
     * @param oldReader the original data object.
     */
    void deleteDataObject(DOReader oldReader)
            throws ResourceIndexException;
	
    /**
     * Exports all triples in the RI.
     *
     * @param out the output stream to which the RDF should be written.
     *        The caller is responsible for eventually closing this stream.
     * @param format the output format (RDF_XML, TURTLE, N_TRIPLESs, etc).
     * @throws ResourceIndexException if triples in the RI cannot be
     *         serialized for any reason.
     */
	void export(OutputStream out, RDFFormat format)
	        throws ResourceIndexException;
    
}




























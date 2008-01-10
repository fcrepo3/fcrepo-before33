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
import fedora.server.storage.DOReader;

/**
 * The main interface to the Fedora Resource Index. The Resource Index (RI)
 * provides read/write access to an RDF representation of all objects in the
 * Fedora repository. The information stored in the RI is derived solely from
 * information stored within the digital objects.
 * 
 * @author Edwin Shin
 * @author Chris Wilper
 */
public interface ResourceIndex
        extends TriplestoreWriter {

    /**
     * At this level, the ResourceIndex will not index anything.
     */
    public static final int INDEX_LEVEL_OFF = 0;

    /**
     * At this level the ResourceIndex will index: object properties datastreams
     * intra-object dependencies
     */
    public static final int INDEX_LEVEL_ON = 1;

    /**
     * Gets the index level of the ResourceIndex.
     * 
     * @return the current index level of the RI, which is either
     *         INDEX_LEVEL_OFF or INDEX_LEVEL_ON.
     */
    int getIndexLevel();

    /**
     * Adds a behavior definition object.
     * 
     * @param reader
     *        the behavior definition to add.
     * @throws ResourceIndexException
     *         if the operation fails for any reason.
     */
    void addBDefObject(BDefReader reader) throws ResourceIndexException;

    /**
     * Adds a data object.
     * 
     * @param reader
     *        the data object to add.
     * @throws ResourceIndexException
     *         if the operation fails for any reason.
     */
    void addDataObject(DOReader reader) throws ResourceIndexException;

    /**
     * Adds a content model object.
     * 
     * @param reader
     *        the content model object to add.
     * @throws ResourceIndexException
     *         if the operation fails for any reason.
     */
    void addCModelObject(DOReader reader) throws ResourceIndexException;

    /**
     * Modifies a behavior definition object.
     * 
     * @param oldReader
     *        the original behavior definition.
     * @param newReader
     *        the modified behavior definition.
     * @throws ResourceIndexException
     *         if the operation fails for any reason.
     */
    void modifyBDefObject(BDefReader oldReader, BDefReader newReader)
            throws ResourceIndexException;

    /**
     * Modifies a data object.
     * 
     * @param oldReader
     *        the original data object.
     * @param newReader
     *        the modified data object.
     * @throws ResourceIndexException
     *         if the operation fails for any reason.
     */
    void modifyDataObject(DOReader oldReader, DOReader newReader)
            throws ResourceIndexException;

    /**
     * Modifies a CModel object.
     * 
     * @param oldReader
     *        the original content model object.
     * @param newReader
     *        the modified content model object.
     * @throws ResourceIndexException
     *         if the operation fails for any reason.
     */
    void modifyCModelObject(DOReader oldReader, DOReader newReader)
            throws ResourceIndexException;

    /**
     * Deletes a behavior definition object.
     * 
     * @param oldReader
     *        the original behavior definition.
     */
    void deleteBDefObject(BDefReader oldReader) throws ResourceIndexException;

    /**
     * Deletes a data object.
     * 
     * @param oldReader
     *        the original data object.
     */
    void deleteDataObject(DOReader oldReader) throws ResourceIndexException;

    /**
     * Deletes a content model object.
     * 
     * @param oldReader
     *        the original content model object.
     */
    void deleteCModelObject(DOReader oldReader) throws ResourceIndexException;

    /**
     * Exports all triples in the RI.
     * 
     * @param out
     *        the output stream to which the RDF should be written. The caller
     *        is responsible for eventually closing this stream.
     * @param format
     *        the output format (RDF_XML, TURTLE, N_TRIPLESs, etc).
     * @throws ResourceIndexException
     *         if triples in the RI cannot be serialized for any reason.
     */
    void export(OutputStream out, RDFFormat format)
            throws ResourceIndexException;

}

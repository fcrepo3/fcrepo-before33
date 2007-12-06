/* The contents of this file are subject to the license and copyright terms
 * detailed in the license directory at the root of the source tree (also 
 * available online at http://www.fedora.info/license/).
 */

package fedora.server.resourceIndex;

import java.util.Set;

import org.jrdf.graph.Triple;

import fedora.server.errors.ResourceIndexException;
import fedora.server.storage.BDefReader;
import fedora.server.storage.DOReader;

/**
 * Generates RDF triples for Fedora objects.
 *
 * @author cwilper@cs.cornell.edu
 */
public interface TripleGenerator {

    /**
     * Get the triples for the given Fedora behavior definition object.
     *
     * @param reader the behavior definition object.
     * @return the set of triples.
     */
    Set<Triple> getTriplesForBDef(BDefReader reader)
            throws ResourceIndexException;

    /**
     * Get the triples for the given Fedora data object.
     *
     * @param reader the data object.
     * @return the set of triples.
     */
    Set<Triple> getTriplesForDataObject(DOReader reader)
            throws ResourceIndexException;

    /**
     * Get the triples for the given Fedora content model object.
     *
     * @param reader the data object.
     * @return the set of triples.
     */
    Set<Triple> getTriplesForCModelObject(DOReader reader)
            throws ResourceIndexException;

}

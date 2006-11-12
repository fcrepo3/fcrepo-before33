package fedora.server.resourceIndex;

import java.util.Set;

import org.jrdf.graph.Triple;

import fedora.server.errors.ResourceIndexException;
import fedora.server.storage.BDefReader;
import fedora.server.storage.BMechReader;
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
     * Get the triples for the given Fedora behavior mechanism object.
     *
     * @param reader the behavior mechanism object.
     * @return the set of triples.
     */
    Set<Triple> getTriplesForBMech(BMechReader reader)
            throws ResourceIndexException;

    /**
     * Get the triples for the given Fedora data object.
     *
     * @param reader the data object.
     * @return the set of triples.
     */
    Set<Triple> getTriplesForDataObject(DOReader reader)
            throws ResourceIndexException;

}

package fedora.server.resourceIndex;

import java.util.Set;

import org.jrdf.graph.Triple;

import fedora.server.errors.ResourceIndexException;
import fedora.server.storage.DOReader;

/**
 * Generates RDF triples for Fedora objects.
 *
 * @author cwilper@cs.cornell.edu
 */
public interface TripleGenerator {

    /**
     * Get the triples for the given Fedora object.
     *
     * @param reader the object.
     * @return the set of triples.
     */
    Set<Triple> getTriplesFor(DOReader reader) throws ResourceIndexException;

}
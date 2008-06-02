
package fedora.server.resourceIndex;

import java.util.HashSet;
import java.util.Set;

import org.jrdf.graph.Triple;

import fedora.server.errors.ResourceIndexException;
import fedora.server.storage.DOReader;

/**
 * Get all triples for a 3.0 service deployment object.
 * 
 * @author Aaron Birkland
 */
public class ServiceDeploymentTripleGenerator
        implements TripleGenerator {

    /**
     * {@inheritDoc}
     */
    public Set<Triple> getTriplesForObject(DOReader reader)
            throws ResourceIndexException {
        // no special triples for this content model
        return new HashSet<Triple>();
    }

}

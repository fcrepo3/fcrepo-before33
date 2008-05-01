
package fedora.server.resourceIndex;

import java.util.Set;

import org.jrdf.graph.GraphElementFactory;
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

    FedoraObjectTripleGenerator_3_0 common =
            new FedoraObjectTripleGenerator_3_0();

    public Set<Triple> getTriplesForObject(DOReader reader)
            throws ResourceIndexException {
        return common.getTriplesForObject(reader);
    }

    public void init(GraphElementFactory elementFactory) {
        common.init(elementFactory);
    }

}

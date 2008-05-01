
package fedora.server.resourceIndex;

import java.util.Set;

import org.jrdf.graph.GraphElementFactory;
import org.jrdf.graph.Triple;

import fedora.server.errors.ResourceIndexException;
import fedora.server.storage.DOReader;

/**
 * Get all triples for a 3.0 content model object.
 * <p>
 * Just adds all common data object triples. There are no special content model
 * triples that are indexed for 3.0 content models.
 * </p>
 * 
 * @author Aaron Birkland
 */
public class ContentModelTripleGenerator_3_0
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

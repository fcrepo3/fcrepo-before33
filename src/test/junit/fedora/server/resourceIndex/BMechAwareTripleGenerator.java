package fedora.server.resourceIndex;

import java.util.HashSet;
import java.util.Set;

import org.jrdf.graph.Triple;
import org.jrdf.graph.URIReference;

import fedora.server.errors.ResourceIndexException;
import fedora.server.storage.DOReader;

/**
 * Generates RDF triples for Fedora objects with the help of a 
 * <code>BMechInfoProvider</code>.
 *
 * @author cwilper@cs.cornell.edu
 */
public class BMechAwareTripleGenerator extends BaseTripleGenerator {

    /**
     * The provider this instance will use for behavior mechanism info.
     */
    private BMechInfoProvider _provider;

    /**
     * Construct an instance that will use the given provider.
     *
     * @param provider the provider to use for behavior mechanism info.
     */
    public BMechAwareTripleGenerator(BMechInfoProvider provider) {
        _provider = provider;
    }

    /**
     * {@inheritDoc}
     */
    public Set<Triple> getTriplesFor(DOReader reader) 
            throws ResourceIndexException {

        Set<Triple> triples = new HashSet<Triple>(super.getTriplesFor(reader));

        try {

            String pid = reader.GetObjectPID();
            URIReference objURI = createResource("info:fedora/" + pid);

            addDisseminatorTriples(reader, objURI, triples);

            return triples;

        } catch (Exception e) {
            throw new ResourceIndexException("Error generating triples", e);
        }
    }

    private void addDisseminatorTriples(DOReader reader,
                                        URIReference objURI,
                                        Set<Triple> triples) {
    }
}
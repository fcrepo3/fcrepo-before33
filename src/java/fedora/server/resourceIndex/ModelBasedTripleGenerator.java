
package fedora.server.resourceIndex;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.jrdf.graph.GraphElementFactory;
import org.jrdf.graph.Triple;

import fedora.server.errors.ResourceIndexException;
import fedora.server.errors.ServerException;
import fedora.server.storage.DOReader;
import fedora.server.storage.types.RelationshipTuple;

import static fedora.common.Constants.MODEL;
import static fedora.common.Models.CONTENT_MODEL_3_0;
import static fedora.common.Models.FEDORA_OBJECT_3_0;
import static fedora.common.Models.SERVICE_DEFINITION_3_0;
import static fedora.common.Models.SERVICE_DEPLOYMENT_3_0;

/**
 * Generates an object's triples based upon its declared content models.
 * <p>
 * For each content model in the object, will see if there is a
 * {@link TripleGenerator} for that model. Returns the union of all triples
 * created by these generators.
 * </p>
 * 
 * @author Aaron Birkland
 */
public class ModelBasedTripleGenerator
        implements TripleGenerator {

    /**
     * Map of model-specific triple generators. Right now, this is entirely
     * static. Change that if the need arises...
     */
    private static final Map<String, Class<? extends TripleGenerator>> m_generatorClasses =
            new HashMap<String, Class<? extends TripleGenerator>>();

    /* hard coded for now... */
    static {
        m_generatorClasses.put(FEDORA_OBJECT_3_0.uri,
                               FedoraObjectTripleGenerator_3_0.class);
        m_generatorClasses.put(SERVICE_DEFINITION_3_0.uri,
                               ServiceDefinitionTripleGenerator_3_0.class);
        m_generatorClasses.put(SERVICE_DEPLOYMENT_3_0.uri,
                               ServiceDeploymentTripleGenerator.class);
        m_generatorClasses.put(CONTENT_MODEL_3_0.uri,
                               ContentModelTripleGenerator_3_0.class);
    }

    /** Contains the initialized triple generators for each model */
    private Map<String, TripleGenerator> m_generators =
            new HashMap<String, TripleGenerator>();

    /**
     * Create an uninitialized ModelBasedTripleGenerator.
     * <p>
     * Until initialized with {@link #init(GraphElementFactory)}, will not
     * produce any triples.
     * </p>
     */
    public ModelBasedTripleGenerator() {
    }

    /**
     * {@inheritDoc}
     */
    public void init(GraphElementFactory factory) {
        for (String modelID : m_generatorClasses.keySet()) {
            Class<? extends TripleGenerator> genClass =
                    m_generatorClasses.get(modelID);

            try {
                TripleGenerator generator = genClass.newInstance();
                generator.init(factory);
                m_generators.put(modelID, generator);
            } catch (Exception e) {
                throw new RuntimeException("Could not instantiate triple generator "
                                                   + genClass.getName()
                                                   + " for model " + modelID,
                                           e);
            }
        }
    }

    /**
     * Gets all triples implied by the object's models.
     * 
     * @param reader
     *        Reads the current object
     * @return Set of all triples implied by the object's models.
     */
    public Set<Triple> getTriplesForObject(DOReader reader)
            throws ResourceIndexException {

        Set<Triple> objectTriples = new HashSet<Triple>();

        try {
            for (RelationshipTuple modelRel : reader
                    .getRelationships(MODEL.HAS_MODEL, null)) {

                if (m_generators.containsKey(modelRel.object)) {
                    objectTriples.addAll(m_generators.get(modelRel.object)
                            .getTriplesForObject(reader));
                }
            }
        } catch (ServerException e) {
            throw new ResourceIndexException("Could not read object's content model",
                                             e);
        }

        return objectTriples;
    }
}

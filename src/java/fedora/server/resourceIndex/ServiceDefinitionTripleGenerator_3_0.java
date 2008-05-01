
package fedora.server.resourceIndex;

import java.io.IOException;
import java.io.InputStream;

import java.util.HashSet;
import java.util.Set;

import org.jrdf.graph.GraphElementFactory;
import org.jrdf.graph.Triple;
import org.jrdf.graph.URIReference;

import org.xml.sax.InputSource;

import fedora.common.PID;

import fedora.server.errors.ResourceIndexException;
import fedora.server.errors.ServerException;
import fedora.server.storage.DOReader;
import fedora.server.storage.service.ServiceMapper;
import fedora.server.storage.types.Datastream;
import fedora.server.storage.types.MethodDef;

import static fedora.common.Constants.MODEL;

/**
 * Generates all triples objects modeled as Fedora 3.0 Service Definitions.
 * 
 * @author Aaron Birkland
 */
public class ServiceDefinitionTripleGenerator_3_0
        extends TripleGeneratorBase
        implements TripleGenerator {

    private static final String METHODMAP_DS = "METHODMAP";

    private FedoraObjectTripleGenerator_3_0 common =
            new FedoraObjectTripleGenerator_3_0();

    public Set<Triple> getTriplesForObject(DOReader reader)
            throws ResourceIndexException {
        Set<Triple> set = new HashSet<Triple>();

        try {
            URIReference objURI =
                    createResource(PID.toURI(reader.GetObjectPID()));

            /*
             * Everything in a data object is also indexed for SDefs
             * (datastreams, properties, etc)
             */
            set.addAll(common.getTriplesForObject(reader));

            /* Now add the SDef operation-specific triples */
            addMethodDefTriples(objURI, reader, set);
        } catch (Exception e) {
            throw new ResourceIndexException("Could not generate triples", e);
        }
        return set;
    }

    public void init(GraphElementFactory g) {
        super.init(g);
        common.init(g);
    }

    /**
     * Add a "defines" statement for the given sDef for each abstract method it
     * defines.
     */
    private void addMethodDefTriples(URIReference objURI,
                                     DOReader reader,
                                     Set<Triple> set)
            throws ResourceIndexException {
        try {
            for (MethodDef element : getAbstractMethods(reader)) {
                add(objURI, MODEL.DEFINES_METHOD, element.methodName, set);
            }
        } catch (ResourceIndexException e) {
            throw e;
        } catch (Exception e) {
            throw new ResourceIndexException("Error adding method def "
                    + "triples", e);
        }
    }

    private MethodDef[] getAbstractMethods(DOReader reader)
            throws ServerException {
        ServiceMapper mapper = new ServiceMapper(reader.GetObjectPID());
        Datastream methodmap = reader.GetDatastream(METHODMAP_DS, null);
        if (methodmap != null) {
            InputStream contentStream = methodmap.getContentStream();
            try {
                return mapper.getMethodDefs(new InputSource(contentStream));
            } finally {
                try {
                    contentStream.close();
                } catch (IOException e) {
                    /* Probably will never happen */
                }
            }
        } else {
            return new MethodDef[0];
        }
    }

}

package fedora.server.resourceIndex;

import java.net.URI;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.jrdf.graph.ObjectNode;
import org.jrdf.graph.PredicateNode;
import org.jrdf.graph.SubjectNode;
import org.jrdf.graph.Triple;
import org.jrdf.graph.URIReference;

import org.trippi.RDFUtil;

import fedora.common.Constants;

import fedora.server.errors.ResourceIndexException;
import fedora.server.errors.ServerException;
import fedora.server.storage.DOReader;
import fedora.server.storage.types.Datastream;
import fedora.server.storage.types.DatastreamXMLMetadata;
import fedora.server.utilities.DateUtility;

/**
 * Generates base RDF triples for Fedora objects.
 *
 * @author cwilper@cs.cornell.edu
 */
public class BaseTripleGenerator implements Constants, TripleGenerator {

    private RDFUtil _rdfUtil;

    /**
     * Constructor.
     */
    public BaseTripleGenerator() {
        try {
            _rdfUtil = new RDFUtil();
        } catch (Exception e) { 
            // never happens
            throw new RuntimeException("Unexpected error", e);
        }
    }

    /**
     * {@inheritDoc}
     */
    public Set<Triple> getTriplesFor(DOReader reader)
            throws ResourceIndexException {
        try {

            Set<Triple> triples = new HashSet<Triple>();
            
            String pid = reader.GetObjectPID();
            URIReference objURI = createResource("info:fedora/" + pid);

            addCoreObjectTriples(reader, objURI, triples);

            addAllDatastreamTriples(reader.GetDatastreams(null, null), objURI,
                    triples);

            return triples;

        } catch (Exception e) {
            throw new ResourceIndexException("Error generating triples", e);
        }
    }

    /**
     * For the given object, add the triples that are common for all objects.
     *
     * This will include:
     * <ul>
     *   <li> object <i>model:contentModel</i></li>
     *   <li> object <i>model:createdDate</i></li>
     *   <li> object <i>model:label</i></li>
     *   <li> object <i>model:owner</i></li>
     *   <li> object <i>model:state</i></li>
     *   <li> object <i>rdf:type</i></li>
     *   <li> object <i>view:lastModifiedDate</i></li>
     * </ul>
     */
    private void addCoreObjectTriples(DOReader r,
                                      URIReference objURI,
                                      Set<Triple> set) throws Exception {
        add(objURI, MODEL.CONTENT_MODEL,      r.getContentModelId(), set);
        add(objURI, MODEL.CREATED_DATE,       r.getCreateDate(), set);
        add(objURI, MODEL.LABEL,              r.GetObjectLabel(), set);
        add(objURI, MODEL.OWNER,              r.getOwnerId(), set);
        add(objURI, MODEL.STATE,              getStateResource(
                                              r.GetObjectState()), set);
        add(objURI, VIEW.LAST_MODIFIED_DATE,  r.getLastModDate(), set);
        add(objURI, RDF.TYPE,                 getObjectTypeResource(
                                              r.getFedoraObjectType()), set);
    }

    /**
     * For the given datastream, add the triples that are common for all
     * datastreams.
     *
     * This will include:
     * <ul>
     *   <li> object     <i>view:hasDatastream</i> datastream</li>
     *   <li> object     <i>view:disseminates</i> datastream</li>
     *   <li> datastream <i>view:disseminationType</i></li>
     *   <li> datastream <i>view:isVolatile</i></li>
     *   <li> datastream <i>view:lastModifiedDate</i></li>
     *   <li> datastream <i>view:mimeType</i></li>
     *   <li> datastream <i>model:state</i></li>
     * </ul>
     */
    private void addCoreDatastreamTriples(Datastream ds,
                                          URIReference objURI,
                                          Set<Triple> triples)
            throws Exception {

        URIReference dsURI = createResource(objURI.getURI().toString() 
                + "/" + ds.DatastreamID);

        add(objURI, VIEW.HAS_DATASTREAM, dsURI, triples);
        add(objURI, VIEW.DISSEMINATES, dsURI, triples);

        URIReference dsDissType = createResource("info:fedora/*/" 
                + ds.DatastreamID);

        add(dsURI, VIEW.DISSEMINATION_TYPE, dsDissType, triples);

        boolean isVolatile = ds.DSControlGrp.equals("E") 
                          || ds.DSControlGrp.equals("R");

        add(dsURI, VIEW.IS_VOLATILE, isVolatile, triples);
        add(dsURI, VIEW.LAST_MODIFIED_DATE, ds.DSCreateDT, triples);
        add(dsURI, VIEW.MIME_TYPE, ds.DSMIME, triples);
        add(dsURI, MODEL.STATE, getStateResource(ds.DSState), triples);

    }

    private void addDCTriples(DatastreamXMLMetadata ds,
                              URIReference objURI,
                              Set<Triple> triples) {
    }

    private void addRELSEXTTriples(DatastreamXMLMetadata ds,
                                   URIReference objURI,
                                   Set<Triple> triples) {
    }

    private void addAllDatastreamTriples(Datastream[] datastreams,
                                         URIReference objURI,
                                         Set<Triple> triples) 
            throws Exception {

        for (int i = 0; i < datastreams.length; i++) {

            addCoreDatastreamTriples(datastreams[i], objURI, triples);

            if (datastreams[i].DatastreamID.equals("DC")) {
                addDCTriples((DatastreamXMLMetadata) datastreams[i], 
                             objURI,
                             triples);
            } else if (datastreams[i].DatastreamID.equals("RELS-EXT")) {
                addRELSEXTTriples((DatastreamXMLMetadata) datastreams[i], 
                                  objURI, 
                                  triples);
            }
            // TODO: add bmech and bdef-specific triples here!!!
        }
    }

    protected URIReference createResource(String uri) throws Exception {
        return _rdfUtil.createResource(new URI(uri));
    }

    protected URIReference getStateResource(String state)
            throws ResourceIndexException {
        if (state == null) {
            throw new ResourceIndexException("State cannot be null");
        } else if (state.equals("A")) {
            return MODEL.ACTIVE;
        } else if (state.equals("D")) {
            return MODEL.DELETED;
        } else if (state.equals("I")) {
            return MODEL.INACTIVE;
        } else {
            throw new ResourceIndexException("Unrecognized state: " + state);
        }
    }

    protected URIReference getObjectTypeResource(String objType)
            throws ResourceIndexException {
        if (objType == null) {
            throw new ResourceIndexException("Object type cannot be null");
        } else if (objType.equals("D")) {
            return MODEL.BDEF_OBJECT;
        } else if (objType.equals("M")) {
            return MODEL.BMECH_OBJECT;
        } else if (objType.equals("O")) {
            return MODEL.DATA_OBJECT;
        } else {
            throw new ResourceIndexException("Unrecognized object type: " 
                    + objType);
        }
    }

    protected void add(SubjectNode subject,
                       PredicateNode predicate,
                       ObjectNode object,
                       Set<Triple> set) throws Exception {
        set.add(_rdfUtil.createTriple(subject, predicate, object));
    }

    protected void add(SubjectNode subject,
                       PredicateNode predicate,
                       boolean booleanValue,
                       Set<Triple> set) throws Exception {
        add(subject, predicate, new Boolean(booleanValue).toString(), set);
    }

    protected void add(SubjectNode subject,
                       PredicateNode predicate,
                       String lexicalValue,
                       Set<Triple> set) throws Exception {
        if (lexicalValue != null) {
            ObjectNode object = _rdfUtil.createLiteral(lexicalValue);
            set.add(_rdfUtil.createTriple(subject, predicate, object));
        }
    }

    protected void add(SubjectNode subject,
                       PredicateNode predicate,
                       Date dateValue,
                       Set<Triple> set) throws Exception {
        if (dateValue != null) {
            String lexicalValue = DateUtility.convertDateToString(dateValue);
            URI dataType = XSD.DATE_TIME.getURI();
            ObjectNode object = _rdfUtil.createLiteral(lexicalValue,
                                                       dataType);
            set.add(_rdfUtil.createTriple(subject, predicate, object));
        }
    }

}
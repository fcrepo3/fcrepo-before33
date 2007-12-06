package fedora.server.resourceIndex;

import java.net.URI;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jrdf.graph.ObjectNode;
import org.jrdf.graph.PredicateNode;
import org.jrdf.graph.SubjectNode;
import org.jrdf.graph.Triple;
import org.jrdf.graph.URIReference;
import org.trippi.RDFFormat;
import org.trippi.RDFUtil;
import org.trippi.TripleIterator;

import fedora.common.Constants;
import fedora.common.PID;
import fedora.common.rdf.RDFName;
import fedora.server.errors.ResourceIndexException;
import fedora.server.storage.BDefReader;
import fedora.server.storage.BMechReader;
import fedora.server.storage.DOReader;
import fedora.server.storage.types.BMechDSBindSpec;
import fedora.server.storage.types.Datastream;
import fedora.server.storage.types.DatastreamXMLMetadata;
import fedora.server.storage.types.MethodDef;
import fedora.server.utilities.DCFields;
import fedora.server.utilities.DateUtility;

/**
 * Generates base RDF triples for Fedora objects.
 *
 * @author cwilper@cs.cornell.edu
 */
public class BaseTripleGenerator implements Constants, TripleGenerator {

    /**
     * The factory this instance will use for creating JRDF objects.
     */
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
    public Set<Triple> getTriplesForBDef(BDefReader reader)
            throws ResourceIndexException {

        Set<Triple> set = new HashSet<Triple>();

        URIReference objURI = addCommonTriples(reader, set);
        add(objURI, RDF.TYPE, MODEL.BDEF_OBJECT, set);
        addMethodDefTriples(objURI, reader, set);

        return set;
    }

    /**
     * {@inheritDoc}
     */
    public Set<Triple> getTriplesForBMech(BMechReader reader)
            throws ResourceIndexException {

        Set<Triple> set = new HashSet<Triple>();

        URIReference objURI = addCommonTriples(reader, set);
        add(objURI, RDF.TYPE, MODEL.BMECH_OBJECT, set);
        addImplementsBDefTriples(objURI, reader, set);

        return set;
    }

    /**
     * {@inheritDoc}
     */
    public Set<Triple> getTriplesForDataObject(DOReader reader)
            throws ResourceIndexException {

        Set<Triple> set = new HashSet<Triple>();

        URIReference objURI = addCommonTriples(reader, set);
        add(objURI, RDF.TYPE, MODEL.DATA_OBJECT, set);

        return set;
    }

    /**
     * Add a "defines" statement for the given bDef for each abstract
     * method it defines.
     */
    private void addMethodDefTriples(URIReference objURI,
                                     BDefReader reader,
                                     Set<Triple> set)
            throws ResourceIndexException {
        try {
            MethodDef[] methodDefs = reader.getAbstractMethods(null);
            for (int i = 0; i < methodDefs.length; i++) {
                add(objURI, MODEL.DEFINES_METHOD, methodDefs[i].methodName, set);
            }
        } catch (ResourceIndexException e) {
            throw e;
        } catch (Exception e) {
            throw new ResourceIndexException("Error adding method def "
                    + "triples", e);
        }
    }

    /**
     * Add an "implements" statement for the given bMech indicating which bDef it
     * implements.
     */
    private void addImplementsBDefTriples(URIReference objURI,
                                          BMechReader reader,
                                          Set<Triple> set)
            throws ResourceIndexException {
        try {
            BMechDSBindSpec bindSpec = reader.getServiceDSInputSpec(null);
            add(objURI, MODEL.IMPLEMENTS_BDEF, 
                    createResource(PID.toURI(bindSpec.bDefPID)), set);
        } catch (ResourceIndexException e) {
            throw e;
        } catch (Exception e) {
            throw new ResourceIndexException("Error adding implements bdef "
                    + "triples", e);
        }
    }

    /**
     * Add the common core and datastream triples for the given object.
     */
    private URIReference addCommonTriples(DOReader reader, Set<Triple> set)
            throws ResourceIndexException {

        try {
            
            URIReference objURI = createResource(PID.toURI(reader.GetObjectPID()));

            addCoreObjectTriples(reader, objURI, set);

            addAllDatastreamTriples(reader.GetDatastreams(null, null), objURI, 
                    set);

            return objURI;

        } catch (ResourceIndexException e) {
            throw e;
        } catch (Exception e) {
            throw new ResourceIndexException("Error generating triples", e);
        }
    }

    /**
     * For the given object, add the common core system metadata triples.
     *
     * This will include:
     * <ul>
     *   <li> object <i>model:contentModel</i></li>
     *   <li> object <i>model:createdDate</i></li>
     *   <li> object <i>model:label</i></li>
     *   <li> object <i>model:owner</i></li>
     *   <li> object <i>model:state</i></li>
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
                                          Set<Triple> set)
            throws Exception {

        URIReference dsURI = createResource(objURI.getURI().toString() 
                + "/" + ds.DatastreamID);

        add(objURI, VIEW.HAS_DATASTREAM, dsURI, set);
        add(objURI, VIEW.DISSEMINATES, dsURI, set);

        URIReference dsDissType = createResource(FEDORA.uri + "*/" 
                + ds.DatastreamID);

        add(dsURI, VIEW.DISSEMINATION_TYPE, dsDissType, set);

        boolean isVolatile = ds.DSControlGrp.equals("E") 
                          || ds.DSControlGrp.equals("R");

        add(dsURI, VIEW.IS_VOLATILE, isVolatile, set);
        add(dsURI, VIEW.LAST_MODIFIED_DATE, ds.DSCreateDT, set);
        add(dsURI, VIEW.MIME_TYPE, ds.DSMIME, set);
        add(dsURI, MODEL.STATE, getStateResource(ds.DSState), set);

    }

    /**
     * Add a statement about the object for each predicate, value
     * pair expressed in the DC datastream.
     */
    private void addDCTriples(DatastreamXMLMetadata ds,
                              URIReference objURI,
                              Set<Triple> set) throws Exception {
        DCFields dc = new DCFields(ds.getContentStream());
        Map<RDFName, List<String>> map = dc.getMap();
        for (RDFName predicate : map.keySet()) {
            for (String value : map.get(predicate)) {
                add(objURI, predicate, value, set);
            }
        }
    }


    /**
     * Add all triples found in the RELS-EXT datastream.
     */
    private void addRELSEXTTriples(DatastreamXMLMetadata ds,
                                   Set<Triple> set) throws Exception {
        TripleIterator iter = TripleIterator.fromStream(ds.getContentStream(),
                                                        RDFFormat.RDF_XML);
        try {
            while (iter.hasNext()) {
                set.add(iter.next());
            }
        } finally {
            iter.close();
        }

    }

    /**
     * Add all triples whose values are determined by datastream
     * metadata or content.
     */
    private void addAllDatastreamTriples(Datastream[] datastreams,
                                         URIReference objURI,
                                         Set<Triple> set) 
            throws Exception {

        for (int i = 0; i < datastreams.length; i++) {

            Datastream ds = datastreams[i];

            // triples determined by datastream's metadata
            addCoreDatastreamTriples(ds, objURI, set);

            // triples determined by parsing the datastream's content
            if (ds.DatastreamID.equals("DC")) {
                addDCTriples((DatastreamXMLMetadata) ds, objURI, set);
            } else if (ds.DatastreamID.equals("RELS-EXT")) {
                addRELSEXTTriples((DatastreamXMLMetadata) ds, set);
            }

        }
    }

    // Helper methods for creating RDF components

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

    // Helper methods for adding triples

    protected void add(SubjectNode subject,
                       PredicateNode predicate,
                       ObjectNode object,
                       Set<Triple> set) throws ResourceIndexException {
        try {
            set.add(_rdfUtil.createTriple(subject, predicate, object));
        } catch (Exception e) {
            throw new ResourceIndexException("Error creating triple", e);
        }
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
            String lexicalValue = DateUtility.convertDateToXSDString(dateValue);
            URI dataType = XSD.DATE_TIME.getURI();
            ObjectNode object = _rdfUtil.createLiteral(lexicalValue,
                                                       dataType);
            set.add(_rdfUtil.createTriple(subject, predicate, object));
        }
    }

}

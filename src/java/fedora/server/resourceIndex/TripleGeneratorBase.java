package fedora.server.resourceIndex;

import java.net.URI;

import java.util.Date;
import java.util.Set;

import org.jrdf.graph.GraphElementFactory;
import org.jrdf.graph.GraphElementFactoryException;
import org.jrdf.graph.ObjectNode;
import org.jrdf.graph.SubjectNode;
import org.jrdf.graph.Triple;
import org.jrdf.graph.URIReference;

import fedora.common.rdf.RDFName;

import fedora.server.errors.ResourceIndexException;
import fedora.server.utilities.DateUtility;

import static fedora.common.Constants.MODEL;
import static fedora.common.Constants.RDF_XSD;

public abstract class TripleGeneratorBase {
    // Helper methods for creating RDF components
    
    private GraphElementFactory _geFactory;
    
    public void init(GraphElementFactory geFactory) {
        _geFactory = geFactory;
    }

    protected URIReference createResource(String uri) throws Exception {
        return _geFactory.createResource(new URI(uri));
    }

    protected RDFName getStateResource(String state)
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
                       RDFName predicate,
                       RDFName object,
                       Set<Triple> set) throws ResourceIndexException {
        try {
            add(subject,
                predicate,
                _geFactory.createResource(object.getURI()),
                set);
        } catch (GraphElementFactoryException e) {
            throw new ResourceIndexException(e.getMessage(), e);
        }
    }

    protected void add(SubjectNode subject,
                       RDFName predicate,
                       ObjectNode object,
                       Set<Triple> set) throws ResourceIndexException {
        try {
            set.add(_geFactory.createTriple(subject, _geFactory
                    .createResource(predicate.getURI()), object));
        } catch (GraphElementFactoryException e) {
            throw new ResourceIndexException(e.getMessage(), e);
        }
    }

    protected void add(SubjectNode subject,
                       RDFName predicate,
                       String lexicalValue,
                       Set<Triple> set) throws Exception {
        if (lexicalValue != null) {
            set.add(_geFactory.createTriple(subject, _geFactory
                    .createResource(predicate.getURI()), _geFactory
                    .createLiteral(lexicalValue)));
        }
    }

    protected void add(SubjectNode subject,
                       RDFName predicate,
                       Date dateValue,
                       Set<Triple> set) throws Exception {
        if (dateValue != null) {
            String lexicalValue = DateUtility.convertDateToXSDString(dateValue);
            ObjectNode object =
                    _geFactory.createLiteral(lexicalValue, RDF_XSD.DATE_TIME
                            .getURI());
            set.add(_geFactory.createTriple(subject, _geFactory
                    .createResource(predicate.getURI()), object));
        }
    }

    protected void add(SubjectNode subject,
                       RDFName predicate,
                       boolean booleanValue,
                       Set<Triple> set) throws Exception {
        add(subject, predicate, Boolean.toString(booleanValue), set);
    }
}

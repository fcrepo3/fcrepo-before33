/* The contents of this file are subject to the license and copyright terms
 * detailed in the license directory at the root of the source tree (also
 * available online at http://www.fedora.info/license/).
 */

package fedora.server.validation;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

import java.net.URI;
import java.net.URISyntaxException;

import java.util.Collection;
import java.util.HashSet;

import junit.framework.TestCase;

import org.jrdf.graph.GraphElementFactory;
import org.jrdf.graph.GraphElementFactoryException;
import org.jrdf.graph.ObjectNode;
import org.jrdf.graph.Triple;

import org.trippi.RDFFormat;
import org.trippi.RDFUtil;
import org.trippi.TripleIterator;

import fedora.common.Constants;
import fedora.common.PID;
import fedora.common.rdf.SimpleLiteral;
import fedora.common.rdf.SimpleTriple;
import fedora.common.rdf.SimpleURIReference;

import fedora.server.errors.GeneralException;
import fedora.server.errors.ServerException;
import fedora.server.errors.ValidationException;

/**
 * Tests the RELS-EXT datastream deserializer and validation.
 * 
 * @author Edwin Shin
 */
public class RelsExtValidatorTest
        extends TestCase {

    private Collection<Triple> triples;

    private static byte[] RELS_EXT;

    private PID pid;

    static {
        // create test RELS-EXT
        StringBuilder sb = new StringBuilder();
        sb
                .append("<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\""
                        + "         xmlns:rel=\"info:fedora/fedora-system:def/relations-external#\">"
                        + "  <rdf:Description rdf:about=\"info:fedora/demo:888\">"
                        + "     <rel:isMemberOf rdf:resource=\"info:fedora/demo:X\" />"
                        + "  </rdf:Description>" + "</rdf:RDF>");

        try {
            RELS_EXT = sb.toString().getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
        }
    }

    @Override
    public void setUp() {
        triples = new HashSet<Triple>();
    }

    public void testConstructor() throws Exception {
        pid = PID.getInstance("demo:888");
        InputStream in = new ByteArrayInputStream(RELS_EXT);
        RelsExtValidator deser;

        deser = new RelsExtValidator("UTF-8", false);
        deser.deserialize(in, pid.toURI());
    }
    
    public void testEmpty() throws Exception {
        pid = PID.getInstance("demo:demo");
        InputStream in;
        String[] empties = {"", " ", "</>"};
        
        for (String s : empties) {
            in = new ByteArrayInputStream(s.getBytes());
            
            try {
                RelsExtValidator.validate(pid, in);
                fail("Empty RELS-EXT datastream not allowed: \"" + s + "\"");
            } catch (ValidationException e) {}
        }
    }

    public void testBadAssertions() throws Exception {
        pid = PID.getInstance("demo:foo");
        String p, o;

        p = "urn:p";
        o = "urn:o";
        triples.add(createTriple(pid, p, o, false, null));

        p = "http://purl.org/dc/elements/1.1/title";
        o = "The God of Small Things";
        triples.add(createTriple(pid, p, o, true, null));

        try {
            validateAndClear();
            fail("Dublin Core assertions not allowed");
        } catch (ValidationException e) {
        }

        p = "info:fedora/fedora-system:def/model#contentModel";
        o = "demo:baz";
        triples.add(createTriple(pid, p, o, false, null));

        try {
            validateAndClear();
            fail("Fedora Model namespace assertions not allowed");
        } catch (ValidationException e) {
        }
    }

    public void testResourceURI() throws Exception {
        pid = PID.getInstance("demo:foo");
        String p, o;

        p = "urn:p";
        o = "urn:o";
        triples.add(createTriple(pid, p, o, false, null));
        p = "urn:p";
        o = "urn:o";
        triples.add(createTriple(pid, p, o, true, null));
        p = "urn:p";
        o = "1970-01-01T00:00:00Z";
        triples.add(createTriple(pid,
                                 p,
                                 o,
                                 true,
                                 Constants.RDF_XSD.DATE_TIME.uri));
        validateAndClear();

        p = "urn:p";
        o = pid.toURI();
        triples.add(createTriple(pid, p, o, false, null));

        try {
            validateAndClear();
            fail("Self-referential assertions not allowed.");
        } catch (ValidationException e) {
        }
    }

    public void testDatatypes() throws Exception {
        pid = PID.getInstance("demo:foo");
        String p, o;

        p = "urn:p";
        o = "abc:123";
        triples.add(createTriple(pid, p, o, false, null));
        validateAndClear();

        o = "1";
        triples.add(createTriple(pid, p, o, true, Constants.RDF_XSD.INT.uri));
        validateAndClear();

        o = "abc";
        triples.add(createTriple(pid, p, o, true, Constants.RDF_XSD.INT.uri));
        try {
            validateAndClear();
            fail("Invalid integer value: " + o);
        } catch (ValidationException e) {
        }

        o = "-0001-01-01T00:00:00";
        triples.add(createTriple(pid,
                                 p,
                                 o,
                                 true,
                                 Constants.RDF_XSD.DATE_TIME.uri));
        validateAndClear();

        o = "1970-01-01T00:00:00";
        triples.add(createTriple(pid,
                                 p,
                                 o,
                                 true,
                                 Constants.RDF_XSD.DATE_TIME.uri));
        validateAndClear();

        o = "1970-01-01T00:00:00.1";
        triples.add(createTriple(pid,
                                 p,
                                 o,
                                 true,
                                 Constants.RDF_XSD.DATE_TIME.uri));
        validateAndClear();

        o = "1970-01-01T00:00:00.01";
        triples.add(createTriple(pid,
                                 p,
                                 o,
                                 true,
                                 Constants.RDF_XSD.DATE_TIME.uri));
        validateAndClear();

        o = "1970-01-01T00:00:00.001";
        triples.add(createTriple(pid,
                                 p,
                                 o,
                                 true,
                                 Constants.RDF_XSD.DATE_TIME.uri));
        validateAndClear();

        o = "-0001-01-01T00:00:00Z";
        triples.add(createTriple(pid,
                                 p,
                                 o,
                                 true,
                                 Constants.RDF_XSD.DATE_TIME.uri));
        validateAndClear();

        o = "1970-01-01T00:00:00Z";
        triples.add(createTriple(pid,
                                 p,
                                 o,
                                 true,
                                 Constants.RDF_XSD.DATE_TIME.uri));
        validateAndClear();

        o = "1970-01-01T00:00:00.1Z";
        triples.add(createTriple(pid,
                                 p,
                                 o,
                                 true,
                                 Constants.RDF_XSD.DATE_TIME.uri));
        validateAndClear();

        o = "1970-01-01T00:00:00.01Z";
        triples.add(createTriple(pid,
                                 p,
                                 o,
                                 true,
                                 Constants.RDF_XSD.DATE_TIME.uri));
        validateAndClear();

        o = "1970-01-01T00:00:00.001Z";
        triples.add(createTriple(pid,
                                 p,
                                 o,
                                 true,
                                 Constants.RDF_XSD.DATE_TIME.uri));
        validateAndClear();

        o = "abc";
        triples.add(createTriple(pid,
                                 p,
                                 o,
                                 true,
                                 Constants.RDF_XSD.DATE_TIME.uri));
        try {
            validateAndClear();
            fail("Invalid dateTime value: " + o);
        } catch (ValidationException e) {
        }
    }

    private void validateAndClear() throws Exception {
        try {
            TripleIterator iter = new MockTripleIterator(triples);
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            iter.toStream(out, RDFFormat.RDF_XML, false);
            RelsExtValidator.validate(pid, new ByteArrayInputStream(out
                    .toByteArray()));
        } finally {
            triples.clear();
        }
    }
    
    private static Triple createTriple(PID pid,
                                       String predicate,
                                       String object,
                                       boolean isLiteral,
                                       String datatype)
            throws ServerException {
        ObjectNode o = null;
        try {
            if (isLiteral) {
                if (datatype == null || datatype.length() == 0) {
                    o = new SimpleLiteral(object);
                } else {
                    o = new SimpleLiteral(object, new URI(datatype));
                }
            } else {
                o = new SimpleURIReference(new URI(object));
            }
            return new SimpleTriple(new SimpleURIReference(new URI(pid.toURI())),
                                    new SimpleURIReference(new URI(predicate)),
                                    o);
        } catch (URISyntaxException e) {
            throw new GeneralException(e.getMessage(), e);
        }
    }

}

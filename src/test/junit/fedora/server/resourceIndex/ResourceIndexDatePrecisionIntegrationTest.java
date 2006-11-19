package fedora.server.resourceIndex;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import java.util.Date;
import java.util.TimeZone;

import org.jrdf.graph.ObjectNode;
import org.jrdf.graph.Literal;

import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.trippi.TripleIterator;

import fedora.common.Constants;

import fedora.server.storage.types.DigitalObject;

/**
 * Date precision tests.
 *
 * @author cwilper@cs.cornell.edu
 */
public class ResourceIndexDatePrecisionIntegrationTest
        extends ResourceIndexIntegrationTest {

    private static TimeZone UTC = TimeZone.getTimeZone("UTC");

    private DateFormat _millisFormat;

    public ResourceIndexDatePrecisionIntegrationTest() {
        _millisFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        _millisFormat.setTimeZone(UTC);
    }

    /**
     * Dates with millisecond precision should come back as given.
     */
    @Test
    public void testMillisecondDatePrecision()
            throws Exception {
        String lex = "2006-11-18T12:22:10.001Z";
        Date date = _millisFormat.parse(lex);

        DigitalObject obj = getTestObject("test:1", "test 1");
        obj.setCreateDate(date);

        initRI(1);
        addObj(obj, true);

        TripleIterator results = spo("<info:fedora/test:1> <" 
                + Constants.MODEL.CREATED_DATE.uri + "> *");

        try {
            assertTrue(results.hasNext());
            ObjectNode dateNode = results.next().getObject();
            assertTrue(dateNode instanceof Literal);
            Literal dateLiteral = (Literal) dateNode;
            assertEquals(dateLiteral.getDatatypeURI().toString(), Constants.XSD.DATE_TIME.uri);
            assertEquals(lex, dateLiteral.getLexicalForm());
        } finally {
            results.close();
        }

    }

    // Supports legacy test runners
    public static junit.framework.Test suite() {
        return new junit.framework.JUnit4TestAdapter(
                ResourceIndexDatePrecisionIntegrationTest.class);
    }

}

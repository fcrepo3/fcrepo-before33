package fedora.server.storage.lowlevel.akubra;

import java.net.URI;

import org.junit.Test;

import org.fedoracommons.akubra.map.IdMapper;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * Unit tests for {@link HashPathIdMapper}.
 *
 * @author Chris Wilper
 */
public class HashPathIdMapperTest {

    private static final URI URI1 = URI.create("urn:example1");
    private static final URI URI1_ENCODED = URI.create("file:93/63/f7/urn%3Aexample1");

    private static final URI URI2 = URI.create("http://tinyurl.com/cxzzf");
    private static final URI URI2_ENCODED = URI.create("file:07/42/41/http%3A%2F%2Ftinyurl.com%2Fcxzzf");

    private static final URI URI3 = URI.create("info:foo/bar.baz.");
    private static final URI URI3_ENCODED = URI.create("file:6a/1b/1e/info%3Afoo%2Fbar.baz%2E");

    /** Generic tests should all pass with these patterns. */
    @Test
    public void testGoodPatterns() throws Exception {
        runGenericTests(null);
        runGenericTests("");
        runGenericTests("#");
        runGenericTests("#/#");
        runGenericTests("##");
        runGenericTests("##/##");
        runGenericTests("##/##/##");
    }

    /** Bad patterns should fail at construction-time. */
    @Test
    public void testBadPatterns() throws Exception {
        assertBadPattern("a");
        assertBadPattern("a#");
        assertBadPattern("#a");
        assertBadPattern("/#");
        assertBadPattern("#/");
        assertBadPattern("/#/");
        assertBadPattern("/#/");
        assertBadPattern("#########");
        assertBadPattern("####/#####");
    }

    /** getInternalId should produce the expected URIs. */
    @Test
    public void testGetInternalId() {
        IdMapper mapper = new HashPathIdMapper("##/##/##");
        assertEquals(URI1_ENCODED, mapper.getInternalId(URI1));
        assertEquals(URI2_ENCODED, mapper.getInternalId(URI2));
        assertEquals(URI3_ENCODED, mapper.getInternalId(URI3));
    }

    /** getExternalId should produce the expected URIs. */
    @Test
    public void testGetExternalId() {
        IdMapper mapper = new HashPathIdMapper("##/##/##");
        assertEquals(URI1, mapper.getExternalId(URI1_ENCODED));
        assertEquals(URI2, mapper.getExternalId(URI2_ENCODED));
        assertEquals(URI3, mapper.getExternalId(URI3_ENCODED));
    }

    private static void runGenericTests(String pattern)
            throws Exception {
        IdMapper mapper = new HashPathIdMapper(pattern);
        checkRoundtrip(mapper, URI1);
        checkRoundtrip(mapper, URI2);
        checkRoundtrip(mapper, URI3);
    }

    private static void checkRoundtrip(IdMapper mapper, URI orig) {
        URI internal = mapper.getInternalId(orig);
        URI external = mapper.getExternalId(internal);
        assertEquals(orig, external);
    }

    private static void assertBadPattern(String pattern) {
        try {
            new HashPathIdMapper(pattern);
        } catch (Throwable th) {
            if (!(th instanceof IllegalArgumentException)) {
                fail("Bad pattern '" + pattern + "' should have thrown "
                     + "IllegalArgumentException, but threw "
                     + th.getClass().getName());
            }
        }

    }

    // Supports legacy test runners
    public static junit.framework.Test suite() {
        return new junit.framework.JUnit4TestAdapter(HashPathIdMapperTest.class);
    }

}

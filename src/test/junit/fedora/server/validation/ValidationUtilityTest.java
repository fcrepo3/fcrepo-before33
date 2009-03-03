package fedora.server.validation;

import java.io.IOException;

import org.junit.Test;

import org.xml.sax.SAXException;

import junit.framework.JUnit4TestAdapter;

import fedora.common.FaultException;
import fedora.common.PID;

import fedora.server.ReadOnlyContext;
import fedora.server.errors.ValidationException;
import fedora.server.security.MockPolicyParser;
import fedora.server.security.PolicyParser;
import fedora.server.storage.DOReader;
import fedora.server.storage.MockRepositoryReader;
import fedora.server.storage.types.BasicDigitalObject;
import fedora.server.storage.types.DigitalObject;
import fedora.server.storage.types.ObjectBuilder;
import fedora.server.utilities.StreamUtility;

import static fedora.server.security.TestPolicyParser.POLICY_GOODENOUGH;
import static fedora.server.security.TestPolicyParser.POLICY_QUESTIONABLE;

/**
 * Unit tests for ValidationUtility.
 */
public class ValidationUtilityTest {

    private static final String TEST_PID = "test:1";

    private static final String RELSEXT_GOOD
            = "<rdf:RDF xmlns:rdf='http://www.w3.org/1999/02/22-rdf-syntax-ns#\'\n"
            + "       xmlns:rel='info:fedora/fedora-system:def/relations-external#'>\n"
            + "  <rdf:Description rdf:about='info:fedora/" + TEST_PID + "'>\n"
            + "     <rel:isMemberOf rdf:resource='info:fedora/test:X'/>\n"
            + "  </rdf:Description>\n"
            + "</rdf:RDF>";

    private final String tmpDir = System.getProperty("java.io.tmpdir");

    @Test
    public void testValidUrls() throws Exception {
        String[] urls = {"http://localhost",
                         "http://localhost:8080",
                         "uploaded:///tmp/foo.xml"};

        for (String url : urls) {
            ValidationUtility.validateURL(url);
        }
    }

    @Test(expected=ValidationException.class)
    public void testInvalidUrls() throws Exception {
        String[] urls = {"", "a",
                         "temp:///etc/passwd",
                         "copy:///etc/passwd",
                         "temp://" + tmpDir + "/../etc/passwd",
                         "temp://" + tmpDir + "/../../etc/passwd",
                         "file:///etc/passwd",
                         "file:/etc/passwd",
                         "/etc/passwd",
                         "../../etc/passwd"};

        for (String url : urls) {
            ValidationUtility.validateURL(url);
        }
    }

    @Test(expected=NullPointerException.class)
    public void testValidatePolicyParserNotSet()
            throws IOException, SAXException, ValidationException {
        validatePolicy(null, POLICY_GOODENOUGH);
    }

    @Test(expected=ValidationException.class)
    public void testValidatePolicyBad()
            throws IOException, SAXException, ValidationException {
        validatePolicy(new MockPolicyParser(), POLICY_QUESTIONABLE);
    }

    @Test
    public void testValidatePolicyGood()
            throws IOException, SAXException, ValidationException {
        validatePolicy(new MockPolicyParser(), POLICY_GOODENOUGH);
    }

    @Test(expected=ValidationException.class)
    public void testValidateRelsExtBad() throws ValidationException {
        validateRelsExt("");
    }

    @Test
    public void testValidateRelsExtGood() throws ValidationException {
        validateRelsExt(RELSEXT_GOOD);
    }

    @Test
    public void testValidateReservedNone() throws ValidationException {
        validateReserved(null, new String[] { });
    }

    @Test(expected=ValidationException.class)
    public void testValidateReservedPolicyBad()
            throws IOException, SAXException, ValidationException {
        validateReserved(new MockPolicyParser(),
                         new String[] { "POLICY", POLICY_QUESTIONABLE });
    }

    @Test
    public void testValidateReservedPolicyGood()
            throws IOException, SAXException, ValidationException {
        validateReserved(new MockPolicyParser(),
                         new String[] { "POLICY", POLICY_GOODENOUGH });
    }

    @Test(expected=ValidationException.class)
    public void testValidateReservedRelsExtBad()
            throws IOException, SAXException, ValidationException {
        validateReserved(new MockPolicyParser(),
                         new String[] { "RELS-EXT", "" });
    }

    @Test
    public void testValidateReservedRelsExtGood()
            throws IOException, SAXException, ValidationException {
        validateReserved(new MockPolicyParser(),
                         new String[] { "RELS-EXT", RELSEXT_GOOD });
    }

    @Test
    public void testValidateReservedBothGood()
            throws IOException, SAXException, ValidationException {
        validateReserved(new MockPolicyParser(),
                         new String[] { "POLICY", POLICY_GOODENOUGH,
                                        "RELS-EXT", RELSEXT_GOOD });
    }

    private static void validatePolicy(PolicyParser parser, String policy)
            throws IOException, SAXException, ValidationException {
        ValidationUtility.setPolicyParser(parser);
        ValidationUtility.validateReservedDatastream(PID.getInstance(TEST_PID),
                                                     "POLICY",
                                                     StreamUtility.getStream(policy));
    }

    private static void validateRelsExt(String relsExt)
            throws ValidationException {
        ValidationUtility.validateReservedDatastream(PID.getInstance(TEST_PID),
                                                     "RELS-EXT",
                                                     StreamUtility.getStream(relsExt));
    }

    private static void validateReserved(PolicyParser parser, String[] dsData)
            throws ValidationException {
        ValidationUtility.setPolicyParser(parser);
        ValidationUtility.validateReservedDatastreams(getDOReader(dsData));
    }

    private static DOReader getDOReader(String[] dsData) {
        MockRepositoryReader repo = new MockRepositoryReader();
        try {
            DigitalObject obj = new BasicDigitalObject();
            obj.setPid(TEST_PID);
            for (int i = 0; i < dsData.length; i+=2) {
                ObjectBuilder.addXDatastream(obj, dsData[i], dsData[i+1]);
            }
            repo.putObject(obj);
            DOReader reader = repo.getReader(false, ReadOnlyContext.EMPTY, TEST_PID);
            return reader;
        } catch (Exception wontHappen) {
            throw new FaultException(wontHappen);
        }
    }

    public static junit.framework.Test suite() {
        return new JUnit4TestAdapter(ValidationUtilityTest.class);
    }
}

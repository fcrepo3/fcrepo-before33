
package fedora.server.journal.helpers;

import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;

import fedora.server.journal.JournalConstants;
import fedora.server.journal.JournalException;

/**
 * Test cases for the ParameterHelper class.
 * 
 * @author Jim Blake
 */
public class TestParameterHelper
        extends TestCase {

    private static final String PARAMETER_NAME = "name";

    private Map parameters;

    public TestParameterHelper(String name) {
        super(name);
    }

    @Override
    public void setUp() {
        parameters = new HashMap();
    }

    public void testGetOptionalBooleanParameter_NullParameters()
            throws JournalException {
        try {
            ParameterHelper.getOptionalBooleanParameter(null,
                                                        PARAMETER_NAME,
                                                        false);
            fail("Expected a NullPointerException");
        } catch (NullPointerException e) {
            // expected the exception
        }
    }

    public void testGetOptionalBooleanParameter_NullParameterName()
            throws JournalException {
        try {
            ParameterHelper
                    .getOptionalBooleanParameter(parameters, null, false);
            fail("Expected a NullPointerException");
        } catch (NullPointerException e) {
            // expected the exception
        }
    }

    public void testGetOptionalBooleanParameter_ValueTrue()
            throws JournalException {
        parameters.put(PARAMETER_NAME, JournalConstants.VALUE_TRUE);
        boolean result =
                ParameterHelper.getOptionalBooleanParameter(parameters,
                                                            PARAMETER_NAME,
                                                            false);
        assertEquals(true, result);
    }

    public void testGetOptionalBooleanParameter_ValueFalse()
            throws JournalException {
        parameters.put(PARAMETER_NAME, JournalConstants.VALUE_FALSE);
        boolean result =
                ParameterHelper.getOptionalBooleanParameter(parameters,
                                                            PARAMETER_NAME,
                                                            true);
        assertEquals(false, result);
    }

    public void testGetOptionalBooleanParameter_NoValue()
            throws JournalException {
        boolean result1 =
                ParameterHelper.getOptionalBooleanParameter(parameters,
                                                            PARAMETER_NAME,
                                                            true);
        assertEquals(true, result1);

        boolean result2 =
                ParameterHelper.getOptionalBooleanParameter(parameters,
                                                            PARAMETER_NAME,
                                                            false);
        assertEquals(false, result2);
    }

    public void testGetOptionalBooleanParameter_InvalidValue() {
        parameters.put(PARAMETER_NAME, "BOGUS");
        try {
            ParameterHelper.getOptionalBooleanParameter(parameters,
                                                        PARAMETER_NAME,
                                                        false);
            fail("Expected a JournalException");
        } catch (JournalException e) {
            // expected the exception
        }
    }
}

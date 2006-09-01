/*
 * -----------------------------------------------------------------------------
 *
 * <p><b>License and Copyright: </b>The contents of this file are subject to the
 * Educational Community License (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License
 * at <a href="http://www.opensource.org/licenses/ecl1.txt">
 * http://www.opensource.org/licenses/ecl1.txt.</a></p>
 *
 * <p>Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License for
 * the specific language governing rights and limitations under the License.</p>
 *
 * <p>The entire file consists of original code.  Copyright &copy; 2002-2006 by 
 * The Rector and Visitors of the University of Virginia and Cornell University.
 * All rights reserved.</p>
 *
 * -----------------------------------------------------------------------------
 */

package fedora.server.journal.helpers;

import java.util.HashMap;
import java.util.Map;

import fedora.server.journal.JournalConstants;
import fedora.server.journal.JournalException;
import junit.framework.TestCase;

/**
 * 
 * <p>
 * <b>Title:</b> TestParameterHelper.java
 * </p>
 * <p>
 * <b>Description:</b> Test cases for the ParameterHelper class.
 * </p>
 * 
 * @author jblake@cs.cornell.edu
 * @version $Id$
 */

public class TestParameterHelper extends TestCase {
    private static final String PARAMETER_NAME = "name";

    private Map parameters;

    public TestParameterHelper(String name) {
        super(name);
    }

    public void setUp() {
        parameters = new HashMap();
    }

    public void testGetOptionalBooleanParameter_NullParameters()
            throws JournalException {
        try {
            ParameterHelper.getOptionalBooleanParameter(null, PARAMETER_NAME,
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
        boolean result = ParameterHelper.getOptionalBooleanParameter(
                parameters, PARAMETER_NAME, false);
        assertEquals(true, result);
    }

    public void testGetOptionalBooleanParameter_ValueFalse()
            throws JournalException {
        parameters.put(PARAMETER_NAME, JournalConstants.VALUE_FALSE);
        boolean result = ParameterHelper.getOptionalBooleanParameter(
                parameters, PARAMETER_NAME, true);
        assertEquals(false, result);
    }

    public void testGetOptionalBooleanParameter_NoValue()
            throws JournalException {
        boolean result1 = ParameterHelper.getOptionalBooleanParameter(
                parameters, PARAMETER_NAME, true);
        assertEquals(true, result1);

        boolean result2 = ParameterHelper.getOptionalBooleanParameter(
                parameters, PARAMETER_NAME, false);
        assertEquals(false, result2);
    }

    public void testGetOptionalBooleanParameter_InvalidValue() {
        parameters.put(PARAMETER_NAME, "BOGUS");
        try {
            ParameterHelper.getOptionalBooleanParameter(parameters,
                    PARAMETER_NAME, false);
            fail("Expected a JournalException");
        } catch (JournalException e) {
            // expected the exception
        }
    }
}

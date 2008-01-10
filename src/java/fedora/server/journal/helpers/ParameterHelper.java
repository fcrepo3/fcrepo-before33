/* The contents of this file are subject to the license and copyright terms
 * detailed in the license directory at the root of the source tree (also 
 * available online at http://www.fedora.info/license/).
 */

package fedora.server.journal.helpers;

import java.util.Map;

import fedora.server.journal.JournalConstants;
import fedora.server.journal.JournalException;

/**
 * A collection of utility methods to help the Journal classes to read 
 * parameter values.
 * 
 * @author Jim Blake
 */
public class ParameterHelper
        implements JournalConstants {

    private ParameterHelper() {
        // no need to instantiate - all methods are static.
    }

    /**
     * Get an optional boolean parameter. If not found, use the default value.
     * 
     * @throws JournalException
     *         if a value is supplied that is neither "true" nor "false".
     * @throws NullPointerException
     *         if either 'parameters' or 'parameterName' is null.
     */
    public static boolean getOptionalBooleanParameter(Map parameters,
                                                      String parameterName,
                                                      boolean defaultValue)
            throws JournalException {
        validateParameters(parameters);
        validateParameterName(parameterName);

        String string = (String) parameters.get(parameterName);
        if (string == null) {
            return defaultValue;
        } else if (string.equals(VALUE_FALSE)) {
            return false;
        } else if (string.equals(VALUE_TRUE)) {
            return true;
        } else {
            throw new JournalException("'" + parameterName
                    + "' parameter must be '" + VALUE_FALSE + "'(default) or '"
                    + VALUE_TRUE + "'");
        }

    }

    private static void validateParameters(Map parameters) {
        if (parameters == null) {
            throw new NullPointerException("'parameters' may not be null.");
        }
    }

    private static void validateParameterName(String parameterName) {
        if (parameterName == null) {
            throw new NullPointerException("'parameterName' may not be null.");
        }
    }

}

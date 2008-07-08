/* The contents of this file are subject to the license and copyright terms
 * detailed in the license directory at the root of the source tree (also 
 * available online at http://www.fedora.info/license/).
 */

package fedora.server.validation;

import java.net.MalformedURLException;
import java.net.URL;

import fedora.server.errors.ValidationException;
import fedora.server.storage.types.DatastreamManagedContent;

/**
 * Misc validation-related functions.
 * 
 * @author Chris Wilper
 * @author Edwin Shin
 * @version $Id$
 */
public abstract class ValidationUtility {

    /**
     * Validates the candidate URL. File URLs (e.g. file:///bar/baz) are
     * rejected as malformed.
     * 
     * @param url The URL to validate.
     * @throws ValidationException if the URL is malformed.
     */
    public static void validateURL(String url)
            throws ValidationException {
        try {
            URL candidate = new URL(url);
            if (candidate.getProtocol().equals("file")) {
                throw new ValidationException("Malformed URL: invalid protocol: " + url);
            }
        } catch (MalformedURLException e) {
            if (url.startsWith(DatastreamManagedContent.UPLOADED_SCHEME)) {
                return;
            }
            throw new ValidationException("Malformed URL: " + url, e);
        }
    }
}
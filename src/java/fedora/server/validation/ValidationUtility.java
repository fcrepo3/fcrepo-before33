package fedora.server.validation;

import java.net.*;

import fedora.server.errors.*;
import fedora.server.storage.types.DatastreamManagedContent;

/**
 * Misc validation-related functions.
 *
 * @author cwilper@cs.cornell.edu
 * @version $Id$
 */
public abstract class ValidationUtility {

    /**
     * Validates the candidate URL. File URLs (e.g. file:///bar/baz) are
     * rejected as malformed.
     * 
     * @param url The URL to validate.
     * @param canBeRelative No effect. All URLs must be absolute.
     * @throws ValidationException if the URL is malformed.
     */
    public static void validateURL(String url, boolean canBeRelative)
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
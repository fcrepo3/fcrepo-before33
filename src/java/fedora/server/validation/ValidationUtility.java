package fedora.server.validation;

import java.net.*;

import fedora.server.errors.*;
import fedora.server.storage.translation.*;

/**
 * Misc validation-related functions.
 *
 * @author cwilper@cs.cornell.edu
 * @version $Id$
 */
public abstract class ValidationUtility {

    public static void validateURL(String url, boolean canBeRelative) 
            throws ValidationException {
        try {
            URL goodURL = new URL(url);
        } catch (MalformedURLException murle) {
            if (url.startsWith("copy://") || url.startsWith("uploaded://")) return;
            if (!canBeRelative) throw new ValidationException("Malformed URL: " + url, murle);
            if (!url.startsWith(
                  DOTranslationUtility.s_relativeGetPattern.pattern()) &&
                !url.startsWith(
                  DOTranslationUtility.s_relativeSearchPattern.pattern())) {
                throw new ValidationException("Missing protocol or invalid "
                  + " relative URL.\n"
                  + " Relative repository URLs must start with 'fedora/get'"
                  + " or 'fedora/search' with NO leading slash.\n " 
                  + " URL is: " + url);
            }
        }
    }

}
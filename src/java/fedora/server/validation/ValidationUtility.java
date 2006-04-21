package fedora.server.validation;

import java.net.*;

import fedora.server.errors.*;


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
            if (url.startsWith("copy://") || url.startsWith("uploaded://") || url.startsWith("temp://")) return;
			if (fedora.server.Debug.DEBUG) {
				System.out.println("ValidationUtility.validateURL: malformed URL " + url);
			}
            throw new ValidationException("Malformed URL: " + url, murle);
        }
    }

}
package fedora.server.validation;

import java.net.*;

import fedora.server.errors.*;
import fedora.server.storage.translation.*;

/**
 * Misc validation-related functions.
 *
 * -----------------------------------------------------------------------------
 *
 * <p><b>License and Copyright: </b>The contents of this file are subject to the
 * Mozilla Public License Version 1.1 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License
 * at <a href="http://www.mozilla.org/MPL">http://www.mozilla.org/MPL/.</a></p>
 *
 * <p>Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License for
 * the specific language governing rights and limitations under the License.</p>
 *
 * <p>The entire file consists of original code.  Copyright &copy; 2002-2005 by The
 * Rector and Visitors of the University of Virginia and Cornell University.
 * All rights reserved.</p>
 *
 * -----------------------------------------------------------------------------
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
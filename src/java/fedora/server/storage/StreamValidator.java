/* The contents of this file are subject to the license and copyright terms
 * detailed in the license directory at the root of the source tree (also 
 * available online at http://www.fedora.info/license/).
 */

package fedora.server.storage;

import fedora.server.errors.ValidationException;

import java.io.InputStream;


/**
 *
 * <p><b>Title:</b> StreamValidator.java</p>
 * <p><b>Description:</b> </p>
 *
 * @author cwilper@cs.cornell.edu
 * @version $Id$
 */
public interface StreamValidator {

    public void validate(InputStream in, String validationType)
            throws ValidationException;

}

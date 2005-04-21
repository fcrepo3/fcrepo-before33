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

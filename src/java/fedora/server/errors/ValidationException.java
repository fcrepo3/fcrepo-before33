package fedora.server.errors;

/**
 *
 * <p><b>Title:</b> ValidationException.java</p>
 * <p><b>Description:</b> Signals an error while validating.</p>
 *
 * @author payette@cs.cornell.edu
 * @version $Id$
 */
public class ValidationException
        extends ObjectIntegrityException {

    public ValidationException(String bundleName, String code, String[] values,
            String[] details, Throwable cause) {
        super(bundleName, code, values, details, cause);
    }

    public ValidationException(String message) {
        super(message);
    }

    public ValidationException(String message, Throwable cause) {
        super(message, cause);
    }

}
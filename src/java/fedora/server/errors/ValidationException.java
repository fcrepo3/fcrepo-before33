package fedora.server.errors;

/**
 * Signals an error while validating.
 */
public class ValidationException 
        extends ObjectIntegrityException {

    public ValidationException(String bundleName, String code, String[] values,
            String[] details, Throwable cause) {
        super(bundleName, code, values, details, cause);
    }

}
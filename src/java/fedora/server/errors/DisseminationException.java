package fedora.server.errors;

/**
 *
 * <p>Title: DisseminationException.java</p>
 * <p>Description: Signals an error in processing a dissemination request.</p>
 *
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 * @author Ross Wayland
 * @version 1.0
 */
public class DisseminationException extends ServerException {

    /**
     * Creates a DisseminationException.
     *
     * @param message An informative message explaining what happened and
     *                (possibly) how to fix it.
     */
    public DisseminationException(String message) {
        super(null, message, null, null, null);
    }

    public DisseminationException(String bundleName, String code, String[] values,
        String[] details, Throwable cause) {
    super(bundleName, code, values, details, cause);
    }

}
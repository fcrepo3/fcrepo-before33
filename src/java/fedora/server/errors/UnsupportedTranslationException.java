package fedora.server.errors;

/**
 *
 * <p><b>Title:</b> UnsupportedTranslationException.java</p>
 * <p><b>Description:</b> Thrown when some format+encoding pair isn't supported
 * by a translator.</p>
 *
 * @author cwilper@cs.cornell.edu
 * @version $Id$
 */
public class UnsupportedTranslationException
        extends ServerException {

    /**
     * Creates an UnsupportedTranslationException.
     *
     * @param message An informative message explaining what happened and
     *                (possibly) how to fix it.
     */
    public UnsupportedTranslationException(String message) {
        super(null, message, null, null, null);
    }

    public UnsupportedTranslationException(String bundleName, String code,
            String[] replacements, String[] details, Throwable cause) {
        super(bundleName, code, replacements, details, cause);
    }

}
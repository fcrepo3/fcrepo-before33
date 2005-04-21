package fedora.server.errors;

/**
 *
 * <p><b>Title:</b> RepositoryConfigurationException.java</p>
 * <p><b>Description:</b> An exception indicating a low-level configuration or
 * related problem with the repository software.  This is likely due
 * to a bad installation.</p>
 *
 * @author cwilper@cs.cornell.edu
 * @version $Id$
 */
public class RepositoryConfigurationException
        extends ServerException {

    /**
     * Creates a RepositoryConfiguration Exception.
     *
     * @param message An informative message explaining what happened and
     *                (possibly) how to fix it.
     */
    public RepositoryConfigurationException(String message) {
        super(null, message, null, null, null);
    }

    public RepositoryConfigurationException(String bundleName, String code, String[] values,
            String[] details, Throwable cause) {
        super(bundleName, code, values, details, cause);
    }

}
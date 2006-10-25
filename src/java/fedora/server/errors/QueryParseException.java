package fedora.server.errors;

/**
 *
 * <p><b>Title:</b> QueryParseException.java</p>
 * <p><b>Description:</b> Thrown when a query is badly formed.</p>
 *
 * @author cwilper@cs.cornell.edu
 * @version $Id$
 */
public class QueryParseException
        extends ServerException {

	private static final long serialVersionUID = 1L;
	
    /**
     * Creates a QueryParseException.
     *
     * @param message An informative message explaining what happened and
     *                (possibly) how to fix it.
     */
    public QueryParseException(String message) {
        super(null, message, null, null, null);
    }

    public QueryParseException(String bundleName, String code,
            String[] replacements, String[] details, Throwable cause) {
        super(bundleName, code, replacements, details, cause);
    }

}
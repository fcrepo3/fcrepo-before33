package fedora.server.resourceIndex;

/**
 * @author eddie
 *
 */
public class RDQLQuery extends RIQuery {
    private final static String QUERY_LANGUAGE = "rdql";
    /**
     * @param queryString
     */
    public RDQLQuery(String queryString) {
        super(queryString, QUERY_LANGUAGE);
    }
}

package fedora.server.resourceIndex;

/**
 * @author eddie
 *
 */
public class ITQLQuery extends RIQuery {
    private final static String QUERY_LANGUAGE = "itql";
    /**
     * @param queryString
     */
    public ITQLQuery(String queryString) {
        super(queryString, QUERY_LANGUAGE);
    }
}

package fedora.server.resourceIndex;

/**
 * @author eddie
 *
 */
public abstract class RIQuery {
    private String queryLanguage;
    private String query;
    private boolean requiresCommitBeforeQuery = true;
    
    protected RIQuery(String queryString, String queryLanguage) {
        setQuery(queryString);
        setQueryLanguage(queryLanguage);
    }
    
    /**
     * @return Returns the query.
     */
    public String getQuery() {
        return query;
    }
    /**
     * @param query The query to set.
     */
    public void setQuery(String query) {
        this.query = query;
    }
    /**
     * @return Returns the queryLanguage.
     */
    public String getQueryLanguage() {
        return queryLanguage;
    }
    
    /**
     * @param queryLanguage The queryLanguage to set.
     */
    private void setQueryLanguage(String queryLanguage) {
        this.queryLanguage = queryLanguage;
    }
}

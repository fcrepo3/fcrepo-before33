package fedora.server.search;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.sql.Connection;
import java.sql.ResultSet;

import fedora.server.Logging;
import fedora.server.StdoutLogging;
import fedora.server.storage.ConnectionPool;
import fedora.server.storage.RepositoryReader;

/**
 * A FieldSearchResults object returned as the result of a 
 * FieldSearchSQLImpl search.
 * <p />
 * A FieldSearchResultSQLImpl is intended to be re-used in cases where
 * the results of a query require more than one call to the server.
 */
public class FieldSearchResultSQLImpl
        extends StdoutLogging
        implements FieldSearchResult {

    /* fields supporting public accessors */
    private ArrayList m_objectFields;
    private String m_token;
    private long m_cursor=-1;
    private long m_completeListSize=-1;
    private Date m_expirationDate;
  
    /* invariants */
    private ConnectionPool m_cPool;
    private RepositoryReader m_repoReader;
    private String[] m_resultFields;
    private int m_maxResults;
    private int m_maxSeconds;

    /* internal state */
    private ResultSet m_resultSet;
    private long m_nextCursor=0;

    /**
     * Construct a FieldSearchResultSQLImpl object.
     * <p />
     * Upon construction, a connection is obtained from the connectionPool,
     * and the query is executed.  (The connection will be returned to the
     * pool only after the last result has been obtained from the ResultSet,
     * the session is expired, or some non-recoverable error has occurred)
     * <p />
     * Once the ResultSet is obtained, one result is requested of it
     * (and remembered for use in step()); then the call returns.
     * 
     * @param cPool the connectionPool
     * @param repoReader the provider of object field information for results
     * @param resultFields which fields should be returned in results
     * @param maxResults how many results should be returned at one time.  This
     *        should be the smaller of a) the FieldSearchImpl's limit [the server
     *        limit] and b) the requested limit [the client limit]
     * @param query the end-user query
     * @param logTarget where to send log messages
     */
    protected FieldSearchResultSQLImpl(ConnectionPool cPool, 
            RepositoryReader repoReader, String[] resultFields, int maxResults,
            int maxSeconds, FieldSearchQuery query, Logging logTarget) {
        super(logTarget);
        m_cPool=cPool;
        m_repoReader=repoReader;
        m_resultFields=resultFields;
        m_maxResults=maxResults;
        m_maxSeconds=maxSeconds;
        
        // get the resultset... 
        // m_resultSet=resultSet;
        //m_objectFields=new ArrayList();
        // cache first result...if there is one
    }
    
    // cache one result...if there is one
    private boolean cacheOneResult() {
        return false;
        // m_nextObjectFields=getObjectFields...
    }
   
    // put the next chunk of results in, updating the fields appropriately.
    // if getToken() is null after this call, the resultSet was exhausted
    protected void step() {
        m_cursor=m_nextCursor;
        int count=0;
        
        // if the cached result doesn't exist, forget it... set the list
        // and token to empty
        
        
        
        
        // jump to next result in resultSet... if there is one, keep it
        // for next time and set:
        //   - a new token for this.
        //   - nextCursor=m_cursor+count+1
        // else
        //   - set token to null
        
        m_nextCursor=m_cursor+count+1;
    }

    public List objectFieldsList() {
        return m_objectFields;
    }

    public String getToken() {
        return m_token;
    }
    
    public long getCursor() {
        return m_cursor;
    }
    
    public long getCompleteListSize() {
        return m_completeListSize;
    }
    
    public Date getExpirationDate() {
        return m_expirationDate;
    }
    
}    


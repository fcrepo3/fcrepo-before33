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

    /* internal state */
    private ResultSet m_resultSet;
    private long m_nextCursor=0;

    protected FieldSearchResultSQLImpl(ConnectionPool cPool, 
            RepositoryReader repoReader, String[] resultFields, int maxResults,
            Logging logTarget) {
        super(logTarget);
        m_cPool=cPool;
        m_repoReader=repoReader;
        m_resultFields=resultFields;
        m_maxResults=maxResults;
        
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
    protected void nextChunk() {
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


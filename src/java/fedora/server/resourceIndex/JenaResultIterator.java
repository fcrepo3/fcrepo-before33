package fedora.server.resourceIndex;

import fedora.server.errors.ResourceIndexException;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.hp.hpl.jena.rdql.QueryResults;
import com.hp.hpl.jena.rdql.ResultBinding;

/**
 * @author eddie
 *
 */
public class JenaResultIterator implements RIResultIterator {
    private QueryResults m_results;
    private List m_names;
    
    public JenaResultIterator(QueryResults results) {
        m_results = results;
        m_names = m_results.getResultVars();
    }
    
    /* (non-Javadoc)
     * @see fedora.server.resourceIndex.RIResultIterator#hasNext()
     */
    public boolean hasNext() {
        return m_results.hasNext();
    }

    /* (non-Javadoc)
     * @see fedora.server.resourceIndex.RIResultIterator#next()
     */
    public Map next() {
        Map map = null;
        if (m_results.hasNext()) {
            map = new HashMap();
	        ResultBinding result = (ResultBinding)m_results.next();
	        Iterator iter = m_names.iterator();
	        String name;
	        while (iter.hasNext()) {
	            name = (String)iter.next();
	            map.put(name, result.getValue(name));
	        }
        }
		return map;
    }

    /* (non-Javadoc)
     * @see fedora.server.resourceIndex.RIResultIterator#names()
     */
    public List names() throws ResourceIndexException {
        return m_names;
    }

    /* (non-Javadoc)
     * @see fedora.server.resourceIndex.RIResultIterator#close()
     */
    public void close() throws ResourceIndexException {
        m_results.close();
    }
}

package fedora.server.resourceIndex;

import fedora.server.errors.ResourceIndexException;

import com.hp.hpl.jena.graph.Node;

import org.kowari.query.Answer;
import org.kowari.query.TuplesException;
import org.kowari.query.Variable;
import org.kowari.store.jena.JenaFactory;
import org.kowari.store.jrdf.JRDFFactory;

import org.jrdf.graph.SubjectNode;
import org.jrdf.graph.PredicateNode;
import org.jrdf.graph.ObjectNode; 

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author eddie
 *
 */
public class KowariResultIterator implements RIResultIterator {
    private Answer m_answer;
    private boolean m_hasNext = false;
    private List m_names;
    private int[] m_columns;
    
    public KowariResultIterator(Answer answer) throws ResourceIndexException {
        m_answer = answer;
        try {
            m_hasNext = m_answer.next();
        } catch (TuplesException e) {
            throw new ResourceIndexException(e.getMessage());
        }
    }
    
    /* (non-Javadoc)
     * @see fedora.server.resourceIndex.RIResultIterator#hasNext()
     */
    public boolean hasNext() {
        return m_hasNext;
    }

    /* (non-Javadoc)
     * @see fedora.server.resourceIndex.RIResultIterator#next()
     */
    public Map next() throws ResourceIndexException {
    	Map map = null;
        if (m_hasNext) {
	        try {
	            List names;
	            names = names();
	            map = new HashMap();
	            for (int i = 0; i < names.size(); i++) {
	                String name = (String)names.get(i);
	                JenaFactory jf = new JenaFactory();
	                jf.setJrdfFactory(new JRDFFactory());
	                Node node = null;
	                
	                //FIXME dirty hack
	                // How do we know the type of node we're working with???
	                Object answer = m_answer.getObject(i);
	                String answerClass = answer.getClass().getName();
	                if (answerClass.equals("org.kowari.query.rdf.URIReferenceImpl")) {
	                    node = jf.convertSubjectToNode((SubjectNode)answer);
	                } else if (answerClass.equals("org.kowari.query.rdf.LiteralImpl")) {
	                    node = jf.convertObjectToNode((ObjectNode)answer);
	                } else {
	                    throw new ResourceIndexException("Column mapping for " + name + " not found.");
	                }
	                       
	                /*
	                 * if only results were always s, p, o :P
	                switch(i) {
	                	case 0: node = jf.convertSubjectToNode((SubjectNode)m_answer.getObject(i)); break;
	                	case 1: node = jf.convertPredicateToNode((PredicateNode)m_answer.getObject(i)); break;
	                	case 2: node = jf.convertObjectToNode((ObjectNode)m_answer.getObject(i)); break;
	                	default: throw new ResourceIndexException("Column mapping for " + name + " not found.");
	                }
	                */
	                map.put(name, new KowariValue(node));
	            }
	            m_hasNext = m_answer.next();
	            return map;
	        } catch (TuplesException e) {
	            throw new ResourceIndexException(e.getMessage());
	        }
        }
        return map;
    }

    /* (non-Javadoc)
     * @see fedora.server.resourceIndex.RIResultIterator#names()
     */
    public List names() throws ResourceIndexException {
        if (m_names == null) {
            m_names = new ArrayList();
            Variable[] v = m_answer.getVariables();
            if (v != null) {
            	m_columns = new int[v.length];
                for (int i = 0; i < v.length; i++) {
                    m_names.add(v[i].getName());
                    try {
						m_columns[i] = m_answer.getColumnIndex(v[i]);
					} catch (TuplesException e) {
						throw new ResourceIndexException(e.getMessage());
					}
                }
            }
        }
        return m_names;
    }

    /* (non-Javadoc)
     * @see fedora.server.resourceIndex.RIResultIterator#close()
     */
    public void close() {
        try {
            m_answer.close();
        } catch (TuplesException e) {
            e.printStackTrace();
        }
    }

}

package fedora.server.resourceIndex;

import java.io.InputStream;
import java.io.OutputStream;

import fedora.server.errors.ResourceIndexException;
import fedora.server.errors.UnsupportedQueryLanguageException;

import org.kowari.itql.ItqlInterpreterBean;
import org.kowari.query.Answer;
import org.kowari.server.Session;
import org.kowari.store.jena.KowariQueryEngine;
import org.kowari.store.jena.RdqlQuery;
import org.kowari.itql.ItqlInterpreterException;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ResourceFactory;
import com.hp.hpl.jena.rdql.*;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.HashSet;
import java.util.Set;

/**
 * @author eddie
 *  
 */
public class KowariRIStore implements RIStore {
	private Session m_session;
	private Model m_model;
	private List m_statements;
	private final static int FLUSH_THRESHOLD = 30000;
	private URI m_fullTextModelURI;
	
	//URI rdql = new URI("http://jena.hpl.hp.com/2003/07/query/RDQL");
	private Set m_supportedQueryLanguages;
	
	/**
	 * This constructor disables full text indexing of literals.
	 * @param session
	 * @param model
	 */
	public KowariRIStore(Session session, Model model) {
	    this(session, model, null);
	}
    
    public KowariRIStore(Session session, Model model, URI fullTextModelURI) {
        m_session = session;
        m_model = model;
        m_statements = new ArrayList();
        m_fullTextModelURI = fullTextModelURI;
        m_supportedQueryLanguages = new HashSet();
        m_supportedQueryLanguages.add("rdql");
        m_supportedQueryLanguages.add("itql");
    }
	
	/* (non-Javadoc)
     * @see fedora.server.resourceIndex.RIStore#getSupportedQueryLanguages()
     */
    public Set getSupportedQueryLanguages() {
        return m_supportedQueryLanguages;
    }

	/* (non-Javadoc)
	 * @see fedora.server.resourceIndex.RIStore#read(java.io.InputStream, java.lang.String)
	 */
	public void read(InputStream rdfxml, String base) {
		m_model.read(rdfxml, base);
	}
	
    /* (non-Javadoc)
     * @see fedora.server.resourceIndex.RIStore#executeQuery(fedora.server.resourceIndex.RIQuery)
     */
    public RIResultIterator executeQuery(RIQuery query) throws ResourceIndexException {
        String queryLanguage = query.getQueryLanguage();
        if (m_supportedQueryLanguages.contains(queryLanguage) && queryLanguage.equals("rdql")) {
            flush();
            return executeQuery((RDQLQuery)query);
        } else if (m_supportedQueryLanguages.contains(queryLanguage) && queryLanguage.equals("itql")) {
            flush();
            return executeQuery((ITQLQuery)query);
        } else {
            throw new UnsupportedQueryLanguageException(queryLanguage + " is not supported.");
        }
    }
    
    /* (non-Javadoc)
	 * @see fedora.server.resourceIndex.RIStore#insert(java.lang.String, java.lang.String, java.lang.String)
	 */
	public void insert(String subject, String predicate, String object) {
	    m_statements.add(ResourceFactory.createStatement(m_model.createResource(subject),
	                                                     m_model.createProperty(predicate), 
	                                                     m_model.createResource(object)));
	    flushAtThreshold();
	}

	/* (non-Javadoc)
	 * @see fedora.server.resourceIndex.RIStore#insertLiteral(java.lang.String, java.lang.String, java.lang.String)
	 */
	public void insertLiteral(String subject, String predicate, String object) {
	    m_statements.add(ResourceFactory.createStatement(m_model.createResource(subject),
                                                         m_model.createProperty(predicate), 
                                                         m_model.createLiteral(object)));
		
	    flushAtThreshold();
		insertLiteralIntoFullTextModel(subject, predicate, object);
	}

	/* (non-Javadoc)
	 * @see fedora.server.resourceIndex.RIStore#insertTypedLiteral(java.lang.String, java.lang.String, java.lang.String, java.lang.String)
	 */
	public void insertTypedLiteral(String subject, String predicate, String object, String datatype) {
	    m_statements.add(ResourceFactory.createStatement(m_model.createResource(subject),
                                                         m_model.createProperty(predicate), 
                                                         m_model.createTypedLiteral(object, datatype)));
	    
	    flushAtThreshold();
		insertLiteralIntoFullTextModel(subject, predicate, object);
	}

	/* (non-Javadoc)
	 * @see fedora.server.resourceIndex.RIStore#insertLocalLiteral(java.lang.String, java.lang.String, java.lang.String, java.lang.String)
	 */
	public void insertLocalLiteral(String subject, String predicate, String object, String language) {
	    m_statements.add(ResourceFactory.createStatement(m_model.createResource(subject),
                										 m_model.createProperty(predicate), 
                                                         m_model.createLiteral(object, language)));

	    flushAtThreshold();
		insertLiteralIntoFullTextModel(subject, predicate, object);
	}
	
	/* (non-Javadoc)
	 * @see fedora.server.resourceIndex.RIStore#write(java.io.OutputStream)
	 */
	public void write(OutputStream rdfxml) {
        flush();
		m_model.write(rdfxml);
	}
    
    public void commit() {
        flush();
    }
    
    private JenaResultIterator executeQuery(RDQLQuery query) {
	    RdqlQuery q = new RdqlQuery(query.getQuery());
		q.setSource(m_model);
		QueryExecution qe = new KowariQueryEngine(q);
		QueryResults results = qe.exec();
		return new JenaResultIterator(results);
	}
	
	private KowariResultIterator executeQuery(ITQLQuery query) throws ResourceIndexException {
	    try {
			ItqlInterpreterBean interpreter = new ItqlInterpreterBean(m_session, null);
			Answer answer = interpreter.executeQuery(query.getQuery());
			return new KowariResultIterator(answer);
		} catch (ItqlInterpreterException iie) {
		    iie.printStackTrace();
			throw new ResourceIndexException(iie.getMessage());
		}
	}
	
	public void insertLiteralIntoFullTextModel(String subject, String predicate, String object) {

//      FIXME Disabled until performance problems are addressed -eddie
//		if (m_fullTextModelURI == null) {
//		    return;
//		}
//		
//		try {
//			URI s = new URI(subject);
//			URI p = new URI(predicate);			
//		    Triple t = new TripleImpl(new URIReferenceImpl(s),
//		            				  new URIReferenceImpl(p),
//		            				  new LiteralImpl(object));
//		    Set fullTextStatements = new HashSet();
//		    fullTextStatements.add(t);
//            m_session.insert(m_fullTextModelURI, fullTextStatements);
//		} catch (URISyntaxException ue) {
//		    // TODO Auto-generated catch block
//            ue.printStackTrace();
//        } catch (QueryException qe) {
//            // TODO Auto-generated catch block
//            qe.printStackTrace();
//        }
	}
	
	private void flushAtThreshold() {
	    if (m_statements.size() >= FLUSH_THRESHOLD) {
	        flush();
	    }
	}
	
	private void flush() {
        m_model.add(m_statements);
        m_statements.clear();
	}
}
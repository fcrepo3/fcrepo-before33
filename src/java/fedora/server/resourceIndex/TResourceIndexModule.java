package fedora.server.resourceIndex;

import java.io.*;
import java.util.*;

import org.trippi.*;
import org.jrdf.graph.*;

import fedora.server.*;
import fedora.server.errors.*;
import fedora.server.storage.types.*;

public class TResourceIndexModule extends Module 
                                implements TResourceIndex {

    private TriplestoreConnector m_conn;
    private TResourceIndex m_impl;

	public TResourceIndexModule(Map moduleParameters, Server server, String role) 
	throws ModuleInitializationException {
		super(moduleParameters, server, role);
	}

	public void postInitModule() throws ModuleInitializationException {
		logConfig("TResourceIndexModule: hello");
		// Parameter validation
		int level;
		if (getParameter("level")==null) {
			throw new ModuleInitializationException(
                    "level parameter must be specified.", getRole());
        } else {
        	try {
                level = Integer.parseInt(getParameter("level"));
                if (level < 1 || level > 3) {
                	throw new NumberFormatException();
                }
    		} catch (NumberFormatException nfe) {
    			throw new ModuleInitializationException(
                        "level parameter must have value 1, 2, or 3.", getRole());
    		}
        }
        String datastoreId = getParameter("datastoreId");
        if (datastoreId == null || datastoreId.equals("")) {
            throw new ModuleInitializationException(
                      "datastoreId parameter must be specified.", getRole());
        }
        Parameterized conf = getServer().getDatastoreConfig(datastoreId);
        if (conf == null) {
            throw new ModuleInitializationException(
                      "No such datastore: " + datastoreId, getRole());
        }
        Map map = conf.getParameters();
        String connectorClassName = (String) map.get("connectorClassName");
        if (connectorClassName == null || connectorClassName.equals("")) {
            throw new ModuleInitializationException(
                      "Datastore \"" + datastoreId + "\" must specify a "
                      + "connectorClassName", getRole());
        }
        // parmas ok, let's init the triplestore
        try {
            m_conn = TriplestoreConnector.init(connectorClassName,
                                               map);
            m_impl = new TResourceIndexImpl(m_conn, this); 
        } catch (TrippiException e) {
            throw new ModuleInitializationException("Error initializing "
                    + "triplestore connector.", getRole(), e);
        } catch (ClassNotFoundException e) {
            throw new ModuleInitializationException("Connector class \"" 
                    + connectorClassName + "\" not in classpath.", getRole(), e);
        }
    }

    public void shutdownModule() throws ModuleShutdownException {
        try {
            if (m_conn != null) m_conn.close();
        } catch (TrippiException e) {
            throw new ModuleShutdownException("Error closing triplestore "
                    + "connector", getRole(), e);
        }
    }

//// from TriplestoreReader interface


    public void setAliasMap(Map aliasToPrefix) throws TrippiException {
        m_impl.setAliasMap(aliasToPrefix);
    }

    public Map getAliasMap() throws TrippiException {
        return m_impl.getAliasMap();
    }

    public TupleIterator findTuples(String queryLang,
                                    String tupleQuery,
                                    int limit,
                                    boolean distinct) throws TrippiException {
        return m_impl.findTuples(queryLang, tupleQuery, limit, distinct);
    }

    public int countTuples(String queryLang,
                           String tupleQuery,
                           int limit,
                           boolean distinct) throws TrippiException {
        return m_impl.countTuples(queryLang, tupleQuery, limit, distinct);
    }

    public TripleIterator findTriples(String queryLang,
                                      String tupleQuery,
                                      int limit,
                                      boolean distinct) throws TrippiException {
        return m_impl.findTriples(queryLang, tupleQuery, limit, distinct);
    }

    public int countTriples(String queryLang,
                            String tupleQuery,
                            int limit,
                            boolean distinct) throws TrippiException {
        return m_impl.countTriples(queryLang, tupleQuery, limit, distinct);
    }

    public TripleIterator findTriples(SubjectNode subject,
                                      PredicateNode predicate,
                                      ObjectNode object,
                                      int limit) throws TrippiException {
        return m_impl.findTriples(subject, predicate, object, limit);
    }

    public int countTriples(SubjectNode subject,
                            PredicateNode predicate,
                            ObjectNode object,
                            int limit) throws TrippiException {
        return m_impl.countTriples(subject, predicate, object, limit);
    }

    public TripleIterator findTriples(String queryLang,
                                      String tupleQuery,
                                      String tripleTemplate,
                                      int limit,
                                      boolean distinct) throws TrippiException {
        return m_impl.findTriples(queryLang, tupleQuery, tripleTemplate, limit, distinct);
    }

    public int countTriples(String queryLang,
                            String tupleQuery,
                            String tripleTemplate,
                            int limit,
                            boolean distinct) throws TrippiException {
        return m_impl.countTriples(queryLang, tupleQuery, tripleTemplate, limit, distinct);
    }

    public void dump(OutputStream out) throws IOException,
                                              TrippiException {
        m_impl.dump(out);
    }

    public String[] listTupleLanguages() {
        return m_impl.listTupleLanguages();
    }

    public String[] listTripleLanguages() {
        return m_impl.listTripleLanguages();
    }

    public void close() throws TrippiException {
        // nope
    }

///////////////////////////////////////////////////// 

	public int getIndexLevel() {
        return m_impl.getIndexLevel();   // do this, it's easy!!!
    }
	
	public void addDigitalObject(DigitalObject digitalObject) 
	        throws ResourceIndexException {
        
    }
	
	public void addDatastream(DigitalObject digitalObject, String datastreamID) { } //throws ResourceIndexException;
	
	public void addDisseminator(DigitalObject digitalObject, String disseminatorID) { } //throws ResourceIndexException;
	
	public void modifyDigitalObject(DigitalObject digitalObject) { } //throws ResourceIndexException;
	
	public void modifyDatastream(Datastream ds) { } //;
	
	public void modifyDisseminator(Disseminator diss) { } //;
	
	public void deleteDigitalObject(String pid) { } //;
	
	public void deleteDatastream(Datastream ds) { } //;
	
	public void deleteDisseminator(Disseminator diss) { } //;



}

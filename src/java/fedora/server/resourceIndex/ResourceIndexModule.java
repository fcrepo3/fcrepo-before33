package fedora.server.resourceIndex;

import java.io.*;
import java.util.*;

import org.trippi.*;
import org.jrdf.graph.*;

import fedora.server.*;
import fedora.server.errors.*;
import fedora.server.storage.ConnectionPool;
import fedora.server.storage.ConnectionPoolManager;
import fedora.server.storage.types.*;

public class ResourceIndexModule extends Module 
                                implements ResourceIndex {

    private TriplestoreConnector m_conn;
    private ResourceIndex m_resourceIndex;

	public ResourceIndexModule(Map moduleParameters, Server server, String role) 
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
        
        //
        // get connectionPool from ConnectionPoolManager
        //
        ConnectionPoolManager cpm=(ConnectionPoolManager) getServer().
                getModule("fedora.server.storage.ConnectionPoolManager");
        if (cpm==null) {
            throw new ModuleInitializationException(
                "ConnectionPoolManager module was required, but apparently has "
                + "not been loaded.", getRole());
        }
        String cPoolName=getParameter("connectionPool");
        ConnectionPool cPool=null;
        try {
            if (cPoolName==null) {
                logConfig("connectionPool unspecified; using default from "
                        + "ConnectionPoolManager.");
                cPool=cpm.getPool();
            } else {
                logConfig("connectionPool specified: " + cPoolName);
                cPool=cpm.getPool(cPoolName);
            }
        } catch (ConnectionPoolNotFoundException cpnfe) {
            throw new ModuleInitializationException("Could not find requested "
                    + "connectionPool.", getRole());
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
        // params ok, let's init the triplestore
        try {
            m_conn = TriplestoreConnector.init(connectorClassName,
                                               map);
            try {
                m_resourceIndex = new ResourceIndexImpl(level, m_conn, cPool, this);
            } catch (ResourceIndexException e) {
                throw new ModuleInitializationException("Error initializing "
                       + "connection pool.", getRole(), e);
            } 
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
        m_resourceIndex.setAliasMap(aliasToPrefix);
    }

    public Map getAliasMap() throws TrippiException {
        return m_resourceIndex.getAliasMap();
    }

    public TupleIterator findTuples(String queryLang,
                                    String tupleQuery,
                                    int limit,
                                    boolean distinct) throws TrippiException {
        return m_resourceIndex.findTuples(queryLang, tupleQuery, limit, distinct);
    }

    public int countTuples(String queryLang,
                           String tupleQuery,
                           int limit,
                           boolean distinct) throws TrippiException {
        return m_resourceIndex.countTuples(queryLang, tupleQuery, limit, distinct);
    }

    public TripleIterator findTriples(String queryLang,
                                      String tupleQuery,
                                      int limit,
                                      boolean distinct) throws TrippiException {
        return m_resourceIndex.findTriples(queryLang, tupleQuery, limit, distinct);
    }

    public int countTriples(String queryLang,
                            String tupleQuery,
                            int limit,
                            boolean distinct) throws TrippiException {
        return m_resourceIndex.countTriples(queryLang, tupleQuery, limit, distinct);
    }

    public TripleIterator findTriples(SubjectNode subject,
                                      PredicateNode predicate,
                                      ObjectNode object,
                                      int limit) throws TrippiException {
        return m_resourceIndex.findTriples(subject, predicate, object, limit);
    }

    public int countTriples(SubjectNode subject,
                            PredicateNode predicate,
                            ObjectNode object,
                            int limit) throws TrippiException {
        return m_resourceIndex.countTriples(subject, predicate, object, limit);
    }

    public TripleIterator findTriples(String queryLang,
                                      String tupleQuery,
                                      String tripleTemplate,
                                      int limit,
                                      boolean distinct) throws TrippiException {
        return m_resourceIndex.findTriples(queryLang, tupleQuery, tripleTemplate, limit, distinct);
    }

    public int countTriples(String queryLang,
                            String tupleQuery,
                            String tripleTemplate,
                            int limit,
                            boolean distinct) throws TrippiException {
        return m_resourceIndex.countTriples(queryLang, tupleQuery, tripleTemplate, limit, distinct);
    }

    public void dump(OutputStream out) throws IOException,
                                              TrippiException {
        m_resourceIndex.dump(out);
    }

    public String[] listTupleLanguages() {
        return m_resourceIndex.listTupleLanguages();
    }

    public String[] listTripleLanguages() {
        return m_resourceIndex.listTripleLanguages();
    }

    public void close() throws TrippiException {
        // nope
    }

///////////////////////////////////////////////////// 

	public int getIndexLevel() {
        return m_resourceIndex.getIndexLevel();   // do this, it's easy!!!
    }
	
	/* (non-Javadoc)
     * @see fedora.server.resourceIndex.ResourceIndex#addDigitalObject(fedora.server.storage.types.DigitalObject)
     */
    public void addDigitalObject(DigitalObject digitalObject) throws ResourceIndexException {
        m_resourceIndex.addDigitalObject(digitalObject);
    }

    /* (non-Javadoc)
     * @see fedora.server.resourceIndex.ResourceIndex#addDatastream(fedora.server.storage.types.DigitalObject, java.lang.String)
     */
    public void addDatastream(DigitalObject digitalObject, String datastreamID) throws ResourceIndexException {
        m_resourceIndex.addDatastream(digitalObject, datastreamID);
    }

    /* (non-Javadoc)
     * @see fedora.server.resourceIndex.ResourceIndex#addDisseminator(fedora.server.storage.types.DigitalObject, java.lang.String)
     */
    public void addDisseminator(DigitalObject digitalObject, String disseminatorID) throws ResourceIndexException {
        m_resourceIndex.addDisseminator(digitalObject, disseminatorID);
    }

    /* (non-Javadoc)
     * @see fedora.server.resourceIndex.ResourceIndex#modifyDigitalObject(fedora.server.storage.types.DigitalObject)
     */
    public void modifyDigitalObject(DigitalObject digitalObject) throws ResourceIndexException {
        m_resourceIndex.modifyDigitalObject(digitalObject);
    }

    /* (non-Javadoc)
     * @see fedora.server.resourceIndex.ResourceIndex#modifyDatastream(fedora.server.storage.types.Datastream)
     */
    public void modifyDatastream(DigitalObject digitalObject, String datastreamID) throws ResourceIndexException {
        m_resourceIndex.modifyDatastream(digitalObject, datastreamID);
    }

    /* (non-Javadoc)
     * @see fedora.server.resourceIndex.ResourceIndex#modifyDisseminator(fedora.server.storage.types.Disseminator)
     */
    public void modifyDisseminator(DigitalObject digitalObject, String disseminatorID) throws ResourceIndexException {
        m_resourceIndex.modifyDisseminator(digitalObject, disseminatorID);
    }

    /* (non-Javadoc)
     * @see fedora.server.resourceIndex.ResourceIndex#deleteDigitalObject(java.lang.String)
     */
    public void deleteDigitalObject(DigitalObject digitalObject) throws ResourceIndexException {
        m_resourceIndex.deleteDigitalObject(digitalObject);
    }

    /* (non-Javadoc)
     * @see fedora.server.resourceIndex.ResourceIndex#deleteDatastream(fedora.server.storage.types.Datastream)
     */
    public void deleteDatastream(DigitalObject digitalObject, String datastreamID) throws ResourceIndexException {
        m_resourceIndex.deleteDatastream(digitalObject, datastreamID);
    }

    /* (non-Javadoc)
     * @see fedora.server.resourceIndex.ResourceIndex#deleteDisseminator(fedora.server.storage.types.Disseminator)
     */
    public void deleteDisseminator(DigitalObject digitalObject, String disseminatorID) throws ResourceIndexException {
        m_resourceIndex.deleteDisseminator(digitalObject, disseminatorID);
    }   

}

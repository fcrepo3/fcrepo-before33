package fedora.server.resourceIndex;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;

import org.kowari.query.QueryException;
import org.kowari.query.rdf.Tucana;
import org.kowari.server.driver.SessionFactoryFinder;
import org.kowari.server.driver.SessionFactoryFinderException;
import org.kowari.server.local.LocalSessionFactory;
import org.kowari.store.DatabaseSession;
import org.kowari.store.jena.GraphKowariMaker;
import org.kowari.store.jena.ModelKowariMaker;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.shared.ReificationStyle;

import fedora.server.Module;
import fedora.server.Server;
import fedora.server.errors.ConnectionPoolNotFoundException;
import fedora.server.errors.ModuleInitializationException;
import fedora.server.errors.ResourceIndexException;
import fedora.server.storage.ConnectionPool;
import fedora.server.storage.ConnectionPoolManager;
import fedora.server.storage.types.Datastream;
import fedora.server.storage.types.DigitalObject;
import fedora.server.storage.types.Disseminator;

/**
 * @author Edwin Shin
 *
 */
public class KowariRIModule extends Module implements ResourceIndex {
	private static final String MODEL_NAME = "resourceIndex";
	private static final String FULL_TEXT_MODEL_NAME = MODEL_NAME +"-fullText";
	private static final String SERVER_NAME = "fedora";
	private ResourceIndex m_resourceIndex;
	private RIStore m_kowariRIStore;

	// i.e. http://tucana.org/tucana#LuceneModel
	private static final String TEXT_MODEL_TYPE = Tucana.NAMESPACE + "LuceneModel";
	/**
	 * @param moduleParameters
	 * @param server
	 * @param role
	 * @throws ModuleInitializationException
	 */
	public KowariRIModule(Map moduleParameters, Server server, String role) 
	throws ModuleInitializationException {
		super(moduleParameters, server, role);
	}
	
	public void postInitModule() throws ModuleInitializationException {
		logConfig("KowariRIModule: Hello, World!");
		
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
		
		String localServerPath;
		if (getParameter("localServerPath") == null) {
            throw new ModuleInitializationException(
                "localServerPath parameter must be specified.", getRole());
        } else {
        	// check path...does it exist/can we create? can we write?
        	localServerPath = getParameter("localServerPath");
        }
		
		DatabaseSession session;
		Model model;
		URI fullTextModelURI;
		try {
			// FIXME should get serverhost from fedora.fcfg? were the issues
		    // in kowari if serverhost changed ever resolved?
			String serverhost = "localhost";
			URI serverURI = new URI("rmi", serverhost, "/" + SERVER_NAME, null);
			fullTextModelURI = new URI(serverURI.toString() + "#" + FULL_TEXT_MODEL_NAME);
			File serverDir = new File(localServerPath + File.separator + MODEL_NAME);
			serverDir.mkdirs();
			LocalSessionFactory factory;
			factory = (LocalSessionFactory) SessionFactoryFinder.newSessionFactory(serverURI);
			if (factory.getDirectory() == null) {
			    factory.setDirectory(serverDir);
			}
			session = (DatabaseSession) factory.newSession();

			//add
			GraphKowariMaker graphMaker = new GraphKowariMaker(session,
					serverURI, ReificationStyle.Minimal);
			ModelKowariMaker modelMaker = new ModelKowariMaker(graphMaker);
			model = modelMaker.openModel(MODEL_NAME);
			
			// add the full-text model
			if (!session.modelExists(fullTextModelURI)) {
			    URI fullTextModelTypeURI = new URI(TEXT_MODEL_TYPE);
			    session.createModel(fullTextModelURI, fullTextModelTypeURI);
			}
		} catch (URISyntaxException e) {
			throw new ModuleInitializationException(e.getMessage(), getRole());
		} catch (SessionFactoryFinderException e2) {
			throw new ModuleInitializationException(e2.getMessage(), getRole());
		} catch (QueryException e3) {
			throw new ModuleInitializationException(e3.getMessage(), getRole());
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
		
		m_kowariRIStore = new KowariRIStore(session, model, fullTextModelURI);
		m_resourceIndex = new ResourceIndexImpl(level, m_kowariRIStore, this);
	}

    /* (non-Javadoc)
     * @see fedora.server.resourceIndex.ResourceIndex#getIndexLevel()
     */
    public int getIndexLevel() {
        return m_resourceIndex.getIndexLevel();
    }

    /* (non-Javadoc)
     * @see fedora.server.resourceIndex.ResourceIndex#executeQuery(fedora.server.resourceIndex.RIQuery)
     */
    public RIResultIterator executeQuery(RIQuery query) throws ResourceIndexException {
        return m_resourceIndex.executeQuery(query);
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
    public void modifyDatastream(Datastream ds) {
        m_resourceIndex.modifyDatastream(ds);
    }

    /* (non-Javadoc)
     * @see fedora.server.resourceIndex.ResourceIndex#modifyDisseminator(fedora.server.storage.types.Disseminator)
     */
    public void modifyDisseminator(Disseminator diss) {
        m_resourceIndex.modifyDisseminator(diss);
    }

    /* (non-Javadoc)
     * @see fedora.server.resourceIndex.ResourceIndex#deleteDigitalObject(java.lang.String)
     */
    public void deleteDigitalObject(String pid) {
        m_resourceIndex.deleteDigitalObject(pid);
    }

    /* (non-Javadoc)
     * @see fedora.server.resourceIndex.ResourceIndex#deleteDatastream(fedora.server.storage.types.Datastream)
     */
    public void deleteDatastream(Datastream ds) {
        m_resourceIndex.deleteDatastream(ds);
    }

    /* (non-Javadoc)
     * @see fedora.server.resourceIndex.ResourceIndex#deleteDisseminator(fedora.server.storage.types.Disseminator)
     */
    public void deleteDisseminator(Disseminator diss) {
        m_resourceIndex.deleteDisseminator(diss);
    }	
}

package fedora.server.resourceIndex;

import fedora.server.Logging;
import fedora.server.StdoutLogging;
import fedora.server.errors.ResourceIndexException;
import fedora.server.storage.ConnectionPool;
import fedora.server.storage.service.ServiceMapper;
import fedora.server.storage.types.*;
import fedora.server.utilities.DCFields;

import javax.wsdl.WSDLException;
import javax.wsdl.extensions.ExtensibilityElement;
import javax.wsdl.extensions.mime.MIMEContent;
import javax.wsdl.factory.*;
import javax.wsdl.xml.*;
import javax.wsdl.*;
import javax.xml.namespace.QName;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;

import org.jrdf.graph.ObjectNode;
import org.jrdf.graph.PredicateNode;
import org.jrdf.graph.SubjectNode;
import org.trippi.TripleFactory;
import org.trippi.TripleIterator;
import org.trippi.TriplestoreConnector;
import org.trippi.TriplestoreReader;
import org.trippi.TriplestoreWriter;
import org.trippi.TrippiException;
import org.trippi.TupleIterator;
import org.xml.sax.InputSource;

/**
 * Implementation of the ResourceIndex interface.
 * 
 * @author Edwin Shin
 *
 */
public class ResourceIndexImpl extends StdoutLogging implements ResourceIndex {
    // TODO how are we going to handle different indexing levels?
    // 		- initial index
    //		- latent indexing: "tagging" objects with current index level to support querying what needs indexing later
    //		- subsequent insert/edits/deletes
    //		- changes in levels
    //		- distinct levels or discrete mix & match (e.g., combinations of DC, REP & REP-DEP, RELS, etc.)
    private int m_indexLevel;
	private static final String FEDORA_URI_SCHEME = "info:fedora/";
    
    // For the database
    private ConnectionPool m_cPool;
    private Connection m_conn;
    private Statement m_statement;
    private ResultSet m_resultSet;
    
    // Triplestore (Trippi)
    private TriplestoreConnector m_connector;
    private TriplestoreReader m_reader;
    private TriplestoreWriter m_writer;
    private List m_tQueue;
    
    public ResourceIndexImpl(int indexLevel, 
                             TriplestoreConnector connector, 
                             ConnectionPool cPool, 
                             Logging target) throws ResourceIndexException {
        super(target);
        m_indexLevel = indexLevel;
        m_connector = connector;
        m_writer = m_connector.getWriter();
        m_reader = m_connector.getReader();
        m_tQueue = new ArrayList();
        m_cPool = cPool;
        try {
            m_conn = m_cPool.getConnection();
        } catch (SQLException e) {
            throw new ResourceIndexException("ResourceIndex Connection Pool " +
                                             "was unable to get a connection", e);
        }
    }

	/* (non-Javadoc)
	 * @see fedora.server.resourceIndex.ResourceIndex#addDigitalObject(fedora.server.storage.types.DigitalObject)
	 */
	public void addDigitalObject(DigitalObject digitalObject) throws ResourceIndexException {
	    String pid = digitalObject.getPid();
		String doIdentifier = getDOURI(digitalObject);
		
		// Insert basic system metadata
        queuePlainLiteralTriple(doIdentifier, LABEL_URI, digitalObject.getLabel());
        queuePlainLiteralTriple(doIdentifier, DATE_CREATED_URI, getDate(digitalObject.getCreateDate()));
        queuePlainLiteralTriple(doIdentifier, DATE_LAST_MODIFIED_URI, getDate(digitalObject.getLastModDate()));
		
		if (digitalObject.getOwnerId() != null) {
		    queuePlainLiteralTriple(doIdentifier, OWNER_ID_URI, digitalObject.getOwnerId());
		}
		queuePlainLiteralTriple(doIdentifier, CONTENT_MODEL_ID_URI, digitalObject.getContentModelId());
		queuePlainLiteralTriple(doIdentifier, STATE_URI, digitalObject.getState());
		
        addQueue(false);

		// handle type specific duties
		// TODO: if it turns out rdfType is the only "special" thing to do,
		// then we may as well use a getRDFType(fedoraObjectType) method instead
		int fedoraObjectType = digitalObject.getFedoraObjectType();
		String rdfType;
		switch (fedoraObjectType) {
			case DigitalObject.FEDORA_BDEF_OBJECT: 
				addBDef(digitalObject);
				break;
			case DigitalObject.FEDORA_BMECH_OBJECT: 
				addBMech(digitalObject);
				break;
			case DigitalObject.FEDORA_OBJECT: 
				addDataObject(digitalObject);
				break;
			default: throw new ResourceIndexException("Unknown DigitalObject type: " + fedoraObjectType);	
		}
		
		// Add datastreams
		Iterator it;
	    it = digitalObject.datastreamIdIterator();
		while (it.hasNext()) {
		    addDatastream(digitalObject, (String)it.next());
		}
		
		// Add disseminators
		it = digitalObject.disseminatorIdIterator();
		while (it.hasNext()) {
		    addDisseminator(digitalObject, (String)it.next());		    
		}
	}

	/* (non-Javadoc)
	 * @see fedora.server.resourceIndex.ResourceIndex#addDatastream(fedora.server.storage.types.Datastream)
	 */
	public void addDatastream(DigitalObject digitalObject, String datastreamID) throws ResourceIndexException {
	    Datastream ds = getLatestDatastream(digitalObject.datastreams(datastreamID));
	    String doURI = getDOURI(digitalObject);
	    String datastreamURI;
	    if (ds.DatastreamURI != null && !ds.DatastreamURI.equals("")) {
	        datastreamURI = ds.DatastreamURI;
	    } else {
	        datastreamURI = doURI + "/" + datastreamID;
	    }
        
        // TODO new datastream attribute
        //String[] altIDs = ds.DatastreamAltIDs;
        
        // Volatile Datastreams: False for datastreams that are locally managed 
        // (have a control group "M" or "I").
        String isVolatile = !(ds.DSControlGrp.equals("M") || ds.DSControlGrp.equals("I")) ? "true" : "false";

        queueTriple(doURI, HAS_REPRESENTATION_URI, datastreamURI);
        queuePlainLiteralTriple(datastreamURI, DATE_LAST_MODIFIED_URI, getDate(ds.DSCreateDT));
        queuePlainLiteralTriple(datastreamURI, DISSEMINATION_DIRECT_URI, "true");
        queuePlainLiteralTriple(datastreamURI, DISSEMINATION_VOLATILE_URI, isVolatile);
        addQueue(false);
        
		// handle special system datastreams: DC, METHODMAP, RELS-EXT
		if (datastreamID.equalsIgnoreCase("DC")) {
			addDublinCoreDatastream(digitalObject, ds);
        } else if (datastreamID.equalsIgnoreCase("DSINPUTSPEC")) { // which objs have this?
            addDSInputSpecDatastream(ds);   
		} else if (datastreamID.equalsIgnoreCase("EXT_PROPERTIES")) { // props
		    addExtPropertiesDatastream(ds);
        } else if (datastreamID.equalsIgnoreCase("METHODMAP")) { 
            addMethodMapDatastream(digitalObject, ds);
        } else if (datastreamID.equalsIgnoreCase("RELS-EXT")) {
            addRelsDatastream(ds);
        } else if (datastreamID.equalsIgnoreCase("SERVICE-PROFILE")) { 
            addServiceProfileDatastream(ds);
		} else if (datastreamID.equalsIgnoreCase("WSDL")) { 
		    addWSDLDatastream(digitalObject, ds);
		}		
	}

	/* (non-Javadoc)
	 * @see fedora.server.resourceIndex.ResourceIndex#addDisseminator(fedora.server.storage.types.Dissemination)
	 */
	public void addDisseminator(DigitalObject digitalObject, String disseminatorID) throws ResourceIndexException {
	    Disseminator diss = getLatestDisseminator(digitalObject.disseminators(disseminatorID));
	    String doIdentifier = getDOURI(digitalObject);
        String bMechPID = diss.bMechID;
        
        queueTriple(doIdentifier, USES_BMECH_URI, getDOURI(bMechPID));
	    DSBindingMap m = diss.dsBindMap; // is this needed???
	    String bDefPID = diss.bDefID;
	    
	    // insert representations
	    if (digitalObject.getFedoraObjectType() == DigitalObject.FEDORA_OBJECT) {
            String query = "SELECT riMethodPermutation.permutation, riMethodMimeType.mimeType " +
                           "FROM riMethodPermutation, riMethodMimeType, riMethodImpl " +
                           "WHERE riMethodPermutation.methodId = riMethodImpl.methodId " +
                           "AND riMethodImpl.methodImplId = riMethodMimeType.methodImplId " +
                           "AND riMethodImpl.bMechPid = '" + bMechPID + "'";
            Statement select;
            
            try {
                 select = m_conn.createStatement();
                 ResultSet rs = select.executeQuery(query);
                 String permutation, mimeType, rep;
                 while (rs.next()) {
                     permutation = rs.getString("permutation");
                     mimeType = rs.getString("mimeType");
                     rep = doIdentifier + "/" + bDefPID + "/" + permutation;
                     queueTriple(doIdentifier, HAS_REPRESENTATION_URI, rep);
                     // TODO mimetype as URI...what form???
                     queuePlainLiteralTriple(rep, 
                                             DISSEMINATION_MEDIA_TYPE_URI, 
                                             mimeType);
                     queuePlainLiteralTriple(rep, 
                                             DISSEMINATION_DIRECT_URI, 
                                             "false"); 
                 }
            } catch (SQLException e) {
                throw new ResourceIndexException(e.getMessage(), e);
            }
	    }

	    // TODO
		//m_store.insertLiteral(disseminatorIdentifier, DATE_LAST_MODIFIED_URI, getDate(diss.dissCreateDT));
		//m_store.insertLiteral(disseminatorIdentifier, STATE_URI, diss.dissState); // change to uri #active/#inactive
        //m_store.insert(disseminatorIdentifier, DISSEMINATION_TYPE_URI, diss.?);
        //m_store.insert(disseminatorIdentifier, DISSEMINATION_VOLATILE_URI, diss.?); // redirect, external, based on diss that depends on red/ext (true/false)
        addQueue(false);
    }

	/* (non-Javadoc)
	 * @see fedora.server.resourceIndex.ResourceIndex#modifyDigitalObject(fedora.server.storage.types.DigitalObject)
	 */
	public void modifyDigitalObject(DigitalObject digitalObject) throws ResourceIndexException {
		// FIXME simple, dumb way to modify
		deleteDigitalObject(digitalObject);
        addDigitalObject(digitalObject);        
	}

	/* (non-Javadoc)
	 * @see fedora.server.resourceIndex.ResourceIndex#modifyDatastream(fedora.server.storage.types.Datastream)
	 */
	public void modifyDatastream(DigitalObject digitalObject, String datastreamID) throws ResourceIndexException {
		deleteDatastream(digitalObject, datastreamID);
        addDatastream(digitalObject, datastreamID);
	}

	/* (non-Javadoc)
	 * @see fedora.server.resourceIndex.ResourceIndex#modifyDissemination(fedora.server.storage.types.Dissemination)
	 */
	public void modifyDisseminator(DigitalObject digitalObject, String disseminatorID) throws ResourceIndexException {
		deleteDisseminator(digitalObject, disseminatorID);
        addDisseminator(digitalObject, disseminatorID);
	}

	/* (non-Javadoc)
	 * @see fedora.server.resourceIndex.ResourceIndex#deleteDigitalObject(java.lang.String)
	 */
	public void deleteDigitalObject(DigitalObject digitalObject) throws ResourceIndexException {
        Iterator it;
        it = digitalObject.datastreamIdIterator();
        while (it.hasNext()) {
            deleteDatastream(digitalObject, (String)it.next());
        }
        
        // Add disseminators
        it = digitalObject.disseminatorIdIterator();
        while (it.hasNext()) {
            deleteDisseminator(digitalObject, (String)it.next());          
        }
        
        // Delete all statements where doURI is the subject
        String doURI = getDOURI(digitalObject);
        try {
            m_writer.delete(m_reader.findTriples(TripleFactory.createResource(doURI), null, null, 0), true);
        } catch (IOException e) {
            throw new ResourceIndexException(e.getMessage(), e);
        } catch (TrippiException e) {
            throw new ResourceIndexException(e.getMessage(), e);
        }
	}

	/* (non-Javadoc)
	 * @see fedora.server.resourceIndex.ResourceIndex#deleteDatastream(fedora.server.storage.types.Datastream)
	 */
	public void deleteDatastream(DigitalObject digitalObject, String datastreamID) throws ResourceIndexException {
	    Datastream ds = getLatestDatastream(digitalObject.datastreams(datastreamID));
        String doURI = getDOURI(digitalObject);
        String datastreamURI;
        if (ds.DatastreamURI != null && !ds.DatastreamURI.equals("")) {
            datastreamURI = ds.DatastreamURI;
        } else {
            datastreamURI = doURI + "/" + datastreamID;
        }
        
        // DELETE statements where datastreamURI is subject
        try {
            m_writer.delete(m_reader.findTriples(TripleFactory.createResource(datastreamURI), null, null, 0), true);
        } catch (IOException e) {
            throw new ResourceIndexException(e.getMessage(), e);
        } catch (TrippiException e) {
            throw new ResourceIndexException(e.getMessage(), e);
        }
        // FIXME handle special case datastreams, e.g. WSDL, METHODMAP
		
	}

	/* (non-Javadoc)
	 * @see fedora.server.resourceIndex.ResourceIndex#deleteDissemination(fedora.server.storage.types.Dissemination)
	 */
	public void deleteDisseminator(DigitalObject digitalObject, String disseminatorID) throws ResourceIndexException {
        Disseminator diss = getLatestDisseminator(digitalObject.disseminators(disseminatorID));
        String doIdentifier = getDOURI(digitalObject);
        
        String bDefPID = diss.bDefID;
        String bMechPID = diss.bMechID;
        
        // delete bMech reference: 
        try {
            m_writer.delete(m_reader.findTriples(TripleFactory.createResource(doIdentifier), 
                                                 TripleFactory.createResource(USES_BMECH_URI), 
                                                 TripleFactory.createResource(getDOURI(bMechPID)), 
                                                 0), 
                            true);
        } catch (IOException e) {
            throw new ResourceIndexException(e.getMessage(), e);
        } catch (TrippiException e) {
            throw new ResourceIndexException(e.getMessage(), e);
        }
        
        if (digitalObject.getFedoraObjectType() == DigitalObject.FEDORA_OBJECT) {
            // delete statement where rep is the subject
            String query = "SELECT permutation " +
                           "FROM riMethodPermutation, riMethodImpl " +
                           "WHERE riMethodPermutation.methodId = riMethodImpl.methodId " +
                           "AND riMethodImpl.bMechPid = '" + bMechPID + "'";
            Statement select;
            
            try {
                 select = m_conn.createStatement();
                 ResultSet rs = select.executeQuery(query);
                 String permutation, rep;
                 while (rs.next()) {
                     permutation = rs.getString("permutation");
                     rep = doIdentifier + "/" + bDefPID + "/" + permutation;
                     m_writer.delete(m_reader.findTriples(TripleFactory.createResource(rep), null, null, 0), false);
                 }
            } catch (SQLException e) {
                throw new ResourceIndexException(e.getMessage(), e);
            } catch (IOException e) {
                throw new ResourceIndexException(e.getMessage(), e);
            } catch (TrippiException e) {
                throw new ResourceIndexException(e.getMessage(), e);
            }
        }	
	}

	private void addBDef(DigitalObject bDef) throws ResourceIndexException {
		String doURI = getDOURI(bDef);
        queueTriple(doURI, RDF_TYPE_URI, BDEF_RDF_TYPE_URI);
		
		Datastream ds = getLatestDatastream(bDef.datastreams("METHODMAP"));
		MethodDef[] mdef = getMethodDefs(bDef.getPid(), ds);
		for (int i = 0; i < mdef.length; i++) {
            queuePlainLiteralTriple(doURI, DEFINES_METHOD_URI, mdef[i].methodName);
	        // m_store.insertLiteral(doIdentifier, "foo:methodLabel", mdef[i].methodLabel);
	    }
        addQueue(false);
	}
	
	private void addBMech(DigitalObject bMech) throws ResourceIndexException {
		String doURI = getDOURI(bMech);
        queueTriple(doURI, RDF_TYPE_URI, BMECH_RDF_TYPE_URI);
	
		String bDefPid = getBDefPid(bMech);
		queueTriple(doURI, IMPLEMENTS_BDEF_URI, getDOURI(bDefPid));
		addQueue(false);	
	}
	
	private void addDataObject(DigitalObject digitalObject) throws ResourceIndexException {
		String identifier = getDOURI(digitalObject);
        queueTriple(identifier, RDF_TYPE_URI, DATA_OBJECT_RDF_TYPE_URI);	
        addQueue(false);
	}
	
	private void addDublinCoreDatastream(DigitalObject digitalObject, Datastream ds) throws ResourceIndexException {
	    String doURI = getDOURI(digitalObject);
	    DatastreamXMLMetadata dc = (DatastreamXMLMetadata)ds;
		DCFields dcf;
        final String DC_URI_PREFIX = "http://purl.org/dc/elements/1.1/";
        
		try {
			dcf = new DCFields(dc.getContentStream());
		} catch (Throwable t) {
			throw new ResourceIndexException(t.getMessage());
		}
		Iterator it;
		it = dcf.titles().iterator();
		while (it.hasNext()) {
            queuePlainLiteralTriple(doURI, DC_URI_PREFIX + "title", (String)it.next());
		}
		it = dcf.creators().iterator();
		while (it.hasNext()) {
            queuePlainLiteralTriple(doURI, DC_URI_PREFIX + "creator", (String)it.next());
		}
		it = dcf.subjects().iterator();
		while (it.hasNext()) {
            queuePlainLiteralTriple(doURI, DC_URI_PREFIX + "subject", (String)it.next());
		}
		it = dcf.descriptions().iterator();
		while (it.hasNext()) {
            queuePlainLiteralTriple(doURI, DC_URI_PREFIX + "description", (String)it.next());
		}
		it = dcf.publishers().iterator();
		while (it.hasNext()) {
            queuePlainLiteralTriple(doURI, DC_URI_PREFIX + "publisher", (String)it.next());
		}
		it = dcf.contributors().iterator();
		while (it.hasNext()) {
			queuePlainLiteralTriple(doURI, DC_URI_PREFIX + "contributor", (String)it.next());
		}
		it = dcf.dates().iterator();
		while (it.hasNext()) {
			queuePlainLiteralTriple(doURI, DC_URI_PREFIX + "date", (String)it.next());
		}
		it = dcf.types().iterator();
		while (it.hasNext()) {
			queuePlainLiteralTriple(doURI, DC_URI_PREFIX + "type", (String)it.next());
		}
		it = dcf.formats().iterator();
		while (it.hasNext()) {
			queuePlainLiteralTriple(doURI, DC_URI_PREFIX + "format", (String)it.next());
		}
		it = dcf.identifiers().iterator();
		while (it.hasNext()) {
			queuePlainLiteralTriple(doURI, DC_URI_PREFIX + "identifier", (String)it.next());
		}
		it = dcf.sources().iterator();
		while (it.hasNext()) {
			queuePlainLiteralTriple(doURI, DC_URI_PREFIX + "source", (String)it.next());
		}
		it = dcf.languages().iterator();
		while (it.hasNext()) {
			queuePlainLiteralTriple(doURI, DC_URI_PREFIX + "language", (String)it.next());
		}
		it = dcf.relations().iterator();
		while (it.hasNext()) {
			queuePlainLiteralTriple(doURI, DC_URI_PREFIX + "relation", (String)it.next());
		}
		it = dcf.coverages().iterator();
		while (it.hasNext()) {
			queuePlainLiteralTriple(doURI, DC_URI_PREFIX + "coverage", (String)it.next());
		}
		it = dcf.rights().iterator();
		while (it.hasNext()) {
			queuePlainLiteralTriple(doURI, DC_URI_PREFIX + "rights", (String)it.next());
		}
        addQueue(false);
	}
	
    private void addDSInputSpecDatastream(Datastream ds) {
        // Placeholder. We don't currently do more than
        // index the fact that said datastream exists
    }
    
    private void addExtPropertiesDatastream(Datastream ds) {
        // Placeholder
    }
	
    /**
     * MethodMap datastream is only required for identifying the various 
     * combinations (permutations) of method names, their parameters and values.
     * However, the MethodMap datastream is only available in the bDef that 
     * defines the methods or the bMech(s) that implement the bDef, but this 
     * information is needed at the ingest of the dataobjects.
     * So, these method permutations are stored in a relational database
     * for performance reasons.
     * 
     * Note that we only track unparameterized disseminations and disseminations
     * with fixed parameters
     * 
     * @param digitalObject
     * @param ds
     * @throws ResourceIndexException
     */
	private void addMethodMapDatastream(DigitalObject digitalObject, Datastream ds) throws ResourceIndexException {
	    // only bdefs & bmechs have mmaps, and we only add when we see bdefs.
	    if (digitalObject.getFedoraObjectType() != DigitalObject.FEDORA_BDEF_OBJECT) {
	        return;
	    }
        
        List permutations = new ArrayList();
        String bDefPid = digitalObject.getPid();
        String doURI = getDOURI(digitalObject);
	    DatastreamXMLMetadata mmapDS = (DatastreamXMLMetadata)ds;
	    ServiceMapper serviceMapper = new ServiceMapper(bDefPid);
	    MethodDef[] mdef;
	    try {
	        mdef = serviceMapper.getMethodDefs(new InputSource(new ByteArrayInputStream(mmapDS.xmlContent)));
	    } catch (Throwable t) {
	        throw new ResourceIndexException(t.getMessage());
	    }
	    
	    String methodName;
	    boolean noRequiredParms;
        int optionalParms;
        PreparedStatement insertMethod, insertPermutation;

        try {
            insertMethod = m_conn.prepareStatement("INSERT INTO riMethod (methodId, bDefPid, methodName) VALUES (?, ?, ?)");
            insertPermutation = m_conn.prepareStatement("INSERT INTO riMethodPermutation (methodId, permutation) VALUES (?, ?)");
        } catch (SQLException se) {
            // TODO Auto-generated catch block
            se.printStackTrace();
            throw new ResourceIndexException(se.getMessage());
        }
        
	    for (int i = 0; i < mdef.length; i++) {
	    	methodName = mdef[i].methodName;
	    	MethodParmDef[] mparms = mdef[i].methodParms;
	    	if (mparms.length == 0) { // no method parameters
                permutations.add(methodName);
	    	} else {
	    		noRequiredParms = true;
                optionalParms = 0;
                List parms = new ArrayList();
	    		for (int j = 0; j < mparms.length; j++) {
	    			if (noRequiredParms && mparms[j].parmRequired) {
	    			    noRequiredParms = false;
    			    }
                    if (!mparms[j].parmRequired) {
                        optionalParms++;
                    }
	    		}
	    		if (noRequiredParms) {
                    permutations.add(methodName);
	    		} else {
	    		    // add methods with their required, fixed parameters
                    parms.addAll(getMethodParameterCombinations(mparms, true));
	    		}
                if (optionalParms > 0) {
                    parms.addAll(getMethodParameterCombinations(mparms, false));
                }
                Iterator it = parms.iterator();
                while (it.hasNext()) {
                    permutations.add(methodName + "?" + it.next());
                }
	    	}
            
            // build the batch of sql statements to execute
            String riMethodPK = getRIMethodPrimaryKey(bDefPid, methodName);
            try {
                insertMethod.setString(1, riMethodPK);
                insertMethod.setString(2, bDefPid);
                insertMethod.setString(3, methodName);
                insertMethod.addBatch();

                Iterator it = permutations.iterator();
                while (it.hasNext()) {
                    insertPermutation.setString(1, riMethodPK);
                    insertPermutation.setString(2, (String)it.next());
                    insertPermutation.addBatch();
                }
                permutations.clear();
            
            } catch (SQLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                throw new ResourceIndexException(e.getMessage());
            }

	    	// FIXME do we need passby and type in the graph?
//	        for (int j = 0; j < mparms.length; j++) {
//	            System.out.println(methodName + " *parmName: " + mparms[j].parmName);
//	            System.out.println(methodName + " *parmPassBy: " + mparms[j].parmPassBy);
//	            System.out.println(methodName + " *parmType: " + mparms[j].parmType);
//	        }
	    }
        
        try {
            insertMethod.executeBatch();
            insertPermutation.executeBatch();
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            throw new ResourceIndexException(e.getMessage());
        }
	}
	
    private void addRelsDatastream(Datastream ds) throws ResourceIndexException {
        DatastreamXMLMetadata rels = (DatastreamXMLMetadata)ds;
        try {
            m_writer.load(rels.getContentStream(), null);
        } catch (IOException e) {
            throw new ResourceIndexException(e.getMessage(), e);
        } catch (TrippiException e) {
            throw new ResourceIndexException(e.getMessage(), e);
        }
    }
    
    private void addServiceProfileDatastream(Datastream ds) {
        // Placeholder
    }
    
    /**
     * The WSDL datastream is only parsed to obtain the mime types of
     * disseminations. As this is only available in bMech objects,
     * we cache this information to a relational database table for later
     * querying when data objects are ingested.
     * 
     * @param digitalObject
     * @param ds
     * @throws ResourceIndexException
     */
    private void addWSDLDatastream(DigitalObject digitalObject, Datastream ds) throws ResourceIndexException {
        // for the moment, we're only interested in WSDL Datastreams
        // in BMechs, so that we can extract mimetypes.
        if (digitalObject.getFedoraObjectType() != DigitalObject.FEDORA_BMECH_OBJECT) {
            return;
        }

        String doURI = getDOURI(digitalObject);
        String bDefPid = getBDefPid(digitalObject);
        String bMechPid = digitalObject.getPid();
        DatastreamXMLMetadata wsdlDS = (DatastreamXMLMetadata)ds;
        Map bindings;
        PreparedStatement insertMethodImpl, insertMethodMimeType;
        
        try {
            WSDLFactory wsdlFactory = WSDLFactory.newInstance();
            WSDLReader wsdlReader = wsdlFactory.newWSDLReader();
            wsdlReader.setFeature("javax.wsdl.verbose",false);
            
            Definition definition = wsdlReader.readWSDL(null, new InputSource(new ByteArrayInputStream(wsdlDS.xmlContent)));
            bindings = definition.getBindings();
            
            Set bindingKeys = bindings.keySet();
            Iterator it = bindingKeys.iterator();
            
            String methodName, mimeType;
            insertMethodImpl = m_conn.prepareStatement("INSERT INTO riMethodImpl (methodImplId, bMechPid, methodId) VALUES (?, ?, ?)");
            insertMethodMimeType = m_conn.prepareStatement("INSERT INTO riMethodMimeType (methodImplId, mimeType) VALUES (?, ?)");
            String riMethodImplPK, riMethodFK;
            QName mimeContentQName = new QName("http://schemas.xmlsoap.org/wsdl/mime/", "content");
            while (it.hasNext()) {
                QName qname = (QName)it.next();
                Binding binding = (Binding)bindings.get(qname);

                List bops = binding.getBindingOperations();
                Iterator bit = bops.iterator();
                while (bit.hasNext()) {
                    BindingOperation bop = (BindingOperation)bit.next();
                    methodName = bop.getName();
                    riMethodFK = getRIMethodPrimaryKey(bDefPid, methodName);
                    riMethodImplPK = getRIMethodImplPrimaryKey(bMechPid, methodName);
                    insertMethodImpl.setString(1, riMethodImplPK);
                    insertMethodImpl.setString(2, bMechPid);
                    insertMethodImpl.setString(3, riMethodFK);
                    insertMethodImpl.addBatch();
                    BindingOutput bout = bop.getBindingOutput();
                    List extEls = bout.getExtensibilityElements();
                    Iterator eit = extEls.iterator();
                    while (eit.hasNext()) {
                        ExtensibilityElement extEl = (ExtensibilityElement)eit.next();
                        QName eType = extEl.getElementType();
                        if (eType.equals(mimeContentQName)) {
                            MIMEContent mc = (MIMEContent)extEl;
                            mimeType = mc.getType();
                            insertMethodMimeType.setString(1, riMethodImplPK);
                            insertMethodMimeType.setString(2, mimeType);
                            insertMethodMimeType.addBatch();
                        }
                    }
                }
            }
            
            try {
                insertMethodImpl.executeBatch();
                insertMethodMimeType.executeBatch();
            } catch (SQLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                throw new ResourceIndexException(e.getMessage());
            }
            
        } catch (WSDLException e) {
            e.printStackTrace();
            throw new ResourceIndexException("WSDLException: " + e.getMessage());
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    
	private Datastream getLatestDatastream(List datastreams) {
	    Iterator it = datastreams.iterator();
	    long latestDSCreateDT = -1;
	    Datastream ds, latestDS = null;
	    while (it.hasNext()) {
	        ds = (Datastream)it.next();
	        if (ds.DSCreateDT.getTime() > latestDSCreateDT) {
	            latestDS = ds;
	        }
	    }
	    return latestDS;
	}
	
	private MethodDef[] getMethodDefs(String pid, Datastream ds) throws ResourceIndexException {
	    DatastreamXMLMetadata mmapDS = (DatastreamXMLMetadata)ds;
	    ServiceMapper serviceMapper = new ServiceMapper(pid);
	    try {
	        return serviceMapper.getMethodDefs(new InputSource(new ByteArrayInputStream(mmapDS.xmlContent)));
	    } catch (Throwable t) {
	        throw new ResourceIndexException(t.getMessage());
	    }
	}
	
	private BMechDSBindSpec getDSBindSpec(String pid, Datastream ds) throws ResourceIndexException {
	    DatastreamXMLMetadata dsInSpecDS = (DatastreamXMLMetadata)ds;
	    ServiceMapper serviceMapper = new ServiceMapper(pid);
	    BMechDSBindSpec dsBindSpec;
	    try {
	        return serviceMapper.getDSInputSpec(new InputSource(new ByteArrayInputStream(dsInSpecDS.xmlContent)));
	    } catch (Throwable t) {
	        throw new ResourceIndexException(t.getMessage());
	    }
	}
	
	private Disseminator getLatestDisseminator(List disseminators) {
	    Iterator it = disseminators.iterator();
	    long latestDISSCreateDT = -1;
	    Disseminator diss, latestDISS = null;
	    while (it.hasNext()) {
	        diss = (Disseminator)it.next();
	        if (diss.dissCreateDT.getTime() > latestDISSCreateDT) {
	            latestDISS = diss;
	        }
	    }
	    return latestDISS;
	}
	
	private String getDOURI(DigitalObject digitalObject) {
	    String identifier;
	    logFinest("ResourceIndex digitalObject.getPid(): " + digitalObject.getPid());
	    if (digitalObject.getURI() != null && !digitalObject.getURI().equals("")) {
	        return digitalObject.getURI();
	    } else {
	        return getDOURI(digitalObject.getPid());
	    }
	}
	
	private String getDOURI(String pid) {
	    return FEDORA_URI_SCHEME + pid;
	}
    
    private void queueTriple(String subject, 
                             String predicate, 
                             String object) throws ResourceIndexException {
        try {
            m_tQueue.add(TripleFactory.create(subject, predicate, object));
        } catch (TrippiException e) {
            throw new ResourceIndexException(e.getMessage(), e);
        }
    }
    
    private void queuePlainLiteralTriple(String subject, 
                                         String predicate, 
                                         String object) throws ResourceIndexException {
        try {
            m_tQueue.add(TripleFactory.createPlain(subject, predicate, object));
        } catch (TrippiException e) {
            throw new ResourceIndexException(e.getMessage(), e);
        }
    }
    
    private void queueLocalLiteralTriple(String subject, 
                                         String predicate, 
                                         String object, 
                                         String language) throws ResourceIndexException {
        try {
            m_tQueue.add(TripleFactory.createLocal(subject, predicate, object, language));
        } catch (TrippiException e) {
            throw new ResourceIndexException(e.getMessage(), e);
        }
    }
    
    private void queueTypedLiteralTriple(String subject, 
                                         String predicate, 
                                         String object, 
                                         String datatype) throws ResourceIndexException {
        try {
            m_tQueue.add(TripleFactory.createTyped(subject, predicate, object, datatype));
        } catch (TrippiException e) {
            throw new ResourceIndexException(e.getMessage(), e);
        }
}
    
    private void addQueue(boolean flush) throws ResourceIndexException {
        try {
            m_writer.add(m_tQueue, flush);
        } catch (IOException e) {
            throw new ResourceIndexException(e.getMessage(), e);
        } catch (TrippiException e) {
            throw new ResourceIndexException(e.getMessage(), e);
        }
        m_tQueue.clear();
    }
	
	/**
	 * 
	 * @param date
	 * @return UTC Date as ISO 8601 formatted string (e.g. 2004-04-20T16:20:00Z)
	 */
	private static String getDate(Date date) {
	    DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
	    df.setTimeZone(TimeZone.getTimeZone("GMT"));
	    return df.format(date);
	}
    
    /**
     * Returns a List of Strings, representing the cross product of possible
     * method parameters and their values, e.g. 
     * ( "arg1=val1&arg2=val2", "foo=bar&baz=quux" )
     * 
     */
    private List getMethodParameterCombinations(MethodParmDef[] mparms, boolean isRequired) {
        List combinations = new ArrayList();
        
        Arrays.sort(mparms, new MethodParmDefParmNameComparator());
        List parms = new ArrayList();
        for (int j = 0; j < mparms.length; j++) {
            List parm = new ArrayList();
            for (int k = 0; k < mparms[j].parmDomainValues.length; k++) {
                if (isRequired) {
                    if (mparms[j].parmRequired) {
                        parm.add(mparms[j].parmName + "=" + mparms[j].parmDomainValues[k]);
                    }
                } else {
                    parm.add(mparms[j].parmName + "=" + mparms[j].parmDomainValues[k]);
                }
            }
            parms.add(parm);
        }
        
        CrossProduct cp = new CrossProduct(parms);
        List results = cp.getCrossProduct();
        Iterator it = results.iterator();
        while (it.hasNext()) {
            List cpParms = (List)it.next();
            Iterator it2 = cpParms.iterator();
            StringBuffer sb = new StringBuffer();
            while (it2.hasNext()) {
                sb.append(it2.next());
                if (it2.hasNext()) {
                    sb.append("&");
                }
            }
            combinations.add(sb.toString());
        }
        return combinations;
    }
    
    /**
     * If we could rely on support for JDBC 3.0's Statement.getGeneratedKeys(),
     * we could use an auto-increment field for the primary key.
     * 
     * A composite primary key isn't used (at the moment) because
     * of varying support for composite keys.
     * A post to the McKoi mailing list suggests that while McKoi
     * allows composite primary keys, it's not indexing them as a 
     * composite, and requiring a (full?) table scan.
     * 
     * @return bDefPid + "/" + methodName, e.g. demo:8/getImage
     */
    private String getRIMethodPrimaryKey(String bDefPid, String methodName) {
        return (bDefPid + "/" + methodName);
    }
    
    private String getRIMethodImplPrimaryKey(String bMechPid, String methodName) {
        return (bMechPid + "/" + methodName);
    }
    
    private String getBDefPid(DigitalObject bMech) throws ResourceIndexException {
        if (bMech.getFedoraObjectType() != DigitalObject.FEDORA_BMECH_OBJECT) {
            throw new ResourceIndexException("Illegal argument: object is not a bMech");
        }
        Datastream ds;
        ds = getLatestDatastream(bMech.datastreams("DSINPUTSPEC"));
        BMechDSBindSpec dsBindSpec = getDSBindSpec(bMech.getPid(), ds);
        return dsBindSpec.bDefPID;
    }

    /* (non-Javadoc)
     * @see fedora.server.resourceIndex.ResourceIndex#getIndexLevel()
     * 
     * Levels:
     *  B
     *  R
     *  P
     *  D
     * 
     * Marking for latent indexing
     */
    public int getIndexLevel() {
        return m_indexLevel;
    }
    
    /**
     * 
     * Case insensitive sort by parameter name
     */
    protected class MethodParmDefParmNameComparator implements Comparator {
        public int compare(Object o1, Object o2) {
            MethodParmDef p1 = (MethodParmDef)o1;
            MethodParmDef p2 = (MethodParmDef)o2;
            return p1.parmName.toUpperCase().compareTo(p2.parmName.toUpperCase());
        }
        
    }
    
    protected class CrossProduct {
        public List crossProduct;
        public List lol;
        
        public CrossProduct(List listOfLists) {
            this.lol = listOfLists;
            this.crossProduct = new ArrayList();
        }
        
        public List getCrossProduct() {
            generateCrossProduct(new ArrayList());
            return crossProduct;
        }
        
        private void generateCrossProduct(List productList) {
            if (productList.size() == lol.size()) {
                addCopy(productList);
            } else {
                int idx = productList.size();
                List elementList = (List)lol.get(idx);
                Iterator it = elementList.iterator();
                if (it.hasNext()) {
                    productList.add(it.next());
                    generateCrossProduct(productList);
                    while (it.hasNext()) {
                        productList.set(idx, it.next());
                        generateCrossProduct(productList);
                    }
                    productList.remove(idx);
                }
            }
        }
        
        private void addCopy(List result) {
            List copy = new ArrayList();
            copy.addAll(result);
            crossProduct.add(copy);
        }
    }

    /* (non-Javadoc)
     * @see org.trippi.TriplestoreReader#setAliasMap(java.util.Map)
     */
    public void setAliasMap(Map aliasToPrefix) throws TrippiException {
        m_reader.setAliasMap(aliasToPrefix);
    }

    /* (non-Javadoc)
     * @see org.trippi.TriplestoreReader#getAliasMap()
     */
    public Map getAliasMap() throws TrippiException {
        return m_reader.getAliasMap();
    }

    /* (non-Javadoc)
     * @see org.trippi.TriplestoreReader#findTuples(java.lang.String, java.lang.String, int, boolean)
     */
    public TupleIterator findTuples(String queryLang,
                                    String tupleQuery,
                                    int limit,
                                    boolean distinct) throws TrippiException {
        return m_reader.findTuples(queryLang, tupleQuery, limit, distinct);
    }

    /* (non-Javadoc)
     * @see org.trippi.TriplestoreReader#countTuples(java.lang.String, java.lang.String, int, boolean)
     */
    public int countTuples(String queryLang,
                           String tupleQuery,
                           int limit,
                           boolean distinct) throws TrippiException {
        return m_reader.countTuples(queryLang, tupleQuery, limit, distinct);
    }

    /* (non-Javadoc)
     * @see org.trippi.TriplestoreReader#findTriples(java.lang.String, java.lang.String, int, boolean)
     */
    public TripleIterator findTriples(String queryLang,
                                      String tripleQuery,
                                      int limit,
                                      boolean distinct) throws TrippiException {
        return m_reader.findTriples(queryLang, tripleQuery, limit, distinct);
    }

    /* (non-Javadoc)
     * @see org.trippi.TriplestoreReader#countTriples(java.lang.String, java.lang.String, int, boolean)
     */
    public int countTriples(String queryLang,
                            String tripleQuery,
                            int limit,
                            boolean distinct) throws TrippiException {
        return m_reader.countTriples(queryLang, tripleQuery, limit, distinct);
    }

    /* (non-Javadoc)
     * @see org.trippi.TriplestoreReader#findTriples(org.jrdf.graph.SubjectNode, org.jrdf.graph.PredicateNode, org.jrdf.graph.ObjectNode, int)
     */
    public TripleIterator findTriples(SubjectNode subject,
                                      PredicateNode predicate,
                                      ObjectNode object,
                                      int limit) throws TrippiException {
        return m_reader.findTriples(subject, predicate, object, limit);
    }

    /* (non-Javadoc)
     * @see org.trippi.TriplestoreReader#countTriples(org.jrdf.graph.SubjectNode, org.jrdf.graph.PredicateNode, org.jrdf.graph.ObjectNode, int)
     */
    public int countTriples(SubjectNode subject,
                            PredicateNode predicate,
                            ObjectNode object,
                            int limit) throws TrippiException {
        return m_reader.countTriples(subject, predicate, object, limit);
    }

    /* (non-Javadoc)
     * @see org.trippi.TriplestoreReader#findTriples(java.lang.String, java.lang.String, java.lang.String, int, boolean)
     */
    public TripleIterator findTriples(String queryLang,
                                      String tripleQuery,
                                      String tripleTemplate,
                                      int limit,
                                      boolean distinct) throws TrippiException {
        return m_reader.findTriples(queryLang, tripleQuery, tripleTemplate, limit, distinct);
    }

    /* (non-Javadoc)
     * @see org.trippi.TriplestoreReader#countTriples(java.lang.String, java.lang.String, java.lang.String, int, boolean)
     */
    public int countTriples(String queryLang,
                            String tripleQuery,
                            String tripleTemplate,
                            int limit,
                            boolean distinct) throws TrippiException {
        return m_reader.countTriples(queryLang, tripleQuery, tripleTemplate, limit, distinct);
    }

    /* (non-Javadoc)
     * @see org.trippi.TriplestoreReader#dump(java.io.OutputStream)
     */
    public void dump(OutputStream out) throws IOException, TrippiException {
        m_reader.dump(out);
    }

    /* (non-Javadoc)
     * @see org.trippi.TriplestoreReader#listTupleLanguages()
     */
    public String[] listTupleLanguages() {
        return m_reader.listTupleLanguages();
    }

    /* (non-Javadoc)
     * @see org.trippi.TriplestoreReader#listTripleLanguages()
     */
    public String[] listTripleLanguages() {
        return m_reader.listTripleLanguages();
    }

    /* (non-Javadoc)
     * @see org.trippi.TriplestoreReader#close()
     */
    public void close() throws TrippiException {
        m_reader.close();
    }

}

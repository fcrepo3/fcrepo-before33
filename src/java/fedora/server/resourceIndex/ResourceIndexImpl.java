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

import org.xml.sax.InputSource;

import com.hp.hpl.jena.rdql.Value;

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
	private RIStore m_store;
	private static final String FEDORA_URI_SCHEME = "info:fedora/";
	private static final String DC_URI_PREFIX = "http://purl.org/dc/elements/1.1/";
    
    // For the database
    private ConnectionPool m_cPool;
    private Connection m_conn;
    private Statement m_statement;
    private ResultSet m_resultSet;
	
	/**
	 * @param target
	 */
	public ResourceIndexImpl(int indexLevel, RIStore store, Logging target) {
		super(target);
		m_indexLevel = indexLevel;
		m_store = store;
	}
    
    public ResourceIndexImpl(int indexLevel, RIStore store, ConnectionPool cPool, Logging target) {
        super(target);
        m_indexLevel = indexLevel;
        m_store = store;
        m_cPool = cPool;
        try {
            m_conn = m_cPool.getConnection();
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
	
	/* (non-Javadoc)
	 * @see fedora.server.resourceIndex.ResourceIndex#query(fedora.server.resourceIndex.RIQuery)
	 */
	public RIResultIterator executeQuery(RIQuery query) throws ResourceIndexException {
		return m_store.executeQuery(query);
	}

	/* (non-Javadoc)
	 * @see fedora.server.resourceIndex.ResourceIndex#addDigitalObject(fedora.server.storage.types.DigitalObject)
	 */
	public void addDigitalObject(DigitalObject digitalObject) throws ResourceIndexException {
	    String pid = digitalObject.getPid();
		String doIdentifier = getDOURI(digitalObject);
		
		// Insert basic system metadata
		m_store.insertLiteral(doIdentifier, LABEL_URI, digitalObject.getLabel());
		m_store.insertLiteral(doIdentifier, DATE_CREATED_URI, getDate(digitalObject.getCreateDate()));
		m_store.insertLiteral(doIdentifier, DATE_LAST_MODIFIED_URI, getDate(digitalObject.getLastModDate()));
		
		if (digitalObject.getOwnerId() != null) {
		    m_store.insertLiteral(doIdentifier, OWNER_ID_URI, digitalObject.getOwnerId());
		}
		m_store.insertLiteral(doIdentifier, CONTENT_MODEL_ID_URI, digitalObject.getContentModelId());
		m_store.insertLiteral(doIdentifier, STATE_URI, digitalObject.getState());
		
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
        
        // Volatile Datastreams: False for datastreams that are locally managed 
        // (have a control group "M" or "I").
        String isVolatile = !(ds.DSControlGrp.equals("M") || ds.DSControlGrp.equals("I")) ? "true" : "false";
	    
        m_store.insert(doURI, HAS_REPRESENTATION_URI, datastreamURI);
	    m_store.insertLiteral(datastreamURI, DATE_LAST_MODIFIED_URI, getDate(ds.DSCreateDT));
        m_store.insertLiteral(datastreamURI, DISSEMINATION_DIRECT_URI, "true");
        m_store.insertLiteral(datastreamURI, DISSEMINATION_VOLATILE_URI, isVolatile);
	    
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
	    m_store.insert(doIdentifier, USES_BMECH_URI, getDOURI(bMechPID));
		
	    DSBindingMap m = diss.dsBindMap; // is this needed???
	    String bDefPID = diss.bDefID;
	    
	    // insert representations
	    if (digitalObject.getFedoraObjectType() == DigitalObject.FEDORA_OBJECT) {
//		    List methods = getMethodNames(bDefPID);
//		    Iterator it = methods.iterator();
//		    String rep;
//		    while (it.hasNext()) {
//		    	rep = doIdentifier + "/" + bDefPID + "/" + (String)it.next();
//		    	m_store.insert(doIdentifier, HAS_REPRESENTATION_URI, rep);
//		    }
            
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
                     m_store.insert(doIdentifier, HAS_REPRESENTATION_URI, rep);
                     // TODO mimetype as URI...what form???
                     m_store.insertLiteral(rep, DISSEMINATION_MEDIA_TYPE_URI, mimeType);
                     
                 }
            } catch (SQLException e) {
                 // TODO Auto-generated catch block
                 e.printStackTrace();
            }
            
	    }
        
        // get reps from db, given bdefPid, bMechPid
        // SELECT permutation FROM riPermutation 
        /*
         * SELECT riMethodPermutation.permutation, riMimeType.mimeType
         * FROM riMethodPermutation, riMimeType, riMethodImpl
         * WHERE riMethodPermutation.methodId = riMethodImpl.methodId
         *       AND riMethodImpl.methodImplId = riMimeType.methodImplId 
         *       AND riMethodImpl.bMechPid = ''; 
         * 
         * SELECT riMethodPermutation.permutation
         * FROM riMethodPermutation, riMethod
         * WHERE riMethodPermutation.methodId = riMethod.methodId
         *       AND riMethod.bDefPid = '';
         */

	    
	    // TODO
	    //m_store.insert(disseminatorIdentifier, DISSEMINATION_MEDIA_TYPE_URI, diss.?);
        //m_store.insertLiteral(disseminatorIdentifier, DISSEMINATION_DIRECT_URI, "false"); // not going against a service, i.e. datastreams (true/false)
        
        //?
		//m_store.insertLiteral(disseminatorIdentifier, DATE_LAST_MODIFIED_URI, getDate(diss.dissCreateDT));
		
        
		//m_store.insertLiteral(disseminatorIdentifier, STATE_URI, diss.dissState); // change to uri #active/#inactive
		
        //?
        //m_store.insert(disseminatorIdentifier, DISSEMINATION_TYPE_URI, diss.?);
		
        
        //m_store.insert(disseminatorIdentifier, DISSEMINATION_VOLATILE_URI, diss.?); // redirect, external, based on diss that depends on red/ext (true/false)
	}

	/* (non-Javadoc)
	 * @see fedora.server.resourceIndex.ResourceIndex#modifyDigitalObject(fedora.server.storage.types.DigitalObject)
	 */
	public void modifyDigitalObject(DigitalObject digitalObject) throws ResourceIndexException {
		// FIXME simple, dumb way to modify
		deleteDigitalObject(digitalObject.getPid());
        addDigitalObject(digitalObject);        
	}

	/* (non-Javadoc)
	 * @see fedora.server.resourceIndex.ResourceIndex#modifyDatastream(fedora.server.storage.types.Datastream)
	 */
	public void modifyDatastream(Datastream ds) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see fedora.server.resourceIndex.ResourceIndex#modifyDissemination(fedora.server.storage.types.Dissemination)
	 */
	public void modifyDisseminator(Disseminator diss) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see fedora.server.resourceIndex.ResourceIndex#deleteDigitalObject(java.lang.String)
	 */
	public void deleteDigitalObject(String pid) {
		// TODO Auto-generated method stub
	}

	/* (non-Javadoc)
	 * @see fedora.server.resourceIndex.ResourceIndex#deleteDatastream(fedora.server.storage.types.Datastream)
	 */
	public void deleteDatastream(Datastream ds) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see fedora.server.resourceIndex.ResourceIndex#deleteDissemination(fedora.server.storage.types.Dissemination)
	 */
	public void deleteDisseminator(Disseminator diss) {
		// TODO Auto-generated method stub
		
	}

	private void addBDef(DigitalObject bDef) throws ResourceIndexException {
		String doURI = getDOURI(bDef);
		m_store.insert(doURI, RDF_TYPE_URI, BDEF_RDF_TYPE_URI);
		
		Datastream ds = getLatestDatastream(bDef.datastreams("METHODMAP"));
		MethodDef[] mdef = getMethodDefs(bDef.getPid(), ds);
		for (int i = 0; i < mdef.length; i++) {
	        m_store.insertLiteral(doURI, DEFINES_METHOD_URI, mdef[i].methodName);
	        // m_store.insertLiteral(doIdentifier, "foo:methodLabel", mdef[i].methodLabel);
	    }
	}
	
	private void addBMech(DigitalObject bMech) throws ResourceIndexException {
		String doURI = getDOURI(bMech);
		m_store.insert(doURI, RDF_TYPE_URI, BMECH_RDF_TYPE_URI);
		
		String bDefPid = getBDefPid(bMech);
		m_store.insert(doURI, IMPLEMENTS_BDEF_URI, getDOURI(bDefPid));
		
	}
	
	private void addDataObject(DigitalObject digitalObject) {
		String identifier = getDOURI(digitalObject);
		m_store.insert(identifier, RDF_TYPE_URI, DATA_OBJECT_RDF_TYPE_URI);	
	}
	
	private void addDublinCoreDatastream(DigitalObject digitalObject, Datastream ds) throws ResourceIndexException {
	    String doURI = getDOURI(digitalObject);
	    DatastreamXMLMetadata dc = (DatastreamXMLMetadata)ds;
		DCFields dcf;
		try {
			dcf = new DCFields(dc.getContentStream());
		} catch (Throwable t) {
			throw new ResourceIndexException(t.getMessage());
		}
		Iterator it;
		it = dcf.titles().iterator();
		while (it.hasNext()) {
			m_store.insertLiteral(doURI, DC_URI_PREFIX + "title", (String)it.next());
		}
		it = dcf.creators().iterator();
		while (it.hasNext()) {
			m_store.insertLiteral(doURI, DC_URI_PREFIX + "creator", (String)it.next());
		}
		it = dcf.subjects().iterator();
		while (it.hasNext()) {
			m_store.insertLiteral(doURI, DC_URI_PREFIX + "subject", (String)it.next());
		}
		it = dcf.descriptions().iterator();
		while (it.hasNext()) {
			m_store.insertLiteral(doURI, DC_URI_PREFIX + "description", (String)it.next());
		}
		it = dcf.publishers().iterator();
		while (it.hasNext()) {
			m_store.insertLiteral(doURI, DC_URI_PREFIX + "publisher", (String)it.next());
		}
		it = dcf.contributors().iterator();
		while (it.hasNext()) {
			m_store.insertLiteral(doURI, DC_URI_PREFIX + "contributor", (String)it.next());
		}
		it = dcf.dates().iterator();
		while (it.hasNext()) {
			m_store.insertLiteral(doURI, DC_URI_PREFIX + "date", (String)it.next());
		}
		it = dcf.types().iterator();
		while (it.hasNext()) {
			m_store.insertLiteral(doURI, DC_URI_PREFIX + "type", (String)it.next());
		}
		it = dcf.formats().iterator();
		while (it.hasNext()) {
			m_store.insertLiteral(doURI, DC_URI_PREFIX + "format", (String)it.next());
		}
		it = dcf.identifiers().iterator();
		while (it.hasNext()) {
			m_store.insertLiteral(doURI, DC_URI_PREFIX + "identifier", (String)it.next());
		}
		it = dcf.sources().iterator();
		while (it.hasNext()) {
			m_store.insertLiteral(doURI, DC_URI_PREFIX + "source", (String)it.next());
		}
		it = dcf.languages().iterator();
		while (it.hasNext()) {
			m_store.insertLiteral(doURI, DC_URI_PREFIX + "language", (String)it.next());
		}
		it = dcf.relations().iterator();
		while (it.hasNext()) {
			m_store.insertLiteral(doURI, DC_URI_PREFIX + "relation", (String)it.next());
		}
		it = dcf.coverages().iterator();
		while (it.hasNext()) {
			m_store.insertLiteral(doURI, DC_URI_PREFIX + "coverage", (String)it.next());
		}
		it = dcf.rights().iterator();
		while (it.hasNext()) {
			m_store.insertLiteral(doURI, DC_URI_PREFIX + "rights", (String)it.next());
		}
	}
	
    private void addDSInputSpecDatastream(Datastream ds) {
        // Placeholder. We don't currently do more than
        // index the fact that there is said datastream
        //      DatastreamXMLMetadata dsInSpecDS = (DatastreamXMLMetadata)ds;
        //      ServiceMapper serviceMapper = new ServiceMapper(digitalObject.getPid());
        //      BMechDSBindSpec dsBindSpec;
        //      try {
        //          dsBindSpec = serviceMapper.getDSInputSpec(new InputSource(new ByteArrayInputStream(dsInSpecDS.xmlContent)));
        //      } catch (Throwable t) {
        //          throw new ResourceIndexException(t.getMessage());
        //      }
        //      String bDefPID = dsBindSpec.bDefPID;
        //      
        //      String bMechPID = dsBindSpec.bMechPID;
        //      
        //      BMechDSBindRule[] x = dsBindSpec.dsBindRules;
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
	
    private void addRelsDatastream(Datastream ds) {
        // TODO ristore can take the rdfxml straight... need test case for this method
        DatastreamXMLMetadata rels = (DatastreamXMLMetadata)ds;
        m_store.read(rels.getContentStream(), "");
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
        

        // SELECT methodId FROM riMethod WHERE bDefPid = '' AND methodName = '';
        // INSERT INTO riMethodMimeType (methodId, mimeType) VALUES ()
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
                //System.out.println("*bindingKey: " + qname);
                
                List bops = binding.getBindingOperations();
                Iterator bit = bops.iterator();
                while (bit.hasNext()) {
                    BindingOperation bop = (BindingOperation)bit.next();
                    methodName = bop.getName();
                    System.out.println("*BindingOperation.getName(): " + methodName);
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
                            System.out.println("*mc.getType(): " + mimeType);
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
	
//	private List getMethodNames(String bdefPID) throws ResourceIndexException {
//		RDQLQuery query = new RDQLQuery("SELECT ?o WHERE (<" + getDOURI(bdefPID) + "> <" + DEFINES_METHOD_URI + "> ?o)");
//        
//        logFinest("Started query to resource index (rdql).");
//        JenaResultIterator results = (JenaResultIterator)executeQuery(query);
//		
//		Value v;
//		List methods = new ArrayList();
//		while (results.hasNext()) {
//			v = (Value)results.next().get("o");
//			methods.add(v.getString());
//		}
//        logFinest("Finished query to resource index and iteration of results.");
//		return methods;
//	}
	
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

}

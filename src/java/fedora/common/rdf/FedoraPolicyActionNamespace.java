package fedora.common.rdf;

import fedora.common.Constants;

/**
 *
 * -----------------------------------------------------------------------------
 *
 * <p><b>License and Copyright: </b>The contents of this file are subject to the
 * Mozilla Public License Version 1.1 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License
 * at <a href="http://www.mozilla.org/MPL">http://www.mozilla.org/MPL/.</a></p>
 *
 * <p>Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License for
 * the specific language governing rights and limitations under the License.</p>
 *
 * <p>The entire file consists of original code.  Copyright &copy; 2002-2005 by The
 * Rector and Visitors of the University of Virginia and Cornell University.
 * All rights reserved.</p>
 *
 * -----------------------------------------------------------------------------
 *
 */
public class FedoraPolicyActionNamespace extends RDFNamespace {

	// Properties
	public final RDFName API;
	public final RDFName NEW_OBJECT_STATE;

    // Values of API
	public final RDFName APIM;
	public final RDFName APIA;
	
    // Values of urn:oasis:names:tc:xacml:1.0:action:action-id    
	public final RDFName ADD_DATASTREAM;	
	public final RDFName ADD_DISSEMINATOR;	
	public final RDFName EXPORT_OBJECT;	
	public final RDFName GET_DATASTREAM;	
	public final RDFName GET_DATASTREAM_HISTORY;	
	public final RDFName GET_DATASTREAMS;	
	public final RDFName GET_DISSEMINATOR;
	public final RDFName GET_DISSEMINATORS;	
	public final RDFName GET_DISSEMINATOR_HISTORY;	
	public final RDFName GET_NEXT_PID;
	public final RDFName GET_OBJECT_PROPERTIES;	
	public final RDFName GET_OBJECT_XML;	
	public final RDFName INGEST_OBJECT;
	public final RDFName MODIFY_DATASTREAM_BY_REFERENCE;	
	public final RDFName MODIFY_DATASTREAM_BY_VALUE;
	public final RDFName MODIFY_DISSEMINATOR;		
	public final RDFName MODIFY_OBJECT;
	public final RDFName PURGE_OBJECT;
	public final RDFName PURGE_DATASTREAM;
	public final RDFName PURGE_DISSEMINATOR;	
	public final RDFName SET_DATASTREAM_STATE;	
	public final RDFName SET_DISSEMINATOR_STATE;	
	public final RDFName DESCRIBE_REPOSITORY;	
	public final RDFName FIND_OBJECTS;	
	public final RDFName GET_DATASTREAM_DISSEMINATION;	
	public final RDFName GET_DISSEMINATION;	
	public final RDFName GET_OBJECT_HISTORY;	
	public final RDFName GET_OBJECT_PROFILE;	
	public final RDFName LIST_DATASTREAMS;	
	public final RDFName LIST_METHODS;		
	public final RDFName LIST_OBJECT_IN_FIELD_SEARCH_RESULTS;
	public final RDFName LIST_OBJECT_IN_RESOURCE_INDEX_RESULTS;


    public FedoraPolicyActionNamespace() {

        this.uri = Constants.FEDORA_SYSTEM_DEF_URI + "/policy-action#";

        // Properties
    	this.API = new RDFName(this, "api");
    	this.NEW_OBJECT_STATE = new RDFName(this, "newObjectState");

    	// Values of CONTEXT_ID are sequential numerals, hence not enumerated here.
    	
        // Values of API
    	this.APIM               = new RDFName(this, "apim");
    	this.APIA               = new RDFName(this, "apia");

        // Values of urn:oasis:names:tc:xacml:1.0:action:action-id    
    	// derived from respective Java methods in Access.java or Management.java

    	this. ADD_DATASTREAM               = new RDFName(this, "addDatastream");	
    	this. ADD_DISSEMINATOR               = new RDFName(this, "addDisseminator");	
    	this. EXPORT_OBJECT               = new RDFName(this, "exportObject");	
    	this. GET_DATASTREAM               = new RDFName(this, "getDatastream");	
    	this. GET_DATASTREAM_HISTORY               = new RDFName(this, "getDatastreamHistory");	
    	this. GET_DATASTREAMS               = new RDFName(this, "getDatastreams");	
    	this. GET_DISSEMINATOR               = new RDFName(this, "getDisseminator");
    	this. GET_DISSEMINATORS               = new RDFName(this, "getDisseminators");	
    	this. GET_DISSEMINATOR_HISTORY               = new RDFName(this, "getDisseminatorHistory");	
    	this. GET_NEXT_PID               = new RDFName(this, "getNextPid");
    	this. GET_OBJECT_PROPERTIES               = new RDFName(this, "getObjectProperties");	
    	this. GET_OBJECT_XML               = new RDFName(this, "getObjectXML");	
    	this. INGEST_OBJECT               = new RDFName(this, "ingestObject");
    	this. MODIFY_DATASTREAM_BY_REFERENCE               = new RDFName(this, "modifyDatastreamByReference");	
    	this. MODIFY_DATASTREAM_BY_VALUE               = new RDFName(this, "modifyDatastreamByValue");
    	this. MODIFY_DISSEMINATOR               = new RDFName(this, "modifyDisseminator");		
    	this. MODIFY_OBJECT               = new RDFName(this, "modifyObject");
    	this. PURGE_OBJECT               = new RDFName(this, "purgeObject");
    	this. PURGE_DATASTREAM               = new RDFName(this, "purgeDatastream");
    	this. PURGE_DISSEMINATOR               = new RDFName(this, "purgeDisseminator");	
    	this. SET_DATASTREAM_STATE               = new RDFName(this, "setDatastreamState");	
    	this. SET_DISSEMINATOR_STATE               = new RDFName(this, "setDisseminatorState");	
    	this. DESCRIBE_REPOSITORY               = new RDFName(this, "describeRepository");	
    	this. FIND_OBJECTS               = new RDFName(this, "findObjects");	
    	this. GET_DATASTREAM_DISSEMINATION               = new RDFName(this, "getDatastreamDissemination");	
    	this. GET_DISSEMINATION               = new RDFName(this, "getDissemination");	
    	this. GET_OBJECT_HISTORY               = new RDFName(this, "getObjectHistory");	
    	this. GET_OBJECT_PROFILE               = new RDFName(this, "getObjectProfile");	
    	this. LIST_DATASTREAMS               = new RDFName(this, "listDatastreams");	
    	this. LIST_METHODS               = new RDFName(this, "listMethods");		
    	this. LIST_OBJECT_IN_FIELD_SEARCH_RESULTS               = new RDFName(this, "listObjectInFieldSearchResults");
    	this. LIST_OBJECT_IN_RESOURCE_INDEX_RESULTS               = new RDFName(this, "listObjectInResourceIndexResults");
    }

}

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
public class FedoraPolicyResourceNamespace extends RDFNamespace {

	// Properties
	public final RDFName AS_OF_DATE;
	public final RDFName DATASTREAM_ID;
	public final RDFName DATASTREAM_LOCATION;
	public final RDFName CONTROL_GROUP;
	public final RDFName DISSEMINATOR_ID;
	public final RDFName DISSEMINATOR_PID;
	public final RDFName DISSEMINATOR_NAMESPACE;
	public final RDFName DISSEMINATOR_STATE;


    // Values
	


    public FedoraPolicyResourceNamespace() {

        this.uri = Constants.FEDORA_SYSTEM_DEF_URI + "/policy-resource#";

        // Properties
    	this.AS_OF_DATE = new RDFName(this, "asOfDate");
    	this.DATASTREAM_ID = new RDFName(this, "datastreamId");
    	this.DATASTREAM_LOCATION = new RDFName(this, "datastreamLocation");
    	this.CONTROL_GROUP = new RDFName(this, "controlGroup");
    	this.DISSEMINATOR_ID = new RDFName(this, "disseminatorId");
    	this.DISSEMINATOR_PID = new RDFName(this, "disseminatorPid");
    	this.DISSEMINATOR_NAMESPACE = new RDFName(this, "disseminatorNamespace");
    	this.DISSEMINATOR_STATE = new RDFName(this, "disseminatorState");


    	// Values
    }

}

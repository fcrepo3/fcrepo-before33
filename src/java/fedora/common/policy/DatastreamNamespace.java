package fedora.common.policy;

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
public class DatastreamNamespace extends XacmlNamespace {

	// Properties
	public final XacmlName ID;
	public final XacmlName STATE;
	public final XacmlName LOCATION;	
	public final XacmlName CONTROL_GROUP;	

    // Values
	
    private DatastreamNamespace(XacmlNamespace parent, String localName) {
    	super(parent, localName);

        // Properties
    	ID = addName(new XacmlName(this, "id")); 
    	STATE = addName(new XacmlName(this, "state")); 
    	LOCATION = addName(new XacmlName(this, "location"));    	
    	CONTROL_GROUP = addName(new XacmlName(this, "control-group"));    	

    	// Values
    	
    }

	public static DatastreamNamespace onlyInstance = new DatastreamNamespace(ResourceNamespace.getInstance(), "datastream");
	
	public static final DatastreamNamespace getInstance() {
		return onlyInstance;
	}


}

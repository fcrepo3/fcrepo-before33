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
public class DisseminatorNamespace extends XacmlNamespace {

	// Properties
	public final XacmlName ID;
	public final XacmlName PID;
	public final XacmlName NAMESPACE;
	public final XacmlName STATE;	
	public final XacmlName METHOD;	

    // Values
	
    private DisseminatorNamespace(XacmlNamespace parent, String localName) {
    	super(parent, localName);

        // Properties
    	ID = addName(new XacmlName(this, "id"));
    	PID = addName(new XacmlName(this, "pid"));
    	NAMESPACE = addName(new XacmlName(this, "namespace"));
    	STATE = addName(new XacmlName(this, "state"));    	
    	METHOD = addName(new XacmlName(this, "method"));    	

    	// Values
    	
    }

	public static DisseminatorNamespace onlyInstance = new DisseminatorNamespace(ResourceNamespace.getInstance(), "disseminator");
	
	public static final DisseminatorNamespace getInstance() {
		return onlyInstance;
	}


}

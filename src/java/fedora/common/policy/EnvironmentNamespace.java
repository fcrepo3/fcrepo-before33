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
public class EnvironmentNamespace extends XacmlNamespace {
	
	// Properties
	public final XacmlName CURRENT_DATE_TIME;
	public final XacmlName CURRENT_DATE;
	public final XacmlName CURRENT_TIME;	

    // Values

    private EnvironmentNamespace() {
    	super(Release2_1Namespace.getInstance(), "environment");

        // Properties
    	CURRENT_DATE_TIME = addName(new XacmlName(this, "current-date-time"));
    	CURRENT_DATE = addName(new XacmlName(this, "current-date"));
    	CURRENT_TIME = addName(new XacmlName(this, "current-time"));	

    	// Values

    }

	public static EnvironmentNamespace onlyInstance = new EnvironmentNamespace();
	static {
		onlyInstance.addNamespace(HttpRequestNamespace.getInstance());
	}
	
	public static final EnvironmentNamespace getInstance() {
		return onlyInstance;
	}

}

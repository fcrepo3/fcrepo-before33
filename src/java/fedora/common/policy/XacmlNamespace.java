package fedora.common.policy;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Hashtable;
import java.util.Vector;

import fedora.common.rdf.RDFNamespace;

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
public abstract class XacmlNamespace {
	
	private XacmlNamespace parent = null;

	private String localName = null;
	
    public String uri;

	private Vector memberNamespaces = new Vector();

	private Vector memberNames = new Vector();
	
	protected XacmlNamespace(XacmlNamespace parent, String localName) {
   		this.parent = parent;
   		this.localName = localName;
   		this.uri = ((parent == null) ? "" : (parent.uri) + ":") + localName;
    }

	/*package*/ XacmlNamespace addNamespace(XacmlNamespace namespace) {
		XacmlNamespace result = null;
		if (memberNamespaces.add(namespace)) {
			result = namespace;
		}
		return result;
	}
	
	/*package*/ XacmlName addName(XacmlName name) {
		XacmlName result = null;
		if (memberNames.add(name)) {
			result = name;
		}
		return result;
	}
	
	public void flatRep(Vector flatRep) {
		flatRep.addAll(memberNames);		
		for (int i=0; i<memberNamespaces.size(); i++) {
			((XacmlNamespace)memberNamespaces.get(i)).flatRep(flatRep);
		}
	}

}

package fedora.common.policy;

import java.util.List;
import java.util.Vector;

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
public class Release2_1Namespace extends XacmlNamespace {

    private Release2_1Namespace(XacmlNamespace parent, String localName) {
    	super(parent, localName);
    }

	public static Release2_1Namespace onlyInstance = new Release2_1Namespace(FedoraAsProjectNamespace.getInstance(), "2.1");
	static {
		onlyInstance.addNamespace(SubjectNamespace.getInstance()); 
		onlyInstance.addNamespace(ActionNamespace.getInstance()); 
		onlyInstance.addNamespace(ResourceNamespace.getInstance()); 
		onlyInstance.addNamespace(EnvironmentNamespace.getInstance()); 
	}
	
	public static final Release2_1Namespace getInstance() {
		return onlyInstance;
	}
	
	public static final void main (String[] args) {
		Release2_1Namespace instance = Release2_1Namespace.getInstance();
		Vector list = new Vector();
		instance.flatRep(list);
		for (int i=0; i<list.size(); i++) {
			System.out.println(list.get(i));
		} 
	}
	
	//C:\fedora\mellon\dist>
	//java -cp server\jakarta-tomcat-5.0.28\webapps\fedora\WEB-INF\classes;client\lib\jrdf-0.3.3.jar fedora.common.policy.Release2_1Namespace

}

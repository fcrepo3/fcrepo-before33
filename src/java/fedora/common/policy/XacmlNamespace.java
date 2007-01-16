package fedora.common.policy;

import java.util.Vector;

public abstract class XacmlNamespace {
	
	private XacmlNamespace parent = null;

	private String localName = null;
	
    public String uri;

	private Vector<XacmlNamespace> memberNamespaces = new Vector<XacmlNamespace>();

	private Vector<XacmlName> memberNames = new Vector<XacmlName>();
	
	protected XacmlNamespace(XacmlNamespace parent, String localName) {
   		this.parent = parent;
   		this.localName = localName;
   		this.uri = ((parent == null) ? "" : (parent.uri) + ":") + localName;
    }

	 XacmlNamespace addNamespace(XacmlNamespace namespace) {
		XacmlNamespace result = null;
		if (memberNamespaces.add(namespace)) {
			result = namespace;
		}
		return result;
	}
	
	 XacmlName addName(XacmlName name) {
		XacmlName result = null;
		if (memberNames.add(name)) {
			result = name;
		}
		return result;
	}
	
	public void flatRep(Vector<XacmlName> flatRep) {
		flatRep.addAll(memberNames);		
		for (int i=0; i<memberNamespaces.size(); i++) {
			((XacmlNamespace)memberNamespaces.get(i)).flatRep(flatRep);
		}
	}

}

package fedora.common.policy;

public class NamesNamespace extends XacmlNamespace {

    private NamesNamespace(XacmlNamespace parent, String localName) {
    	super(parent, localName);
    }

	public static NamesNamespace onlyInstance = new NamesNamespace(FedoraAsOrganizationNamespace.getInstance(), "names");
	static {
		onlyInstance.addNamespace(FedoraAsProjectNamespace.getInstance()); 
	}
	
	public static final NamesNamespace getInstance() {
		return onlyInstance;
	}

}

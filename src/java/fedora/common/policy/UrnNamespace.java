package fedora.common.policy;

public class UrnNamespace extends XacmlNamespace {

    private UrnNamespace(XacmlNamespace parent, String localName) {
    	super(parent, localName);
    }

	public static UrnNamespace onlyInstance = new UrnNamespace(null, "urn");
	static {
		onlyInstance.addNamespace(FedoraAsOrganizationNamespace.getInstance()); 
	}
	
	public static final UrnNamespace getInstance() {
		return onlyInstance;
	}

}

package fedora.common.policy;

public class BDefNamespace extends XacmlNamespace {

	// Properties
	public final XacmlName ID;	
	public final XacmlName STATE;
	public final XacmlName LOCATION;	

    // Values
	
    private BDefNamespace(XacmlNamespace parent, String localName) {
    	super(parent, localName);

        // Properties
    	ID = addName(new XacmlName(this, "id")); 
    	STATE = addName(new XacmlName(this, "state")); 
    	LOCATION = addName(new XacmlName(this, "location"));    	

    	// Values
    	
    }

	public static BDefNamespace onlyInstance = new BDefNamespace(ResourceNamespace.getInstance(), "bdef");
	
	public static final BDefNamespace getInstance() {
		return onlyInstance;
	}


}

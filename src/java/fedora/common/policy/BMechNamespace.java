package fedora.common.policy;

public class BMechNamespace extends XacmlNamespace {

	// Properties
	public final XacmlName ID;
	public final XacmlName STATE;
	public final XacmlName LOCATION;	

    // Values
	
    private BMechNamespace(XacmlNamespace parent, String localName) {
    	super(parent, localName);

        // Properties
    	ID = addName(new XacmlName(this, "id")); 
    	STATE = addName(new XacmlName(this, "state")); 
    	LOCATION = addName(new XacmlName(this, "location"));    	

    	// Values
    	
    }

	public static BMechNamespace onlyInstance = new BMechNamespace(ResourceNamespace.getInstance(), "bmech");
	
	public static final BMechNamespace getInstance() {
		return onlyInstance;
	}


}

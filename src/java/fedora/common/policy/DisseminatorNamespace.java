package fedora.common.policy;

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

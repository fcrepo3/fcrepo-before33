package fedora.common.policy;

public class DatastreamNamespace extends XacmlNamespace {

	// Properties
	public final XacmlName ID;
	public final XacmlName STATE;
	public final XacmlName LOCATION;	
	public final XacmlName CONTROL_GROUP;	

    // Values
	
    private DatastreamNamespace(XacmlNamespace parent, String localName) {
    	super(parent, localName);

        // Properties
    	ID = addName(new XacmlName(this, "id")); 
    	STATE = addName(new XacmlName(this, "state")); 
    	LOCATION = addName(new XacmlName(this, "location"));    	
    	CONTROL_GROUP = addName(new XacmlName(this, "control-group"));    	

    	// Values
    	
    }

	public static DatastreamNamespace onlyInstance = new DatastreamNamespace(ResourceNamespace.getInstance(), "datastream");
	
	public static final DatastreamNamespace getInstance() {
		return onlyInstance;
	}


}

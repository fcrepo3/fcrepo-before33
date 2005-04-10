package fedora.common.policy;

import com.sun.xacml.attr.AnyURIAttribute;
import com.sun.xacml.attr.StringAttribute;

public class BMechNamespace extends XacmlNamespace {

	// Properties
	public final XacmlName ID;
	public final XacmlName STATE;
	public final XacmlName LOCATION;	

    // Values
	
    private BMechNamespace(XacmlNamespace parent, String localName) {
    	super(parent, localName);

        // Properties
    	ID = addName(new XacmlName(this, "id", StringAttribute.identifier)); 
    	STATE = addName(new XacmlName(this, "state", StringAttribute.identifier)); 
    	LOCATION = addName(new XacmlName(this, "location", AnyURIAttribute.identifier));    	

    	// Values
    	
    }

	public static BMechNamespace onlyInstance = new BMechNamespace(ResourceNamespace.getInstance(), "bmech");
	
	public static final BMechNamespace getInstance() {
		return onlyInstance;
	}


}

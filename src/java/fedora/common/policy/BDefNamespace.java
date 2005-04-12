package fedora.common.policy;

import com.sun.xacml.attr.AnyURIAttribute;
import com.sun.xacml.attr.StringAttribute;

public class BDefNamespace extends XacmlNamespace {

	public final XacmlName ID;
	public final XacmlName LOCATION;		
	public final XacmlName STATE;
	
    private BDefNamespace(XacmlNamespace parent, String localName) {
    	super(parent, localName);
    	ID = addName(new XacmlName(this, "id", StringAttribute.identifier)); 
    	LOCATION = addName(new XacmlName(this, "location", AnyURIAttribute.identifier));    	
    	STATE = addName(new XacmlName(this, "state", StringAttribute.identifier)); 
    }

	public static BDefNamespace onlyInstance = new BDefNamespace(ResourceNamespace.getInstance(), "bdef");
	
	public static final BDefNamespace getInstance() {
		return onlyInstance;
	}


}

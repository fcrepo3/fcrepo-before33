package fedora.common.policy;

import com.sun.xacml.attr.AnyURIAttribute;
import com.sun.xacml.attr.DateTimeAttribute;
import com.sun.xacml.attr.StringAttribute;

import fedora.common.Constants;

public class DisseminatorNamespace extends XacmlNamespace {

	// Properties
	public final XacmlName ID;
	public final XacmlName PID;
	public final XacmlName NAMESPACE;
	public final XacmlName STATE;	
	public final XacmlName METHOD;	
	public final XacmlName BDEF_PID;	
	public final XacmlName BMECH_PID;	
	public final XacmlName AS_OF_DATETIME;	

    // Values
	
    private DisseminatorNamespace(XacmlNamespace parent, String localName) {
    	super(parent, localName);

        // Properties
    	ID = addName(new XacmlName(this, "id", StringAttribute.identifier));
    	PID = addName(new XacmlName(this, "pid", StringAttribute.identifier));
    	NAMESPACE = addName(new XacmlName(this, "namespace", StringAttribute.identifier));
    	STATE = addName(new XacmlName(this, "state", StringAttribute.identifier));    	
    	METHOD = addName(new XacmlName(this, "method", StringAttribute.identifier));    	
    	BDEF_PID = addName(new XacmlName(this, "bdefPid", StringAttribute.identifier));    	
    	BMECH_PID = addName(new XacmlName(this, "bmechPid", StringAttribute.identifier));    
    	AS_OF_DATETIME = addName(new XacmlName(this, "asOfDateTime", DateTimeAttribute.identifier));       	

    	// Values
    	
    }

	public static DisseminatorNamespace onlyInstance = new DisseminatorNamespace(ResourceNamespace.getInstance(), "disseminator");
	
	public static final DisseminatorNamespace getInstance() {
		return onlyInstance;
	}


}

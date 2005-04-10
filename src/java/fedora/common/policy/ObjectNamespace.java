package fedora.common.policy;

import com.sun.xacml.attr.DateTimeAttribute;
import com.sun.xacml.attr.StringAttribute;

public class ObjectNamespace extends XacmlNamespace {
	
	// Properties
	public final XacmlName PID;
	public final XacmlName NAMESPACE; //not a "patterning" error; this is the pid prefix, part before ":"	
	public final XacmlName STATE;	
	public final XacmlName CONTROL_GROUP;	
	public final XacmlName OWNER;
	public final XacmlName CONTENT_MODEL;	
	public final XacmlName CREATED_DATETIME;	
	public final XacmlName LAST_MODIFIED_DATETIME;	
	public final XacmlName OBJECT_TYPE;	
	
    // Values
	
    private ObjectNamespace(XacmlNamespace parent, String localName) {
    	super(parent, localName);

        // Properties
    	PID = addName(new XacmlName(this, "pid", StringAttribute.identifier)); 
    	NAMESPACE = addName(new XacmlName(this, "namespace", StringAttribute.identifier)); //see declaration 
    	STATE = addName(new XacmlName(this, "state", StringAttribute.identifier)); 
    	CONTROL_GROUP = addName(new XacmlName(this, "controlGroup", StringAttribute.identifier));    	
    	OWNER = addName(new XacmlName(this, "owner", StringAttribute.identifier));    	
    	CONTENT_MODEL = addName(new XacmlName(this, "contentModel", StringAttribute.identifier)); 
    	CREATED_DATETIME = addName(new XacmlName(this, "createdDate", DateTimeAttribute.identifier)); 
    	LAST_MODIFIED_DATETIME = addName(new XacmlName(this, "lastModifiedDate", DateTimeAttribute.identifier)); 
    	OBJECT_TYPE = addName(new XacmlName(this, "objectType", StringAttribute.identifier)); 
    	// Values
    	
    }

	public static ObjectNamespace onlyInstance = new ObjectNamespace(ResourceNamespace.getInstance(), "object");
	
	public static final ObjectNamespace getInstance() {
		return onlyInstance;
	}


}

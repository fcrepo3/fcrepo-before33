package fedora.common.policy;

import com.sun.xacml.attr.DateTimeAttribute;
import com.sun.xacml.attr.StringAttribute;

public class ResourceNamespace extends XacmlNamespace {
	
	// Properties
	public final XacmlName AS_OF_DATE;	

    private ResourceNamespace(XacmlNamespace parent, String localName) {
    	super(parent, localName);
        // Properties
    	this.AS_OF_DATE = new XacmlName(this, "asOfDate", DateTimeAttribute.identifier);        	

    }

	public static ResourceNamespace onlyInstance = new ResourceNamespace(Release2_1Namespace.getInstance(), "resource");
	static {
		onlyInstance.addNamespace(ObjectNamespace.getInstance()); 
		onlyInstance.addNamespace(DatastreamNamespace.getInstance()); 
		onlyInstance.addNamespace(DisseminatorNamespace.getInstance()); 
		onlyInstance.addNamespace(BDefNamespace.getInstance()); 
		onlyInstance.addNamespace(BMechNamespace.getInstance());		
	}
	
	public static final ResourceNamespace getInstance() {
		return onlyInstance;
	}

}

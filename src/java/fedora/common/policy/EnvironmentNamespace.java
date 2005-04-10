package fedora.common.policy;

import com.sun.xacml.attr.DateAttribute;
import com.sun.xacml.attr.DateTimeAttribute;
import com.sun.xacml.attr.TimeAttribute;

public class EnvironmentNamespace extends XacmlNamespace {
	
	// Properties
	public final XacmlName CURRENT_DATE_TIME;
	public final XacmlName CURRENT_DATE;
	public final XacmlName CURRENT_TIME;	

    // Values

    private EnvironmentNamespace() {
    	super(Release2_1Namespace.getInstance(), "environment");

        // Properties
    	CURRENT_DATE_TIME = addName(new XacmlName(this, "current-date-time", DateTimeAttribute.identifier));
    	CURRENT_DATE = addName(new XacmlName(this, "current-date", DateAttribute.identifier));
    	CURRENT_TIME = addName(new XacmlName(this, "current-time", TimeAttribute.identifier));	

    	// Values

    }

	public static EnvironmentNamespace onlyInstance = new EnvironmentNamespace();
	static {
		onlyInstance.addNamespace(HttpRequestNamespace.getInstance());
	}
	
	public static final EnvironmentNamespace getInstance() {
		return onlyInstance;
	}

}

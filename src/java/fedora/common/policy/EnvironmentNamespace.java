package fedora.common.policy;

public class EnvironmentNamespace extends XacmlNamespace {
	
	// Properties
	public final XacmlName CURRENT_DATE_TIME;
	public final XacmlName CURRENT_DATE;
	public final XacmlName CURRENT_TIME;	

    // Values

    private EnvironmentNamespace() {
    	super(Release2_1Namespace.getInstance(), "environment");

        // Properties
    	CURRENT_DATE_TIME = addName(new XacmlName(this, "current-date-time"));
    	CURRENT_DATE = addName(new XacmlName(this, "current-date"));
    	CURRENT_TIME = addName(new XacmlName(this, "current-time"));	

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

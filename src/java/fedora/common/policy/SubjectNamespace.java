package fedora.common.policy;

public class SubjectNamespace extends XacmlNamespace {
	
	// Properties
	public final XacmlName LOGIN_ID;

    // Values
	
    private SubjectNamespace(XacmlNamespace parent, String localName) {
    	super(parent, localName);

        // Properties
    	this.LOGIN_ID = addName(new XacmlName(this, "login-id")); 

    	// Values
    	
    }

	public static SubjectNamespace onlyInstance = new SubjectNamespace(Release2_1Namespace.getInstance(), "subject");
	
	public static final SubjectNamespace getInstance() {
		return onlyInstance;
	}


}

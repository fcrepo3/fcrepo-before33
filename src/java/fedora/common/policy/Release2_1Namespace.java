package fedora.common.policy;

import java.util.Vector;

public class Release2_1Namespace extends XacmlNamespace {

    private Release2_1Namespace(XacmlNamespace parent, String localName) {
    	super(parent, localName);
    }

	public static Release2_1Namespace onlyInstance = new Release2_1Namespace(FedoraAsProjectNamespace.getInstance(), "2.1");
	static {
		onlyInstance.addNamespace(SubjectNamespace.getInstance()); 
		onlyInstance.addNamespace(ActionNamespace.getInstance()); 
		onlyInstance.addNamespace(ResourceNamespace.getInstance()); 
		onlyInstance.addNamespace(EnvironmentNamespace.getInstance()); 
	}
	
	public static final Release2_1Namespace getInstance() {
		return onlyInstance;
	}
	
	public static final void main (String[] args) {
		Release2_1Namespace instance = Release2_1Namespace.getInstance();
		Vector list = new Vector();
		instance.flatRep(list);
		for (int i=0; i<list.size(); i++) {
			System.out.println(list.get(i));
		} 
	}
	
	//C:\fedora\mellon\dist>
	//java -cp server\jakarta-tomcat-5.0.28\webapps\fedora\WEB-INF\classes;client\lib\jrdf-0.3.3.jar fedora.common.policy.Release2_1Namespace

}

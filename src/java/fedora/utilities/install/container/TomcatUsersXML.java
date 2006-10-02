package fedora.utilities.install.container;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;

import fedora.utilities.XMLDocument;

public class TomcatUsersXML extends XMLDocument {
	public static final String FEDORA_ADMIN_ROLE = "fedoraRole=administrator";
	public static final String FEDORA_BACKEND_ROLE = "fedoraRole=fedoraInternalCall-1";
	public static final String FEDORA_BACKEND_PASSWORD = "changeme";
	
	private Document document;
	
	public TomcatUsersXML(File usersXML) throws FileNotFoundException, DocumentException {
		this(new FileInputStream(usersXML));
	}
	
	public TomcatUsersXML(InputStream usersXML) throws DocumentException {
		super(usersXML);
		document = getDocument();
	}
	
	public void setFedoraAdminPassword(String password) {
		Element adminRole = getRole(FEDORA_ADMIN_ROLE);
		if (adminRole == null) {
			adminRole = document.getRootElement().addElement("role");
			adminRole.addAttribute("rolename", FEDORA_ADMIN_ROLE);
		}
		
		Element[] adminUsers = getUsers(FEDORA_ADMIN_ROLE);
		if (adminUsers.length == 0) {
			Element adminUser = document.getRootElement().addElement("user");
			adminUser.addAttribute("username", "fedoraAdmin");
			adminUser.addAttribute("password", password);
			adminUser.addAttribute("roles", FEDORA_ADMIN_ROLE);
		} else {
			for (int i = 0; i < adminUsers.length; i++) {
				adminUsers[i].addAttribute("password", password);
			}
		}
	}
	
	public void setFedoraBackendRole() {
		String password = "changeme";
		Element adminRole = getRole(FEDORA_BACKEND_ROLE);
		if (adminRole == null) {
			adminRole = document.getRootElement().addElement("role");
			adminRole.addAttribute("rolename", FEDORA_BACKEND_ROLE);
		}
		
		Element[] adminUsers = getUsers(FEDORA_BACKEND_ROLE);
		if (adminUsers.length == 0) {
			Element adminUser = document.getRootElement().addElement("user");
			adminUser.addAttribute("username", "fedoraIntCallUser");
			adminUser.addAttribute("password", password);
			adminUser.addAttribute("roles", FEDORA_BACKEND_ROLE);
		} else {
			for (int i = 0; i < adminUsers.length; i++) {
				adminUsers[i].addAttribute("password", password);
			}
		}
	}
	
	protected Element getRole(String rolename) {
		return (Element)document.selectSingleNode("/tomcat-users/role[@rolename='" + rolename +"']");
	}
	
	protected Element[] getUsers(String role) {
		List list = document.selectNodes("/tomcat-users/user[contains(@roles, '" + role + "')]");
		return (Element[])list.toArray(new Element[list.size()]);
	}
}

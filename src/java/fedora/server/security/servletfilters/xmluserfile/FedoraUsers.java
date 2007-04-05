/* The contents of this file are subject to the license and copyright terms
 * detailed in the license directory at the root of the source tree (also 
 * available online at http://www.fedora.info/license/).
 */

package fedora.server.security.servletfilters.xmluserfile;

import java.beans.IntrospectionException;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.betwixt.io.BeanReader;
import org.apache.commons.betwixt.io.BeanWriter;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import fedora.common.Constants;

public class FedoraUsers implements Serializable, Constants {
	private static final long serialVersionUID = 1L;
	private static final String BETWIXT_MAPPING = "/fedora/server/security/servletfilters/xmluserfile/fedorausers-mapping.xml";
	private List<Role> roles;
	private List<User> users;
	
	public static File fedoraUsersXML = new File(FEDORA_HOME + File.separator + 
			"server" + File.separator + "config" + File.separator + "fedora-users.xml");
	
	public FedoraUsers() {
		roles = new ArrayList<Role>();
		users = new ArrayList<User>();
	}
	
	public static FedoraUsers getInstance() {
		return getInstance(fedoraUsersXML.getAbsolutePath());
	}
	
	public static FedoraUsers getInstance(String fedoraUsersXML) {
		FedoraUsers fu = null;
		BeanReader reader = new BeanReader();
		reader.getXMLIntrospector().getConfiguration().setAttributesForPrimitives(false);
		reader.getBindingConfiguration().setMapIDs(false);

		try {
			reader.registerMultiMapping(getBetwixtMapping());
			fu = (FedoraUsers)reader.parse(fedoraUsersXML);	
		} catch (IntrospectionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return fu;
	}
	
	public List<Role> getRoles() {
		return roles;
	}
	public List<User> getUsers() {
		return users;
	}
	public void addRole(Role role) {
		roles.add(role);
	}
	public void addUser(User user) {
		users.add(user);
	}
	
	public void write(Writer outputWriter) throws IOException {
		outputWriter.write("<?xml version='1.0' ?>\n");
		
		BeanWriter beanWriter = new BeanWriter(outputWriter);
		beanWriter.getBindingConfiguration().setMapIDs(false);
		beanWriter.setWriteEmptyElements(false);
		beanWriter.enablePrettyPrint();
		try {
			beanWriter.getXMLIntrospector().register(getBetwixtMapping());
			beanWriter.write("users", this);
		} catch (IntrospectionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		beanWriter.flush();
		beanWriter.close();
	}
	
	private static InputSource getBetwixtMapping() {
		return new InputSource(FedoraUsers.class.getResourceAsStream(BETWIXT_MAPPING));
	}
}

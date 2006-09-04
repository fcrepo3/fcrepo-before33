package fedora.utilities.install.webxml;

import java.util.Iterator;
import java.util.Set;

import org.dom4j.Document;
import org.dom4j.Element;

abstract public class SecurityConstraint {
	private Document document;
	private Element scElement;
	private Element wrcElement;
	
	public SecurityConstraint(Document document, WebXMLOptions options) {
		this.document = document;
		scElement = addSecurityConstraint();
		wrcElement = scElement.element("web-resource-collection");
		setUrlPatterns();
		setHttpMethods();
	}
	
	public abstract Set getUrlPatterns();
	
	public abstract Set getHttpMethods();
	
	protected void addAuthConstraint() {
		Element ac = scElement.element("auth-constraint");
		if (ac == null) {
			ac = scElement.addElement("auth-constraint");
		}
		Element rn = ac.addElement("role-name");
		if (rn == null) {
			rn = ac.addElement("role-name");
		}
		if (!rn.getText().equals("*")) {
			rn.setText("*");
		}
	}
	
	protected void addUserDataConstraint() {
		Element udc = scElement.element("user-data-constraint");
		if (udc == null) {
			udc = scElement.addElement("user-data-constraint");
		}
		Element tg = udc.addElement("transport-guarantee");
		if (tg == null) {
			tg = udc.addElement("transport-guarantee");
		}
		if (!tg.getText().equals("CONFIDENTIAL")) {
			tg.setText("CONFIDENTIAL");
		}
	}
	
	protected boolean removeAuthConstraint() {
		scElement.remove(scElement.element("auth-constraint"));
		
		Element ac = scElement.element("auth-constraint");
		if (ac == null) {
			return true;
		}
		return scElement.remove(ac);
	}
	
	protected boolean removeUserDataConstraint() {
		Element udc = scElement.element("user-data-constraint");
		if (udc == null) {
			return true;
		}
		return scElement.remove(udc);
	}
	
	private Element addSecurityConstraint() {
		Element root = document.getRootElement();
		Element scElement = root.addElement("security-constraint");
		Element wrcElement = scElement.addElement("web-resource-collection");
		Element wrnElement = wrcElement.addElement("web-resource-name");
		wrnElement.setText("Fedora Repository Server");
		Element descElement = wrcElement.addElement("description");
		descElement.setText(WebXML.FEDORA_GENERATED);
		return scElement;
	}
	
	private void setUrlPatterns() {
		Iterator it = getUrlPatterns().iterator();
		while (it.hasNext()) {
			Element up = wrcElement.addElement("url-pattern");
			up.setText((String)it.next());
		}
	}
	
	private void setHttpMethods() {
		Iterator it = getHttpMethods().iterator();
		while (it.hasNext()) {
			Element up = wrcElement.addElement("http-method");
			up.setText((String)it.next());
		}
	}
}

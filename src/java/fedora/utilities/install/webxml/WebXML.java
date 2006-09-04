package fedora.utilities.install.webxml;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.List;

import org.dom4j.DocumentException;
import org.dom4j.Element;

import fedora.utilities.XMLDocument;
import fedora.utilities.install.InstallOptions;

/**
 * 
 * @author Edwin Shin
 *
 */
public class WebXML extends XMLDocument {	
	protected static final String FEDORA_GENERATED = "Fedora-generated security-constraint";
	
	private WebXMLOptions options;
	
	public WebXML(File webXML, InstallOptions installOptions) throws FileNotFoundException, DocumentException {
		this(new FileInputStream(webXML), installOptions);
	}
	
	public WebXML(InputStream webXML, InstallOptions installOptions) throws DocumentException {
		super(webXML);
		options = new WebXMLOptions(installOptions);
	}
	
	public void setSecurityConstraints() {
		// clear out any Fedora-generated security constraint blocks
		removeSecurityConstraints();
		
		// create new, required security constraints
		new DefaultSecurityConstraint(getDocument(), options);
		new JSPSecurityConstraint(getDocument(), options);
		new GetDSSecurityConstraint(getDocument(), options);
		new MControlSecurityConstraint(getDocument(), options);
		new APIMSecurityConstraint(getDocument(), options);
		new APIASecurityConstraint(getDocument(), options);
	}
	
	private void removeSecurityConstraints() {
		List list = getDocument().selectNodes("/web-app/*[local-name()='security-constraint']");
		Iterator iter = list.iterator();
		while (iter.hasNext()) {
			Element sc = (Element) iter.next();
			// Assuming only one web-resource-collection per security-constraint element
			Element wrc = sc.element("web-resource-collection");
			Element desc = wrc.element("description");
			if (desc != null && desc.getText().indexOf(FEDORA_GENERATED) != -1) {
				sc.getParent().remove(sc);
			}
		}
	}
}

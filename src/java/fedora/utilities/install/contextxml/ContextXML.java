package fedora.utilities.install.contextxml;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.List;

import org.dom4j.DocumentException;
import org.dom4j.Node;

import fedora.utilities.XMLDocument;

/**
 * Tomcat's META-INF/context.xml file.
 *
 * @author cwilper@cs.cornell.edu
 */
public class ContextXML extends XMLDocument {

    /**
     * Gets an instance populated with the given file.
     */
	public ContextXML(File contextXML)
	        throws FileNotFoundException, DocumentException {
		this(new FileInputStream(contextXML));
	}

    /**
     * Gets an instance populated with the given stream.
     */
    public ContextXML(InputStream in)
            throws DocumentException {
        super(in);
    }

    /**
     * Sets the realm classname to the given value.
     */
    public void setRealmClassName(String className) {
        Node classNameNode = getDocument().selectSingleNode(
                "/Context/Realm/@className");
        classNameNode.setText(className);
    }
	
}

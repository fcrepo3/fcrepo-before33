/*
 * Created on Aug 12, 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package fedora.server.security;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletContext;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Element;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import com.sun.xacml.AbstractPolicy;
import com.sun.xacml.EvaluationCtx;
import com.sun.xacml.ParsingException;
import com.sun.xacml.Policy;
import com.sun.xacml.PolicySet;
import com.sun.xacml.Rule;
import com.sun.xacml.Target;
import com.sun.xacml.combine.OrderedDenyOverridesPolicyAlg;
import com.sun.xacml.ctx.Status;
import com.sun.xacml.finder.PolicyFinder;
import com.sun.xacml.finder.PolicyFinderModule;
import com.sun.xacml.finder.PolicyFinderResult;
import fedora.server.ReadOnlyContext;
import fedora.server.errors.GeneralException;
import fedora.server.errors.ObjectNotInLowlevelStorageException;
import fedora.server.errors.ServerException;
import fedora.server.storage.DOManager;
import fedora.server.storage.DOReader;
import fedora.server.storage.lowlevel.FileSystemLowlevelStorage;
import fedora.server.storage.lowlevel.ILowlevelStorage;
import fedora.server.storage.types.Datastream;

/**
 * @author wdn5e
 * to understand why this class is needed 
 * (why configuring the xacml pdp with all of the multiplexed policy finders just won't work),
 * @see http://sourceforge.net/mailarchive/message.php?msg_id=6068981
 */
public class ValidatePolicy extends PolicyFinderModule { 
	private File schemaFile = null;

	public ValidatePolicy() {
		String schemaName = System.getProperty(POLICY_SCHEMA_PROPERTY);
		if (schemaName != null) {
			schemaFile = new File(schemaName);
			System.err.println("using schemaFile="+schemaFile);
		}
	}

	public static final String POLICY_SCHEMA_PROPERTY = "com.sun.xacml.PolicySchema";

	public static final String JAXP_SCHEMA_LANGUAGE = "http://java.sun.com/xml/jaxp/properties/schemaLanguage";

	public static final String W3C_XML_SCHEMA = "http://www.w3.org/2001/XMLSchema";

	public static final String JAXP_SCHEMA_SOURCE = "http://java.sun.com/xml/jaxp/properties/schemaSource";
	
	private final DocumentBuilder getDocumentBuilder(ErrorHandler handler) throws ParserConfigurationException {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setIgnoringComments(true);

		DocumentBuilder builder = null;

		// as of 1.2, we always are namespace aware
		factory.setNamespaceAware(true);

		if (schemaFile == null) {
			System.err.println("not validating");			
			factory.setValidating(false);
			builder = factory.newDocumentBuilder();
		} else {
			System.err.println("validating against "+schemaFile);			
			factory.setValidating(true);
			factory.setAttribute(JAXP_SCHEMA_LANGUAGE, W3C_XML_SCHEMA);
			factory.setAttribute(JAXP_SCHEMA_SOURCE, schemaFile);
			builder = factory.newDocumentBuilder();
			builder.setErrorHandler(handler);
		}
		return builder;
	}
	
	

    public void init(PolicyFinder finder) {
    }
	public static void main(String[] args) {
		String filepath = args[0];
		File file = new File(filepath);
		if (! file.exists()) {
			System.err.println(filepath + " does not exist");
		} else if (! file.canRead()) {
			System.err.println("cannot read " + filepath);
		} else {
			ValidatePolicy policyChecker = new ValidatePolicy();
			String name = "";
			Element rootElement = null;
			try {
				DocumentBuilder builder = policyChecker.getDocumentBuilder(null);
				builder.setErrorHandler(policyChecker.new MyErrorHandler());
				rootElement = builder.parse(file).getDocumentElement();
				name = rootElement.getTagName();
			} catch (Throwable e) {
				System.err.println("couldn't parse repo-wide policy");
				System.err.println(e);
				e.printStackTrace();		
			}
	        AbstractPolicy abstractPolicy = null;
			try {
				if ("Policy".equals(name)) {
					System.err.println("root node is Policy");
					abstractPolicy = Policy.getInstance(rootElement);
				} else if ("PolicySet".equals(name)) {
					System.err.println("root node is PolicySet");
					abstractPolicy = PolicySet.getInstance(rootElement);
				} else {
					System.err.println("bad root node for repo-wide policy");
				}
			} catch (ParsingException e) {
				System.err.println("couldn't parse repo-wide policy");
				System.err.println(e);
				e.printStackTrace();
			}
		}
	}
	
	class MyErrorHandler implements ErrorHandler {

		/* (non-Javadoc)
		 * @see org.xml.sax.ErrorHandler#error(org.xml.sax.SAXParseException)
		 */
		public void error(SAXParseException exception) throws SAXException {
			System.err.println("error via handler");
			System.err.println(exception);
			exception.printStackTrace();
		}

		/* (non-Javadoc)
		 * @see org.xml.sax.ErrorHandler#fatalError(org.xml.sax.SAXParseException)
		 */
		public void fatalError(SAXParseException exception) throws SAXException {
			System.err.println("fatal error via handler");
			System.err.println(exception);
			exception.printStackTrace();
		}

		/* (non-Javadoc)
		 * @see org.xml.sax.ErrorHandler#warning(org.xml.sax.SAXParseException)
		 */
		public void warning(SAXParseException exception) throws SAXException {
			System.err.println("warning via handler");
			System.err.println(exception);
			exception.printStackTrace();
		}
		
	}
	
}

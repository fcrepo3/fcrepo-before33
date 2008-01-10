/* The contents of this file are subject to the license and copyright terms
 * detailed in the license directory at the root of the source tree (also 
 * available online at http://www.fedora.info/license/).
 */

package fedora.server.security;

import java.io.File;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import com.sun.xacml.AbstractPolicy;
import com.sun.xacml.ParsingException;
import com.sun.xacml.Policy;
import com.sun.xacml.PolicySet;
import com.sun.xacml.finder.PolicyFinder;
import com.sun.xacml.finder.PolicyFinderModule;

import org.apache.log4j.Logger;

import org.w3c.dom.Element;

import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import fedora.common.Constants;

/**
 * @author Bill Niebel
 * @see http://sourceforge.net/mailarchive/message.php?msg_id=6068981
 *      to understand why this class is needed (why configuring the xacml pdp
 *      with all of the multiplexed policy finders just won't work)
 */
public class ValidatePolicy
        extends PolicyFinderModule {

    public static final String POLICY_SCHEMA_PROPERTY =
            "com.sun.xacml.PolicySchema";

    public static final String JAXP_SCHEMA_LANGUAGE =
            "http://java.sun.com/xml/jaxp/properties/schemaLanguage";

    public static final String W3C_XML_SCHEMA = Constants.XML_XSD.uri;

    public static final String JAXP_SCHEMA_SOURCE =
            "http://java.sun.com/xml/jaxp/properties/schemaSource";

    /** Logger for this class. */
    private static final Logger LOG =
            Logger.getLogger(ValidatePolicy.class.getName());

    private File schemaFile = null;

    public ValidatePolicy() {
        String schemaName = System.getProperty(POLICY_SCHEMA_PROPERTY);
        if (schemaName != null) {
            schemaFile = new File(schemaName);
            LOG.debug("using schemaFile=" + schemaFile);
        }
    }

    private final DocumentBuilder getDocumentBuilder(ErrorHandler handler)
            throws ParserConfigurationException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setIgnoringComments(true);

        DocumentBuilder builder = null;

        // as of 1.2, we always are namespace aware
        factory.setNamespaceAware(true);

        if (schemaFile == null) {
            LOG.debug("not validating");
            factory.setValidating(false);
            builder = factory.newDocumentBuilder();
        } else {
            LOG.debug("validating against " + schemaFile);
            factory.setValidating(true);
            factory.setAttribute(JAXP_SCHEMA_LANGUAGE, W3C_XML_SCHEMA);
            factory.setAttribute(JAXP_SCHEMA_SOURCE, schemaFile);
            builder = factory.newDocumentBuilder();
            builder.setErrorHandler(handler);
        }
        return builder;
    }

    @Override
    public void init(PolicyFinder finder) {
    }

    public static void main(String[] args) {
        String filepath = args[0];
        File file = new File(filepath);
        if (!file.exists()) {
            LOG.error(filepath + " does not exist");
            System.exit(1);
        } else if (!file.canRead()) {
            LOG.error("cannot read " + filepath);
            System.exit(1);
        } else {
            ValidatePolicy policyChecker = new ValidatePolicy();
            String name = "";
            Element rootElement = null;
            try {
                DocumentBuilder builder =
                        policyChecker.getDocumentBuilder(null);
                builder.setErrorHandler(policyChecker.new MyErrorHandler());
                rootElement = builder.parse(file).getDocumentElement();
                name = rootElement.getTagName();
            } catch (Throwable e) {
                LOG.error("couldn't parse repo-wide policy", e);
                System.exit(1);
            }
            AbstractPolicy abstractPolicy = null;
            try {
                if ("Policy".equals(name)) {
                    LOG.debug("root node is Policy");
                    abstractPolicy = Policy.getInstance(rootElement);
                } else if ("PolicySet".equals(name)) {
                    LOG.debug("root node is PolicySet");
                    abstractPolicy = PolicySet.getInstance(rootElement);
                } else {
                    LOG.debug("bad root node for repo-wide policy");
                }
            } catch (ParsingException e) {
                LOG.error("couldn't parse repo-wide policy", e);
                System.exit(1);
            }
        }
    }

    class MyErrorHandler
            implements ErrorHandler {

        /**
         * {@inheritDoc}
         */
        public void error(SAXParseException exception) throws SAXException {
            throw exception;
        }

        /**
         * {@inheritDoc}
         */
        public void fatalError(SAXParseException exception) throws SAXException {
            throw exception;
        }

        /**
         * {@inheritDoc}
         */
        public void warning(SAXParseException exception) throws SAXException {
            LOG.warn("Sax warning while parsing", exception);
        }

    }

}

/*
 * Created on Apr 26, 2005
 */
package fedora.server.security;

import org.apache.log4j.Logger;

import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/**
 * @author wdn5e@virginia.edu
 */
public class SchemaErrorHandler implements ErrorHandler {

    /** Logger for this class. */
    private static final Logger LOG = Logger.getLogger(
            SchemaErrorHandler.class.getName());

	public void error(SAXParseException exception) throws SAXException {
		LOG.error("Schema error", exception);
		throw exception;
	}
	public void fatalError(SAXParseException exception) throws SAXException {
		LOG.error("Schema fatalError", exception);
		throw exception;		
	}
	public void warning(SAXParseException exception) throws SAXException {
		LOG.error("Schema warning", exception);
		throw exception;		
	}
	
}

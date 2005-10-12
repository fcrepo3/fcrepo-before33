/*
 * Created on Apr 26, 2005
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package fedora.server.security;

import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/**
 * @author wdn5e@virginia.edu
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class SchemaErrorHandler implements ErrorHandler {
	public void error(SAXParseException exception) throws SAXException {
		System.err.println("schema error " + exception.getMessage());
		throw exception;
	}
	public void fatalError(SAXParseException exception) throws SAXException {
		System.err.println("schema fatalError " + exception.getMessage());		
		throw exception;		
	}
	public void warning(SAXParseException exception) throws SAXException {
		System.err.println("schema warning " + exception.getMessage());
		throw exception;		
	}
	
	public static void main(String[] args) {
	}
}

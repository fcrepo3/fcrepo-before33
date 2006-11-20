package fedora.server.storage;

import org.apache.log4j.Logger;

import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;;

/**
 * @author payette@cs.cornell.edu
 * @version $Id$
 */
public class DOReaderSAXErrorHandler implements ErrorHandler
{

  /** Logger for this class. */
  private static final Logger LOG = Logger.getLogger(
        DOReaderSAXErrorHandler.class.getName());

  public DOReaderSAXErrorHandler()
  {
  }

  public void warning(SAXParseException e) throws SAXException
  {
    LOG.warn("SAX WARNING (publicId=" + e.getPublicId()
            + ", line=" + e.getLineNumber() + ")", e);
  }

  public void error(SAXParseException e) throws SAXException
  {
    LOG.warn("SAX ERROR (publicId=" + e.getPublicId()
            + ", line=" + e.getLineNumber() + ")", e);
  }

  public void fatalError(SAXParseException e) throws SAXException
  {
    LOG.error("SAX FATAL ERROR (publicId=" + e.getPublicId()
            + ", line=" + e.getLineNumber() + ")", e);
    throw e;
  }

}
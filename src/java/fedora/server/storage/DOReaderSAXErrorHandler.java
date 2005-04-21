package fedora.server.storage;

import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;;

/**
 *
 * <p><b>Title:</b> DOReaderSAXErrorHandler.java</p>
 * <p><b>Description:</b> </p>
 *
 * @author payette@cs.cornell.edu
 * @version $Id$
 */
public class DOReaderSAXErrorHandler implements ErrorHandler
{

  public DOReaderSAXErrorHandler()
  {
  }

  public void warning(SAXParseException e) throws SAXException
  {
    System.err.print("SAX WARNING: ");
    printPubID(e);
    printMsg(e);
  }

  public void error(SAXParseException e) throws SAXException
  {
    System.err.print("SAX ERROR: ");
    printPubID(e);
    printMsg(e);
    printStack(e);
  }

  public void fatalError(SAXParseException e) throws SAXException
  {
    System.err.print("SAX FATAL ERROR: ");
    printPubID(e);
    printMsg(e);
    printStack(e);
    throw e;
  }

  private void printPubID(SAXParseException e)
  {
    if (e.getPublicId() != null)
    {
      System.err.print(e.getPublicId() + " ");
    }
    if (e.getLineNumber() != -1)
    {
      System.err.print("line: " + e.getLineNumber() + " ");
    }
  }

  private void printMsg(SAXParseException e)
  {
    if (e.getMessage() != null)
    {
      System.err.println(e.getMessage());
    }
  }

  private void printStack(SAXParseException e)
  {
    Exception exception = e;
    while (exception != null)
    {
      exception.printStackTrace();
      if (exception instanceof SAXException)
        exception = ((SAXException) exception).getException();
    }
  }
}
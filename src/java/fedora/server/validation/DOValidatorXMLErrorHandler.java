package fedora.server.validation;

import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;;


/**
 *
 * <p><b>Title:</b> ValidatorXMLErrorHandler.java</p>
 * <p><b>Description:</b> </p>
 *
 * -----------------------------------------------------------------------------
 *
 * <p><b>License and Copyright: </b>The contents of this file are subject to the
 * Mozilla Public License Version 1.1 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License
 * at <a href="http://www.mozilla.org/MPL">http://www.mozilla.org/MPL/.</a></p>
 *
 * <p>Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License for
 * the specific language governing rights and limitations under the License.</p>
 *
 * <p>The entire file consists of original code.  Copyright &copy; 2002, 2003 by The
 * Rector and Visitors of the University of Virginia and Cornell University.
 * All rights reserved.</p>
 *
 * -----------------------------------------------------------------------------
 *
 * @author payette@cs.cornell.edu
 * @version $Id$
 */
public class DOValidatorXMLErrorHandler implements ErrorHandler
{

  public DOValidatorXMLErrorHandler()
  {
  }

  public void warning(SAXParseException e) throws SAXException
  {
    System.err.print("ValidationErrorHandler says SAX WARNING: ");
    printPubID(e);
    printMsg(e);
  }

  public void error(SAXParseException e) throws SAXException
  {
    System.err.print("ValidationErrorHandler says SAX ERROR found.  Re-throwing SAXException.");
    //printPubID(e);
    //printMsg(e);
    //printStack(e);
    throw new SAXException(formatParseExceptionMsg(e));
  }

  public void fatalError(SAXParseException e) throws SAXException
  {
    System.err.print("ValidationErrorHandler says SAX FATAL ERROR found.  Re-throwing SAXException.");
    //printPubID(e);
    //printMsg(e);
    //printStack(e);
    throw new SAXException(formatParseExceptionMsg(e));
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

  private String formatParseExceptionMsg(SAXParseException spe)
  {
    String systemId = spe.getSystemId();
    if (systemId == null) {
        systemId = "null";
    }
    String info = "URI=" + systemId +
        " Line=" + spe.getLineNumber() +
        ": " + spe.getMessage();
    return info;
  }
}
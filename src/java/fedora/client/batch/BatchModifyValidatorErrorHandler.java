package fedora.client.batch;

import java.io.PrintStream;

import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/**
 *
 * <p><b>Title:</b> BatchModifyValidatorErrorHandler.java</p>
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
 * <p>The entire file consists of original code.  Copyright &copy; 2002-2004 by The
 * Rector and Visitors of the University of Virginia and Cornell University.
 * All rights reserved.</p>
 *
 * -----------------------------------------------------------------------------
 *
 * @author rlw@virginia.edu
 * @version $Id$
 */
public class BatchModifyValidatorErrorHandler implements ErrorHandler {

    protected static int errorCount = 0;
    private static PrintStream out;

  public BatchModifyValidatorErrorHandler(PrintStream out)
  {
      BatchModifyValidatorErrorHandler.out = out;
      BatchModifyValidatorErrorHandler.errorCount = 0;
  }

  public void warning(SAXParseException e) throws SAXException {
      System.err.println("BatchModifyValidatorErrorHandler detected SAX WARNING: ");
      logError(e);
  }

  public void error(SAXParseException e) throws SAXException {
      System.err.println("BatchModifyValidatorErrorHandler detected SAX ERROR: ");
      logError(e);
  }

  public void fatalError(SAXParseException e) throws SAXException {
      System.err.print("BatchModifyValidatorErrorHandler detected SAX FATAL ERROR:");
      logError(e);
  }


  private static void printPubID(SAXParseException e) {
      if (e.getPublicId() != null) {
          System.err.print(e.getPublicId() + " ");
          out.println("    "+e.getPublicId() + " ");
      }
      if (e.getLineNumber() != -1) {
          System.err.println("[modify-directives-file] Parsing error at line: " + e.getLineNumber() + " column: " + e.getColumnNumber() + " ");
          out.println("    [modify-directives-file] Parsing error at line: " + e.getLineNumber() + " column: " + e.getColumnNumber() + " ");
      }
  }

  private static void printMsg(SAXParseException e) {
      System.err.println(e.getClass().getName() + " - "
          + (e.getMessage()==null ? "(no detail provided)" : e.getMessage()));
      out.println("    "+e.getClass().getName() + " - "
          + (e.getMessage()==null ? "(no detail provided)" : e.getMessage()));
  }

  private static void logError(SAXParseException e) {
      out.println("  <parser-error>");
      printPubID(e);
      printMsg(e);
      out.println("  </parser-error>");
      errorCount++;
    }

}
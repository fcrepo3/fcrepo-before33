package fedora.client.batch;
import fedora.server.utilities.StreamUtility;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.PrintStream;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;


/**
 *
 * <p><b>Title:</b> BatchModifyValidator.java</p>
 * <p><b>Description:</b> A class for parsing the xml modify directives in the
 * Batch Modify input file. The parsing is configured to parse directives in the
 * file sequentially. Logs are written for each successful and failed directive
 * that is processed. Recoverable(non-fatal) errors are written to the log file
 * and processing continues. Catastrophic errors will cause parsing to halt and
 * set the count of failed directives to -1 indicating that parsing was halted
 * prior to the end of the file. In this case the logs will contain all directives
 * processed up to the point of failure.</p>
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
public class BatchModifyValidator extends DefaultHandler
{
    //private InputStream in;
    private static PrintStream out;
    private boolean isValid = false;
    private static int errorCount = 0;

    /**
     * <p>Constructor allows this class to initiate the parsing.</p>
     *
     * @param UPLOADER - An instance of Uploader.
     * @param APIM - An instance of FedoraAPIM.
     * @param in - An input stream containing the xml to be parsed.
     * @param out - A print stream used for writing log info.
     * @throws Exception - If an error occurs in configuring
     *                     the SAX parser.
     */
    public BatchModifyValidator(InputStream in, PrintStream out) throws Exception
    {
        //this.in = in;
        BatchModifyValidator.out = out;
        BatchModifyValidator.errorCount = 0;
        XMLReader xmlReader = null;


        // Configure the SAX parser.
        BatchModifyValidatorErrorHandler errorHandler =
                new BatchModifyValidatorErrorHandler(out);
        try
        {
            SAXParserFactory saxfactory=SAXParserFactory.newInstance();
            saxfactory.setValidating(true);
            SAXParser parser=saxfactory.newSAXParser();
            xmlReader=parser.getXMLReader();
            xmlReader.setContentHandler(this);
            xmlReader.setFeature("http://xml.org/sax/features/namespaces", true);
            xmlReader.setFeature("http://xml.org/sax/features/namespace-prefixes", true);
            xmlReader.setFeature("http://apache.org/xml/features/validation/schema", true);
            xmlReader.setFeature("http://apache.org/xml/features/continue-after-fatal-error", true);
            xmlReader.setErrorHandler(errorHandler);
        }
        catch (Exception e)
        {
            System.err.println("ERROR: "+e.getClass().getName()
                               + " - " + (e.getMessage()==null ? "(no detail provided)" : e.getMessage()));
            logError(e, "(no detail provided.)");
            errorCount = BatchModifyValidatorErrorHandler.errorCount;
            if (errorCount==0)
                errorCount++;
            isValid = false;
        }

        // Parse the file.
        try
        {
            xmlReader.parse(new InputSource(in));
            errorCount = BatchModifyValidatorErrorHandler.errorCount;
            if (BatchModifyValidatorErrorHandler.errorCount == 0) {
            	isValid = true;
            }
        }
        catch (Exception e)
        {
            System.err.println("ERROR: "+e.getClass().getName()
                               + " - " + (e.getMessage()==null ? "(no detail provided)" : e.getMessage()));
            logError(e, "(no detail provided.)");
            errorCount = BatchModifyValidatorErrorHandler.errorCount;
            isValid = false;

        }
    }

    public boolean isValid() {
        return isValid;
    }

    public int getErrorCount() {
        return errorCount;
    }

    private static void logError(Exception e, String msg) {
        out.println("  <parser-error>");
        if (e!=null) {
            String message=e.getMessage();
            if (message==null)
                message=e.getClass().getName();
            out.println("    " + StreamUtility.enc(message));
        } else {
            out.println("    " + StreamUtility.enc(msg));
        }
        out.println("  </parser-error>");
    }

    public static void main(String[] args)
    {

        try {
            PrintStream log = new PrintStream(new FileOutputStream("c:\\zlogfile.txt"));
            InputStream file = new FileInputStream("c:\\fedora\\mellon\\dist\\client\\demo\\batch-demo\\modify-batch-directives.xml");
            BatchModifyValidator bmv = new BatchModifyValidator(file, log);
            file.close();
        } catch (Exception e) {
            System.out.println("ERROR: "+e.getClass().getName()
                               + " - " + (e.getMessage()==null ? "(no detail provided)" : e.getMessage()));
        }
    }

}

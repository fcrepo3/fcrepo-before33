package fedora.server.validation;

// JAXP imports
import javax.xml.parsers.*;
import org.xml.sax.*;
import org.xml.sax.helpers.*;

import java.io.File;
import java.io.InputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URI;

import fedora.server.errors.ObjectValidityException;
import fedora.server.errors.GeneralException;
import fedora.server.errors.ServerException;

/**
 *
 * <p><b>Title:</b> DOValidatorXMLSchema.java</p>
 * <p><b>Description:</b> XML Schema validation for Digital Objects</p>
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
public class DOValidatorXMLSchema
{
    /** Constants used for JAXP 1.2 */
    private static final String JAXP_SCHEMA_LANGUAGE =
        "http://java.sun.com/xml/jaxp/properties/schemaLanguage";
    private static final String W3C_XML_SCHEMA =
        "http://www.w3.org/2001/XMLSchema";
    private static final String JAXP_SCHEMA_SOURCE =
        "http://java.sun.com/xml/jaxp/properties/schemaSource";

    private URI schemaURI = null;
    private DOIntegrityHandler iHandler = null; // new

    public static void main(String[] args)
    {
      if (args.length < 2)
      {
        System.err.println("usage: java DOValidatorXMLSchema schemaLocation " +
          "objectLocation" + "\n" +
          "  schemaLocation: the file path of the XML schema to validate against" + "\n" +
          "  objectLocation: the file path of the object to be validated");
        System.exit(1);
      }

      try
      {
        DOValidatorXMLSchema dov = new DOValidatorXMLSchema(args[0]);
        dov.validate(new File(args[1]));
      }
      catch (ServerException e)
      {
        System.out.println("DOValidatorXMLSchema caught ServerException in main().");
        System.out.println("Suppressing message since not attached to Server.");
      }
      catch (Throwable th)
      {
        System.out.println("DOValidatorXMLSchema returned error in main(). "
                  + "The underlying error was a " + th.getClass().getName()
                  + "The message was "  + "\"" + th.getMessage() + "\"");
      }
    }

    public DOValidatorXMLSchema(String schemaPath) throws GeneralException
    {
      try
      {
        //System.out.println("XML Schema Path: " + schemaPath);
        schemaURI = (new File(schemaPath)).toURI();
        //System.out.println("XML Schema URI: " + schemaURI);
      }
      catch (Exception e)
      {
        System.err.println("DOValidatorXMLSchema caught ERROR in Constructor: "
          + e.getMessage());
        throw new GeneralException(e.getMessage());
      }
    }

    public void validate(File objectAsFile)
      throws ObjectValidityException, GeneralException
    {
      try
      {
        validate(new InputSource(new FileInputStream(objectAsFile)));
      }
      catch (IOException e)
      {
        String msg = "DOValidatorXMLSchema returned error. "
                  + "The underlying exception was a " + e.getClass().getName() + ".  "
                  + "The message was "  + "\"" + e.getMessage() + "\"";
        throw new GeneralException(msg);
      }
    }

    public void validate(InputStream objectAsStream)
      throws ObjectValidityException, GeneralException
    {
      validate(new InputSource(objectAsStream));
    }

    public void validate(InputSource objectAsSource)
      throws ObjectValidityException, GeneralException
    {
      InputSource doXML = objectAsSource;
      try
      {
      // XMLSchema validation via SAX parser
      SAXParserFactory spf = SAXParserFactory.newInstance();
      spf.setNamespaceAware(true);
      spf.setValidating(true);
      SAXParser sp = spf.newSAXParser();
      sp.setProperty(JAXP_SCHEMA_LANGUAGE, W3C_XML_SCHEMA);

      // JAXP property for schema location
      sp.setProperty(
        "http://java.sun.com/xml/jaxp/properties/schemaSource",
        schemaURI.toString());

      iHandler = new DOIntegrityHandler(); // new
      XMLReader xmlreader = sp.getXMLReader();
      xmlreader.setContentHandler(iHandler); // new
      xmlreader.setErrorHandler(new DOValidatorXMLErrorHandler());
      xmlreader.parse(doXML);
      }
      catch (ParserConfigurationException e)
      {
        String msg = "DOValidatorXMLSchema returned parser error. "
                  + "The underlying exception was a " + e.getClass().getName() + ".  "
                  + "The message was "  + "\"" + e.getMessage() + "\"";
        throw new GeneralException(msg);
      }
      catch (SAXException e)
      {
        String msg = "DOValidatorXMLSchema returned validation exception. "
                  + "The underlying exception was a " + e.getClass().getName() + ".  "
                  + "The message was "  + "\"" + e.getMessage() + "\"";
        throw new ObjectValidityException(msg);
      }
      catch (Exception e)
      {
        String msg = "DOValidatorXMLSchema returned error. "
                  + "The underlying error was a " + e.getClass().getName() + ".  "
                  + "The message was "  + "\"" + e.getMessage() + "\"";
        throw new GeneralException(msg);
      }
    }

    public DOIntegrityVariables getDOIntegrityVariables()
    {
      return iHandler.iVars;
    }
}
package fedora.server.validation;

/**
 * <p>Title: DOValidatorXMLSchema</p>
 * <p>Description: XML Schema validation for Digital Objects</p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 * @author Sandy Payette, payette@cs.cornell.edu
 * @version 1.0
 */

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
        System.err.println("usage: java DOValidatorXMLSchema schemaLocation objectLocation" + "\n" +
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

    public DOValidatorXMLSchema(String schemaID) throws GeneralException
    {
      try
      {
        System.out.println("XML Schema location: " + schemaID);
        schemaURI = new URI(schemaID);
      }
      catch (Exception e)
      {
        System.err.println("DOValidatorXMLSchema says caught ERROR in Constructor: " + e.getMessage());
        throw new GeneralException(e.getMessage());
      }
    }

    public DOValidatorXMLSchema(File schema) throws GeneralException
    {
      try
      {
        File schemaFile = schema;
        schemaURI = schemaFile.toURI();
      }
      catch (Exception e)
      {
        System.err.println("DOValidatorXMLSchema says caught ERROR in Constructor: " + e.getMessage());
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
        System.out.println(msg);
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
      sp.setProperty("http://java.sun.com/xml/jaxp/properties/schemaSource", schemaURI.toString());
      //sp.setProperty("http://java.sun.com/xml/jaxp/properties/schemaSource", schemaFile);

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
        System.out.println(msg);
        throw new GeneralException(msg);
      }
      catch (SAXException e)
      {
        String msg = "DOValidatorXMLSchema returned validation exception. "
                  + "The underlying exception was a " + e.getClass().getName() + ".  "
                  + "The message was "  + "\"" + e.getMessage() + "\"";
        System.out.println(msg);
        throw new ObjectValidityException(msg);
      }
      catch (Exception e)
      {
        String msg = "DOValidatorXMLSchema returned error. "
                  + "The underlying error was a " + e.getClass().getName() + ".  "
                  + "The message was "  + "\"" + e.getMessage() + "\"";
        System.out.println(msg);
        throw new GeneralException(msg);
      }
    }

    public DOIntegrityVariables getDOIntegrityVariables()
    {
      return iHandler.iVars;
    }
}
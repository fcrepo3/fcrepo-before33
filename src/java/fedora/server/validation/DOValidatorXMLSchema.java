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
      catch (SAXException e)
      {
        System.err.println("DOValidatorXMLSchema says SAXException in main(): " + e.getMessage());
      }
      catch (ParserConfigurationException e)
      {
        System.err.println("DOValidatorXMLSchema says ParserConfigurationException in main(): " + e.getMessage());
      }
      catch (IOException e)
      {
        System.err.println("DOValidatorXMLSchema says IOException in main(): " + e.getMessage());
      }
    }

    public DOValidatorXMLSchema(String schemaID)
    {
      try
      {
        schemaURI = new URI(schemaID);
      }
      catch (Exception e)
      {
        System.err.println("DOValidatorXMLSchema says caught ERROR in Constructor: " + e.getMessage());
      }
    }

    public DOValidatorXMLSchema(File schema)
    {
      File schemaFile = schema;
      schemaURI = schemaFile.toURI();
    }

    public void validate(File objectAsFile)
      throws SAXException, ParserConfigurationException, IOException
    {
      try
      {
        validate(new InputSource(new FileInputStream(objectAsFile)));
      }
      catch (IOException e)
      {
        System.err.println("DOValidatorXMLSchema says caught IO ERROR in Validation: " + e.getMessage());
      }
    }

    public void validate(InputStream objectAsStream)
      throws SAXException, ParserConfigurationException, IOException
    {
      validate(new InputSource(objectAsStream));
    }

    public void validate(InputSource objectAsSource)
      throws SAXException, ParserConfigurationException, IOException
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

      XMLReader xmlreader = sp.getXMLReader();
      xmlreader.setErrorHandler(new DOValidatorXMLErrorHandler());
      xmlreader.parse(doXML);
      }
      catch (SAXException e)
      {
        System.err.println("DOValidatorXMLSchema says re-throwing SAXException. ERROR in Validation: " + e.getMessage());
        throw e;
      }
    }
}
package fedora.server.validation;

// JAXP imports
import javax.xml.parsers.*;
import org.xml.sax.*;
import org.xml.sax.helpers.*;

import java.io.ByteArrayInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
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
 * <p>The entire file consists of original code.  Copyright &copy; 2002-2004 by The
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

    public DOValidatorXMLSchema(String schemaPath) throws GeneralException
    {
      try
      {
        schemaURI = (new File(schemaPath)).toURI();
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
        //validate(new InputSource(getInputStreamWithoutSchemaLocations(new FileInputStream(objectAsFile))));
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
      //validate(new InputSource(getInputStreamWithoutSchemaLocations(objectAsStream)));
	  validate(new InputSource(objectAsStream));
    }

    private void validate(InputSource objectAsSource)
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
    
	/**
	 * This is necessary so that the XML schema validation doesn't take into account
	 * the schema locations specified via xsi:schemaLocation and xsi:noNamespaceSchemaLocation
	 * in the file.  They should be ignored because we explicitly tell the parser what
	 * schema to validate with, and if other schema locations are specified, it could
	 * cause the server to hang for a long time if they can't be resolved...and they're 
	 * not even needed.
	 */
	// SDP:  Commented out. For now, schemaLocation attributes within
	// inline XML will be forbidden via Schematron validation check.
	/*
	private InputStream getInputStreamWithoutSchemaLocations(InputStream in) 
			throws GeneralException {
		try {
			BufferedReader rdr = new BufferedReader(
					  new InputStreamReader(in, "UTF-8"));
			StringBuffer buf=new StringBuffer();
			String line=rdr.readLine();
			while (line!=null) {
				buf.append(line.replaceAll("schemaLocation", "schemaNoitacol")
						.replaceAll("SchemaLocation", "SchemaNoitacol")
						+ "\n"); 
				line = rdr.readLine();
			}
			if (fedora.server.Debug.DEBUG) System.out.println("LOOK! schemaLocation replacement: ");
			if (fedora.server.Debug.DEBUG) System.out.println(buf);
			rdr.close();
			return new ByteArrayInputStream(buf.toString().getBytes("UTF-8"));
		} catch (Exception e) {
			throw new GeneralException("Error during getInputStreamWithoutSchemaLocations: " 
					+ e.getClass().getName() + ": " + e.getMessage());
		}
	}
	*/

    public DOIntegrityVariables getDOIntegrityVariables()
    {
      return iHandler.iVars;
    }
}

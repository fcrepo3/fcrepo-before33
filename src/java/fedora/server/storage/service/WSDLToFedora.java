package fedora.server.storage.service;

/**
 * <p>Title: WSDLToFedora.java</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 * @author Sandy Payette  payette@cs.cornell.edu
 * @version 1.0
 */

import fedora.server.storage.types.*;
import fedora.server.errors.*;

import java.util.Vector;
import java.io.File;
import java.io.FileInputStream;

import javax.xml.parsers.*;
import org.xml.sax.*;
import org.xml.sax.helpers.DefaultHandler;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;


public class WSDLToFedora
{

    /** Constants used for JAXP 1.2 */
    private static final String JAXP_SCHEMA_LANGUAGE =
        "http://java.sun.com/xml/jaxp/properties/schemaLanguage";
    private static final String W3C_XML_SCHEMA =
        "http://www.w3.org/2001/XMLSchema";
    private static final String JAXP_SCHEMA_SOURCE =
        "http://java.sun.com/xml/jaxp/properties/schemaSource";

  /**
   * Fedora structure for set of abstract method definitions that
   * will be populated as a result of the SAX parse.
   */
  protected MethodDef[] methodDefs;

  /**
   * Fedora structure for set of method bindings that
   * will be populated as a result of the SAX parse.
   */
  protected MethodDefOperationBind[] methodDefBind;

  private WSDLParser wsdlHandler;
  //private MethodMapParser methodmapHandler;

    public static void main(String[] args)
    {
      if (args.length < 2)
      {
        System.err.println("usage: java WSDLToFedora wsdlLocation methodMapLocation " + "\n" +
          "  wsdlLocation: the file path of the wsdl to be parsed" + "\n" +
          "  methodMapLocation: the file path of the method map to be parsed.");
        System.exit(1);
      }

      try
      {
        File wsdlFile = new File((String)args[0]);
        File mmapFile = new File((String)args[1]);
        WSDLToFedora wtof =
          new WSDLToFedora(
            new InputSource(new FileInputStream(wsdlFile)),
            new InputSource(new FileInputStream(mmapFile)));
      }
      catch (ServerException e)
      {
        System.out.println("WSDLToFedora caught ServerException.");
        System.out.println("Suppressing message since not attached to Server.");
      }
      catch (Throwable th)
      {
        System.out.println("WSDLToFedora returned error in main(). "
                  + "The underlying error was a " + th.getClass().getName() + ".  "
                  + "The message was "  + "\"" + th.getMessage() + "\"");
      }
      System.exit(1);

    }

  public WSDLToFedora(InputSource wsdlXML, InputSource methodMapXML) throws ServerException
  {
    parseWSDL(wsdlXML);
    //parseMethodMap(methodMapXML);
    //merge(wsdlHandler, methodMapHandler);
  }

  public MethodDef[] getMethodDefs()
  {
    return null;
  }

  public MethodDefOperationBind[] getMethodDefBindings()
  {
    return null;
  }

  private void parseWSDL(InputSource wsdlXML) throws ObjectValidityException, GeneralException
  {
    try
    {
      // XMLSchema validation via SAX parser
      SAXParserFactory spf = SAXParserFactory.newInstance();
      spf.setNamespaceAware(true);
      spf.setValidating(false);
      SAXParser sp = spf.newSAXParser();
      //sp.setProperty(JAXP_SCHEMA_LANGUAGE, W3C_XML_SCHEMA);

      // JAXP property for schema location
      //sp.setProperty("http://java.sun.com/xml/jaxp/properties/schemaSource", schemaURI.toString());
      //sp.setProperty("http://java.sun.com/xml/jaxp/properties/schemaSource", schemaFile);

      wsdlHandler = new WSDLParser();
      XMLReader xmlreader = sp.getXMLReader();
      xmlreader.setContentHandler(wsdlHandler);
      //xmlreader.setErrorHandler(new DOValidatorXMLErrorHandler());
      xmlreader.parse(wsdlXML);
      }
      catch (ParserConfigurationException e)
      {
        String msg = "WSDLParser returned parser error. "
                  + "The underlying exception was a " + e.getClass().getName() + ".  "
                  + "The message was "  + "\"" + e.getMessage() + "\"";
        System.out.println(msg);
        throw new GeneralException(msg);
      }
      catch (SAXException e)
      {
        String msg = "WSDLParser returned validation exception. "
                  + "The underlying exception was a " + e.getClass().getName() + ".  "
                  + "The message was "  + "\"" + e.getMessage() + "\"";
        System.out.println(msg);
        throw new ObjectValidityException(msg);
      }
      catch (Exception e)
      {
        String msg = "WSDLParser returned error. "
                  + "The underlying error was a " + e.getClass().getName() + ".  "
                  + "The message was "  + "\"" + e.getMessage() + "\"";
        System.out.println(msg);
        throw new GeneralException(msg);
      }
  }
  // OLD METHODS NEED TO BE REWORKED TO JOIN INFO FROM WSDL AND METHODMAP
  /*
  private void processMethodBindings() throws SAXException
  {
    // Pre-process the behavior method bindings.  Evaluate WSDL message parts to
    // separate out datastream bindings from other parameters (user and system parms).

    System.out.println("Processing Method Bindings...");
    int methodBind_cnt = tmp_vMethodBindings.size();
    if (methodBind_cnt > 0)
    {
      methodDefBind = new MethodDefOperationBind[methodBind_cnt];
      Iterator method_it = tmp_vMethodBindings.iterator();
      for (int i = 0; i < methodBind_cnt; i++)
      {
        MethodDefOperationBind method = (MethodDefOperationBind) method_it.next();
        // Locate the methood parms that are datastream inputs(message parts that are of
        // type dataStreamInput). Put the dsBindingKeys in their own vector and
        // remove datastream parms from the set of method parms.
        Vector messageParts = (Vector)messageTbl.get(methodTbl.get(method.methodName));
        Vector vdsBindKeys = new Vector();
        Vector vParms = new Vector();
        Iterator parms_it = messageParts.iterator();
        while (parms_it.hasNext())
        {
          MethodParmDef parm = (MethodParmDef)parms_it.next();
          if (parm.parmType.equalsIgnoreCase("fedora:datastreamInputType"))
          {
            vdsBindKeys.add(parm.parmName);
          }
          else if (parm.parmType.equalsIgnoreCase("fedora:userInputType") ||
                   parm.parmType.equalsIgnoreCase("fedora:defaultInputType"))
          {
            // Set the array of possible values for the parm
            Vector vParmValues = (Vector)parmValueTbl.get(parm.parmName);
            if (vParmValues.size() > 0)
            {
              parm.parmDomainValues = (String[])vParmValues.toArray(new String[0]);
              // For system default parms, there will be only one value in the
              // parm value array.  It should be considered the default value.
              if (parm.parmType.equalsIgnoreCase("fedora:defaultInputType"))
              {
                parm.parmDefaultValue = parm.parmDomainValues[0];
              }
            }
            // For now, set all parms as required.
            // FIXIT!  There is no encoding in the WSDL now to denote required parms.
            parm.parmRequired = true;
            vParms.add(parm);
          }
          else
          {
            throw new SAXException("FATAL ERROR in WSDL for Behavior Mechanism: " +
            "method parm must be of type datastreamInputType, userInputType, or defaultInputType.");
          }
        }
        // Set the final view of the method bindings.
        // Datastream binding keys are in their own array.
        // The method parm array will contain user parms and system default parms.
        method.dsBindingKeys = (String[])vdsBindKeys.toArray(new String[0]);
        method.methodParms = (MethodParmDef[])vParms.toArray(new MethodParmDef[0]);
        methodDefBind[i] = method;
      }
    }
  }
  */
}
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
  protected MethodDef[] fedoraMethodDefSet = new MethodDef[0];

  /**
   * Fedora structure for set of method bindings that
   * will be populated as a result of the SAX parse.
   */
  private MethodDefOperationBind[] fedoraMethodDefBindings = new MethodDefOperationBind[0];

  private WSDLParser wsdlHandler;
  private MmapParser methodMapHandler;
  private InputSource wsdlSource;
  private InputSource mmapSource;

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
      MethodDef[] methods = wtof.getMethodDefs();
      MethodDefOperationBind[] methodBindings = wtof.getMethodDefBindings();
      System.out.println("END TEST VIA MAIN()");
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
    wsdlSource = wsdlXML;
    mmapSource = methodMapXML;
    System.out.println("Will do late bind for parsing....");
  }

  public MethodDef[] getMethodDefs()
    throws ObjectValidityException, GeneralException
  {
    if (fedoraMethodDefSet.length == 0)
    {
      methodMapHandler = (MmapParser)parse(mmapSource, new MmapParser());
      fedoraMethodDefSet = methodMapHandler.getMethodMap().fedoraMethodDefs;
    }
    return fedoraMethodDefSet;
  }

  public MethodDefOperationBind[] getMethodDefBindings()
    throws ObjectValidityException, GeneralException
  {
    if (fedoraMethodDefBindings.length == 0)
    {
      getMethodDefs();
      wsdlHandler = (WSDLParser)parse(wsdlSource, new WSDLParser());
      merge();
    }
    return fedoraMethodDefBindings;
  }

  private DefaultHandler parse(InputSource xml, DefaultHandler eventHandler)
    throws ObjectValidityException, GeneralException
  {
    try
    {
      // XMLSchema validation via SAX parser
      SAXParserFactory spf = SAXParserFactory.newInstance();
      spf.setNamespaceAware(true);
      spf.setValidating(false);
      SAXParser sp = spf.newSAXParser();
      DefaultHandler handler = eventHandler;
      XMLReader xmlreader = sp.getXMLReader();
      xmlreader.setContentHandler(handler);
      //xmlreader.setErrorHandler(new DOValidatorXMLErrorHandler());
      xmlreader.parse(xml);
      return handler;
      }
      catch (ParserConfigurationException e)
      {
        String msg = "Parser returned parser error. "
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

  private void merge() throws GeneralException
  {
    Service service = wsdlHandler.getService();
    Mmap methodMap = methodMapHandler.getMethodMap();
    Port port = null;
    Binding binding = null;

    // If the WSDL Service defines multiple Ports (with Binding) for the abstract operations
    // we must CHOOSE ONE binding for Fedora to work with.
    if (service.ports.length > 1)
    {
      port = choosePort(service);
      binding = port.binding;
    }

    // Reflect on the type of binding we are dealing with, then Fedora-ize things.
    if (binding.getClass().getName().equalsIgnoreCase("fedora.server.storage.service.HTTPBinding"))
    {
      // Initialize the array to hold the Fedora-ized operation bindings.
      fedoraMethodDefBindings = new MethodDefOperationBind[((HTTPBinding)binding).operations.length];

      for (int i = 0; i < ((HTTPBinding)binding).operations.length; i++)
      {
        // From methodMap which was previously created by parsing Fedora method map metadata
        // which provides a Fedora overlay on the service WSDL.
        MethodDef methodDef = (MethodDef)
          methodMap.wsdlOperationToMethodDef.get(((HTTPBinding)binding).operations[i].operationName);
        fedoraMethodDefBindings[i] = new MethodDefOperationBind();
        fedoraMethodDefBindings[i].methodName = methodDef.methodName;
        fedoraMethodDefBindings[i].methodLabel = methodDef.methodLabel;
        fedoraMethodDefBindings[i].methodParms = methodDef.methodParms;
        fedoraMethodDefBindings[i].wsdlMessageName = methodDef.wsdlMessageName;
        fedoraMethodDefBindings[i].wsdlMsgParts = methodDef.wsdlMsgParts;
        fedoraMethodDefBindings[i].wsdlOutputMessageName = methodDef.wsdlOutputMessageName;

        // From WSDL Port found in Service object
        fedoraMethodDefBindings[i].serviceBindingAddress = port.portBaseURL;

        // From WSDL Binding found in Service object
        fedoraMethodDefBindings[i].protocolType = MethodDefOperationBind.HTTP_MESSAGE_PROTOCOL;
        fedoraMethodDefBindings[i].operationLocation = ((HTTPBinding)binding).operations[i].operationLocation;
        fedoraMethodDefBindings[i].operationURL =
          fedoraMethodDefBindings[i].serviceBindingAddress.concat(fedoraMethodDefBindings[i].operationLocation);

        // FIXIT!  from ds binding spec which are did not parse in this class!
        fedoraMethodDefBindings[i].dsBindingKeys = null;
      }
    }
    else if (binding.getClass().getName().equalsIgnoreCase("fedora.server.storage.service.SOAPBinding"))
    {

    }
  }

  private Port choosePort(Service service)
  {
    // If there is an HTTP binding, this will be preferred.
    for (int i = 0; i < service.ports.length; i++)
    {
      Binding binding = service.ports[i].binding;
      if (binding.getClass().getName().equalsIgnoreCase("fedora.server.storage.service.HTTPBinding"))
      {
        return service.ports[i];
      }
    }

    // Otherwise, just return the first binding
    return service.ports[0];

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
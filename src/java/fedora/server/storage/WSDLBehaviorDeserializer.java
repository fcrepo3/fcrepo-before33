package fedora.server.storage;

/**
 * <p>Title: WSDLBehaviorDeserializer.java</p>
 * <p>Description: Parses WSDL to instantiate behavior definitions (MethodDef[])
 * and service bindings (MethodDefOperationBind[]). Used by the
 * DefinitiveBDefReader and DefinitiveBMech Reader to read the
 * WSDL datastreams that contain behavior service metadata.</p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 * @author Sandy Payette  payette@cs.cornell.edu
 * @version 1.0
 */

import fedora.server.storage.types.*;
import fedora.server.errors.*;
import java.util.Date;
import java.util.Vector;
import java.util.Hashtable;
import java.util.StringTokenizer;
import java.util.Iterator;
import java.util.Set;
import java.io.InputStream;
import java.io.ByteArrayInputStream;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.*;
import org.xml.sax.helpers.DefaultHandler;

class WSDLBehaviorDeserializer extends DefaultHandler
{
  // FIXIT!!
  // Convert this handler to use namespace URIs!

  /** The namespaces we need to deal with */
  //private final static String WSDL = "http://schemas.xmlsoap.org/wsdl/";
  //private final static String XSD = "http://www.w3.org/2001/XMLSchema";
  //private final static String SOAP = "http://schemas.xmlsoap.org/wsdl/soap";
  //private final static String FEDORA = "http://fedora.comm.nsdlib.org/";
  //private final static String HTTP = "http://schemas.xmlsoap.org/wsdl/http/";

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

  // Variables for keeping state during SAX parse.
  // FIXIT!  I can probably get rid of some of these!
  private boolean inXSDType = false;
  private boolean inParmBaseType = false;
  private boolean inMessage = false;
  private boolean inMessagePart = false;
  private boolean inAbstractOperation = false;
  private boolean inAbstractInput = false;
  private boolean inPortType = false;
  private boolean inService = false;
  private boolean inPort = false;
  private boolean inHTTPAddress = false;
  private boolean inSOAPAddress = false;
  private boolean inBinding = false;
  private boolean isHTTPBinding = false;
  private boolean isSOAPBinding = false;
  private boolean inWSDLOperation = false;
  private boolean inHTTPOperation = false;
  private boolean inSOAPOperation = false;

  // Working tables to facilitate joining up data after SAX parse
  private Hashtable messageTbl;
  private Hashtable parmTypeTbl;
  private Hashtable parmValueTbl;
  private Hashtable methodTbl;
  private Hashtable portTbl;

  // Temporary variables during the SAX parse
  private MethodDef tmp_method;
  private MethodDefOperationBind tmp_methodBind;
  private String tmp_nameWSDL;
  private String tmp_parmName;
  private String tmp_baseParmType;
  private String tmp_messageName;
  private String tmp_methodName;
  private Vector tmp_vMessageParts;
  private Vector tmp_vParms;
  private Vector tmp_vParmValues;
  private Vector tmp_vdsBindKeys;
  private String tmp_portBindingName = null;
  private String tmp_bindingName = null;
  private Vector tmp_vMethodBindings;
  private String tmp_httpAddress;
  private String tmp_soapAddress;


  public void startDocument() throws SAXException
  {
    tmp_vMethodBindings = new Vector();
    parmTypeTbl = new Hashtable();
    parmValueTbl = new Hashtable();
    portTbl = new Hashtable();
    messageTbl = new Hashtable();
    methodTbl = new Hashtable();
  }

  public void endDocument() throws SAXException
  {
    processAbstractMethods();
    processMethodBindings();
    tmp_vMethodBindings = null;
    parmTypeTbl = null;
    parmValueTbl = null;
    portTbl = null;
    messageTbl = null;
    methodTbl = null;
  }

  public void skippedEntity(String name) throws SAXException
  {
    StringBuffer sb = new StringBuffer();
    sb.append('&');
    sb.append(name);
    sb.append(';');
    char[] text = new char[sb.length()];
    sb.getChars(0, sb.length(), text, 0);
    this.characters(text, 0, text.length);
  }


  public void characters(char ch[], int start, int length)  throws SAXException
  {
  }

  public void startElement(String namespaceURI, String localName, String qName, Attributes attrs)
    throws SAXException
  {
    // First, evaluate all the schema type definitions in the WSDL.
    if (qName.equalsIgnoreCase("xsd:complexType") || qName.equalsIgnoreCase("xsd:simpleType"))
    {
      if (!inParmBaseType)
      {
        tmp_parmName = attrs.getValue("name");
      }
    }
    // Load a hashtable that registers those types that are restrictions on
    // a fedora base type (datastreamInputType, userInputType, or defaultInputType).
    // This table will be used later to de-reference message part types
    // to their base types. Ultimately, this will enable us to to distinguish
    // between datastream input parms, user input parms and default system
    // input parms on behavior methods (operations) defined by WSDL messages.
    else if (qName.equalsIgnoreCase("xsd:restriction"))
    {
      String base = attrs.getValue("base");
      if (base.equalsIgnoreCase("fedora:datastreamInputType") ||
          base.equalsIgnoreCase("fedora:userInputType") ||
          base.equalsIgnoreCase("fedora:defaultInputType"))
     {
        if (tmp_parmName == null)
        {
          throw new SAXException("FATAL ERROR in WSDL for Behavior Mechanism. " +
          "Missing name on type def for datastreamInputType, userInputType, or defaultInputType.");
        }
        inParmBaseType = true;
        tmp_baseParmType = base;
        parmTypeTbl.put(tmp_parmName, tmp_baseParmType);
        tmp_vParmValues = new Vector();
     }
    }

    else if (qName.equalsIgnoreCase("xsd:enumeration") && inParmBaseType)
    {
      if (tmp_baseParmType.equalsIgnoreCase("fedora:userInputType") ||
          tmp_baseParmType.equalsIgnoreCase("fedora:defaultInputType"))
      {
        tmp_vParmValues.add(attrs.getValue("value"));
      }
    }

    // Second, hold on to message definitions that will be used in defining
    // abstract operations (behavior methods).

    else if (qName.equalsIgnoreCase("wsdl:message"))
    {
      inMessage = true;
      tmp_messageName = attrs.getValue("name");
      tmp_vMessageParts = new Vector();
    }
    else if (qName.equalsIgnoreCase("wsdl:part") && inMessage)
    {
      inMessagePart = true;
      MethodParmDef msgPart = new MethodParmDef();
      msgPart.parmName = attrs.getValue("name");

      // Get the message part type.  Then, look up its
      // base type in parm type table.  The parm type table was
      // created using the schema type definitions section of the WSDL.
      StringTokenizer st = new StringTokenizer(attrs.getValue("type"), ":");
      st.nextToken();
      msgPart.parmType = (String)parmTypeTbl.get(st.nextToken());
      tmp_vMessageParts.add(msgPart);
    }

    // Third, process the abstract operations (behavior method defs)

    else if (qName.equalsIgnoreCase("wsdl:portType"))
    {
      inPortType = true;
    }
    else if (qName.equalsIgnoreCase("wsdl:operation") && inPortType)
    {
      inAbstractOperation = true;
      tmp_methodName = attrs.getValue("name");
    }
    else if (qName.equalsIgnoreCase("wsdl:input") && inAbstractOperation)
    {
      inAbstractInput = true;
      methodTbl.put(tmp_methodName, attrs.getValue("message"));
    }

    // Finally, get the operation (behavior methods) binding information
    else if (qName.equalsIgnoreCase("wsdl:service"))
    {
      inService = true;
    }
    else if (qName.equalsIgnoreCase("wsdl:port"))
    {
      inPort = true;
      tmp_portBindingName = attrs.getValue("binding");
    }
    else if (qName.equalsIgnoreCase("http:address") && inService && inPort)
    {
      inHTTPAddress = true;
      portTbl.put(tmp_portBindingName, attrs.getValue("location"));
    }
    else if (qName.equalsIgnoreCase("soap:address") && inService && inPort)
    {
      inSOAPAddress = true;
      portTbl.put(tmp_portBindingName, attrs.getValue("location"));
    }
    else if (qName.equalsIgnoreCase("wsdl:binding"))
    {
      inBinding = true;
      tmp_bindingName = attrs.getValue("name");
    }
    else if (qName.equalsIgnoreCase("http:binding"))
    {
      isHTTPBinding = true;
    }
    else if (qName.equalsIgnoreCase("soap:binding"))
    {
      isSOAPBinding = true;
    }
    else if (qName.equalsIgnoreCase("wsdl:operation") && inBinding)
    {
      inWSDLOperation = true;
      tmp_methodBind = new MethodDefOperationBind();
      tmp_methodBind.methodName = attrs.getValue("name");
    }
    else if (qName.equalsIgnoreCase("http:operation") && inWSDLOperation && isHTTPBinding )
    {
      inHTTPOperation = true;
      tmp_methodBind.protocolType = "HTTP";
      tmp_httpAddress = (String)portTbl.get(tmp_bindingName);
      tmp_methodBind.serviceBindingAddress = tmp_httpAddress;
      tmp_methodBind.operationLocation = attrs.getValue("location");
      tmp_methodBind.operationURL = tmp_httpAddress.concat(attrs.getValue("location"));
    }
    else if (qName.equalsIgnoreCase("soap:operation") && inWSDLOperation && isSOAPBinding )
    {
      inSOAPOperation = true;
      tmp_methodBind.protocolType = "SOAP";
      tmp_soapAddress = (String)portTbl.get(tmp_bindingName);
      tmp_methodBind.serviceBindingAddress = tmp_soapAddress;
      tmp_methodBind.operationLocation = attrs.getValue("soapAction");
      tmp_methodBind.operationURL = attrs.getValue("soapAction");
    }
  }

  public void endElement(String namespaceURI, String localName, String qName) throws SAXException
  {
    if (qName.equalsIgnoreCase("xsd:complexType") || qName.equalsIgnoreCase("xsd:simpleType") &&inXSDType)
    {
      //tmp_parmName = null;
      //isNamedType = false;
    }
    else if (qName.equalsIgnoreCase("xsd:restriction"))
    {
      if (inParmBaseType)
      {
        inParmBaseType = false;
        parmValueTbl.put(tmp_parmName, tmp_vParmValues);
        tmp_baseParmType = null;
        tmp_vParmValues = null;
      }
    }
    else if (qName.equalsIgnoreCase("wsdl:message"))
    {
      inMessage = false;
      messageTbl.put(tmp_messageName, tmp_vMessageParts);
      tmp_messageName = null;
      tmp_vMessageParts = null;
    }
    else if (qName.equalsIgnoreCase("wsdl:part") && inMessage)
    {
      inMessagePart = false;
    }
    else if (qName.equalsIgnoreCase("wsdl:portType"))
    {
      inPortType = false;
    }
    else if (qName.equalsIgnoreCase("wsdl:operation") && inPortType)
    {
      inAbstractOperation = false;
      tmp_methodName = null;
    }
    else if (qName.equalsIgnoreCase("wsdl:input") && inAbstractOperation)
    {
      inAbstractInput = false;
    }
    else if (qName.equalsIgnoreCase("wsdl:service") && inService)
    {
      inService = false;
    }
    else if (qName.equalsIgnoreCase("wsdl:port") && inPort)
    {
      inPort = false;
      tmp_portBindingName = null;
    }
    else if (qName.equalsIgnoreCase("http:address") && inHTTPAddress)
    {
      inHTTPAddress = false;
    }
    else if (qName.equalsIgnoreCase("soap:address") && inSOAPAddress)
    {
      inSOAPAddress = false;
    }
    else if (qName.equalsIgnoreCase("wsdl:binding") && inBinding)
    {
      inBinding = false;
      tmp_bindingName = null;
      tmp_httpAddress = null;
      tmp_soapAddress = null;
      isHTTPBinding = false;
      isSOAPBinding = false;
    }
    else if (qName.equalsIgnoreCase("wsdl:operation") && inWSDLOperation)
    {
      inWSDLOperation = false;
      tmp_vMethodBindings.addElement(tmp_methodBind);
      tmp_methodBind = null;
    }
    else if (qName.equalsIgnoreCase("http:operation") && inHTTPOperation)
    {
      inHTTPOperation = false;
    }
    else if (qName.equalsIgnoreCase("soap:operation") && inSOAPOperation)
    {
      inSOAPOperation = false;
    }
  }

  private void processAbstractMethods() throws SAXException
  {
    System.out.println("Processing Abstract Method Defs...");
    Set methodNames = methodTbl.keySet();
    int method_cnt = methodNames.size();
    methodDefs = new MethodDef[method_cnt];
    Iterator method_it = methodNames.iterator();
      for (int i = 0; i < method_cnt; i++)
      {
        MethodDef method = new MethodDef();
        method.methodName = (String)method_it.next();
        // Locate the method parms that are user inputs (message parts that are of
        // type userInputType).
        Vector messageParts = (Vector)messageTbl.get(methodTbl.get(method.methodName));
        Vector vParms = new Vector();
        Iterator parms_it = messageParts.iterator();
        while (parms_it.hasNext())
        {
          MethodParmDef parm = (MethodParmDef)parms_it.next();
          if (parm.parmType.equalsIgnoreCase("fedora:userInputType"))
          {
            // Set the array of possible values for the parm
            Vector vParmValues = (Vector)parmValueTbl.get(parm.parmName);
            if (vParmValues.size() > 0)
            {
              parm.parmDomainValues = (String[])vParmValues.toArray(new String[0]);
            }
            parm.parmRequired = true;
            vParms.add(parm);
          }
        }
        // Set the final view of the method definition.
        // The method parm array will contain user parms.
        method.methodParms = (MethodParmDef[])vParms.toArray(new MethodParmDef[0]);
        methodDefs[i] = method;
      }
  }

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
}
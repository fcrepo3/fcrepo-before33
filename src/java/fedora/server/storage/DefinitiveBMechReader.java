package fedora.server.storage;

/**
 * <p>Title: DefinitiveBMechReader.java</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 * @author Sandy Payette
 * @version 1.0
 */

import fedora.server.storage.types.*;
import java.util.Date;
import java.util.Vector;
import java.util.Hashtable;
import java.util.StringTokenizer;
import java.util.Iterator;
import java.io.InputStream;
import java.io.ByteArrayInputStream;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.*;
import org.xml.sax.helpers.DefaultHandler;

public class DefinitiveBMechReader extends DefinitiveDOReader implements BMechReader
{

  //private MethodDef[] behaviorDefs;
  private MethodDefOperationBind[] behaviorBindings;
  private BMechDSBindSpec dsBindSpec;

  public static void main(String[] args)
  {
    if (args.length == 0)
    {
      System.err.println("provide args: [0]=debug(true/false) [1]=PID");
      System.exit(1);
    }

    debug = (args[0].equalsIgnoreCase("true")) ? true : false;

    // FOR TESTING...
    DefinitiveBMechReader doReader = new DefinitiveBMechReader(args[1]);
    doReader.GetObjectPID();
    doReader.GetObjectLabel();
    doReader.ListDatastreamIDs("A");
    doReader.ListDisseminatorIDs("A");
    doReader.GetDatastreams(null);
    Datastream[] dsArray = doReader.GetDatastreams(null);
    doReader.GetDatastream(dsArray[0].DatastreamID, null);
    doReader.GetDisseminators(null);
    String[] bdefArray = doReader.GetBehaviorDefs(null);
    doReader.GetBMechMethods(bdefArray[0], null);
    doReader.GetDSBindingMaps(null);
    doReader.GetBehaviorMethods(null);
    doReader.GetBehaviorMethodsWSDL(null);
  }

  public DefinitiveBMechReader(String objectPID)
  {
    super(objectPID);
  }

  public MethodDef[] GetBehaviorMethods(Date versDateTime)
  {
    try
    {
      DatastreamXMLMetadata wsdlDS = (DatastreamXMLMetadata)datastreamTbl.get("WSDL");
      InputSource wsdlXML = new InputSource(new ByteArrayInputStream(wsdlDS.xmlContent));

      // reset the xmlreader of superclass to the specical WSDLEventHandler
      xmlreader.setContentHandler(new WSDLEventHandler());
      xmlreader.setFeature("http://xml.org/sax/features/namespaces", false);
      xmlreader.setFeature("http://xml.org/sax/features/namespace-prefixes", false);
      xmlreader.parse(wsdlXML);

      //if (debug)
      //{
        System.out.println("GetBehaviorMethods: ");
        for (int i = 0; i < behaviorBindings.length; i++)
        {
          System.out.println("METHOD: " + i);
          System.out.println("  method[" + i + "]=" + behaviorBindings[i].methodName);
          System.out.println("  protocol[" + i + "]=" + behaviorBindings[i].protocolType);
          System.out.println("  service address[" + i + "]=" + behaviorBindings[i].serviceBindingAddress);
          System.out.println("  operation loc[" + i + "]=" + behaviorBindings[i].operationLocation);
          System.out.println("  operation URL[" + i + "]=" + behaviorBindings[i].operationURL);
          for (int j = 0; j < behaviorBindings[i].dsBindingKeys.length; j++)
          {
            System.out.println("  dsBindingKey[" + j + "]=" + behaviorBindings[i].dsBindingKeys[j]);
          }
        }
      //}
    }
    catch (Exception e)
    {
      System.err.println("Error: " + e.toString());
    }
    return(behaviorBindings);
  }

  // Overloaded method: returns InputStream of WSDL as alternative
  public InputStream GetBehaviorMethodsWSDL(Date versDateTime)
  {
    DatastreamXMLMetadata wsdlDS = (DatastreamXMLMetadata)datastreamTbl.get("WSDL");
    InputStream wsdl = new ByteArrayInputStream(wsdlDS.xmlContent);
    return(wsdl);
  }

  public BMechDSBindSpec GetDSBindingSpec(Date versDateTime)
  {
    try
    {
      DatastreamXMLMetadata dsBindDS = (DatastreamXMLMetadata)datastreamTbl.get("DSBIND");
      InputSource dsBindXML = new InputSource(new ByteArrayInputStream(dsBindDS.xmlContent));

      // reset the xmlreader of superclass to the special DS Binding EventHandler
      xmlreader.setContentHandler(new DSBindEventHandler());
      xmlreader.setFeature("http://xml.org/sax/features/namespaces", false);
      xmlreader.setFeature("http://xml.org/sax/features/namespace-prefixes", false);
      xmlreader.parse(dsBindXML);
    }
    catch (Exception e)
    {
      System.err.println("Error: " + e.toString());
    }
    if (debug)
    {
      System.out.println("GetDSBindingSpec.bMechID = " + dsBindSpec.bMechPID);
      System.out.println("GetDSBindingSpec.bDefID = " + dsBindSpec.bDefPID);
      for (int i = 0; i < dsBindSpec.dsBindRules.length; i++)
      {
        System.out.println(">>>>>Binding[" + i + "].bindKey = " + dsBindSpec.dsBindRules[i].bindingKeyName);
        System.out.println(">>>>>Binding[" + i + "].instruct = " + dsBindSpec.dsBindRules[i].bindingInstruction);
        System.out.println(">>>>>Binding[" + i + "].label = " + dsBindSpec.dsBindRules[i].bindingLabel);
        System.out.println(">>>>>Binding[" + i + "].max = " + dsBindSpec.dsBindRules[i].maxNumBindings);
        System.out.println(">>>>>Binding[" + i + "].min = " + dsBindSpec.dsBindRules[i].minNumBindings);
        System.out.println(">>>>>Binding[" + i + "].ordin = " + dsBindSpec.dsBindRules[i].ordinality);
      }
    }
    return(dsBindSpec);
  }

  class WSDLEventHandler extends DefaultHandler
  {

    private boolean inDefinitions = false;
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
    private boolean doGET = false;
    private boolean inWSDLOperation = false;
    private boolean inHTTPOperation = false;
    private boolean inSOAPOperation = false;

    private String portBindingName = null;
    private String bindingName = null;
    private Hashtable portTbl;

    //========================new
    private Hashtable tmp_messageTbl;
    private String tmp_messageName;
    private String tmp_methodName;
    private Vector tmp_vMessageParts;
    private Hashtable tmp_methodTbl;
    //========================end new

    private String h_nameWSDL;
    private String h_portType;
    private String h_operation;
    private MethodDefOperationBind h_methodBind;
    private Vector h_vMethodBindings;
    private String h_httpAddress;
    private String h_soapAddress;

    public void startDocument() throws SAXException
    {
      //initialize the event handler variables

      h_vMethodBindings = new Vector();
      portTbl = new Hashtable();
      tmp_messageTbl = new Hashtable();
      tmp_methodTbl = new Hashtable();
    }

    public void endDocument() throws SAXException
    {
        int cnt = h_vMethodBindings.size();
        behaviorBindings = new MethodDefOperationBind[cnt];
        Iterator it = h_vMethodBindings.iterator();
        for (int i = 0; i < cnt; i++)
        {
          MethodDefOperationBind mbind = (MethodDefOperationBind) it.next();
          // Look up the message parts (dsBindingKeys) that go with the method
          Vector messageParts = (Vector)tmp_messageTbl.get(tmp_methodTbl.get(mbind.methodName));
          mbind.dsBindingKeys = (String[])messageParts.toArray(new String[0]);
          behaviorBindings[i] = mbind;
        }
        //behaviorBindings = (MethodDefOperationBind[])h_vMethodBindings.toArray(new MethodDefOperationBind[0]);
        h_vMethodBindings = null;
        portTbl = null;
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
      if (qName.equalsIgnoreCase("wsdl:definitions"))
      {
        inDefinitions = true;
        h_nameWSDL = attrs.getValue("name");
        if (debug) System.out.println("wsdl name= " + h_nameWSDL);
      }
      // ===============================new

      else if (qName.equalsIgnoreCase("wsdl:message"))
      {
        inMessage = true;
        tmp_messageName = attrs.getValue("name");
        tmp_vMessageParts = new Vector();
      }
      else if (qName.equalsIgnoreCase("wsdl:part") && inMessage)
      {
        inMessagePart = true;
        tmp_vMessageParts.add(attrs.getValue("name"));
      }
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
        tmp_methodTbl.put(tmp_methodName, attrs.getValue("message"));
      }
      //===============================end new
      else if (qName.equalsIgnoreCase("wsdl:service"))
      {
        inService = true;
      }
      else if (qName.equalsIgnoreCase("wsdl:port"))
      {
        inPort = true;
        portBindingName = attrs.getValue("binding");
      }
      else if (qName.equalsIgnoreCase("http:address") && inService && inPort)
      {
        inHTTPAddress = true;
        portTbl.put(portBindingName, attrs.getValue("location"));
      }
      else if (qName.equalsIgnoreCase("soap:address") && inService && inPort)
      {
        inSOAPAddress = true;
        portTbl.put(portBindingName, attrs.getValue("location"));
      }
      else if (qName.equalsIgnoreCase("wsdl:binding"))
      {
        inBinding = true;
        bindingName = attrs.getValue("name");
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
        h_methodBind = new MethodDefOperationBind();
        h_methodBind.methodName = attrs.getValue("name");
      }
      else if (qName.equalsIgnoreCase("http:operation") && inWSDLOperation && isHTTPBinding )
      {
        inHTTPOperation = true;
        h_methodBind.protocolType = "HTTP";
        h_httpAddress = (String)portTbl.get(bindingName);
        h_methodBind.serviceBindingAddress = h_httpAddress;
        h_methodBind.operationLocation = attrs.getValue("location");
        h_methodBind.operationURL = h_httpAddress.concat(attrs.getValue("location"));
      }
      else if (qName.equalsIgnoreCase("soap:operation") && inWSDLOperation && isSOAPBinding )
      {
        inSOAPOperation = true;
        h_methodBind.protocolType = "SOAP";
        h_soapAddress = (String)portTbl.get(bindingName);
        h_methodBind.serviceBindingAddress = h_soapAddress;
        h_methodBind.operationLocation = attrs.getValue("soapAction");
        h_methodBind.operationURL = attrs.getValue("soapAction");
      }
    }

    public void endElement(String namespaceURI, String localName, String qName) throws SAXException
    {
      if (qName.equalsIgnoreCase("wsdl:definitions") && inDefinitions)
      {
        inDefinitions = false;
      }
      else if (qName.equalsIgnoreCase("wsdl:message"))
      {
        inMessage = false;
        tmp_messageTbl.put(tmp_messageName, tmp_vMessageParts);
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
        portBindingName = null;
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
        bindingName = null;
        h_httpAddress = null;
        h_soapAddress = null;
        isHTTPBinding = false;
        isSOAPBinding = false;
      }
      else if (qName.equalsIgnoreCase("wsdl:operation") && inWSDLOperation)
      {
        inWSDLOperation = false;
        h_vMethodBindings.addElement(h_methodBind);
        h_methodBind = null;
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
  }

  class DSBindEventHandler extends DefaultHandler
  {

    private boolean inDSBindSpec = false;
    private boolean inBMechID = false;
    private boolean inBDefID = false;
    private boolean inDSBind = false;
    private boolean inDSBindLabel = false;
    private boolean inDSBindInstructions = false;
    private boolean inDSBindMIME = false;

    private BMechDSBindRule h_dsBindRule;
    private Vector h_vBindRules;
    private BMechDSBindSpec h_dsBindSpec;

    public void startDocument() throws SAXException
    {
      //initialize the event handler variables

      h_vBindRules = new Vector();
      h_dsBindSpec = new BMechDSBindSpec();
    }

    public void endDocument() throws SAXException
    {
        h_dsBindSpec.dsBindRules = (BMechDSBindRule[]) h_vBindRules.toArray(new BMechDSBindRule[0]);
        h_vBindRules = null;
        dsBindSpec = h_dsBindSpec;
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

      if (inBMechID)
      {
        h_dsBindSpec.bMechPID = new String(ch, start, length);
      }
      else if (inBDefID)
      {
        h_dsBindSpec.bDefPID = new String(ch, start, length);
      }
      else if (inDSBindLabel)
      {
        h_dsBindRule.bindingLabel = new String(ch, start, length);
      }
      else if (inDSBindInstructions)
      {
        h_dsBindRule.bindingInstruction = new String(ch, start, length);
      }
      else if (inDSBindMIME)
      {
        StringTokenizer st = new StringTokenizer(new String(ch, start, length), " ");
        String[] MIMETypes = new String[st.countTokens()];
        for (int i = 0; i < st.countTokens(); i++)
        {
          MIMETypes[i] = st.nextToken();
        }
        h_dsBindRule.bindingMIMETypes = MIMETypes;
      }
    }

    //FIXIT! How can we deal with prefix variability?  fbs.DSBinding vs. something.DSBinding?
    // Maybe Fedora controls the prefix so we can know what it will be.
    // But we need to do something better here, so the event handler is not fragile.
    public void startElement(String namespaceURI, String localName, String qName, Attributes attrs)
      throws SAXException
    {
      if (qName.equalsIgnoreCase("fbs:DSBindingSpec"))
      {
        inDSBindSpec = true;
      }
      else if (qName.equalsIgnoreCase("fbs:mechanismID"))
      {
        inBMechID = true;
      }
      else if (qName.equalsIgnoreCase("fbs:behaviorDefID"))
      {
        inBDefID = true;
      }
      else if (qName.equalsIgnoreCase("fbs:DSBinding"))
      {
        inDSBind = true;
        h_dsBindRule = new BMechDSBindRule();
        h_dsBindRule.bindingKeyName = attrs.getValue("BindingKeyName");
        h_dsBindRule.maxNumBindings = new Integer(attrs.getValue("DSMax")).intValue();
        h_dsBindRule.minNumBindings = new Integer(attrs.getValue("DSMin")).intValue();
        h_dsBindRule.ordinality = Boolean.getBoolean(attrs.getValue("DSOrdinality"));
      }
      else if (qName.equalsIgnoreCase("fbs:DSBindingLabel"))
      {
        inDSBindLabel = true;
      }
      else if (qName.equalsIgnoreCase("fbs:DSBindingInstructions"))
      {
        inDSBindInstructions = true;
      }
      else if (qName.equalsIgnoreCase("fbs:DSMIME"))
      {
        inDSBindMIME = true;
      }
    }

    public void endElement(String namespaceURI, String localName, String qName) throws SAXException
    {

      if (qName.equalsIgnoreCase("fbs:DSBindingSpec") && inDSBindSpec)
      {
        inDSBindSpec = false;
      }
      else if (qName.equalsIgnoreCase("fbs:mechanismID") && inBMechID)
      {
        inBMechID = false;
      }
      else if (qName.equalsIgnoreCase("fbs:behaviorDefID") && inBDefID)
      {
        inBDefID = false;
      }
      else if (qName.equalsIgnoreCase("fbs:DSBinding") && inDSBind)
      {
        inDSBind = false;
        h_vBindRules.add(h_dsBindRule);
        h_dsBindRule = null;
      }
      else if (qName.equalsIgnoreCase("fbs:DSBindingLabel") && inDSBindLabel)
      {
        inDSBindLabel = false;
      }
      else if (qName.equalsIgnoreCase("fbs:DSBindingInstructions") && inDSBindInstructions)
      {
        inDSBindInstructions = false;
      }
      else if (qName.equalsIgnoreCase("fbs:DSMIME") && inDSBindMIME)
      {
        inDSBindMIME = false;
      }
    }
  }
}
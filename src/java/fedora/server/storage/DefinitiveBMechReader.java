package fedora.server.storage;

/**
 * <p>Title: DefinitiveBMechReader.java</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 * @author Sandy Payette
 * @version 1.0
 */

import fedora.server.storage.abstraction.*;
import java.util.Date;
import java.util.Vector;
import java.util.StringTokenizer;
import java.io.InputStream;
import java.io.ByteArrayInputStream;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.*;
import org.xml.sax.helpers.DefaultHandler;

public class DefinitiveBMechReader extends DefinitiveDOReader implements BMechReader
{

  //private MethodDef[] behaviorDefs;
  private MethodDef[] behaviorBindings;
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
    doReader.ListDatastreamIDs("A");
    doReader.ListDisseminatorIDs("A");
    doReader.GetDatastreams(null);
    doReader.GetDatastream("DS1", null);
    doReader.GetDisseminators(null);
    doReader.GetDisseminator("DISS1", null);
    doReader.GetBehaviorDefs(null);
    doReader.GetBehaviorMethods(null);
    doReader.GetBehaviorMethodsWSDL(null);
    doReader.GetDSBindingSpec(null);
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

      if (debug)
      {
        for (int i = 0; i < behaviorBindings.length; i++)
        {
          System.out.println("GetBehaviorMethods: ");
          System.out.println("  method[" + i + "]=" + behaviorBindings[i].methodName);
          System.out.println("  binding location[" + i + "]=" + behaviorBindings[i].httpBindingURL);
        }
      }
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
    private boolean inPortType = false;
    private boolean inOperation = false;
    private boolean inService = false;
    private boolean inPort = false;
    private boolean inHTTPAddress = false;
    private boolean inBinding = false;
    private boolean inHTTPBinding = false;
    private boolean doGET = false;
    private boolean inBindOperation = false;
    private boolean inHTTPOperation = false;

    private String h_nameWSDL;
    private String h_portType;
    private String h_operation;
    //private Vector h_vOperations;
    //private MethodDef h_methodDef;
    private MethodDef h_methodBind;
    private Vector h_vMethodBindings;
    private String h_httpAddress;

    public void startDocument() throws SAXException
    {
      //initialize the event handler variables

      //h_vOperations = new Vector();
      h_vMethodBindings = new Vector();
    }

    public void endDocument() throws SAXException
    {
        //behaviorDefs = (MethodDef[]) h_vOperations.toArray(new MethodDef[0]);
        behaviorBindings = (MethodDef[]) h_vMethodBindings.toArray(new MethodDef[0]);
        //h_vOperations = null;
        h_vMethodBindings = null;
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
  /*
      else if (qName.equalsIgnoreCase("wsdl:portType"))
      {
        inPortType = true;
        h_portType = attrs.getValue("name");
        if (debug) System.out.println("portType name = " + h_portType);
      }
      else if (qName.equalsIgnoreCase("wsdl:operation") && inPortType)
      {
        inOperation = true;
        h_methodDef = new MethodDef();
        h_methodDef.methodName = attrs.getValue("name");
      }
*/
      else if (qName.equalsIgnoreCase("wsdl:service"))
      {
        inService = true;
      }
      else if (qName.equalsIgnoreCase("wsdl:port"))
      {
        inPort = true;
      }
      else if (qName.equalsIgnoreCase("http:address") && inService && inPort)
      {
        inHTTPAddress = true;
        h_httpAddress = attrs.getValue("location");
      }
      else if (qName.equalsIgnoreCase("wsdl:binding"))
      {
        inBinding = true;
      }
/*
      else if (qName.equalsIgnoreCase("http:binding"))
      {
        inHTTPBinding = true;
        doGET = true;
      }
*/
      else if (qName.equalsIgnoreCase("wsdl:operation") && inBinding)
      {
        inBindOperation = true;
        h_methodBind = new MethodDef();
        h_methodBind.methodName = attrs.getValue("name");
      }
      else if (qName.equalsIgnoreCase("http:operation") && inBindOperation)
      {
        inHTTPOperation = true;
        h_methodBind.httpBindingURL = h_httpAddress.concat(attrs.getValue("location"));
      }
    }

    public void endElement(String namespaceURI, String localName, String qName) throws SAXException
    {
      if (qName.equalsIgnoreCase("wsdl:definitions") && inDefinitions)
      {
        inDefinitions = false;
      }
/*
      else if (qName.equalsIgnoreCase("wsdl:portType") && inPortType)
      {
        inPortType = false;
      }
      else if (qName.equalsIgnoreCase("wsdl:operation") && inOperation && inPortType)
      {
        inOperation = false;
        h_vOperations.addElement(h_methodDef);
        h_methodDef = null;
      }
*/
      else if (qName.equalsIgnoreCase("wsdl:service") && inService)
      {
        inService = false;
      }
      else if (qName.equalsIgnoreCase("wsdl:port") && inPort)
      {
        inPort = false;
      }
      else if (qName.equalsIgnoreCase("http:address") && inHTTPAddress)
      {
        inHTTPAddress = false;
      }
      else if (qName.equalsIgnoreCase("wsdl:binding") && inBinding)
      {
        inBinding = false;
      }
/*
      else if (qName.equalsIgnoreCase("http:binding") && inHTTPBinding)
      {
        inHTTPBinding = false;
      }
*/
      else if (qName.equalsIgnoreCase("wsdl:operation") && inBindOperation)
      {
        inBindOperation = false;
        h_vMethodBindings.addElement(h_methodBind);
        h_methodBind = null;
      }
      else if (qName.equalsIgnoreCase("http:operation") && inHTTPOperation)
      {
        inHTTPOperation = false;
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
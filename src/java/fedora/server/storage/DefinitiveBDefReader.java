package fedora.server.storage;

/**
 * <p>Title: DefinitiveBDefReader.java</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 * @author Sandy Payette
 * @version 1.0
 */

import fedora.server.storage.types.*;
import java.util.Date;
import java.util.Vector;
import java.util.StringTokenizer;
import java.io.InputStream;
import java.io.ByteArrayInputStream;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.*;
import org.xml.sax.helpers.DefaultHandler;

public class DefinitiveBDefReader extends DefinitiveDOReader implements BDefReader
{

  private MethodDef[] behaviorDefs;

  public static void main(String[] args)
  {
    if (args.length == 0)
    {
      System.err.println("provide args: [0]=debug(true/false) [1]=PID");
      System.exit(1);
    }

    debug = (args[0].equalsIgnoreCase("true")) ? true : false;

    // FOR TESTING...
    DefinitiveBDefReader doReader = new DefinitiveBDefReader(args[1]);
    doReader.GetObjectPID();
    doReader.GetObjectLabel();
    doReader.ListDatastreamIDs("A");
    doReader.ListDisseminatorIDs("A");
    doReader.GetDatastreams(null);
    Datastream[] dsArray = doReader.GetDatastreams(null);
    doReader.GetDatastream(dsArray[0].DatastreamID, null);
    doReader.GetDisseminators(null);
    doReader.GetBehaviorDefs(null);
    doReader.GetBehaviorMethods(null);
    doReader.GetBehaviorMethodsWSDL(null);
    Disseminator d = doReader.GetDisseminator("DISS1", null);
    doReader.GetBMechMethods(d.bDefID, null);
    doReader.GetDSBindingMaps(null);
  }

  public DefinitiveBDefReader(String objectPID)
  {
    super(objectPID);
  }

  //FIXIT!! Not dealing with parms on the method defs now!!
  public MethodDef[] GetBehaviorMethods(Date versDateTime)
  {
    try
    {
      DatastreamXMLMetadata wsdlDS = (DatastreamXMLMetadata)datastreamTbl.get("WSDL");
      InputSource wsdlXML = new InputSource(new ByteArrayInputStream(wsdlDS.xmlContent));

      // reset the xmlreader of superclass to the specical WSDLEventHandler
      xmlreader.setContentHandler(new BDefWSDLEventHandler());
      xmlreader.setFeature("http://xml.org/sax/features/namespaces", false);
      xmlreader.setFeature("http://xml.org/sax/features/namespace-prefixes", false);
      xmlreader.parse(wsdlXML);

      if (debug)
      {
        for (int i = 0; i < behaviorDefs.length; i++)
        {
          System.out.println("GetBehaviorMethods: ");
          System.out.println("  method[" + i + "]=" + behaviorDefs[i].methodName);
        }
      }
    }
    catch (Exception e)
    {
      System.err.println("Error: " + e.toString());
    }
    return(behaviorDefs);
  }

  // Overloaded method: returns InputStream of WSDL as alternative
  public InputStream GetBehaviorMethodsWSDL(Date versDateTime)
  {
    DatastreamXMLMetadata wsdlDS = (DatastreamXMLMetadata)datastreamTbl.get("WSDL");
    InputStream wsdl = new ByteArrayInputStream(wsdlDS.xmlContent);
    return(wsdl);
  }

  class BDefWSDLEventHandler extends DefaultHandler
  {

    private boolean inDefinitions = false;
    private boolean inPortType = false;
    private boolean inOperation = false;

    private String h_nameWSDL;
    private String h_portType;
    private String h_operation;
    private Vector h_vOperations;
    private MethodDef h_methodDef;

    public void startDocument() throws SAXException
    {
      //initialize the event handler variables

      h_vOperations = new Vector();
    }

    public void endDocument() throws SAXException
    {
        behaviorDefs = (MethodDef[]) h_vOperations.toArray(new MethodDef[0]);
        h_vOperations = null;
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
      // FIXIT!! Deal with method parms (input in WSDL)
      if (qName.equalsIgnoreCase("wsdl:definitions"))
      {
        inDefinitions = true;
        h_nameWSDL = attrs.getValue("name");
        if (debug) System.out.println("wsdl name= " + h_nameWSDL);
      }
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
    }

    public void endElement(String namespaceURI, String localName, String qName) throws SAXException
    {

      if (qName.equalsIgnoreCase("wsdl:definitions") && inDefinitions)
      {
        inDefinitions = false;
      }
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
    }
  }
}
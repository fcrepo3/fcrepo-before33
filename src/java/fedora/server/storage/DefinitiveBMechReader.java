package fedora.server.storage;

/**
 * <p>Title: DefinitiveBMechReader.java</p>
 * <p>Description: A Reader for Fedora Behavior Mechanism Objects. Since
 * a Behavior Mechanism Object can be treated like any other Fedora
 * object, the DefinitiveBMechReader extends the functionality of the
 * DefinitiveDOReader. The DefinitiveBMechReader can provide a list
 * of behavior methods for the service that the Behavior Mechanism Object
 * represents.  The method definitions and bindings are drawn out of the
 * WSDL datastream in the Behavior Mechanism Object. </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 * @author Sandy Payette payette@cs.cornell.edu
 * @version 1.0
 */

import fedora.server.storage.types.*;
import fedora.server.errors.*;
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
  private MethodDefOperationBind[] methodDefBind;
  private BMechDSBindSpec dsBindSpec;

  public static void main(String[] args)
  {
    if (args.length == 0)
    {
      System.err.println("provide args: [0]=debug(true/false) [1]=PID");
      System.exit(1);
    }

    debug = (args[0].equalsIgnoreCase("true")) ? true : false;
    try
    {
      // FOR TESTING...
      DefinitiveBMechReader doReader = new DefinitiveBMechReader(null, args[1]);
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
      // BMech reader methods
      doReader.GetBehaviorMethods(null);
      doReader.GetBehaviorMethodsWSDL(null);
      doReader.GetDSBindingSpec(null);
    }
    catch (ServerException e)
    {
      System.err.println("FEDORA EXCEPTION: suppressing print of ServerException");
    }
    catch (Exception e)
    {
      System.err.println("NON-FEDORA EXCEPTION:)" + e.toString());
    }
  }

  public DefinitiveBMechReader(DefaultDOManager mgr, String objectPID) throws ServerException
  {
    super(mgr, objectPID);
  }

  /**
   * Get the set of method definitions that are implemented by the service
   * that this Behavior Mechanism Object represents.
   * Specifically, these are the service operations that are defined in the WSDL
   * datastream of the Behavior Mechanism Object.
   * @param versDateTime
   * @return  an array of method definitions
   * @throws GeneralException
   */
  public MethodDef[] GetBehaviorMethods(Date versDateTime) throws GeneralException
  {
    try
    {
      DatastreamXMLMetadata wsdlDS = (DatastreamXMLMetadata)datastreamTbl.get("WSDL");
      InputSource wsdlXML = new InputSource(new ByteArrayInputStream(wsdlDS.xmlContent));

      // reset the xmlreader of superclass to the specical WSDLEventHandler
      WSDLBehaviorDeserializer wsdl = new WSDLBehaviorDeserializer();
      xmlreader.setContentHandler(wsdl);
      xmlreader.setFeature("http://xml.org/sax/features/namespaces", false);
      xmlreader.setFeature("http://xml.org/sax/features/namespace-prefixes", false);
      xmlreader.parse(wsdlXML);
      methodDefBind = wsdl.methodDefBind;
      if (debug) printBehaviorMethods();
    }
    catch (Exception e)
    {
      System.err.println("GetBehaviorMethods: " + e.toString());
      throw new GeneralException("DefinitiveBMechReader.GetBehaviorMethods: " + e.getMessage());

    }
    return(methodDefBind);
  }

  /**
   * Get the set of method definitions that are implemented by the service
   * that this Behavior Mechanism Object represents.  In this case, the
   * method defintions will be returned as a stream of XML that is encoded
   * in accordance with the WSDL schema.  The WSDL expression of the methods
   * is stored this way as a datastream in the Behavior Mechanism Object.
   * @param versDateTime
   * @return  an input stream that is XML encoded to the WSDL schema
   * @throws GeneralException
   */
  public InputStream GetBehaviorMethodsWSDL(Date versDateTime) throws GeneralException
  {
    DatastreamXMLMetadata wsdlDS = (DatastreamXMLMetadata)datastreamTbl.get("WSDL");
    InputStream wsdl = new ByteArrayInputStream(wsdlDS.xmlContent);
    return(wsdl);
  }

  /**
   * Get the datastream binding specification that defines the datastream requirements
   * for Data Objects that will associate with this Behavior Mechanism Object.
   * It can be seen as a "contract" between the Behavior Mechanism Object and any
   * Data Objects that use the service that the Behavior Mechanism Object represents.
   * Specifically, the binding specification defines what kinds of datastreams
   * should be made available to the service that this Behavior Mechanism Object
   * represents.  It defines an abstract perspective of the contents of a
   * Data Object from the perspective of the service that will process the
   * contents of the Data Object.  The datastream binding specification
   * will define a formal name for each kind of datastream input (i.e., a binding key),
   * acceptable MIME types for those datastreams, and the cardinality and ordinality
   * for those datastreams.  The datastream binding specification can be used
   * to facilitate the creation of Disseminators on Data Objects, or to validate
   * the relationships between Data Objects and Behavior Mechanism Objects.
   * @param versDateTime
   * @return  the datastream binding specification for the Behavior Mechansim Object
   * @throws GeneralException
   */
  public BMechDSBindSpec GetDSBindingSpec(Date versDateTime) throws GeneralException
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
      if (debug) printDSBindingSpec();
    }
    catch (Exception e)
    {
      System.err.println("Error: " + e.toString());
      throw new GeneralException("DefinitiveBMechReader.GetDSBindingSpec: " + e.getMessage());
    }
    return(dsBindSpec);
  }

  /**
   * Print the methods definitions for the service represented by
   * the Behavior Mechanism Object.
   */
  private void printBehaviorMethods()
  {
    System.out.println("Printing Behavior Methods...");
    for (int i = 0; i < methodDefBind.length; i++)
    {
      System.out.println("METHOD: " + i);
      System.out.println("  method[" + i + "]=" + methodDefBind[i].methodName);
      System.out.println("  protocol[" + i + "]=" + methodDefBind[i].protocolType);
      System.out.println("  service address[" + i + "]=" + methodDefBind[i].serviceBindingAddress);
      System.out.println("  operation loc[" + i + "]=" + methodDefBind[i].operationLocation);
      System.out.println("  operation URL[" + i + "]=" + methodDefBind[i].operationURL);
      for (int j = 0; j < methodDefBind[i].methodParms.length; j++)
      {
        System.out.println(
              "  parm[" + j + "]=" +
              methodDefBind[i].methodParms[j].parmName +
              " parmType=" + methodDefBind[i].methodParms[j].parmType +
              " defaultValue=" + methodDefBind[i].methodParms[j].parmDefaultValue +
              " required=" + methodDefBind[i].methodParms[j].parmRequired);

        for (int j2 = 0; j2 < methodDefBind[i].methodParms[j].parmDomainValues.length; j2++)
        {
          System.out.println(
              "  parmDomainValue[" + j2 + "]=" +
              methodDefBind[i].methodParms[j].parmDomainValues[j2]);
        }
      }
      for (int k = 0; k < methodDefBind[i].dsBindingKeys.length; k++)
      {
        System.out.println("  dsBindingKey[" + k + "]=" + methodDefBind[i].dsBindingKeys[k]);
      }
    }
  }

  /**
   * Print the datastream binding specification for the service
   * represented by the Behavior Mechanism Object.
   */
  private void printDSBindingSpec()
  {
    System.out.println("Printing DS Binding Specification...");
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

  /**
   *
   * <p>Title: DSBindEventHandler.java</p>
   * <p>Description: Inner class to parse a datastream bindings specification.
   *    A datastream binding specificaiton is a special XML datastream
   *    found in a Behavior Mechanism Object.</p>
   * <p>Copyright: Copyright (c) 2002</p>
   * <p>Company: </p>
   * @author Sandy Payette   payette@cs.cornell.edu
   * @version 1.0
   */
  class DSBindEventHandler extends DefaultHandler
  {
    // FIXIT! How can we deal with prefix variability?  fbs.DSBinding vs. something.DSBinding?
    // I need to do something better here to deal with namespaces,
    // so the event handler is not fragile.

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
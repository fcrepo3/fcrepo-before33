package fedora.server.storage.service;

/**
 * <p>Title: MmapParser.java</p>
 * <p>Description:  </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 * @author Sandy Payette  payette@cs.cornell.edu
 * @version 1.0
 */

import fedora.server.errors.*;
import fedora.server.storage.types.MethodDef;
import fedora.server.storage.types.MethodParmDef;
import java.util.Vector;
import java.util.Hashtable;
import java.util.HashMap;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.*;
import org.xml.sax.helpers.DefaultHandler;

class MmapParser extends DefaultHandler
{

  /** The namespaces we know we will encounter */
  private final static String FMM = "http://fedora.comm.nsdlib.org/service/methodmap";

  /**
   * URI-to-namespace prefix mapping info from SAX2 startPrefixMapping events.
   */
  private HashMap nsPrefixMap;

  // Variables for keeping state during SAX parse.
  private boolean inMethod = false;
  private boolean inUserInputParm = false;

  // Fedora Method Map Entities

  private Mmap methodMap;
  private MethodDef methodMapMethod;
  private MethodParmDef methodMapParm;

  private Hashtable wsdlMsgToMethodTbl;
  private Hashtable wsdlOperationToMethodDefTbl;
  private Hashtable wsdlMsgPartsTbl;

  // Working variables...

  private Vector tmp_enum;
  private Vector tmp_parms;
  private Vector tmp_methods;

  protected Mmap getMethodMap()
  {
    return methodMap;
  }

  public void startDocument() throws SAXException
  {
    System.out.println("MmapParser: START DOC");
    nsPrefixMap = new HashMap();
    wsdlMsgToMethodTbl = new Hashtable();
    wsdlOperationToMethodDefTbl = new Hashtable();
  }

  public void endDocument() throws SAXException
  {
    System.out.println("MmapParser: END DOC");
    nsPrefixMap = null;
  }

  public void startPrefixMapping(String prefix, String uri) throws SAXException
  {
    System.out.println("MmapParser: START PREFIX MAP");
    nsPrefixMap.put(uri, prefix);
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
    System.out.println("MmapParser: START ELEMENT " + qName);
    if (namespaceURI.equalsIgnoreCase(FMM) && localName.equalsIgnoreCase("fedoraToWSDLMap"))
    {
      methodMap = new Mmap();
      methodMap.mmapName = attrs.getValue("name");
      tmp_methods = new Vector();
    }
    else if (namespaceURI.equalsIgnoreCase(FMM) && localName.equalsIgnoreCase("Method"))
    {
        inMethod = true;
        methodMapMethod = new MethodDef();
        methodMapMethod.methodName = attrs.getValue("wsdlOperationName");
        methodMapMethod.methodLabel = "fix me";
        methodMapMethod.wsdlMessageName = attrs.getValue("wsdlMsgName");
        methodMapMethod.wsdlOutputMessageName = attrs.getValue("wsdlMsgOutput");
        tmp_parms = new Vector();
        wsdlMsgPartsTbl = new Hashtable();
    }
    else if (inMethod)
    {
      if (namespaceURI.equalsIgnoreCase(FMM) && localName.equalsIgnoreCase("DatastreamInputParm"))
      {
        methodMapParm = new MethodParmDef();
        methodMapParm.wsdlMessagePartName = attrs.getValue("wsdlMsgPartName");
        methodMapParm.parmName = attrs.getValue("wsdlMsgPartName");
        methodMapParm.parmLabel = "fix me";
        methodMapParm.parmPassBy = attrs.getValue("passBy");
        methodMapParm.parmType = "fedora:datastreamInputType";
        methodMapParm.parmDefaultValue = null;
        methodMapParm.parmDomainValues = new String[0];
      }
      else if (namespaceURI.equalsIgnoreCase(FMM) && localName.equalsIgnoreCase("DefaultInputParm"))
      {
        methodMapParm = new MethodParmDef();
        methodMapParm.wsdlMessagePartName = attrs.getValue("wsdlMsgPartName");
        methodMapParm.parmName = attrs.getValue("wsdlMsgPartName");
        methodMapParm.parmLabel = "fix me";
        methodMapParm.parmPassBy = MethodParmDef.PASS_BY_VALUE;
        methodMapParm.parmType = "fedora:defaultInputType";
        methodMapParm.parmDefaultValue = attrs.getValue("defaultValue");
        methodMapParm.parmDomainValues = new String[0];
      }
      else if (namespaceURI.equalsIgnoreCase(FMM) && localName.equalsIgnoreCase("UserInputParm"))
      {
        inUserInputParm = true;
        methodMapParm = new MethodParmDef();
        methodMapParm.wsdlMessagePartName = attrs.getValue("wsdlMsgPartName");
        methodMapParm.parmName = attrs.getValue("wsdlMsgPartName");
        methodMapParm.parmLabel = "fix me";
        methodMapParm.parmPassBy = MethodParmDef.PASS_BY_VALUE;
        methodMapParm.parmType = "fedora:userInputType";
        methodMapParm.parmDefaultValue = attrs.getValue("defaultValue");
      }
      else if (inUserInputParm)
      {
        if (namespaceURI.equalsIgnoreCase(FMM) && localName.equalsIgnoreCase("ValidParmValues"))
        {
          tmp_enum = new Vector();
        }
        else if (namespaceURI.equalsIgnoreCase(FMM) && localName.equalsIgnoreCase("ValidParm"))
        {
          tmp_enum.add(attrs.getValue("value"));
        }
      }
    }
  }

  public void endElement(String namespaceURI, String localName, String qName) throws SAXException
  {
    System.out.println("MmapParser: END ELEMENT " + qName);
    if (namespaceURI.equalsIgnoreCase(FMM) && localName.equalsIgnoreCase("fedoraToWSDLMap"))
    {
      methodMap.fedoraMethodDefs = (MethodDef[])tmp_methods.toArray(new MethodDef[0]);
      methodMap.wsdlMsgToMethodDef = wsdlMsgToMethodTbl;
      methodMap.wsdlOperationToMethodDef = wsdlOperationToMethodDefTbl;
      wsdlMsgToMethodTbl = null;
      tmp_methods = null;
    }
    else if (namespaceURI.equalsIgnoreCase(FMM) && localName.equalsIgnoreCase("Method"))
    {
        methodMapMethod.methodParms = (MethodParmDef[])tmp_parms.toArray(new MethodParmDef[0]);
        tmp_methods.add(methodMapMethod);
        methodMapMethod.wsdlMsgParts = wsdlMsgPartsTbl;
        wsdlMsgPartsTbl = null;
        wsdlMsgToMethodTbl.put(methodMapMethod.wsdlMessageName, methodMapMethod);
        wsdlOperationToMethodDefTbl.put(methodMapMethod.methodName, methodMapMethod);
        methodMapMethod = null;
        tmp_parms = null;
        inMethod = false;
    }
    else if (inMethod)
    {
      if (namespaceURI.equalsIgnoreCase(FMM) && localName.equalsIgnoreCase("DatastreamInputParm"))
      {
        tmp_parms.add(methodMapParm);
        wsdlMsgPartsTbl.put(methodMapParm.wsdlMessagePartName, methodMapParm);
        methodMapParm = null;
      }
      else if (namespaceURI.equalsIgnoreCase(FMM) && localName.equalsIgnoreCase("DefaultInputParm"))
      {
        tmp_parms.add(methodMapParm);
        wsdlMsgPartsTbl.put(methodMapParm.wsdlMessagePartName, methodMapParm);
        methodMapParm = null;
      }
      else if (namespaceURI.equalsIgnoreCase(FMM) && localName.equalsIgnoreCase("UserInputParm"))
      {
        tmp_parms.add(methodMapParm);
        wsdlMsgPartsTbl.put(methodMapParm.wsdlMessagePartName, methodMapParm);
        methodMapParm = null;
        inUserInputParm = false;
      }
      else if (inUserInputParm && namespaceURI.equalsIgnoreCase(FMM) && localName.equalsIgnoreCase("ValidParmValues"))
      {
        methodMapParm.parmDomainValues = (String[])tmp_enum.toArray(new String[0]);
        tmp_enum = null;
      }
    }
  }
}
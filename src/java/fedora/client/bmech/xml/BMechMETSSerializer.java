package fedora.client.bmech.xml;

import org.w3c.dom.Document;
import org.w3c.dom.DOMException;
import org.w3c.dom.Element;
import org.w3c.dom.Attr;
import org.w3c.dom.Text;
import org.w3c.dom.Node;
import org.w3c.dom.DocumentFragment;
import javax.xml.transform.dom.DOMResult;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.ParserConfigurationException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.Collection;
import java.util.Iterator;
import java.util.Vector;
import java.io.PrintWriter;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.File;
import java.io.IOException;

import fedora.client.bmech.data.*;
import fedora.client.bmech.BMechBuilderException;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author unascribed
 * @version 1.0
 */

public class BMechMETSSerializer
{
  private static final String METS = "http://www.loc.gov/METS/";

  private static final String AUDIT = "http://fedora.comm.nsdlib.org/audit";

  private static final String DESC = "http://dl.lib.virginia.edu/bin/dtd/descmeta/descmeta.dtd";

  private static final String ADMIN = "http://dl.lib.virginia.edu/bin/dtd/admin/admin.dtd";

  private static final String XLINK = "http://www.w3.org/TR/xlink";

  private static final String SCHEMALOC =
    "http://www.loc.gov/standards/METS/ http://www.fedora.info/definitions/1/0/mets-fedora-ext.xsd";

  private static final String XSI = "http://www.w3.org/2001/XMLSchema-instance";

  private static final String XMLNS = "http://www.w3.org/2000/xmlns/";

  private PrintWriter out;
  private Document document;
  private Element root;
  private Element header;
  private Element profileNode;
  private Element dcNode;
  private Element mmapNode;
  private Element wsdlNode;

  public BMechMETSSerializer(BMechTemplate bMechData,
    Element methodMap, Element wsdl) throws BMechBuilderException
  {
    try
    {
      File file = new File("bmechtest.xml");
      out = new PrintWriter(new BufferedWriter(new FileWriter(file)));
    }
    catch (IOException e)
    {
      e.printStackTrace();
      throw new BMechBuilderException("BMechMETSSerializer: " +
        " IO Exception initializing PrintWriter in constructor.");
    }
    initializeTree();
    genMETS(bMechData, methodMap, wsdl);
    finalizeTree();
  }

  private void initializeTree() throws BMechBuilderException
  {
    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    factory.setValidating(false);
    try
    {
        DocumentBuilder builder =   factory.newDocumentBuilder();
        document = builder.newDocument();
    }
    catch (ParserConfigurationException pce)
    {
      // Parser with specified options can't be built
      pce.printStackTrace();
      throw new BMechBuilderException("BMechMETSSerializer: " +
        " Parser configuration exception initializing document builder.");
    }
    root = (Element)document.createElementNS(METS, "METS:mets");
    header = (Element)document.createElementNS(METS, "METS:metsHdr");
  }

  private void finalizeTree()
  {
    // put the tree together
    document.appendChild(root);
    root.appendChild(header);
    root.appendChild(mmapNode);
    root.appendChild(wsdlNode);
  }

  private void genMETS(BMechTemplate bMechData, Element methodMap, Element wsdl)
    throws BMechBuilderException
  {
    // METS root element
    root.setAttributeNS(XMLNS, "xmlns:METS", METS);
    root.setAttributeNS(XMLNS, "xmlns:fedoraAudit", AUDIT);
    root.setAttributeNS(XMLNS, "xmlns:uvalibdesc", DESC);
    root.setAttributeNS(XMLNS, "xmlns:uvalibadmin", ADMIN);
    root.setAttributeNS(XMLNS, "xmlns:xlink", XLINK);
    root.setAttributeNS(XSI, "xsi:schemaLocation", SCHEMALOC);
    root.setAttribute("OBJID", "");
    root.setAttribute("TYPE", "FedoraBMechObject");
    root.setAttribute("LABEL", "Behavior Mechanism Object implementing " +
      "BDEF " + bMechData.getbDefPID() + " contract using " + bMechData.getbMechLabel());
    root.setAttribute("PROFILE", "fedora:BMECH");

    // METS header element
    header.setAttribute("ID", "H1");
    header.setAttribute("RECORDSTATUS", "I");
    Element agent = document.createElementNS(METS, "METS:agent");
    agent.setAttribute("ID", "A1");
    agent.setAttribute("ROLE", "CREATOR");
    agent.setAttribute("TYPE", "INDIVIDUAL");
    Element agentName = document.createElementNS(METS, "METS:name");
    agentName.appendChild(document.createTextNode("FIXME: name of user here"));
    Element agentNote = document.createElementNS(METS, "METS:note");
    agentNote.appendChild(document.createTextNode("Behavior Mechanism Object " +
      "created using Fedora AdminGUI BMech Builder."));
    agent.appendChild(agentName);
    agent.appendChild(agentNote);
    header.appendChild(agent);
    setInlineMD(methodMap, wsdl);
  }

  private void setInlineMD(Element methodMap, Element wsdl)
    throws BMechBuilderException
  {
    setMethodMap(methodMap);
    setWSDL(wsdl);
  }

  private void setServiceProfile(Element serviceProfile)
  {
    profileNode = document.createElementNS(METS, "METS:amdSec");
    profileNode.setAttribute("ID", "SERVICE-PROFILE");
    Element techMD = document.createElementNS(METS, "METS:techMD");
    techMD.setAttribute("ID", "SERVICE-PROFILE1.0");
    techMD.setAttribute("STATUS", "A");
    Element mdWrap = document.createElementNS(METS, "METS:mdWrap");
    mdWrap.setAttribute("MIMETYPE", "text/xml");
    mdWrap.setAttribute("MDTYPE", "OTHER");
    mdWrap.setAttribute("LABEL", "Service Profile - Technical description of the service");
    Element xmlData = document.createElementNS(METS, "METS:xmlData");
    profileNode.appendChild(
      techMD.appendChild(
        mdWrap.appendChild(
          xmlData.appendChild(
            serviceProfile))));
  }

  private void setMethodMap(Element methodMap) throws BMechBuilderException
  {
    mmapNode = document.createElementNS(METS, "METS:amdSec");
    mmapNode.setAttribute("ID", "METHODMAP");
    Element techMD = document.createElementNS(METS, "METS:techMD");
    techMD.setAttribute("ID", "METHODMAP1.0");
    techMD.setAttribute("STATUS", "A");
    Element mdWrap = document.createElementNS(METS, "METS:mdWrap");
    mdWrap.setAttribute("MIMETYPE", "text/xml");
    mdWrap.setAttribute("MDTYPE", "OTHER");
    mdWrap.setAttribute("LABEL", "Mapping of WSDL to Fedora notion of Method Definitions");
    Element xmlData = document.createElementNS(METS, "METS:xmlData");
    Node importMethodMap = document.importNode(methodMap, true);
    //xmlData.appendChild(importMethodMap);
    //mdWrap.appendChild(xmlData);
    //techMD.appendChild(mdWrap);
    //mmap.appendChild(techMD);

    mmapNode.appendChild(
      techMD.appendChild(
        mdWrap.appendChild(
          xmlData.appendChild(
            importMethodMap))));
  }

  private void setWSDL(Element wsdl) throws BMechBuilderException
  {
    wsdlNode = document.createElementNS(METS, "METS:amdSec");
    wsdlNode.setAttribute("ID", "WSDL");
    Element techMD = document.createElementNS(METS, "METS:techMD");
    techMD.setAttribute("ID", "WSDL1.0");
    techMD.setAttribute("STATUS", "A");
    Element mdWrap = document.createElementNS(METS, "METS:mdWrap");
    mdWrap.setAttribute("MIMETYPE", "text/xml");
    mdWrap.setAttribute("MDTYPE", "OTHER");
    mdWrap.setAttribute("LABEL", "WSDL definition of service");
    Element xmlData = document.createElementNS(METS, "METS:xmlData");
    Node importWSDL = document.importNode(wsdl, true);
    wsdlNode.appendChild(
      techMD.appendChild(
        mdWrap.appendChild(
          xmlData.appendChild(
            importWSDL))));
  }

  public void printMETS()
  {
    try
    {
      String str = new XMLWriter(new DOMResult(document)).getXMLAsString();
      System.out.println(str);
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
  }
}
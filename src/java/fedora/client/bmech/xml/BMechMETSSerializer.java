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
import java.io.InputStream;
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
  private Element bMechFileSec;
  private Vector docDSIDs = new Vector();
  private Element bMechStructMap;
  private Element bMechBehaviorSec;
  private Element profileNode;
  private Element dsInputNode;
  private Element dcNode;
  private Element mmapNode;
  private Element wsdlNode;

  public BMechMETSSerializer(BMechTemplate bMechData, Element dsInputSpec,
    Element methodMap, Element wsdl) throws BMechBuilderException
  {
    initializeTree();
    genMETS(bMechData, dsInputSpec, methodMap, wsdl);
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
    bMechFileSec = (Element)document.createElementNS(METS, "METS:fileSec");
    bMechStructMap = (Element)document.createElementNS(METS, "METS:structMap");
    bMechStructMap.setAttribute("ID", "S1");
    bMechStructMap.setAttribute("TYPE", "fedora:dsBindingMap");
    bMechBehaviorSec = (Element)document.createElementNS(METS, "METS:behaviorSec");
    bMechBehaviorSec.setAttribute("ID", "DISS1");
    bMechBehaviorSec.setAttribute("STATUS", "A");
  }

  private void finalizeTree()
  {
    // put the tree together
    document.appendChild(root);
    root.appendChild(header);
    root.appendChild(dsInputNode);
    root.appendChild(mmapNode);
    root.appendChild(wsdlNode);
    root.appendChild(bMechFileSec);
    root.appendChild(bMechStructMap);
    root.appendChild(bMechBehaviorSec);
  }

  private void genMETS(BMechTemplate bMechData, Element dsInputSpec,
    Element methodMap, Element wsdl) throws BMechBuilderException
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

    setInlineMD(dsInputSpec, methodMap, wsdl);
    setBMechFileSec(bMechData);
    setBMechStructMap(bMechData);
    setBMechBehaviorSec(bMechData);
  }

  private void setBMechFileSec(BMechTemplate bMechData)
  {
    // METS fileSec element for the bMech
    Element datastreamsFileGrp = document.createElementNS(METS, "METS:fileGrp");
    datastreamsFileGrp.setAttribute("ID", "DATASTREAMS");
    // FIXIT!  do a for loop to encode array of documents interned in the GUI
    Datastream[] docs = bMechData.getDocDatastreams();
    for (int i=0; i<docs.length; i++)
    {
      Element dsFileGrp = document.createElementNS(METS, "METS:fileGrp");
      String dsid = "DS" + (i+1);
      dsFileGrp.setAttribute("ID", dsid);
      dsFileGrp.setAttribute("STATUS", "A");
      Element dsFile = document.createElementNS(METS, "METS:file");
      dsFile.setAttribute("ID", (dsid + ".0"));
      dsFile.setAttribute("SEQ", "0");
      dsFile.setAttribute("MIMETYPE", docs[i].dsMIMEType);
      dsFile.setAttribute("OWNERID", docs[i].dsControlGrpType);
      dsFile.setAttribute("STATUS", "A");
      Element dsFileLoc = document.createElementNS(METS, "METS:FLocat");
      dsFileLoc.setAttribute("LOCTYPE", "URL");
      dsFileLoc.setAttributeNS(XLINK, "xlink:href", docs[i].dsURL);
      dsFileLoc.setAttributeNS(XLINK, "xlink:title", docs[i].dsLabel);
      dsFile.appendChild(dsFileLoc);
      dsFileGrp.appendChild(dsFile);
      datastreamsFileGrp.appendChild(dsFileGrp);
      docDSIDs.add(dsid);
    }
    bMechFileSec.appendChild(datastreamsFileGrp);
  }

  private void setBMechStructMap(BMechTemplate bMechData)
  {
    // METS structMap element for the bMech
    // This structMap represents the datastream bindings for the
    // bootstrap disseminator known by the PID fedora-system:2

    Element mainDiv = document.createElementNS(METS, "METS:div");
    mainDiv.setAttribute("TYPE", "fedora-system:2");
    mainDiv.setAttribute("LABEL", "Datastream Binding Map for Fedora Bootstrap Mechanism");

    Element dsInputSpecDiv =
      setDiv("FEDORA-TO-WSDL-DSINPUTSPEC",
      "XML data that describes the requirements for Datastreams that will be used as input to this service",
      "DSINPUTSPEC");
    mainDiv.appendChild(dsInputSpecDiv);

    Element methodMapDiv =
      setDiv("FEDORA-TO-WSDL-METHODMAP",
      "XML data that enables Fedora to understand how to use the service WSDL",
      "METHODMAP");
    mainDiv.appendChild(methodMapDiv);

    Element wsdlDiv =
      setDiv("WSDL",
      "Service definition in WSDL format.",
      "WSDL");
    mainDiv.appendChild(wsdlDiv);

    String[] DSIDs = (String[])docDSIDs.toArray(new String[0]);
    for (int i=0; i<DSIDs.length; i++)
    {
      Element docDiv =
        setDiv("SERVICEDOC",
        "Documentation fo the Service",
        DSIDs[i]);
      mainDiv.appendChild(docDiv);
    }
    bMechStructMap.appendChild(mainDiv);
  }

  private Element setDiv(String type, String label, String dsid)
  {
    Element div = document.createElementNS(METS, "METS:div");
    div.setAttribute("TYPE", type);
    div.setAttribute("LABEL", label);
    Element file = document.createElementNS(METS, "METS:fptr");
    file.setAttribute("FILEID", dsid);
    div.appendChild(file);
    return div;
  }

  private void setBMechBehaviorSec(BMechTemplate bMechData)
  {
    // METS behaviorSec element for the Bootstrap Disseminator on the bMech
    Element serviceBinding = document.createElementNS(METS, "METS:serviceBinding");
    serviceBinding.setAttribute("ID", "DISS1.0");
    serviceBinding.setAttribute("STRUCTID", "S1");
    serviceBinding.setAttribute("BTYPE", "fedora-system:1");
    serviceBinding.setAttribute("LABEL", "Bootstrap Behaviors for a bMech object");

    Element bdef = document.createElementNS(METS, "METS:interfaceMD");
    bdef.setAttribute("LABEL", "Bootstrap Behavior Definition");
    bdef.setAttribute("LOCTYPE", "URN");
    bdef.setAttributeNS(XLINK, "xlink:href", "fedora-system:1");
    serviceBinding.appendChild(bdef);

    Element bmech = document.createElementNS(METS, "METS:serviceBindMD");
    bmech.setAttribute("LABEL", "Bootstrap Behavior Mechanism");
    bmech.setAttribute("LOCTYPE", "URN");
    bmech.setAttributeNS(XLINK, "xlink:href", "fedora-system:2");
    serviceBinding.appendChild(bmech);

    bMechBehaviorSec.appendChild(serviceBinding);
  }

  private void setInlineMD(Element dsInputSpec, Element methodMap, Element wsdl)
    throws BMechBuilderException
  {
    setDSInputSpec(dsInputSpec);
    setMethodMap(methodMap);
    setWSDL(wsdl);
  }

  private void setDSInputSpec(Element dsInputSpec) throws BMechBuilderException
  {
    dsInputNode = document.createElementNS(METS, "METS:amdSec");
    dsInputNode.setAttribute("ID", "DSINPUTSPEC");
    Element techMD = document.createElementNS(METS, "METS:techMD");
    techMD.setAttribute("ID", "DSINPUTSPEC1.0");
    techMD.setAttribute("STATUS", "A");
    Element mdWrap = document.createElementNS(METS, "METS:mdWrap");
    mdWrap.setAttribute("MIMETYPE", "text/xml");
    mdWrap.setAttribute("MDTYPE", "OTHER");
    mdWrap.setAttribute("LABEL", "Datastream Input Specification for Service");
    Element xmlData = document.createElementNS(METS, "METS:xmlData");
    Node importDSInput = document.importNode(dsInputSpec, true);
    xmlData.appendChild(importDSInput);
    mdWrap.appendChild(xmlData);
    techMD.appendChild(mdWrap);
    dsInputNode.appendChild(techMD);
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
    xmlData.appendChild(importMethodMap);
    mdWrap.appendChild(xmlData);
    techMD.appendChild(mdWrap);
    mmapNode.appendChild(techMD);
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
    xmlData.appendChild(importWSDL);
    mdWrap.appendChild(xmlData);
    techMD.appendChild(mdWrap);
    wsdlNode.appendChild(techMD);
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
    Node importProfile = document.importNode(serviceProfile, true);
    xmlData.appendChild(importProfile);
    mdWrap.appendChild(xmlData);
    techMD.appendChild(mdWrap);
    profileNode.appendChild(techMD);
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

  public void writeMETSFile(File file) throws BMechBuilderException
  {
    try
    {
      XMLWriter w = new XMLWriter(document);
      w.writeXMLToFile(file);
    }
    catch (Exception e)
    {
      e.printStackTrace();
      throw new BMechBuilderException("BMechMETSSerializer: " +
        " IO or parser exception writing BMech METS file." +
        " Underlying exception was: " +
        e.getMessage());
    }
  }

  public InputStream writeMETSStream() throws BMechBuilderException
  {
    InputStream in = null;
    try
    {
      XMLWriter w = new XMLWriter(document);
      in = w.writeXMLToStream();
    }
    catch (Exception e)
    {
      e.printStackTrace();
      throw new BMechBuilderException("BMechMETSSerializer: " +
        " IO or parser exception writing BMech METS to stream." +
        " Underlying exception was: " +
        e.getMessage());
    }
    return in;
  }
}
package fedora.client.bmech.xml;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Attr;
import org.w3c.dom.Node;
import javax.xml.transform.dom.DOMResult;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.ParserConfigurationException;
import java.util.Vector;
import java.util.Calendar;
import java.util.Date;
import java.io.PrintWriter;
import java.io.File;
import java.io.InputStream;
import java.text.SimpleDateFormat;

import fedora.client.bmech.data.*;
import fedora.client.bmech.BMechBuilderException;

/**
 *
 * <p><b>Title:</b> BObjMETSSerializer.java</p>
 * <p><b>Description:</b> </p>
 *
 * -----------------------------------------------------------------------------
 *
 * <p><b>License and Copyright: </b>The contents of this file are subject to the
 * Mozilla Public License Version 1.1 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License
 * at <a href="http://www.mozilla.org/MPL">http://www.mozilla.org/MPL/.</a></p>
 *
 * <p>Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License for
 * the specific language governing rights and limitations under the License.</p>
 *
 * <p>The entire file consists of original code.  Copyright &copy; 2002, 2003 by The
 * Rector and Visitors of the University of Virginia and Cornell University.
 * All rights reserved.</p>
 *
 * -----------------------------------------------------------------------------
 *
 * @author payette@cs.cornell.edu
 * @version $Id$
 */

public abstract class BObjMETSSerializer
{
  protected static final String METS = "http://www.loc.gov/METS/";

  protected static final String AUDIT = "http://fedora.comm.nsdlib.org/audit";

  protected static final String DESC = "http://dl.lib.virginia.edu/bin/dtd/descmeta/descmeta.dtd";

  protected static final String ADMIN = "http://dl.lib.virginia.edu/bin/dtd/admin/admin.dtd";

  protected static final String XLINK = "http://www.w3.org/TR/xlink";

  protected static final String SCHEMALOC =
    "http://www.loc.gov/standards/METS/ http://www.fedora.info/definitions/1/0/mets-fedora-ext.xsd";

  protected static final String XSI = "http://www.w3.org/2001/XMLSchema-instance";

  protected static final String XMLNS = "http://www.w3.org/2000/xmlns/";

  protected PrintWriter out;
  protected Document document;
  protected Element root;
  protected Element header;
  protected Element bObjFileSec;
  protected Vector docDSIDs = new Vector();
  protected Element bObjStructMap;
  protected Element bObjBehaviorSec;
  protected BObjTemplate bObjData;
  protected String now;

  public BObjMETSSerializer(BObjTemplate bObjData)
    throws BMechBuilderException
  {
    this.bObjData = bObjData;
    this.now = convertDateToString(getCurrentDate());
  }

  // The BDefMETSSerializer and BMechMETSSerializer will implement this to
  // deal with the specific metadata formats required in each.
  protected abstract Element[] getInlineMD()
    throws BMechBuilderException;

  // The BDefMETSSerializer and BMechMETSSerializer will implement this to
  // deal with the TYPE and PROFILE attributes.
  protected abstract Attr[] getVariableRootAttrs()
    throws BMechBuilderException;

  // The BDefMETSSerializer and BMechMETSSerializer will implement this to
  // deal with the specific datastream bindings required in each.
  protected abstract Element[] getVariableStructMapDivs()
    throws BMechBuilderException;

  protected void serialize() throws BMechBuilderException
  {
    initializeTree();
    genBaseMETS(bObjData);
    finalizeTree();
  }

  protected void initializeTree() throws BMechBuilderException
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
      throw new BMechBuilderException("BObjMETSSerializer: " +
        " Parser configuration exception initializing document builder.");
    }
    root = (Element)document.createElementNS(METS, "METS:mets");
    header = (Element)document.createElementNS(METS, "METS:metsHdr");
    bObjFileSec = (Element)document.createElementNS(METS, "METS:fileSec");
    bObjStructMap = (Element)document.createElementNS(METS, "METS:structMap");
    bObjStructMap.setAttribute("ID", "S1");
    bObjStructMap.setAttribute("TYPE", "fedora:dsBindingMap");
    bObjBehaviorSec = (Element)document.createElementNS(METS, "METS:behaviorSec");
    bObjBehaviorSec.setAttribute("ID", "DISS1");
    bObjBehaviorSec.setAttribute("STATUS", "A");
  }

  protected void finalizeTree() throws BMechBuilderException
  {
    // put the tree together
    document.appendChild(root);
    root.appendChild(header);
    Element[] mdSecs = getInlineMD();
    for (int i=0; i<mdSecs.length; i++)
    {
      root.appendChild(mdSecs[i]);
    }
    root.appendChild(bObjFileSec);
    root.appendChild(bObjStructMap);
    root.appendChild(bObjBehaviorSec);
  }

  protected void genBaseMETS(BObjTemplate bObjData)
    throws BMechBuilderException
  {
    // METS root element
    setMETSRoot(bObjData);
    setMETSHeader();
    setBObjFileSec(bObjData);
    setBObjStructMap(bObjData);
    setBObjBehaviorSec(bObjData);
  }

  protected void setMETSRoot(BObjTemplate bObjData) throws BMechBuilderException
  {
    // METS root element
    root.setAttributeNS(XMLNS, "xmlns:METS", METS);
    root.setAttributeNS(XMLNS, "xmlns:fedoraAudit", AUDIT);
    root.setAttributeNS(XMLNS, "xmlns:uvalibdesc", DESC);
    root.setAttributeNS(XMLNS, "xmlns:uvalibadmin", ADMIN);
    root.setAttributeNS(XMLNS, "xmlns:xlink", XLINK);
    root.setAttributeNS(XSI, "xsi:schemaLocation", SCHEMALOC);
    String pid = (bObjData.getbObjPID() == null) ? "" : bObjData.getbObjPID();
    root.setAttribute("OBJID", pid);
    String label = (bObjData.getbObjLabel() == null) ? "" : bObjData.getbObjLabel();
    root.setAttribute("LABEL", label);
    Attr[] attrSet = getVariableRootAttrs();
    for (int i=0; i<attrSet.length; i++)
    {
      root.setAttributeNodeNS(attrSet[i]);
    }
  }

  protected void setMETSHeader()
  {
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
    agentNote.appendChild(document.createTextNode("Object " +
      "created using Fedora AdminGUI Behavior Object Builder."));
    agent.appendChild(agentName);
    agent.appendChild(agentNote);
    header.appendChild(agent);
  }

  protected void setBObjFileSec(BObjTemplate bObjData)
  {
    // METS fileSec element for the behavior object
    Element datastreamsFileGrp = document.createElementNS(METS, "METS:fileGrp");
    datastreamsFileGrp.setAttribute("ID", "DATASTREAMS");
    Datastream[] docs = bObjData.getDocDatastreams();
    for (int i=0; i<docs.length; i++)
    {
      Element dsFileGrp = document.createElementNS(METS, "METS:fileGrp");
      String dsid = "DS" + (i+1);
      dsFileGrp.setAttribute("ID", dsid);
      dsFileGrp.setAttribute("STATUS", "A");
      Element dsFile = document.createElementNS(METS, "METS:file");
      dsFile.setAttribute("ID", (dsid + ".0"));
      dsFile.setAttribute("CREATED", now);
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
    bObjFileSec.appendChild(datastreamsFileGrp);
  }

  protected void setBObjStructMap(BObjTemplate bObjData) throws BMechBuilderException
  {
    // METS structMap element for the behavior object
    // This structMap represents the datastream bindings for the
    // bootstrap disseminator known by the PID fedora-system:2

    Element mainDiv = document.createElementNS(METS, "METS:div");
    mainDiv.setAttribute("TYPE", "fedora-system:2");
    mainDiv.setAttribute("LABEL", "Datastream Binding Map for Fedora Bootstrap Mechanism");

    Element[] customDivs = getVariableStructMapDivs();
    for (int d=0; d<customDivs.length; d++)
    {
      mainDiv.appendChild(customDivs[d]);
    }

    String[] DSIDs = (String[])docDSIDs.toArray(new String[0]);
    for (int i=0; i<DSIDs.length; i++)
    {
      Element docDiv =
        setDiv("SERVICEDOC",
        "Documentation for the behavior object",
        DSIDs[i]);
      mainDiv.appendChild(docDiv);
    }
    bObjStructMap.appendChild(mainDiv);
  }

  protected Element setDiv(String type, String label, String dsid)
  {
    Element div = document.createElementNS(METS, "METS:div");
    div.setAttribute("TYPE", type);
    div.setAttribute("LABEL", label);
    Element file = document.createElementNS(METS, "METS:fptr");
    file.setAttribute("FILEID", dsid);
    div.appendChild(file);
    return div;
  }

  protected void setBObjBehaviorSec(BObjTemplate bObjData)
  {
    // METS behaviorSec element for the Bootstrap Disseminator on the bMech
    Element serviceBinding = document.createElementNS(METS, "METS:serviceBinding");
    serviceBinding.setAttribute("ID", "DISS1.0");
    serviceBinding.setAttribute("CREATED", now);
    serviceBinding.setAttribute("STRUCTID", "S1");
    serviceBinding.setAttribute("BTYPE", "fedora-system:1");
    serviceBinding.setAttribute("LABEL", "Bootstrap Behaviors for a behavior object");

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

    bObjBehaviorSec.appendChild(serviceBinding);
  }

  protected Element setDC(Element dc) throws BMechBuilderException
  {
    Element dcNode = document.createElementNS(METS, "METS:dmdSecFedora");
    dcNode.setAttribute("ID", "DC");
    Element descMD = document.createElementNS(METS, "METS:descMD");
    descMD.setAttribute("ID", "DC1.0");
    descMD.setAttribute("CREATED", now);
    descMD.setAttribute("STATUS", "A");
    Element mdWrap = document.createElementNS(METS, "METS:mdWrap");
    mdWrap.setAttribute("MIMETYPE", "text/xml");
    mdWrap.setAttribute("MDTYPE", "OTHER");
    mdWrap.setAttribute("LABEL", "Dublin Core Metadata for Service");
    Element xmlData = document.createElementNS(METS, "METS:xmlData");
    Node importDC = document.importNode(dc, true);
    xmlData.appendChild(importDC);
    mdWrap.appendChild(xmlData);
    descMD.appendChild(mdWrap);
    dcNode.appendChild(descMD);
    return dcNode;
  }

  public Date getCurrentDate()
  {
    //Calendar cal = Calendar.getInstance(TimeZone.getDefault());
    Calendar cal = Calendar.getInstance();
    return cal.getTime();
  }

  public String convertDateToString(Date date)
  {
    String dateTime = null;
    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
    if (!(date == null))
    {
      dateTime = formatter.format(date);
    }
    return(dateTime);
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
      throw new BMechBuilderException("BObjMETSSerializer: " +
        " IO or parser exception writing METS file." +
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
      throw new BMechBuilderException("BObjMETSSerializer: " +
        " IO or parser exception writing METS to stream." +
        " Underlying exception was: " +
        e.getMessage());
    }
    return in;
  }
}
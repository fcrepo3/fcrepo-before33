package fedora.client.bmech.xml;

import org.w3c.dom.Element;
import org.w3c.dom.Attr;
import org.w3c.dom.Node;
import java.util.Vector;
import fedora.client.bmech.data.*;
import fedora.client.bmech.BMechBuilderException;

/**
 *
 * <p><b>Title:</b> BMechMETSSerializer.java</p>
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
 * <p>The entire file consists of original code.  Copyright © 2002, 2003 by The
 * Rector and Visitors of the University of Virginia and Cornell University.
 * All rights reserved.</p>
 *
 * -----------------------------------------------------------------------------
 *
 * @author payette@cs.cornell.edu
 * @version 1.0
 */

public class BMechMETSSerializer extends BObjMETSSerializer
{
  private Element in_dc;
  private Element in_dsInputSpec;
  private Element in_methodMap;
  private Element in_wsdl;


  public BMechMETSSerializer(BMechTemplate bMechData, Element dc,
    Element dsInputSpec, Element methodMap, Element wsdl)
    throws BMechBuilderException
  {
    super((BObjTemplate)bMechData);
    in_dc = dc;
    in_dsInputSpec = dsInputSpec;
    in_methodMap = methodMap;
    in_wsdl = wsdl;
    serialize();
  }

  protected Attr[] getVariableRootAttrs()
  {
    Vector v_attrs = new Vector();
    Attr type = document.createAttribute("TYPE");
    type.setValue("FedoraBMechObject");
    v_attrs.add(type);
    Attr profile = document.createAttribute("PROFILE");
    profile.setValue("fedora:BMECH");
    v_attrs.add(profile);
    return (Attr[])v_attrs.toArray(new Attr[0]);
  }

  protected Element[] getVariableStructMapDivs()
  {
    Vector v_divs = new Vector();

    Element dsInputSpecDiv =
      setDiv("FEDORA-TO-WSDL-DSINPUTSPEC",
      "XML data that describes the requirements for Datastreams that will be used as input to this service",
      "DSINPUTSPEC");
    v_divs.add(dsInputSpecDiv);

    Element methodMapDiv =
      setDiv("FEDORA-TO-WSDL-METHODMAP",
      "XML data that enables Fedora to understand how to use the service WSDL",
      "METHODMAP");
    v_divs.add(methodMapDiv);

    Element wsdlDiv =
      setDiv("WSDL",
      "Service definition in WSDL format.",
      "WSDL");
    v_divs.add(wsdlDiv);

    return (Element[])v_divs.toArray(new Element[0]);
  }

  protected Element[] getInlineMD() throws BMechBuilderException
  {
    Vector v_elements = new Vector();
    v_elements.add(setDC(in_dc));
    v_elements.add(setDSInputSpec(in_dsInputSpec));
    v_elements.add(setMethodMap(in_methodMap));
    v_elements.add(setWSDL(in_wsdl));
    return (Element[])v_elements.toArray(new Element[0]);
  }

  private Element setDSInputSpec(Element dsInputSpec) throws BMechBuilderException
  {
    Element dsInputNode = document.createElementNS(METS, "METS:amdSec");
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
    return dsInputNode;
  }

  private Element setMethodMap(Element methodMap) throws BMechBuilderException
  {
    Element mmapNode = document.createElementNS(METS, "METS:amdSec");
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
    return mmapNode;
  }

  private Element setWSDL(Element wsdl) throws BMechBuilderException
  {
    Element wsdlNode = document.createElementNS(METS, "METS:amdSec");
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
    return wsdlNode;
  }

  private Element setServiceProfile(Element serviceProfile)
  {
    Element profileNode = document.createElementNS(METS, "METS:amdSec");
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
    return profileNode;
  }
}

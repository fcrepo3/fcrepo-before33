package fedora.client.bmech.xml;

import org.w3c.dom.Element;
import org.w3c.dom.Attr;
import org.w3c.dom.Node;
import java.util.Vector;
import fedora.client.bmech.data.*;
import fedora.client.bmech.BMechBuilderException;

/**
 *
 * <p><b>Title:</b> BDefMETSSerializer.java</p>
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
 * <p>The entire file consists of original code.  Copyright &copy; 2002-2004 by The
 * Rector and Visitors of the University of Virginia and Cornell University.
 * All rights reserved.</p>
 *
 * -----------------------------------------------------------------------------
 *
 * @author payette@cs.cornell.edu
 * @version $Id$
 */

public class BDefMETSSerializer extends BObjMETSSerializer
{
  private Element in_dc;
  private Element in_methodMap;

  public BDefMETSSerializer(BObjTemplate bDefData, Element dc, Element methodMap)
    throws BMechBuilderException
  {
    super((BObjTemplate)bDefData);
    in_dc = dc;
    in_methodMap = methodMap;
    serialize();
  }

  protected Attr[] getVariableRootAttrs()
  {
    Vector v_attrs = new Vector();
    Attr type = document.createAttribute("TYPE");
    type.setValue("FedoraBDefObject");
    v_attrs.add(type);
    Attr profile = document.createAttribute("PROFILE");
    profile.setValue("fedora:BDEF");
    v_attrs.add(profile);
    return (Attr[])v_attrs.toArray(new Attr[0]);
  }

  protected Element[] getVariableStructMapDivs()
  {
    Vector v_divs = new Vector();
    Element methodMapDiv =
      setDiv("FEDORA-TO-WSDL-METHODMAP",
      "XML data that describes an abstract set of methods for the bdef.",
      "METHODMAP");
    v_divs.add(methodMapDiv);
    return (Element[])v_divs.toArray(new Element[0]);
  }

  protected Element[] getInlineMD() throws BMechBuilderException
  {
    Vector v_elements = new Vector();
    v_elements.add(setDC(in_dc));
    v_elements.add(setMethodMap(in_methodMap));
    return (Element[])v_elements.toArray(new Element[0]);
  }

  private Element setMethodMap(Element methodMap) throws BMechBuilderException
  {
    Element mmapNode = document.createElementNS(METS, "METS:amdSec");
    mmapNode.setAttribute("ID", "METHODMAP");
    Element techMD = document.createElementNS(METS, "METS:techMD");
    techMD.setAttribute("ID", "METHODMAP1.0");
    techMD.setAttribute("CREATED", now);
    techMD.setAttribute("STATUS", "A");
    Element mdWrap = document.createElementNS(METS, "METS:mdWrap");
    mdWrap.setAttribute("MIMETYPE", "text/xml");
    mdWrap.setAttribute("MDTYPE", "OTHER");
    mdWrap.setAttribute("LABEL", "Abstract Method Definitions");
    Element xmlData = document.createElementNS(METS, "METS:xmlData");
    Node importMethodMap = document.importNode(methodMap, true);
    xmlData.appendChild(importMethodMap);
    mdWrap.appendChild(xmlData);
    techMD.appendChild(mdWrap);
    mmapNode.appendChild(techMD);
    return mmapNode;
  }
}
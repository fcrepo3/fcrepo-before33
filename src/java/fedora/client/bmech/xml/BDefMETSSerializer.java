package fedora.client.bmech.xml;

import org.w3c.dom.Element;
import org.w3c.dom.Attr;
import org.w3c.dom.Node;
import java.util.Vector;
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

public class BDefMETSSerializer extends BObjMETSSerializer
{
  private Element in_methodMap;

  public BDefMETSSerializer(BObjTemplate bDefData, Element methodMap)
    throws BMechBuilderException
  {
    super((BObjTemplate)bDefData);
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
    v_elements.add(setMethodMap(in_methodMap));
    return (Element[])v_elements.toArray(new Element[0]);
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
}
package fedora.client.bmech.xml;

import org.w3c.dom.Document;
import org.w3c.dom.DOMException;
import org.w3c.dom.Element;
import javax.xml.transform.dom.DOMResult;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.ParserConfigurationException;

import fedora.client.bmech.data.*;
import fedora.client.bmech.BMechBuilderException;

/**
 *
 * <p><b>Title:</b> DSInputSpecGenerator.java</p>
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
public class DSInputSpecGenerator
{
  private static final String FBS =
    "http://fedora.comm.nsdlib.org/service/bindspec";

  private static final String XMLNS = "http://www.w3.org/2000/xmlns/";

  private Document document;

  public DSInputSpecGenerator(BMechTemplate newBMech) throws BMechBuilderException
  {
    createDOM();
    genDSInputSpec(newBMech);
  }

  private void createDOM() throws BMechBuilderException
  {
    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    try
    {
        DocumentBuilder builder =   factory.newDocumentBuilder();
        document = builder.newDocument();

    }
    catch (ParserConfigurationException pce)
    {
      // Parser with specified options can't be built
      pce.printStackTrace();
      throw new BMechBuilderException("DSInputSpecGenerator: error configuring parser."
        + "Underlying exception: " + pce.getMessage());
    }
  }

  private void genDSInputSpec(BMechTemplate newBMech)
  {
    DSInputRule[] rules = newBMech.getDSInputSpec();

    Element root = (Element)document.createElementNS(FBS, "fbs:DSInputSpec");
    root.setAttributeNS(XMLNS, "xmlns:fbs", FBS);
    String bmlabel = (newBMech.getbObjLabel() == null) ? "" : newBMech.getbObjLabel();
    root.setAttribute("label", ("Datastream Input Specification for " + bmlabel));
    String bDefPID = (newBMech.getbDefContractPID() == null) ? "" : newBMech.getbDefContractPID();
    root.setAttribute("bDefPID", bDefPID);
    document.appendChild(root);

    for (int i=0; i<rules.length; i++)
    {
      Element dsInput = document.createElementNS(FBS, "fbs:DSInput");
      String bindKeyName = (rules[i].bindingKeyName == null) ? "" : rules[i].bindingKeyName;
      String mime = (rules[i].bindingMIMEType == null) ? "" : rules[i].bindingMIMEType;
      String min = (rules[i].minNumBindings == null) ? "" : rules[i].minNumBindings;
      String max = (rules[i].maxNumBindings == null) ? "" : rules[i].maxNumBindings;
      String order = (rules[i].ordinality == null) ? "" : rules[i].ordinality;
      String label = (rules[i].bindingLabel == null) ? "" : rules[i].bindingLabel;
      String instr = (rules[i].bindingInstruction == null) ? "" : rules[i].bindingInstruction;
      dsInput.setAttribute("wsdlMsgPartName", bindKeyName.trim());
      dsInput.setAttribute("DSMin", min.trim());
      dsInput.setAttribute("DSMax", max.trim());
      dsInput.setAttribute("DSOrdinality", order.trim());
      Element dsLabel = document.createElementNS(FBS, "fbs:DSInputLabel");
      dsLabel.appendChild(document.createTextNode(label));
      Element dsMIME = document.createElementNS(FBS, "fbs:DSMIME");
      dsMIME.appendChild(document.createTextNode(mime));
      Element dsInstr = document.createElementNS(FBS, "fbs:DSInputInstruction");
      dsInstr.appendChild(document.createTextNode(instr));
      dsInput.appendChild(dsLabel);
      dsInput.appendChild(dsMIME);
      dsInput.appendChild(dsInstr);
      root.appendChild(dsInput);
    }
  }

  public Element getRootElement()
  {
    return document.getDocumentElement();
  }

  public Document getDocument()
  {
    return document;
  }

  public void printDSInputSpec()
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
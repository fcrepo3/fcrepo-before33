package fedora.client.bmech.xml;

import org.w3c.dom.Document;
import org.w3c.dom.DOMException;
import org.w3c.dom.Element;
import javax.xml.transform.dom.DOMResult;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.ParserConfigurationException;

import fedora.client.bmech.data.*;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author unascribed
 * @version 1.0
 */

public class DSInputSpecGenerator
{
  private static final String FBS =
    "http://fedora.comm.nsdlib.org/service/bindspec";

  private Document document;

  public DSInputSpecGenerator(BMechTemplate newBMech)
  {
    createDOM();
    genDSInputSpec(newBMech);
  }

  private void createDOM()
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
    }
  }

  private void genDSInputSpec(BMechTemplate newBMech)
  {
    DSInputRule[] rules = newBMech.getDSInputSpec();

    Element root = (Element)document.createElementNS(FBS, "fbs:DSInputSpec");
    String bmlabel = (newBMech.getbMechLabel() == null) ? "" : newBMech.getbMechLabel();
    root.setAttribute("label", ("Datastream Input Specification for " + bmlabel));
    String bDefPID = (newBMech.getbDefPID() == null) ? "" : newBMech.getbDefPID();
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
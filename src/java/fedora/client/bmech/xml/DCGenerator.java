package fedora.client.bmech.xml;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import javax.xml.transform.dom.DOMResult;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.ParserConfigurationException;

import fedora.client.bmech.data.*;
import fedora.client.bmech.BMechBuilderException;

/**
 *
 * <p><b>Title:</b> DCGenerator.java</p>
 * <p><b>Description:</b> </p>
 *
 * @author payette@cs.cornell.edu
 * @version $Id$
 */
public class DCGenerator
{
  private static final String OAIDC =
    "http://www.openarchives.org/OAI/2.0/oai_dc/";

  private static final String DC = "http://purl.org/dc/elements/1.1/";

  private static final String XMLNS = "http://www.w3.org/2000/xmlns/";

  private Document document;

  public DCGenerator(BObjTemplate newBObj) throws BMechBuilderException
  {
    createDOM();
    genDC(newBObj);
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
      pce.printStackTrace();
      throw new BMechBuilderException("DCGenerator: error configuring parser."
        + "Underlying exception: " + pce.getMessage());
    }
  }

  private void genDC(BObjTemplate newBObj)
  {
    DCElement[] elements = newBObj.getDCRecord();

    Element root = (Element)document.createElementNS(OAIDC, "oai_dc:dc");
    root.setAttributeNS(XMLNS, "xmlns:oai_dc", OAIDC);
    root.setAttributeNS(XMLNS, "xmlns:dc", DC);
    document.appendChild(root);

    for (int i=0; i<elements.length; i++)
    {
      Element e = document.createElementNS(DC, ("dc:" + elements[i].elementName));
      e.appendChild(document.createTextNode(elements[i].elementValue));
      root.appendChild(e);
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

  public void printDC()
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
/* The contents of this file are subject to the license and copyright terms
 * detailed in the license directory at the root of the source tree (also 
 * available online at http://www.fedora.info/license/).
 */

package fedora.client.bmech.xml;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.dom.DOMResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import fedora.client.bmech.BMechBuilderException;
import fedora.client.bmech.data.BObjTemplate;
import fedora.client.bmech.data.DCElement;

import fedora.common.Constants;

/**
 * @author Sandy Payette
 */
public class DCGenerator
        implements Constants {

    private Document document;

    public DCGenerator(BObjTemplate newBObj)
            throws BMechBuilderException {
        createDOM();
        genDC(newBObj);
    }

    private void createDOM() throws BMechBuilderException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder builder = factory.newDocumentBuilder();
            document = builder.newDocument();

        } catch (ParserConfigurationException pce) {
            pce.printStackTrace();
            throw new BMechBuilderException("DCGenerator: error configuring parser."
                    + "Underlying exception: " + pce.getMessage());
        }
    }

    private void genDC(BObjTemplate newBObj) {
        DCElement[] elements = newBObj.getDCRecord();

        Element root = document.createElementNS(OAI_DC.uri, "oai_dc:dc");
        root.setAttributeNS(XMLNS.uri, "xmlns:oai_dc", OAI_DC.uri);
        root.setAttributeNS(XMLNS.uri, "xmlns:dc", DC.uri);
        document.appendChild(root);

        for (DCElement element : elements) {
            Element e =
                    document.createElementNS(DC.uri,
                                             ("dc:" + element.elementName));
            e.appendChild(document.createTextNode(element.elementValue));
            root.appendChild(e);
        }
    }

    public Element getRootElement() {
        return document.getDocumentElement();
    }

    public Document getDocument() {
        return document;
    }

    public void printDC() {
        try {
            String str =
                    new XMLWriter(new DOMResult(document)).getXMLAsString();
            System.out.println(str);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
/* The contents of this file are subject to the license and copyright terms
 * detailed in the license directory at the root of the source tree (also 
 * available online at http://www.fedora.info/license/).
 */

package fedora.client.deployment.xml;

import java.util.Vector;

import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import fedora.client.deployment.DeploymentBuilderException;
import fedora.client.deployment.data.BObjTemplate;

/**
 * @author Sandy Payette
 */
public class ServiceDefinitionMETSSerializer
        extends BObjMETSSerializer {

    private final Element in_dc;

    private final Element in_methodMap;

    public ServiceDefinitionMETSSerializer(BObjTemplate sDefData,
                              Element dc,
                              Element methodMap)
            throws DeploymentBuilderException {
        super(sDefData);
        in_dc = dc;
        in_methodMap = methodMap;
        serialize();
    }

    @Override
    protected Attr[] getVariableRootAttrs() {
        Vector<Attr> v_attrs = new Vector<Attr>();
        Attr extVersion = document.createAttribute("EXT_VERSION");
        extVersion.setValue("1.1");
        v_attrs.add(extVersion);;
        return v_attrs.toArray(new Attr[0]);
    }

    @Override
    protected Element[] getInlineMD() throws DeploymentBuilderException {
        Vector<Element> v_elements = new Vector<Element>();
        v_elements.add(setDC(in_dc));
        v_elements.add(setMethodMap(in_methodMap));
        return v_elements.toArray(new Element[0]);
    }

    private Element setMethodMap(Element methodMap)
            throws DeploymentBuilderException {
        Element mmapNode = document.createElementNS(METS.uri, "METS:amdSec");
        mmapNode.setAttribute("ID", "METHODMAP");
        Element techMD = document.createElementNS(METS.uri, "METS:techMD");
        techMD.setAttribute("ID", "METHODMAP1.0");
        techMD.setAttribute("CREATED", now);
        techMD.setAttribute("STATUS", "A");
        Element mdWrap = document.createElementNS(METS.uri, "METS:mdWrap");
        mdWrap.setAttribute("MIMETYPE", "text/xml");
        mdWrap.setAttribute("MDTYPE", "OTHER");
        mdWrap.setAttribute("LABEL", "Abstract Method Definitions");
        Element xmlData = document.createElementNS(METS.uri, "METS:xmlData");
        Node importMethodMap = document.importNode(methodMap, true);
        xmlData.appendChild(importMethodMap);
        mdWrap.appendChild(xmlData);
        techMD.appendChild(mdWrap);
        mmapNode.appendChild(techMD);
        return mmapNode;
    }
}
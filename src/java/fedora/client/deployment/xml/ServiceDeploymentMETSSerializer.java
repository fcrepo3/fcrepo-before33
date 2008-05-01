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
import fedora.client.deployment.data.ServiceDeploymentTemplate;

/**
 * @author Sandy Payette
 */
public class ServiceDeploymentMETSSerializer
        extends BObjMETSSerializer {

    private final Element in_dc;

    private final Element in_profile;

    private final Element in_dsInputSpec;

    private final Element in_methodMap;

    private final Element in_wsdl;

    public ServiceDeploymentMETSSerializer(ServiceDeploymentTemplate sDepData,
                               Element dc,
                               Element serviceProfile,
                               Element dsInputSpec,
                               Element methodMap,
                               Element wsdl)
            throws DeploymentBuilderException {
        super(sDepData);
        in_dc = dc;
        in_profile = serviceProfile;
        in_dsInputSpec = dsInputSpec;
        in_methodMap = methodMap;
        in_wsdl = wsdl;
        serialize();
    }

    @Override
    protected Attr[] getVariableRootAttrs() {
        Vector<Attr> v_attrs = new Vector<Attr>();
        Attr extVersion = document.createAttribute("EXT_VERSION");
        extVersion.setValue("1.1");
        v_attrs.add(extVersion);
        return v_attrs.toArray(new Attr[0]);
    }

    @Override
    protected Element[] getInlineMD() throws DeploymentBuilderException {
        Vector<Element> v_elements = new Vector<Element>();
        v_elements.add(setDC(in_dc));
        v_elements.add(setServiceProfile(in_profile));
        v_elements.add(setDSInputSpec(in_dsInputSpec));
        v_elements.add(setMethodMap(in_methodMap));
        v_elements.add(setWSDL(in_wsdl));
        return v_elements.toArray(new Element[0]);
    }

    private Element setDSInputSpec(Element dsInputSpec)
            throws DeploymentBuilderException {
        Element dsInputNode = document.createElementNS(METS.uri, "METS:amdSec");
        dsInputNode.setAttribute("ID", "DSINPUTSPEC");
        Element techMD = document.createElementNS(METS.uri, "METS:techMD");
        techMD.setAttribute("ID", "DSINPUTSPEC1.0");
        techMD.setAttribute("CREATED", now);
        techMD.setAttribute("STATUS", "A");
        Element mdWrap = document.createElementNS(METS.uri, "METS:mdWrap");
        mdWrap.setAttribute("MIMETYPE", "text/xml");
        mdWrap.setAttribute("MDTYPE", "OTHER");
        mdWrap.setAttribute("LABEL",
                            "Datastream Input Specification for Service");
        Element xmlData = document.createElementNS(METS.uri, "METS:xmlData");
        Node importDSInput = document.importNode(dsInputSpec, true);
        xmlData.appendChild(importDSInput);
        mdWrap.appendChild(xmlData);
        techMD.appendChild(mdWrap);
        dsInputNode.appendChild(techMD);
        return dsInputNode;
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
        mdWrap
                .setAttribute("LABEL",
                              "Mapping of WSDL to Fedora notion of Method Definitions");
        Element xmlData = document.createElementNS(METS.uri, "METS:xmlData");
        Node importMethodMap = document.importNode(methodMap, true);
        xmlData.appendChild(importMethodMap);
        mdWrap.appendChild(xmlData);
        techMD.appendChild(mdWrap);
        mmapNode.appendChild(techMD);
        return mmapNode;
    }

    private Element setWSDL(Element wsdl) throws DeploymentBuilderException {
        Element wsdlNode = document.createElementNS(METS.uri, "METS:amdSec");
        wsdlNode.setAttribute("ID", "WSDL");
        Element techMD = document.createElementNS(METS.uri, "METS:techMD");
        techMD.setAttribute("ID", "WSDL1.0");
        techMD.setAttribute("CREATED", now);
        techMD.setAttribute("STATUS", "A");
        Element mdWrap = document.createElementNS(METS.uri, "METS:mdWrap");
        mdWrap.setAttribute("MIMETYPE", "text/xml");
        mdWrap.setAttribute("MDTYPE", "OTHER");
        mdWrap.setAttribute("LABEL", "WSDL definition of service");
        Element xmlData = document.createElementNS(METS.uri, "METS:xmlData");
        Node importWSDL = document.importNode(wsdl, true);
        xmlData.appendChild(importWSDL);
        mdWrap.appendChild(xmlData);
        techMD.appendChild(mdWrap);
        wsdlNode.appendChild(techMD);
        return wsdlNode;
    }

    private Element setServiceProfile(Element serviceProfile) {
        Element profileNode = document.createElementNS(METS.uri, "METS:amdSec");
        profileNode.setAttribute("ID", "SERVICE-PROFILE");
        Element techMD = document.createElementNS(METS.uri, "METS:techMD");
        techMD.setAttribute("ID", "SERVICE-PROFILE1.0");
        techMD.setAttribute("CREATED", now);
        techMD.setAttribute("STATUS", "A");
        Element mdWrap = document.createElementNS(METS.uri, "METS:mdWrap");
        mdWrap.setAttribute("MIMETYPE", "text/xml");
        mdWrap.setAttribute("MDTYPE", "OTHER");
        mdWrap
                .setAttribute("LABEL",
                              "Service Profile - Technical description of the service");
        Element xmlData = document.createElementNS(METS.uri, "METS:xmlData");
        Node importProfile = document.importNode(serviceProfile, true);
        xmlData.appendChild(importProfile);
        mdWrap.appendChild(xmlData);
        techMD.appendChild(mdWrap);
        profileNode.appendChild(techMD);
        return profileNode;
    }
}

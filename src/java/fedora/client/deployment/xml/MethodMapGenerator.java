/* The contents of this file are subject to the license and copyright terms
 * detailed in the license directory at the root of the source tree (also 
 * available online at http://www.fedora.info/license/).
 */

package fedora.client.deployment.xml;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.dom.DOMResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import fedora.client.deployment.DeploymentBuilderException;
import fedora.client.deployment.data.ServiceDeploymentTemplate;
import fedora.client.deployment.data.BObjTemplate;
import fedora.client.deployment.data.Method;
import fedora.client.deployment.data.MethodParm;

import fedora.common.Constants;

/**
 * @author Sandy Payette
 */
public class MethodMapGenerator
        implements Constants {

    private Document document;

    public MethodMapGenerator(ServiceDeploymentTemplate newSDep)
            throws DeploymentBuilderException {
        createDOM();
        genMethodMap(newSDep);
    }

    public MethodMapGenerator(BObjTemplate newSDef)
            throws DeploymentBuilderException {
        createDOM();
        genMethodMap(newSDef);
    }

    private void createDOM() throws DeploymentBuilderException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder builder = factory.newDocumentBuilder();
            document = builder.newDocument();

        } catch (ParserConfigurationException pce) {
            // Parser with specified options can't be built
            pce.printStackTrace();
            throw new DeploymentBuilderException("MethodMapGenerator: error configuring parser."
                    + "Underlying exception: " + pce.getMessage());
        }
    }

    private void genMethodMap(BObjTemplate newSDef) {
        Method[] methods = newSDef.getMethods();
        Element root =
                document.createElementNS(METHOD_MAP.uri, "fmm:MethodMap");
        root.setAttributeNS(XMLNS.uri, "xmlns:fmm", METHOD_MAP.uri);
        String sdeflabel =
                newSDef.getbObjLabel() == null ? "fix me" : newSDef
                        .getbObjLabel();
        root.setAttribute("name", ("MethodMap - " + sdeflabel));
        document.appendChild(root);

        for (Method element : methods) {
            Element method =
                    document.createElementNS(METHOD_MAP.uri, "fmm:Method");
            String mname = element.methodName;
            String mlabel =
                    element.methodLabel == null ? "fix me"
                            : element.methodLabel;
            method.setAttribute("operationName", mname.trim());
            method.setAttribute("label", mlabel.trim());
            root.appendChild(method);

            // Append Method Parm elements
            MethodParm[] parms = element.methodProperties.methodParms;
            for (MethodParm element2 : parms) {
                Element parm = null;
                if (element2.parmType.equalsIgnoreCase(MethodParm.USER_INPUT)) {
                    parm =
                            document.createElementNS(METHOD_MAP.uri,
                                                     "fmm:UserInputParm");
                } else {
                    //FIXIT!  throw error on invalid parm type.
                }
                String name =
                        element2.parmName == null ? "" : element2.parmName;
                parm.setAttribute("parmName", name);
                String passby =
                        element2.parmPassBy == null ? "" : element2.parmPassBy;
                parm.setAttribute("passBy", passby);
                String req =
                        element2.parmRequired == null ? ""
                                : element2.parmRequired;
                parm.setAttribute("required", req);
                String def =
                        element2.parmDefaultValue == null ? ""
                                : element2.parmDefaultValue;
                parm.setAttribute("defaultValue", def);
                String label =
                        element2.parmLabel == null ? "" : element2.parmLabel;
                parm.setAttribute("label", label);

                if (element2.parmDomainValues.length > 0) {
                    Element parmDomain =
                            document.createElementNS(METHOD_MAP.uri,
                                                     "fmm:ValidParmValues");
                    for (String element3 : element2.parmDomainValues) {
                        Element parmDomainVal =
                                document.createElementNS(METHOD_MAP.uri,
                                                         "fmm:ValidParm");
                        String value = element3 == null ? "" : element3;
                        parmDomainVal.setAttribute("value", value);
                        parmDomain.appendChild(parmDomainVal);
                    }
                    parm.appendChild(parmDomain);
                }
                method.appendChild(parm);
            }
        }
    }

    private void genMethodMap(ServiceDeploymentTemplate newSDep) {
        Method[] methods = newSDep.getMethods();
        Element root =
                document.createElementNS(METHOD_MAP.uri, "fmm:MethodMap");
        String bmlabel =
                newSDep.getbObjLabel() == null ? "fix me" : newSDep
                        .getbObjLabel();
        root.setAttribute("name", ("MethodMap - " + bmlabel));
        
     // FIXME: this bdefPID attribute may not be necessary any more
        root.setAttribute("bDefPID", newSDep.getSDefContractPID());
        document.appendChild(root);

        for (Method element : methods) {
            Element method =
                    document.createElementNS(METHOD_MAP.uri, "fmm:Method");
            String mname = element.methodName;
            String mlabel =
                    element.methodLabel == null ? "fix me"
                            : element.methodLabel;
            method.setAttribute("operationName", mname.trim());
            method.setAttribute("operationLabel", mlabel.trim());
            method.setAttribute("wsdlMsgName", (mname.trim() + "Request"));
            method.setAttribute("wsdlMsgOutput", "dissemResponse");
            root.appendChild(method);

            // Append Method Parm elements
            MethodParm[] parms = element.methodProperties.methodParms;
            for (MethodParm element2 : parms) {
                Element parm = null;
                if (element2.parmType
                        .equalsIgnoreCase(MethodParm.DATASTREAM_INPUT)) {
                    parm =
                            document.createElementNS(METHOD_MAP.uri,
                                                     "fmm:DatastreamInputParm");
                } else if (element2.parmType
                        .equalsIgnoreCase(MethodParm.USER_INPUT)) {
                    parm =
                            document.createElementNS(METHOD_MAP.uri,
                                                     "fmm:UserInputParm");
                } else if (element2.parmType
                        .equalsIgnoreCase(MethodParm.DEFAULT_INPUT)) {
                    parm =
                            document.createElementNS(METHOD_MAP.uri,
                                                     "fmm:DefaultInputParm");
                } else {
                    //FIXIT!  throw error on invalid parm type.
                }

                String name =
                        element2.parmName == null ? "" : element2.parmName;
                parm.setAttribute("parmName", name);
                String passby =
                        element2.parmPassBy == null ? "" : element2.parmPassBy;
                parm.setAttribute("passBy", passby);
                String req =
                        element2.parmRequired == null ? ""
                                : element2.parmRequired;
                parm.setAttribute("required", req);
                String def =
                        element2.parmDefaultValue == null ? ""
                                : element2.parmDefaultValue;
                parm.setAttribute("defaultValue", def);
                String label =
                        element2.parmLabel == null ? "" : element2.parmLabel;
                parm.setAttribute("label", label);

                if (element2.parmDomainValues.length > 0) {
                    Element parmDomain =
                            document.createElementNS(METHOD_MAP.uri,
                                                     "fmm:ValidParmValues");
                    for (String element3 : element2.parmDomainValues) {
                        Element parmDomainVal =
                                document.createElementNS(METHOD_MAP.uri,
                                                         "fmm:ValidParm");
                        String value = element3 == null ? "" : element3;
                        parmDomainVal.setAttribute("value", value);
                        parmDomain.appendChild(parmDomainVal);
                    }
                    parm.appendChild(parmDomain);
                }
                method.appendChild(parm);
            }
            // Append Method Return Type element
            String[] mimeTypes = element.methodProperties.returnMIMETypes;
            StringBuffer sb = new StringBuffer();
            for (String element2 : mimeTypes) {
                sb.append(element2.toString() + " ");
            }
            Element methodReturn =
                    document.createElementNS(METHOD_MAP.uri,
                                             "fmm:MethodReturnType");
            methodReturn.setAttribute("wsdlMsgName", "dissemResponse");
            methodReturn.setAttribute("wsdlMsgTOMIME", sb.toString().trim());
            method.appendChild(methodReturn);
        }
    }

    public Element getRootElement() {
        return document.getDocumentElement();
    }

    public Document getDocument() {
        return document;
    }

    public void printMethodMap() {
        try {
            String str =
                    new XMLWriter(new DOMResult(document)).getXMLAsString();
            System.out.println(str);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
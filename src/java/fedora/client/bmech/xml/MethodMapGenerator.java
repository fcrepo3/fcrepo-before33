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
import fedora.client.bmech.data.BMechTemplate;
import fedora.client.bmech.data.BObjTemplate;
import fedora.client.bmech.data.Method;
import fedora.client.bmech.data.MethodParm;

import fedora.common.Constants;

/**
 * @author Sandy Payette
 */
public class MethodMapGenerator
        implements Constants {

    private Document document;

    public MethodMapGenerator(BMechTemplate newBMech)
            throws BMechBuilderException {
        createDOM();
        genMethodMap(newBMech);
    }

    public MethodMapGenerator(BObjTemplate newBDef)
            throws BMechBuilderException {
        createDOM();
        genMethodMap(newBDef);
    }

    private void createDOM() throws BMechBuilderException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder builder = factory.newDocumentBuilder();
            document = builder.newDocument();

        } catch (ParserConfigurationException pce) {
            // Parser with specified options can't be built
            pce.printStackTrace();
            throw new BMechBuilderException("MethodMapGenerator: error configuring parser."
                    + "Underlying exception: " + pce.getMessage());
        }
    }

    private void genMethodMap(BObjTemplate newBDef) {
        Method[] methods = newBDef.getMethods();
        Element root =
                document.createElementNS(METHOD_MAP.uri, "fmm:MethodMap");
        root.setAttributeNS(XMLNS.uri, "xmlns:fmm", METHOD_MAP.uri);
        String bdeflabel =
                newBDef.getbObjLabel() == null ? "fix me" : newBDef
                        .getbObjLabel();
        root.setAttribute("name", ("MethodMap - " + bdeflabel));
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

    private void genMethodMap(BMechTemplate newBMech) {
        Method[] methods = newBMech.getMethods();
        Element root =
                document.createElementNS(METHOD_MAP.uri, "fmm:MethodMap");
        String bmlabel =
                newBMech.getbObjLabel() == null ? "fix me" : newBMech
                        .getbObjLabel();
        root.setAttribute("name", ("MethodMap - " + bmlabel));
        root.setAttribute("bDefPID", newBMech.getbDefContractPID());
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
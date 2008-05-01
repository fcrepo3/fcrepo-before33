/* The contents of this file are subject to the license and copyright terms
 * detailed in the license directory at the root of the source tree (also 
 * available online at http://www.fedora.info/license/).
 */

package fedora.client.deployment.xml;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.dom.DOMResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import fedora.client.deployment.DeploymentBuilderException;
import fedora.client.deployment.data.ServiceDeploymentTemplate;
import fedora.client.deployment.data.Method;
import fedora.client.deployment.data.MethodParm;

import fedora.common.Constants;

/**
 * @author Sandy Payette
 */
public class WSDLGenerator
        implements Constants {

    //FIXME: Can this value be safely changed (bmech -> sDep??)
    private static final String THIS = "bmech";

    private Document document;

    private Element root;

    private Element types;

    private Vector<Element> messageElements;

    private Element portType;

    private Element service;

    private Element binding;

    public WSDLGenerator(ServiceDeploymentTemplate newSDep)
            throws DeploymentBuilderException {
        initializeTree();
        genWSDL(newSDep);
        finalizeTree();
    }

    private void initializeTree() throws DeploymentBuilderException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder builder = factory.newDocumentBuilder();
            document = builder.newDocument();

        } catch (ParserConfigurationException pce) {
            // Parser with specified options can't be built
            pce.printStackTrace();
            throw new DeploymentBuilderException("WSDLGenerator: error configuring parser."
                    + "Underlying exception: " + pce.getMessage());
        }
        root = document.createElementNS(WSDL.uri, "wsdl:definitions");
        types = document.createElementNS(WSDL.uri, "wsdl:types");
        messageElements = new Vector<Element>();
        portType = document.createElementNS(WSDL.uri, "wsdl:portType");
        service = document.createElementNS(WSDL.uri, "wsdl:service");
        binding = document.createElementNS(WSDL.uri, "wsdl:binding");
    }

    private void finalizeTree() {
        // put the tree together
        document.appendChild(root);
        root.appendChild(types);
        Element[] messages = messageElements.toArray(new Element[0]);
        for (Element element : messages) {
            root.appendChild(element);
        }
        root.appendChild(portType);
        root.appendChild(service);
        root.appendChild(binding);
    }

    private void genWSDL(ServiceDeploymentTemplate newSDep) {
        // The data we gathered via the GUI
        boolean hasBaseURL = newSDep.getHasBaseURL();
        String baseURL = newSDep.getServiceBaseURL();
        String sDefPID = newSDep.getSDefContractPID();
        String sDepLabel = newSDep.getbObjLabel();
        // FIXIT!! make sure there are no spaces in middle of sDepName
        String sDepName = newSDep.getbObjName();
        Method[] methods = newSDep.getMethods();

        String name = sDepLabel == null ? "" : sDepLabel;
        root.setAttribute("name", name);
        root.setAttribute("targetNamespace", THIS);
        root.setAttributeNS(XMLNS.uri, "xmlns:this", THIS);
        root.setAttributeNS(XMLNS.uri, "xmlns:wsdl", WSDL.uri);
        root.setAttributeNS(XMLNS.uri, "xmlns:soap", SOAP.uri);
        root.setAttributeNS(XMLNS.uri, "xmlns:soapenc", SOAP_ENC.uri);
        root.setAttributeNS(XMLNS.uri, "xmlns:http", WSDL_HTTP.uri);
        root.setAttributeNS(XMLNS.uri, "xmlns:mime", WSDL_MIME.uri);
        root.setAttributeNS(XMLNS.uri, "xmlns:xsd", XML_XSD.uri);
        createService(sDepName, hasBaseURL, baseURL);
        processMethods(sDepName, hasBaseURL, methods);
    }

    private void createService(String sDepName,
                               boolean hasBaseURL,
                               String baseURL) {
        // create wsdl:service
        service.setAttribute("name", sDepName);
        Element port = document.createElementNS(WSDL.uri, "wsdl:port");
        port.setAttribute("name", (sDepName + "_port"));
        // FIXIT! We are just assuming an HTTP port for now!!
        port.setAttribute("binding", ("this:" + sDepName + "_http"));
        Element httpAddr =
                document.createElementNS(WSDL_HTTP.uri, "http:address");
        if (hasBaseURL) {
            httpAddr.setAttribute("location", baseURL);
        } else {
            // FIXIT!! This is a Fedora-specific thing that we want to do differently
            // like maybe have a Fedora extension element for WSDL.
            httpAddr.setAttribute("location", "LOCAL");
        }
        port.appendChild(httpAddr);
        service.appendChild(port);

        // create wsdl:binding
        // FIXIT!! assumes only an HTTP binding at this time!!
        binding.setAttribute("name", (sDepName + "_http"));
        binding.setAttribute("type", ("this:" + sDepName + "PortType"));
        Element httpBinding =
                document.createElementNS(WSDL_HTTP.uri, "http:binding");
        httpBinding.setAttribute("verb", "GET");
        binding.appendChild(httpBinding);
    }

    private void processMethods(String sDepName,
                                boolean hasBaseURL,
                                Method[] methods) {
        Element schema = document.createElementNS(XML_XSD.uri, "xsd:schema");
        schema.setAttribute("targetNamespace", THIS);
        HashMap<String, MethodParm> parmUnion =
                new HashMap<String, MethodParm>();

        for (Method element : methods) {
            // create wsdl:message
            Element message =
                    document.createElementNS(WSDL.uri, "wsdl:message");
            message.setAttribute("name", (element.methodName + "Request"));

            MethodParm[] parms = element.methodProperties.methodParms;
            for (MethodParm element2 : parms) {
                // Create wsdl:part
                Element part = document.createElementNS(WSDL.uri, "wsdl:part");
                part.setAttribute("name", element2.parmName);
                part.setAttribute("type",
                                  ("this:" + element2.parmName + "Type"));
                message.appendChild(part);

                // Hold on to parm info for creation of wsdl:types
                // If the parm with this name already exists in another method
                // assume it means the same thing, and update the domain of possible
                // values for this parm with anything new showing up in the current
                // method definition.
                if (parmUnion.containsKey(element2.parmName)) {
                    MethodParm existingParm = parmUnion.get(element2.parmName);
                    String[] existingDomain = existingParm.parmDomainValues;
                    HashSet<String> oldVals = new HashSet<String>();
                    for (String element3 : existingDomain) {
                        oldVals.add(element3);
                    }
                    String[] newDomain = element2.parmDomainValues;
                    HashSet<String> newVals = new HashSet<String>();
                    for (String element3 : newDomain) {
                        newVals.add(element3);
                    }
                    Set<String> unionVals = new HashSet<String>(oldVals);
                    unionVals.addAll(newVals);
                    existingParm.parmDomainValues =
                            unionVals.toArray(new String[0]);
                    parmUnion.put(element2.parmName, existingParm);
                } else {
                    parmUnion.put(element2.parmName, element2);
                }
            }
            // add wsdl:message to the message vector
            messageElements.add(message);

            // create wsdl:portType with one or more wsdl:operation elements
            portType.setAttribute("name", (sDepName + "PortType"));
            Element operation =
                    document.createElementNS(WSDL.uri, "wsdl:operation");
            operation.setAttribute("name", element.methodName);
            Element input = document.createElementNS(WSDL.uri, "wsdl:input");
            input.setAttribute("message",
                               ("this:" + element.methodName + "Request"));
            Element output = document.createElementNS(WSDL.uri, "wsdl:output");
            output.setAttribute("message", "this:dissemResponse");
            operation.appendChild(input);
            operation.appendChild(output);
            portType.appendChild(operation);

            Element wsdlOperation =
                    document.createElementNS(WSDL.uri, "wsdl:operation");
            wsdlOperation.setAttribute("name", element.methodName);
            Element httpOperation =
                    document.createElementNS(WSDL_HTTP.uri, "http:operation");
            if (hasBaseURL) {
                httpOperation
                        .setAttribute("location",
                                      element.methodProperties.methodRelativeURL);
            } else {
                httpOperation
                        .setAttribute("location",
                                      element.methodProperties.methodFullURL);
            }
            Element wsdlInput =
                    document.createElementNS(WSDL.uri, "wsdl:input");
            wsdlInput.appendChild(document
                    .createElementNS(WSDL_HTTP.uri, "http:urlReplacement"));
            Element wsdlOutput =
                    document.createElementNS(WSDL.uri, "wsdl:output");
            String[] MIMETypes = element.methodProperties.returnMIMETypes;
            for (String element2 : MIMETypes) {
                Element MIMEType =
                        document.createElementNS(WSDL_MIME.uri, "mime:content");
                MIMEType.setAttribute("type", element2);
                wsdlOutput.appendChild(MIMEType);
            }
            wsdlOperation.appendChild(httpOperation);
            wsdlOperation.appendChild(wsdlInput);
            wsdlOperation.appendChild(wsdlOutput);
            binding.appendChild(wsdlOperation);
        }
        // end methods loop

        // add wsdl:message for Fedora dissemination response to wsdl:definitions
        Element responseMessage =
                document.createElementNS(WSDL.uri, "wsdl:message");
        responseMessage.setAttribute("name", "dissemResponse");
        Element responseMessagePart =
                document.createElementNS(WSDL.uri, "wsdl:part");
        responseMessagePart.setAttribute("name", "dissem");
        responseMessagePart.setAttribute("type", "xsd:base64Binary");
        responseMessage.appendChild(responseMessagePart);
        messageElements.add(responseMessage);

        // create wsdl:types
        Iterator iparm = parmUnion.values().iterator();
        while (iparm.hasNext()) {
            MethodParm parm = (MethodParm) iparm.next();
            // XSD Schema Type element
            Element typeDef =
                    document.createElementNS(XML_XSD.uri, "xsd:simpleType");
            typeDef.setAttribute("name", (parm.parmName + "Type"));

            // XSD Schema Type Restriction element
            Element restrict =
                    document.createElementNS(XML_XSD.uri, "xsd:restriction");
            restrict.setAttribute("base", "xsd:string");
            String[] domainValues = parm.parmDomainValues;
            for (String element : domainValues) {
                Element val =
                        document
                                .createElementNS(XML_XSD.uri, "xsd:enumeration");
                val.setAttribute("value", element);
                restrict.appendChild(val);
            }
            typeDef.appendChild(restrict);
            schema.appendChild(typeDef);
        }
        types.appendChild(schema);
    }

    public Element getRootElement() {
        return document.getDocumentElement();
    }

    public void printWSDL() {
        try {
            String str =
                    new XMLWriter(new DOMResult(document)).getXMLAsString();
            System.out.println(str);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
// NMI's Java Code Viewer 5.1 © 1997-2001 B. Lemaire
// http://njcv.htmlplanet.com - info@njcv.htmlplanet.com

// Copy registered to Evaluation Copy                                   

// Source File Name:   SOAPExtensionHandler.java

package com.sun.xml.rpc.wsdl.parser;

import com.sun.xml.rpc.util.xml.XmlUtil;
import com.sun.xml.rpc.wsdl.document.soap.*;
import com.sun.xml.rpc.wsdl.framework.*;
import java.io.IOException;
import java.util.Iterator;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

// Referenced classes of package com.sun.xml.rpc.wsdl.parser:
//            ExtensionHandlerBase, Util

public class SOAPExtensionHandler extends ExtensionHandlerBase {

    public SOAPExtensionHandler() {
    }

    public String getNamespaceURI() {
        return "http://schemas.xmlsoap.org/wsdl/soap/";
    }

    protected boolean handleDefinitionsExtension(ParserContext context, Extensible parent, Element e) {
        Util.fail("parsing.invalidExtensionElement", e.getTagName(), e.getNamespaceURI());
        return false;
    }

    protected boolean handleTypesExtension(ParserContext context, Extensible parent, Element e) {
        Util.fail("parsing.invalidExtensionElement", e.getTagName(), e.getNamespaceURI());
        return false;
    }

    protected boolean handleBindingExtension(ParserContext context, Extensible parent, Element e) {
        if(XmlUtil.matchesTagNS(e, SOAPConstants.QNAME_BINDING)) {
            context.push();
            context.registerNamespaces(e);
            SOAPBinding binding = new SOAPBinding();
            String transport = Util.getRequiredAttribute(e, "transport");
            binding.setTransport(transport);
            String style = XmlUtil.getAttributeOrNull(e, "style");
            if(style != null)
                if(style.equals("rpc"))
                    binding.setStyle(SOAPStyle.RPC);
                else
                if(style.equals("document"))
                    binding.setStyle(SOAPStyle.DOCUMENT);
                else
                    Util.fail("parsing.invalidAttributeValue", "style", style);
            parent.addExtension(binding);
            context.pop();
            context.fireDoneParsingEntity(SOAPConstants.QNAME_BINDING, binding);
            return true;
        } else {
            Util.fail("parsing.invalidExtensionElement", e.getTagName(), e.getNamespaceURI());
            return false;
        }
    }

    protected boolean handleOperationExtension(ParserContext context, Extensible parent, Element e) {
        if(XmlUtil.matchesTagNS(e, SOAPConstants.QNAME_OPERATION)) {
            context.push();
            context.registerNamespaces(e);
            SOAPOperation operation = new SOAPOperation();
            String soapAction = XmlUtil.getAttributeOrNull(e, "soapAction");
            if(soapAction != null)
                operation.setSOAPAction(soapAction);
            String style = XmlUtil.getAttributeOrNull(e, "style");
            if(style != null)
                if(style.equals("rpc"))
                    operation.setStyle(SOAPStyle.RPC);
                else
                if(style.equals("document"))
                    operation.setStyle(SOAPStyle.DOCUMENT);
                else
                    Util.fail("parsing.invalidAttributeValue", "style", style);
            parent.addExtension(operation);
            context.pop();
            context.fireDoneParsingEntity(SOAPConstants.QNAME_OPERATION, operation);
            return true;
        } else {
            Util.fail("parsing.invalidExtensionElement", e.getTagName(), e.getNamespaceURI());
            return false;
        }
    }

    protected boolean handleInputExtension(ParserContext context, Extensible parent, Element e) {
        return handleInputOutputExtension(context, parent, e);
    }

    protected boolean handleOutputExtension(ParserContext context, Extensible parent, Element e) {
        return handleInputOutputExtension(context, parent, e);
    }

    protected boolean handleMIMEPartExtension(ParserContext context, Extensible parent, Element e) {
        return handleInputOutputExtension(context, parent, e);
    }

    protected boolean handleInputOutputExtension(ParserContext context, Extensible parent, Element e) {
        if(XmlUtil.matchesTagNS(e, SOAPConstants.QNAME_BODY)) {
            context.push();
            context.registerNamespaces(e);
            SOAPBody body = new SOAPBody();
            String use = XmlUtil.getAttributeOrNull(e, "use");
            if(use != null)
                if(use.equals("literal"))
                    body.setUse(SOAPUse.LITERAL);
                else
                if(use.equals("encoded"))
                    body.setUse(SOAPUse.ENCODED);
                else
                    Util.fail("parsing.invalidAttributeValue", "use", use);
            String namespace = XmlUtil.getAttributeOrNull(e, "namespace");
            if(namespace != null)
                body.setNamespace(namespace);
            String encodingStyle = XmlUtil.getAttributeOrNull(e, "encodingStyle");
            if(encodingStyle != null)
                body.setEncodingStyle(encodingStyle);
            String parts = XmlUtil.getAttributeOrNull(e, "parts");
            if(parts != null)
                body.setNamespace(parts);
            parent.addExtension(body);
            context.pop();
            context.fireDoneParsingEntity(SOAPConstants.QNAME_BODY, body);
            return true;
        }
        if(XmlUtil.matchesTagNS(e, SOAPConstants.QNAME_HEADER)) {
            context.push();
            context.registerNamespaces(e);
            SOAPHeader header = new SOAPHeader();
            String use = XmlUtil.getAttributeOrNull(e, "use");
            if(use != null)
                if(use.equals("literal"))
                    header.setUse(SOAPUse.LITERAL);
                else
                if(use.equals("encoded"))
                    header.setUse(SOAPUse.ENCODED);
                else
                    Util.fail("parsing.invalidAttributeValue", "use", use);
            String namespace = XmlUtil.getAttributeOrNull(e, "namespace");
            if(namespace != null)
                header.setNamespace(namespace);
            String encodingStyle = XmlUtil.getAttributeOrNull(e, "encodingStyle");
            if(encodingStyle != null)
                header.setEncodingStyle(encodingStyle);
            String part = XmlUtil.getAttributeOrNull(e, "part");
            if(part != null)
                header.setPart(part);
            String messageAttr = XmlUtil.getAttributeOrNull(e, "message");
            if(messageAttr != null)
                header.setMessage(context.translateQualifiedName(messageAttr));
            for(Iterator iter = XmlUtil.getAllChildren(e); iter.hasNext();) {
                Element e2 = Util.nextElement(iter);
                if(e2 == null)
                    break;
                if(XmlUtil.matchesTagNS(e2, SOAPConstants.QNAME_HEADERFAULT)) {
                    context.push();
                    context.registerNamespaces(e);
                    SOAPHeaderFault headerfault = new SOAPHeaderFault();
                    String use2 = XmlUtil.getAttributeOrNull(e, "use");
                    if(use2 != null)
                        if(use2.equals("literal"))
                            headerfault.setUse(SOAPUse.LITERAL);
                        else
                        if(use.equals("encoded"))
                            headerfault.setUse(SOAPUse.ENCODED);
                        else
                            Util.fail("parsing.invalidAttributeValue", "use", use2);
                    String namespace2 = XmlUtil.getAttributeOrNull(e, "namespace");
                    if(namespace2 != null)
                        headerfault.setNamespace(namespace2);
                    String encodingStyle2 = XmlUtil.getAttributeOrNull(e, "encodingStyle");
                    if(encodingStyle2 != null)
                        headerfault.setEncodingStyle(encodingStyle2);
                    String part2 = XmlUtil.getAttributeOrNull(e, "part");
                    if(part2 != null)
                        headerfault.setPart(part2);
                    String messageAttr2 = XmlUtil.getAttributeOrNull(e, "message");
                    if(messageAttr2 != null)
                        headerfault.setMessage(context.translateQualifiedName(messageAttr2));
                    header.add(headerfault);
                    context.pop();
                } else {
                    Util.fail("parsing.invalidElement", e2.getTagName(), e2.getNamespaceURI());
                }
            }

            parent.addExtension(header);
            context.pop();
            context.fireDoneParsingEntity(SOAPConstants.QNAME_HEADER, header);
            return true;
        } else {
            Util.fail("parsing.invalidExtensionElement", e.getTagName(), e.getNamespaceURI());
            return false;
        }
    }

    protected boolean handleFaultExtension(ParserContext context, Extensible parent, Element e) {
        if(XmlUtil.matchesTagNS(e, SOAPConstants.QNAME_FAULT)) {
            context.push();
            context.registerNamespaces(e);
            SOAPFault fault = new SOAPFault();
            String use = XmlUtil.getAttributeOrNull(e, "use");
            if(use != null)
                if(use.equals("literal"))
                    fault.setUse(SOAPUse.LITERAL);
                else
                if(use.equals("encoded"))
                    fault.setUse(SOAPUse.ENCODED);
                else
                    Util.fail("parsing.invalidAttributeValue", "use", use);
            String namespace = XmlUtil.getAttributeOrNull(e, "namespace");
            if(namespace != null)
                fault.setNamespace(namespace);
            String encodingStyle = XmlUtil.getAttributeOrNull(e, "encodingStyle");
            if(encodingStyle != null)
                fault.setEncodingStyle(encodingStyle);
            parent.addExtension(fault);
            context.pop();
            context.fireDoneParsingEntity(SOAPConstants.QNAME_FAULT, fault);
            return true;
        } else {
            Util.fail("parsing.invalidExtensionElement", e.getTagName(), e.getNamespaceURI());
            return false;
        }
    }

    protected boolean handleServiceExtension(ParserContext context, Extensible parent, Element e) {
        Util.fail("parsing.invalidExtensionElement", e.getTagName(), e.getNamespaceURI());
        return false;
    }

    protected boolean handlePortExtension(ParserContext context, Extensible parent, Element e) {
        if(XmlUtil.matchesTagNS(e, SOAPConstants.QNAME_ADDRESS)) {
            context.push();
            context.registerNamespaces(e);
            SOAPAddress address = new SOAPAddress();
            String location = Util.getRequiredAttribute(e, "location");
            address.setLocation(location);
            parent.addExtension(address);
            context.pop();
            context.fireDoneParsingEntity(SOAPConstants.QNAME_ADDRESS, address);
            return true;
        } else {
            Util.fail("parsing.invalidExtensionElement", e.getTagName(), e.getNamespaceURI());
            return false;
        }
    }

    public void doHandleExtension(WriterContext context, Extension extension) throws IOException {
        if(extension instanceof SOAPAddress) {
            SOAPAddress address = (SOAPAddress)extension;
            context.writeStartTag(address.getElementName());
            context.writeAttribute("location", address.getLocation());
            context.writeEndTag(address.getElementName());
        } else
        if(extension instanceof SOAPBinding) {
            SOAPBinding binding = (SOAPBinding)extension;
            context.writeStartTag(binding.getElementName());
            context.writeAttribute("transport", binding.getTransport());
            String style = binding.getStyle() != null ? binding.getStyle() != SOAPStyle.DOCUMENT ? "rpc" : "document" : null;
            context.writeAttribute("style", style);
            context.writeEndTag(binding.getElementName());
        } else
        if(extension instanceof SOAPBody) {
            SOAPBody body = (SOAPBody)extension;
            context.writeStartTag(body.getElementName());
            context.writeAttribute("encodingStyle", body.getEncodingStyle());
            context.writeAttribute("parts", body.getParts());
            String use = body.getUse() != null ? body.getUse() != SOAPUse.LITERAL ? "encoded" : "literal" : null;
            context.writeAttribute("use", use);
            context.writeAttribute("namespace", body.getNamespace());
            context.writeEndTag(body.getElementName());
        } else
        if(extension instanceof SOAPFault) {
            SOAPFault fault = (SOAPFault)extension;
            context.writeStartTag(fault.getElementName());
            context.writeAttribute("encodingStyle", fault.getEncodingStyle());
            String use = fault.getUse() != null ? fault.getUse() != SOAPUse.LITERAL ? "encoded" : "literal" : null;
            context.writeAttribute("use", use);
            context.writeAttribute("namespace", fault.getNamespace());
            context.writeEndTag(fault.getElementName());
        } else
        if(extension instanceof SOAPHeader) {
            SOAPHeader header = (SOAPHeader)extension;
            context.writeStartTag(header.getElementName());
            context.writeAttribute("message", header.getMessage());
            context.writeAttribute("part", header.getPart());
            context.writeAttribute("encodingStyle", header.getEncodingStyle());
            String use = header.getUse() != null ? header.getUse() != SOAPUse.LITERAL ? "encoded" : "literal" : null;
            context.writeAttribute("use", use);
            context.writeAttribute("namespace", header.getNamespace());
            context.writeEndTag(header.getElementName());
        } else
        if(extension instanceof SOAPHeaderFault) {
            SOAPHeaderFault headerfault = (SOAPHeaderFault)extension;
            context.writeStartTag(headerfault.getElementName());
            context.writeAttribute("message", headerfault.getMessage());
            context.writeAttribute("part", headerfault.getPart());
            context.writeAttribute("encodingStyle", headerfault.getEncodingStyle());
            String use = headerfault.getUse() != null ? headerfault.getUse() != SOAPUse.LITERAL ? "encoded" : "literal" : null;
            context.writeAttribute("use", use);
            context.writeAttribute("namespace", headerfault.getNamespace());
            context.writeEndTag(headerfault.getElementName());
        } else
        if(extension instanceof SOAPOperation) {
            SOAPOperation operation = (SOAPOperation)extension;
            context.writeStartTag(operation.getElementName());
            context.writeAttribute("soapAction", operation.getSOAPAction());
            String style = operation.getStyle() != null ? operation.isDocument() ? "document" : "rpc" : null;
            context.writeAttribute("style", style);
            context.writeEndTag(operation.getElementName());
        } else {
            throw new IllegalArgumentException();
        }
    }
}

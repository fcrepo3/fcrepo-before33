// NMI's Java Code Viewer 5.1 © 1997-2001 B. Lemaire
// http://njcv.htmlplanet.com - info@njcv.htmlplanet.com

// Copy registered to Evaluation Copy                                   

// Source File Name:   HTTPExtensionHandler.java

package com.sun.xml.rpc.wsdl.parser;

import com.sun.xml.rpc.util.xml.XmlUtil;
import com.sun.xml.rpc.wsdl.document.http.*;
import com.sun.xml.rpc.wsdl.framework.*;
import java.io.IOException;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

// Referenced classes of package com.sun.xml.rpc.wsdl.parser:
//            ExtensionHandlerBase, Util

public class HTTPExtensionHandler extends ExtensionHandlerBase {

    public HTTPExtensionHandler() {
    }

    public String getNamespaceURI() {
        return "http://schemas.xmlsoap.org/wsdl/http/";
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
        if(XmlUtil.matchesTagNS(e, HTTPConstants.QNAME_BINDING)) {
            context.push();
            context.registerNamespaces(e);
            HTTPBinding binding = new HTTPBinding();
            String verb = Util.getRequiredAttribute(e, "verb");
            binding.setVerb(verb);
            parent.addExtension(binding);
            context.pop();
            context.fireDoneParsingEntity(HTTPConstants.QNAME_BINDING, binding);
            return true;
        } else {
            Util.fail("parsing.invalidExtensionElement", e.getTagName(), e.getNamespaceURI());
            return false;
        }
    }

    protected boolean handleOperationExtension(ParserContext context, Extensible parent, Element e) {
        if(XmlUtil.matchesTagNS(e, HTTPConstants.QNAME_OPERATION)) {
            context.push();
            context.registerNamespaces(e);
            HTTPOperation operation = new HTTPOperation();
            String location = Util.getRequiredAttribute(e, "location");
            operation.setLocation(location);
            parent.addExtension(operation);
            context.pop();
            context.fireDoneParsingEntity(HTTPConstants.QNAME_OPERATION, operation);
            return true;
        } else {
            Util.fail("parsing.invalidExtensionElement", e.getTagName(), e.getNamespaceURI());
            return false;
        }
    }

    protected boolean handleInputExtension(ParserContext context, Extensible parent, Element e) {
        if(XmlUtil.matchesTagNS(e, HTTPConstants.QNAME_URL_ENCODED)) {
            parent.addExtension(new HTTPUrlEncoded());
            return true;
        }
        if(XmlUtil.matchesTagNS(e, HTTPConstants.QNAME_URL_REPLACEMENT)) {
            parent.addExtension(new HTTPUrlReplacement());
            return true;
        } else {
            Util.fail("parsing.invalidExtensionElement", e.getTagName(), e.getNamespaceURI());
            return false;
        }
    }

    protected boolean handleOutputExtension(ParserContext context, Extensible parent, Element e) {
        Util.fail("parsing.invalidExtensionElement", e.getTagName(), e.getNamespaceURI());
        return false;
    }

    protected boolean handleFaultExtension(ParserContext context, Extensible parent, Element e) {
        Util.fail("parsing.invalidExtensionElement", e.getTagName(), e.getNamespaceURI());
        return false;
    }

    protected boolean handleServiceExtension(ParserContext context, Extensible parent, Element e) {
        Util.fail("parsing.invalidExtensionElement", e.getTagName(), e.getNamespaceURI());
        return false;
    }

    protected boolean handlePortExtension(ParserContext context, Extensible parent, Element e) {
        if(XmlUtil.matchesTagNS(e, HTTPConstants.QNAME_ADDRESS)) {
            context.push();
            context.registerNamespaces(e);
            HTTPAddress address = new HTTPAddress();
            String location = Util.getRequiredAttribute(e, "location");
            address.setLocation(location);
            parent.addExtension(address);
            context.pop();
            context.fireDoneParsingEntity(HTTPConstants.QNAME_ADDRESS, address);
            return true;
        } else {
            Util.fail("parsing.invalidExtensionElement", e.getTagName(), e.getNamespaceURI());
            return false;
        }
    }

    protected boolean handleMIMEPartExtension(ParserContext context, Extensible parent, Element e) {
        Util.fail("parsing.invalidExtensionElement", e.getTagName(), e.getNamespaceURI());
        return false;
    }

    public void doHandleExtension(WriterContext context, Extension extension) throws IOException {
        if(extension instanceof HTTPAddress) {
            HTTPAddress address = (HTTPAddress)extension;
            context.writeStartTag(address.getElementName());
            context.writeAttribute("location", address.getLocation());
            context.writeEndTag(address.getElementName());
        } else
        if(extension instanceof HTTPBinding) {
            HTTPBinding binding = (HTTPBinding)extension;
            context.writeStartTag(binding.getElementName());
            context.writeAttribute("verb", binding.getVerb());
            context.writeEndTag(binding.getElementName());
        } else
        if(extension instanceof HTTPOperation) {
            HTTPOperation operation = (HTTPOperation)extension;
            context.writeStartTag(operation.getElementName());
            context.writeAttribute("location", operation.getLocation());
            context.writeEndTag(operation.getElementName());
        } else
        if(extension instanceof HTTPUrlEncoded) {
            context.writeStartTag(extension.getElementName());
            context.writeEndTag(extension.getElementName());
        } else
        if(extension instanceof HTTPUrlReplacement) {
            context.writeStartTag(extension.getElementName());
            context.writeEndTag(extension.getElementName());
        } else {
            throw new IllegalArgumentException();
        }
    }
}

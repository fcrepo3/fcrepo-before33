// NMI's Java Code Viewer 5.1 © 1997-2001 B. Lemaire
// http://njcv.htmlplanet.com - info@njcv.htmlplanet.com

// Copy registered to Evaluation Copy                                   

// Source File Name:   MIMEExtensionHandler.java

package com.sun.xml.rpc.wsdl.parser;

import com.sun.xml.rpc.util.xml.XmlUtil;
import com.sun.xml.rpc.wsdl.document.WSDLConstants;
import com.sun.xml.rpc.wsdl.document.mime.*;
import com.sun.xml.rpc.wsdl.framework.*;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import javax.xml.rpc.namespace.QName;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

// Referenced classes of package com.sun.xml.rpc.wsdl.parser:
//            ExtensionHandler, Util

public class MIMEExtensionHandler extends ExtensionHandler {

    public MIMEExtensionHandler() {
    }

    public String getNamespaceURI() {
        return "http://schemas.xmlsoap.org/wsdl/mime/";
    }

    public boolean doHandleExtension(ParserContext context, Extensible parent, Element e) {
        if(parent.getElementName().equals(WSDLConstants.QNAME_OUTPUT))
            return handleInputOutputExtension(context, parent, e);
        if(parent.getElementName().equals(WSDLConstants.QNAME_INPUT))
            return handleInputOutputExtension(context, parent, e);
        if(parent.getElementName().equals(MIMEConstants.QNAME_PART)) {
            return handleMIMEPartExtension(context, parent, e);
        } else {
            context.fireIgnoringExtension(new QName(e.getNamespaceURI(), e.getLocalName()), parent.getElementName());
            return false;
        }
    }

    protected boolean handleInputOutputExtension(ParserContext context, Extensible parent, Element e) {
        if(XmlUtil.matchesTagNS(e, MIMEConstants.QNAME_MULTIPART_RELATED)) {
            context.push();
            context.registerNamespaces(e);
            MIMEMultipartRelated mpr = new MIMEMultipartRelated();
            for(Iterator iter = XmlUtil.getAllChildren(e); iter.hasNext();) {
                Element e2 = Util.nextElement(iter);
                if(e2 == null)
                    break;
                if(XmlUtil.matchesTagNS(e2, MIMEConstants.QNAME_PART)) {
                    context.push();
                    context.registerNamespaces(e2);
                    MIMEPart part = new MIMEPart();
                    String name = XmlUtil.getAttributeOrNull(e2, "name");
                    if(name != null)
                        part.setName(name);
                    for(Iterator iter2 = XmlUtil.getAllChildren(e2); iter2.hasNext();) {
                        Element e3 = Util.nextElement(iter2);
                        if(e3 == null)
                            break;
                        ExtensionHandler h = (ExtensionHandler)super._extensionHandlers.get(e3.getNamespaceURI());
                        boolean handled = false;
                        if(h != null)
                            handled = h.doHandleExtension(context, part, e3);
                        if(!handled) {
                            String required = XmlUtil.getAttributeNSOrNull(e3, "required", "http://schemas.xmlsoap.org/wsdl/");
                            if(required != null && required.equals("true"))
                                Util.fail("parsing.requiredExtensibilityElement", e3.getTagName(), e3.getNamespaceURI());
                            else
                                context.fireIgnoringExtension(new QName(e3.getNamespaceURI(), e3.getLocalName()), part.getElementName());
                        }
                    }

                    mpr.add(part);
                    context.pop();
                    context.fireDoneParsingEntity(MIMEConstants.QNAME_PART, part);
                } else {
                    Util.fail("parsing.invalidElement", e2.getTagName(), e2.getNamespaceURI());
                }
            }

            parent.addExtension(mpr);
            context.pop();
            context.fireDoneParsingEntity(MIMEConstants.QNAME_MULTIPART_RELATED, mpr);
            return true;
        }
        if(XmlUtil.matchesTagNS(e, MIMEConstants.QNAME_CONTENT)) {
            MIMEContent content = parseMIMEContent(context, e);
            parent.addExtension(content);
            return true;
        }
        if(XmlUtil.matchesTagNS(e, MIMEConstants.QNAME_MIME_XML)) {
            MIMEXml mimeXml = parseMIMEXml(context, e);
            parent.addExtension(mimeXml);
            return true;
        } else {
            Util.fail("parsing.invalidExtensionElement", e.getTagName(), e.getNamespaceURI());
            return false;
        }
    }

    protected boolean handleMIMEPartExtension(ParserContext context, Extensible parent, Element e) {
        if(XmlUtil.matchesTagNS(e, MIMEConstants.QNAME_CONTENT)) {
            MIMEContent content = parseMIMEContent(context, e);
            parent.addExtension(content);
            return true;
        }
        if(XmlUtil.matchesTagNS(e, MIMEConstants.QNAME_MIME_XML)) {
            MIMEXml mimeXml = parseMIMEXml(context, e);
            parent.addExtension(mimeXml);
            return true;
        } else {
            Util.fail("parsing.invalidExtensionElement", e.getTagName(), e.getNamespaceURI());
            return false;
        }
    }

    protected MIMEContent parseMIMEContent(ParserContext context, Element e) {
        context.push();
        context.registerNamespaces(e);
        MIMEContent content = new MIMEContent();
        String part = XmlUtil.getAttributeOrNull(e, "part");
        if(part != null)
            content.setPart(part);
        String type = XmlUtil.getAttributeOrNull(e, "type");
        if(type != null)
            content.setType(type);
        context.pop();
        context.fireDoneParsingEntity(MIMEConstants.QNAME_CONTENT, content);
        return content;
    }

    protected MIMEXml parseMIMEXml(ParserContext context, Element e) {
        context.push();
        context.registerNamespaces(e);
        MIMEXml mimeXml = new MIMEXml();
        String part = XmlUtil.getAttributeOrNull(e, "part");
        if(part != null)
            mimeXml.setPart(part);
        context.pop();
        context.fireDoneParsingEntity(MIMEConstants.QNAME_MIME_XML, mimeXml);
        return mimeXml;
    }

    public void doHandleExtension(WriterContext context, Extension extension) throws IOException {
        if(extension instanceof MIMEContent) {
            MIMEContent content = (MIMEContent)extension;
            context.writeStartTag(content.getElementName());
            context.writeAttribute("part", content.getPart());
            context.writeAttribute("type", content.getType());
            context.writeEndTag(content.getElementName());
        } else
        if(extension instanceof MIMEXml) {
            MIMEXml mimeXml = (MIMEXml)extension;
            context.writeStartTag(mimeXml.getElementName());
            context.writeAttribute("part", mimeXml.getPart());
            context.writeEndTag(mimeXml.getElementName());
        } else
        if(extension instanceof MIMEMultipartRelated) {
            MIMEMultipartRelated mpr = (MIMEMultipartRelated)extension;
            context.writeStartTag(mpr.getElementName());
            MIMEPart part;
            for(Iterator iter = mpr.getParts(); iter.hasNext(); context.writeEndTag(part.getElementName())) {
                part = (MIMEPart)iter.next();
                context.writeStartTag(part.getElementName());
                for(Iterator iter2 = part.extensions(); iter2.hasNext();) {
                    Extension e = (Extension)iter2.next();
                    ExtensionHandler h = (ExtensionHandler)super._extensionHandlers.get(e.getElementName().getNamespaceURI());
                    if(h != null)
                        h.doHandleExtension(context, e);
                }

            }

            context.writeEndTag(mpr.getElementName());
        } else {
            throw new IllegalArgumentException();
        }
    }
}

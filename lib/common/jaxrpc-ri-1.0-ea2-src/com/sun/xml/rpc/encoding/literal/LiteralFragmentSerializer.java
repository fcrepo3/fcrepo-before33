// NMI's Java Code Viewer 5.1 © 1997-2001 B. Lemaire
// http://njcv.htmlplanet.com - info@njcv.htmlplanet.com

// Copy registered to Evaluation Copy

// Source File Name:   LiteralFragmentSerializer.java

package com.sun.xml.rpc.encoding.literal;

import com.sun.xml.rpc.encoding.*;
import com.sun.xml.rpc.encoding.xsd.XSDConstants;
import com.sun.xml.rpc.soap.message.NameFactory;
import com.sun.xml.rpc.soap.message.NameFactoryImpl;
import com.sun.xml.rpc.streaming.*;
import java.util.Iterator;
import javax.xml.rpc.namespace.QName;
import javax.xml.soap.*;

// Referenced classes of package com.sun.xml.rpc.encoding.literal:
//            LiteralObjectSerializerBase

public class LiteralFragmentSerializer extends LiteralObjectSerializerBase {

    protected SOAPElementFactory elementFactory;
    protected NameFactory nameFactory;
    private static final String FIRST_PREFIX = "ns";

    public LiteralFragmentSerializer(QName type, boolean isNullable, String encodingStyle) {
        super(type, isNullable, encodingStyle);
        try {
            elementFactory = SOAPElementFactory.newInstance();
            nameFactory = new NameFactoryImpl();
        }
        catch(SOAPException soapexception) { }
    }

    protected void writeAdditionalNamespaceDeclarations(Object obj1, XMLWriter xmlwriter) throws Exception {
    }

    protected void internalSerialize(Object obj, QName name, XMLWriter writer, SOAPSerializationContext context) throws Exception {
        boolean pushedEncodingStyle = false;
        if(obj == null) {
            if(!super.isNullable)
                throw new SerializationException("literal.unexpectedNull");
            writer.startElement(name == null ? super.type : name);
            writer.writeAttribute(XSDConstants.QNAME_XSI_NIL, "1");
            writer.endElement();
        } else {
            SOAPElement element = (SOAPElement)obj;
            Name elementName = element.getElementName();
            writer.startElement(elementName.getLocalName(), elementName.getURI(), elementName.getPrefix());
            for(Iterator iter = element.getNamespacePrefixes(); iter.hasNext();) {
                String prefix = (String)iter.next();
                String uri = element.getNamespaceURI(prefix);
                String existingURI = writer.getURI(prefix);
                if(existingURI == null || !existingURI.equals(uri))
                    writer.writeNamespaceDeclaration(prefix, uri);
            }

            pushedEncodingStyle = context.pushEncodingStyle(super.encodingStyle, writer);
            Name aname;
            String value;
            for(Iterator iter = element.getAllAttributes(); iter.hasNext(); writer.writeAttribute(aname.getLocalName(), aname.getURI(), value)) {
                aname = (Name)iter.next();
                value = element.getAttributeValue(aname);
            }

            for(Iterator iter = element.getChildElements(); iter.hasNext();) {
                Node node = (Node)iter.next();
                if(node instanceof Text) {
                    Text text = (Text)node;
                    if(!text.isComment())
                        writer.writeChars(text.getValue());
                } else
                if(node instanceof SOAPElement)
                    serialize(node, null, null, writer, context);
            }

            writer.endElement();
            if(pushedEncodingStyle)
                context.popEncodingStyle();
        }
    }

    protected Object doDeserialize(XMLReader reader, SOAPDeserializationContext context) throws Exception {
        String elementURI = reader.getURI();
        SOAPElement element;
        if(elementURI == null || elementURI.equals(""))
            element = elementFactory.create(reader.getLocalName());
        else
            element = elementFactory.create(reader.getLocalName(), "ns", reader.getURI());
        String defaultURI = reader.getURI("");
        if(defaultURI != null)
            element.addNamespaceDeclaration("", defaultURI);
        String prefix;
        String uri;
        for(Iterator iter = reader.getPrefixes(); iter.hasNext(); element.addNamespaceDeclaration(prefix, uri)) {
            prefix = (String)iter.next();
            uri = reader.getURI(prefix);
        }

        Attributes attributes = reader.getAttributes();
        for(int i = 0; i < attributes.getLength(); i++)
            if(!attributes.isNamespaceDeclaration(i)) {
                String uri2 = attributes.getURI(i);
                Name name;
                if(uri2 == null) {
                    name = nameFactory.createName(attributes.getLocalName(i));
                } else {
                    String prefix2 = attributes.getPrefix(i);
                    name = nameFactory.createName(attributes.getLocalName(i), prefix2, uri2);
                }
                element.addAttribute(name, attributes.getValue(i));
            }

        reader.next();
        for(; reader.getState() != 2; reader.next()) {
            int state = reader.getState();
            if(state == 1) {
                SOAPElement child = (SOAPElement)deserialize(null, reader, context);
                element.addChildElement(child);
            } else
            if(state == 3)
                element.addTextNode(reader.getValue());
        }

        return element;
    }

    protected void doSerialize(Object obj1, XMLWriter xmlwriter, SOAPSerializationContext soapserializationcontext) throws Exception {
    }

    protected void doSerializeAttributes(Object obj1, XMLWriter xmlwriter, SOAPSerializationContext soapserializationcontext) throws Exception {
    }
}

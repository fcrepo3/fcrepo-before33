// NMI's Java Code Viewer 5.1 © 1997-2001 B. Lemaire
// http://njcv.htmlplanet.com - info@njcv.htmlplanet.com

// Copy registered to Evaluation Copy                                   

// Source File Name:   LiteralObjectSerializerBase.java

package com.sun.xml.rpc.encoding.literal;

import com.sun.xml.rpc.encoding.*;
import com.sun.xml.rpc.encoding.xsd.XSDConstants;
import com.sun.xml.rpc.streaming.*;
import com.sun.xml.rpc.util.exception.JAXRPCExceptionBase;
import com.sun.xml.rpc.util.exception.LocalizableExceptionAdapter;
import javax.activation.DataHandler;
import javax.xml.rpc.namespace.QName;

public abstract class LiteralObjectSerializerBase
    implements SerializerConstants, CombinedSerializer {

    protected QName type;
    protected boolean isNullable;
    protected String encodingStyle;

    protected LiteralObjectSerializerBase(QName type, boolean isNullable, String encodingStyle) {
        if(type == null) {
            throw new IllegalArgumentException();
        } else {
            this.type = type;
            this.isNullable = isNullable;
            this.encodingStyle = encodingStyle;
            return;
        }
    }

    public QName getXmlType() {
        return type;
    }

    public boolean getEncodeType() {
        return false;
    }

    public CombinedSerializer getInnermostSerializer() {
        return this;
    }

    public boolean isNullable() {
        return isNullable;
    }

    public String getEncodingStyle() {
        return encodingStyle;
    }

    public void serialize(Object value, QName name, SerializerCallback callback, XMLWriter writer, SOAPSerializationContext context) {
        try {
            internalSerialize(value, name, writer, context);
        }
        catch(JAXRPCExceptionBase e) {
            throw new SerializationException(e);
        }
        catch(Exception e) {
            throw new SerializationException(new LocalizableExceptionAdapter(e));
        }
    }

    public Object deserialize(QName name, XMLReader reader, SOAPDeserializationContext context) {
        try {
            return internalDeserialize(name, reader, context);
        }
        catch(JAXRPCExceptionBase e) {
            throw new DeserializationException(e);
        }
        catch(Exception e) {
            throw new DeserializationException(new LocalizableExceptionAdapter(e));
        }
    }

    public Object deserialize(DataHandler dataHandler, SOAPDeserializationContext context) throws DeserializationException, UnsupportedOperationException {
        throw new UnsupportedOperationException();
    }

    protected void internalSerialize(Object obj, QName name, XMLWriter writer, SOAPSerializationContext context) throws Exception {
        context.beginSerializing(obj);
        writer.startElement(name == null ? type : name);
        boolean pushedEncodingStyle = context.pushEncodingStyle(encodingStyle, writer);
        if(obj == null) {
            if(!isNullable)
                throw new SerializationException("literal.unexpectedNull");
            writer.writeAttribute(XSDConstants.QNAME_XSI_NIL, "1");
        } else {
            writeAdditionalNamespaceDeclarations(obj, writer);
            doSerializeAttributes(obj, writer, context);
            doSerialize(obj, writer, context);
        }
        writer.endElement();
        if(pushedEncodingStyle)
            context.popEncodingStyle();
        context.doneSerializing(obj);
    }

    protected Object internalDeserialize(QName name, XMLReader reader, SOAPDeserializationContext context) throws Exception {
        boolean pushedEncodingStyle = context.processEncodingStyle(reader);
        try {
            context.verifyEncodingStyle(encodingStyle);
            if(name != null) {
                QName actualName = reader.getName();
                if(!actualName.equals(name))
                    throw new DeserializationException("xsd.unexpectedElementName", new Object[] {
                        name.toString(), actualName.toString()
                    });
            }
            Attributes attrs = reader.getAttributes();
            String typeVal = attrs.getValue("http://www.w3.org/2001/XMLSchema-instance", "type");
            if(typeVal != null) {
                QName actualType = XMLReaderUtil.decodeQName(reader, typeVal);
                if(!actualType.equals(type))
                    throw new DeserializationException("xsd.unexpectedElementType", new Object[] {
                        type.toString(), actualType.toString()
                    });
            }
            String nullVal = attrs.getValue("http://www.w3.org/2001/XMLSchema-instance", "nil");
            boolean isNull = nullVal != null && SerializerBase.decodeBoolean(nullVal);
            Object obj = null;
            if(isNull) {
                if(!isNullable)
                    throw new DeserializationException("xsd.unexpectedNull");
                reader.next();
            } else {
                obj = doDeserialize(reader, context);
            }
            XMLReaderUtil.verifyReaderState(reader, 2);
            Object obj1 = obj;
            return obj1;
        }
        finally {
            if(pushedEncodingStyle)
                context.popEncodingStyle();
        }
    }

    protected void writeAdditionalNamespaceDeclarations(Object obj1, XMLWriter xmlwriter) throws Exception {
    }

    protected abstract void doSerialize(Object obj, XMLWriter xmlwriter, SOAPSerializationContext soapserializationcontext) throws Exception;

    protected abstract void doSerializeAttributes(Object obj, XMLWriter xmlwriter, SOAPSerializationContext soapserializationcontext) throws Exception;

    protected abstract Object doDeserialize(XMLReader xmlreader, SOAPDeserializationContext soapdeserializationcontext) throws Exception;

    public String getMechanismType() {
        return "http://java.sun.com/jax-rpc-ri/1.0/streaming/";
    }
}

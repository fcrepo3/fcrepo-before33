// NMI's Java Code Viewer 5.1 © 1997-2001 B. Lemaire
// http://njcv.htmlplanet.com - info@njcv.htmlplanet.com

// Copy registered to Evaluation Copy                                   

// Source File Name:   DynamicSerializer.java

package com.sun.xml.rpc.encoding;

import com.sun.xml.rpc.encoding.xsd.XSDConstants;
import com.sun.xml.rpc.streaming.*;
import com.sun.xml.rpc.util.exception.JAXRPCExceptionBase;
import com.sun.xml.rpc.util.exception.LocalizableExceptionAdapter;
import com.sun.xml.rpc.wsdl.document.schema.SchemaConstants;
import javax.xml.rpc.namespace.QName;

// Referenced classes of package com.sun.xml.rpc.encoding:
//            SerializerBase, JAXRPCSerializer, SerializationException, DeserializationException, 
//            JAXRPCDeserializer, Initializable, InternalTypeMappingRegistry, SerializerCallback, 
//            SOAPSerializationContext, SOAPDeserializationContext

public class DynamicSerializer extends SerializerBase
    implements SchemaConstants, Initializable {

    InternalTypeMappingRegistry registry;

    public DynamicSerializer(QName xmlType, boolean encodeType, boolean isNullable, String encodingStyle) {
        super(xmlType, encodeType, isNullable, encodingStyle);
        registry = null;
    }

    public void initialize(InternalTypeMappingRegistry registry) throws Exception {
        this.registry = registry;
    }

    public void serialize(Object obj, QName name, SerializerCallback callback, XMLWriter writer, SOAPSerializationContext context) {
        if(obj == null) {
            serializeNull(name, writer);
            return;
        }
        JAXRPCSerializer serializer = getSerializerForObject(obj);
        if(serializer != null)
            serializer.serialize(obj, name, callback, writer, context);
    }

    protected JAXRPCSerializer getSerializerForObject(Object obj) {
        JAXRPCSerializer serializer = null;
        try {
            serializer = (JAXRPCSerializer)registry.getSerializer("http://schemas.xmlsoap.org/soap/encoding/", obj.getClass());
            if(serializer instanceof DynamicSerializer)
                throw new SerializationException("typemapping.serializer.is.dynamic", new Object[] {
                    obj.getClass()
                });
        }
        catch(SerializationException e) {
            throw e;
        }
        catch(Exception e) {
            throw new SerializationException("nestedSerializationError", new LocalizableExceptionAdapter(e));
        }
        return serializer;
    }

    protected void serializeNull(QName name, XMLWriter writer) {
        try {
            writer.startElement(name == null ? SchemaConstants.QNAME_ANY : name);
            String attrVal = XMLWriterUtil.encodeQName(writer, super.type);
            writer.writeAttribute(XSDConstants.QNAME_XSI_TYPE, attrVal);
            writer.writeAttribute(XSDConstants.QNAME_XSI_NIL, "1");
            writer.endElement();
        }
        catch(JAXRPCExceptionBase e) {
            throw new SerializationException("nestedSerializationError", e);
        }
    }

    public Object deserialize(QName name, XMLReader reader, SOAPDeserializationContext context) {
        try {
            JAXRPCDeserializer deserializer = getDeserializerForElement(reader, context);
            if(deserializer == null)
                return null;
            else
                return deserializer.deserialize(name, reader, context);
        }
        catch(DeserializationException e) {
            throw e;
        }
        catch(Exception e) {
            throw new DeserializationException("nestedDeserializationError", new LocalizableExceptionAdapter(e));
        }
    }

    protected JAXRPCDeserializer getDeserializerForElement(XMLReader reader, SOAPDeserializationContext context) throws Exception {
        if(SerializerBase.getNullStatus(reader)) {
            return null;
        } else {
            QName objectXMLType = SerializerBase.getType(reader);
            return (JAXRPCDeserializer)registry.getDeserializer("http://schemas.xmlsoap.org/soap/encoding/", objectXMLType);
        }
    }
}

// NMI's Java Code Viewer 5.1 © 1997-2001 B. Lemaire
// http://njcv.htmlplanet.com - info@njcv.htmlplanet.com

// Copy registered to Evaluation Copy                                   

// Source File Name:   StandardSOAPTypeMappings.java

package com.sun.xml.rpc.encoding.soap;

import com.sun.xml.rpc.encoding.*;
import com.sun.xml.rpc.encoding.simpletype.*;
import com.sun.xml.rpc.wsdl.document.schema.SchemaConstants;
import com.sun.xml.rpc.wsdl.document.soap.SOAPConstants;
import javax.xml.rpc.namespace.QName;

public class StandardSOAPTypeMappings extends TypeMappingImpl
    implements SerializerConstants {

    public StandardSOAPTypeMappings() throws Exception {
        setSupportedNamespaces(new String[] {
            "http://schemas.xmlsoap.org/soap/encoding/"
        });
        QName base64Types[] = {
            SchemaConstants.QNAME_TYPE_BASE64_BINARY, SOAPConstants.QNAME_TYPE_BASE64_BINARY, SOAPConstants.QNAME_TYPE_BASE64
        };
        QName type = SchemaConstants.QNAME_TYPE_BOOLEAN;
        CombinedSerializer serializer = new SimpleTypeSerializer(type, true, true, "http://schemas.xmlsoap.org/soap/encoding/", XSDBooleanEncoder.getInstance());
        registerSerializer(Boolean.TYPE, type, serializer);
        type = SOAPConstants.QNAME_TYPE_BOOLEAN;
        serializer = new SimpleTypeSerializer(type, true, true, "http://schemas.xmlsoap.org/soap/encoding/", XSDBooleanEncoder.getInstance());
        registerSerializer(Boolean.TYPE, type, serializer);
        type = SchemaConstants.QNAME_TYPE_BOOLEAN;
        serializer = new SimpleTypeSerializer(type, true, true, "http://schemas.xmlsoap.org/soap/encoding/", XSDBooleanEncoder.getInstance());
        registerSerializer(java.lang.Boolean.class, type, serializer);
        type = SOAPConstants.QNAME_TYPE_BOOLEAN;
        serializer = new SimpleTypeSerializer(type, true, true, "http://schemas.xmlsoap.org/soap/encoding/", XSDBooleanEncoder.getInstance());
        registerSerializer(java.lang.Boolean.class, type, serializer);
        type = SchemaConstants.QNAME_TYPE_BYTE;
        serializer = new SimpleTypeSerializer(type, true, true, "http://schemas.xmlsoap.org/soap/encoding/", XSDByteEncoder.getInstance());
        registerSerializer(Byte.TYPE, type, serializer);
        type = SOAPConstants.QNAME_TYPE_BYTE;
        serializer = new SimpleTypeSerializer(type, true, true, "http://schemas.xmlsoap.org/soap/encoding/", XSDByteEncoder.getInstance());
        registerSerializer(Byte.TYPE, type, serializer);
        type = SchemaConstants.QNAME_TYPE_BYTE;
        serializer = new SimpleTypeSerializer(type, true, true, "http://schemas.xmlsoap.org/soap/encoding/", XSDByteEncoder.getInstance());
        registerSerializer(java.lang.Byte.class, type, serializer);
        type = SOAPConstants.QNAME_TYPE_BYTE;
        serializer = new SimpleTypeSerializer(type, true, true, "http://schemas.xmlsoap.org/soap/encoding/", XSDByteEncoder.getInstance());
        registerSerializer(java.lang.Byte.class, type, serializer);
        type = SchemaConstants.QNAME_TYPE_BASE64_BINARY;
        serializer = new SimpleMultiTypeSerializer(type, true, true, "http://schemas.xmlsoap.org/soap/encoding/", XSDBase64BinaryEncoder.getInstance(), base64Types);
        registerSerializer(byte[].class, type, serializer);
        type = SOAPConstants.QNAME_TYPE_BASE64_BINARY;
        serializer = new SimpleMultiTypeSerializer(type, true, true, "http://schemas.xmlsoap.org/soap/encoding/", XSDBase64BinaryEncoder.getInstance(), base64Types);
        registerSerializer(byte[].class, type, serializer);
        type = SOAPConstants.QNAME_TYPE_BASE64;
        serializer = new SimpleMultiTypeSerializer(type, true, true, "http://schemas.xmlsoap.org/soap/encoding/", XSDBase64BinaryEncoder.getInstance(), base64Types);
        registerSerializer(byte[].class, type, serializer);
        type = SchemaConstants.QNAME_TYPE_HEX_BINARY;
        serializer = new SimpleTypeSerializer(type, true, true, "http://schemas.xmlsoap.org/soap/encoding/", XSDHexBinaryEncoder.getInstance());
        registerSerializer(byte[].class, type, serializer);
        type = SchemaConstants.QNAME_TYPE_DECIMAL;
        serializer = new SimpleTypeSerializer(type, true, true, "http://schemas.xmlsoap.org/soap/encoding/", XSDDecimalEncoder.getInstance());
        registerSerializer(java.math.BigDecimal.class, type, serializer);
        type = SOAPConstants.QNAME_TYPE_DECIMAL;
        serializer = new SimpleTypeSerializer(type, true, true, "http://schemas.xmlsoap.org/soap/encoding/", XSDDecimalEncoder.getInstance());
        registerSerializer(java.math.BigDecimal.class, type, serializer);
        type = SchemaConstants.QNAME_TYPE_DOUBLE;
        serializer = new SimpleTypeSerializer(type, true, true, "http://schemas.xmlsoap.org/soap/encoding/", XSDDoubleEncoder.getInstance());
        registerSerializer(Double.TYPE, type, serializer);
        type = SOAPConstants.QNAME_TYPE_DOUBLE;
        serializer = new SimpleTypeSerializer(type, true, true, "http://schemas.xmlsoap.org/soap/encoding/", XSDDoubleEncoder.getInstance());
        registerSerializer(Double.TYPE, type, serializer);
        type = SchemaConstants.QNAME_TYPE_DOUBLE;
        serializer = new SimpleTypeSerializer(type, true, true, "http://schemas.xmlsoap.org/soap/encoding/", XSDDoubleEncoder.getInstance());
        registerSerializer(java.lang.Double.class, type, serializer);
        type = SOAPConstants.QNAME_TYPE_DOUBLE;
        serializer = new SimpleTypeSerializer(type, true, true, "http://schemas.xmlsoap.org/soap/encoding/", XSDDoubleEncoder.getInstance());
        registerSerializer(java.lang.Double.class, type, serializer);
        type = SchemaConstants.QNAME_TYPE_FLOAT;
        serializer = new SimpleTypeSerializer(type, true, true, "http://schemas.xmlsoap.org/soap/encoding/", XSDFloatEncoder.getInstance());
        registerSerializer(Float.TYPE, type, serializer);
        type = SOAPConstants.QNAME_TYPE_FLOAT;
        serializer = new SimpleTypeSerializer(type, true, true, "http://schemas.xmlsoap.org/soap/encoding/", XSDFloatEncoder.getInstance());
        registerSerializer(Float.TYPE, type, serializer);
        type = SchemaConstants.QNAME_TYPE_FLOAT;
        serializer = new SimpleTypeSerializer(type, true, true, "http://schemas.xmlsoap.org/soap/encoding/", XSDFloatEncoder.getInstance());
        registerSerializer(java.lang.Float.class, type, serializer);
        type = SOAPConstants.QNAME_TYPE_FLOAT;
        serializer = new SimpleTypeSerializer(type, true, true, "http://schemas.xmlsoap.org/soap/encoding/", XSDFloatEncoder.getInstance());
        registerSerializer(java.lang.Float.class, type, serializer);
        type = SchemaConstants.QNAME_TYPE_INT;
        serializer = new SimpleTypeSerializer(type, true, true, "http://schemas.xmlsoap.org/soap/encoding/", XSDIntEncoder.getInstance());
        registerSerializer(Integer.TYPE, type, serializer);
        type = SOAPConstants.QNAME_TYPE_INT;
        serializer = new SimpleTypeSerializer(type, true, true, "http://schemas.xmlsoap.org/soap/encoding/", XSDIntEncoder.getInstance());
        registerSerializer(Integer.TYPE, type, serializer);
        type = SchemaConstants.QNAME_TYPE_INT;
        serializer = new SimpleTypeSerializer(type, true, true, "http://schemas.xmlsoap.org/soap/encoding/", XSDIntEncoder.getInstance());
        registerSerializer(java.lang.Integer.class, type, serializer);
        type = SOAPConstants.QNAME_TYPE_INT;
        serializer = new SimpleTypeSerializer(type, true, true, "http://schemas.xmlsoap.org/soap/encoding/", XSDIntEncoder.getInstance());
        registerSerializer(java.lang.Integer.class, type, serializer);
        type = SchemaConstants.QNAME_TYPE_INTEGER;
        serializer = new SimpleTypeSerializer(type, true, true, "http://schemas.xmlsoap.org/soap/encoding/", XSDIntegerEncoder.getInstance());
        registerSerializer(java.math.BigInteger.class, type, serializer);
        type = SOAPConstants.QNAME_TYPE_INTEGER;
        serializer = new SimpleTypeSerializer(type, true, true, "http://schemas.xmlsoap.org/soap/encoding/", XSDIntegerEncoder.getInstance());
        registerSerializer(java.math.BigInteger.class, type, serializer);
        type = SchemaConstants.QNAME_TYPE_LONG;
        serializer = new SimpleTypeSerializer(type, true, true, "http://schemas.xmlsoap.org/soap/encoding/", XSDLongEncoder.getInstance());
        registerSerializer(Long.TYPE, type, serializer);
        type = SOAPConstants.QNAME_TYPE_LONG;
        serializer = new SimpleTypeSerializer(type, true, true, "http://schemas.xmlsoap.org/soap/encoding/", XSDLongEncoder.getInstance());
        registerSerializer(Long.TYPE, type, serializer);
        type = SchemaConstants.QNAME_TYPE_LONG;
        serializer = new SimpleTypeSerializer(type, true, true, "http://schemas.xmlsoap.org/soap/encoding/", XSDLongEncoder.getInstance());
        registerSerializer(java.lang.Long.class, type, serializer);
        type = SOAPConstants.QNAME_TYPE_LONG;
        serializer = new SimpleTypeSerializer(type, true, true, "http://schemas.xmlsoap.org/soap/encoding/", XSDLongEncoder.getInstance());
        registerSerializer(java.lang.Long.class, type, serializer);
        type = SchemaConstants.QNAME_TYPE_SHORT;
        serializer = new SimpleTypeSerializer(type, true, true, "http://schemas.xmlsoap.org/soap/encoding/", XSDShortEncoder.getInstance());
        registerSerializer(Short.TYPE, type, serializer);
        type = SOAPConstants.QNAME_TYPE_SHORT;
        serializer = new SimpleTypeSerializer(type, true, true, "http://schemas.xmlsoap.org/soap/encoding/", XSDShortEncoder.getInstance());
        registerSerializer(Short.TYPE, type, serializer);
        type = SchemaConstants.QNAME_TYPE_SHORT;
        serializer = new SimpleTypeSerializer(type, true, true, "http://schemas.xmlsoap.org/soap/encoding/", XSDShortEncoder.getInstance());
        registerSerializer(java.lang.Short.class, type, serializer);
        type = SOAPConstants.QNAME_TYPE_SHORT;
        serializer = new SimpleTypeSerializer(type, true, true, "http://schemas.xmlsoap.org/soap/encoding/", XSDShortEncoder.getInstance());
        registerSerializer(java.lang.Short.class, type, serializer);
        type = SchemaConstants.QNAME_TYPE_STRING;
        serializer = new AttachmentSerializer(type, true, true, "http://schemas.xmlsoap.org/soap/encoding/", false, XSDStringEncoder.getInstance());
        registerSerializer(java.lang.String.class, type, serializer);
        type = SOAPConstants.QNAME_TYPE_STRING;
        serializer = new AttachmentSerializer(type, true, true, "http://schemas.xmlsoap.org/soap/encoding/", false, XSDStringEncoder.getInstance());
        registerSerializer(java.lang.String.class, type, serializer);
        type = SchemaConstants.QNAME_TYPE_DATE_TIME;
        serializer = new SimpleTypeSerializer(type, true, true, "http://schemas.xmlsoap.org/soap/encoding/", XSDDateTimeCalendarEncoder.getInstance());
        registerSerializer(java.util.Calendar.class, type, serializer);
        type = SOAPConstants.QNAME_TYPE_DATE_TIME;
        serializer = new SimpleTypeSerializer(type, true, true, "http://schemas.xmlsoap.org/soap/encoding/", XSDDateTimeCalendarEncoder.getInstance());
        registerSerializer(java.util.Calendar.class, type, serializer);
        type = SchemaConstants.QNAME_TYPE_DATE_TIME;
        serializer = new SimpleTypeSerializer(type, true, true, "http://schemas.xmlsoap.org/soap/encoding/", XSDDateTimeDateEncoder.getInstance());
        registerSerializer(java.util.Date.class, type, serializer);
        type = SOAPConstants.QNAME_TYPE_DATE_TIME;
        serializer = new SimpleTypeSerializer(type, true, true, "http://schemas.xmlsoap.org/soap/encoding/", XSDDateTimeDateEncoder.getInstance());
        registerSerializer(java.util.Date.class, type, serializer);
        type = SchemaConstants.QNAME_TYPE_QNAME;
        serializer = new SimpleTypeSerializer(type, true, true, "http://schemas.xmlsoap.org/soap/encoding/", XSDQNameEncoder.getInstance());
        registerSerializer(javax.xml.rpc.namespace.QName.class, type, serializer);
        type = SOAPConstants.QNAME_TYPE_QNAME;
        serializer = new SimpleTypeSerializer(type, true, true, "http://schemas.xmlsoap.org/soap/encoding/", XSDQNameEncoder.getInstance());
        registerSerializer(javax.xml.rpc.namespace.QName.class, type, serializer);
        type = new QName("http://java.sun.com/jax-rpc-ri/internal", "image");
        serializer = new AttachmentSerializer(type, true, true, "http://schemas.xmlsoap.org/soap/encoding/", true, ImageAttachmentEncoder.getInstance());
        serializer = new ReferenceableSerializerImpl(false, serializer);
        registerSerializer(java.awt.Image.class, type, serializer);
        type = new QName("http://java.sun.com/jax-rpc-ri/internal", "datahandler");
        serializer = new AttachmentSerializer(type, true, true, "http://schemas.xmlsoap.org/soap/encoding/", true, DataHandlerAttachmentEncoder.getInstance());
        serializer = new ReferenceableSerializerImpl(false, serializer);
        registerSerializer(javax.activation.DataHandler.class, type, serializer);
        type = new QName("http://java.sun.com/jax-rpc-ri/internal", "multipart");
        serializer = new AttachmentSerializer(type, true, true, "http://schemas.xmlsoap.org/soap/encoding/", true, MimeMultipartAttachmentEncoder.getInstance());
        serializer = new ReferenceableSerializerImpl(false, serializer);
        registerSerializer(javax.mail.internet.MimeMultipart.class, type, serializer);
        type = new QName("http://java.sun.com/jax-rpc-ri/internal", "text_xml");
        serializer = new AttachmentSerializer(type, true, true, "http://schemas.xmlsoap.org/soap/encoding/", true, SourceAttachmentEncoder.getInstance());
        serializer = new ReferenceableSerializerImpl(false, serializer);
        registerSerializer(javax.xml.transform.Source.class, type, serializer);
    }

    private void registerSerializer(Class javaType, QName xmlType, CombinedSerializer ser) throws Exception {
        ser = new ReferenceableSerializerImpl(false, ser);
        register(javaType, xmlType, new SingletonSerializerFactory(ser), new SingletonDeserializerFactory(ser));
    }
}

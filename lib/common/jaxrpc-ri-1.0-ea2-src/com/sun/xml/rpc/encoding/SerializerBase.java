// NMI's Java Code Viewer 5.1 © 1997-2001 B. Lemaire
// http://njcv.htmlplanet.com - info@njcv.htmlplanet.com

// Copy registered to Evaluation Copy                                   

// Source File Name:   SerializerBase.java

package com.sun.xml.rpc.encoding;

import com.sun.xml.rpc.streaming.*;
import javax.activation.DataHandler;
import javax.xml.rpc.namespace.QName;

// Referenced classes of package com.sun.xml.rpc.encoding:
//            DeserializationException, CombinedSerializer, SerializerConstants, SOAPDeserializationContext, 
//            SerializerCallback, SOAPSerializationContext

public abstract class SerializerBase
    implements CombinedSerializer, SerializerConstants {

    protected QName type;
    protected boolean encodeType;
    protected boolean isNullable;
    protected String encodingStyle;

    protected SerializerBase(QName xmlType, boolean encodeType, boolean isNullable, String encodingStyle) {
        if(xmlType == null) {
            throw new IllegalArgumentException("xmlType parameter is not allowed to be null");
        } else {
            type = xmlType;
            this.encodeType = encodeType;
            this.isNullable = isNullable;
            this.encodingStyle = encodingStyle;
            return;
        }
    }

    public QName getXmlType() {
        return type;
    }

    public boolean getEncodeType() {
        return encodeType;
    }

    public boolean isNullable() {
        return isNullable;
    }

    public String getEncodingStyle() {
        return encodingStyle;
    }

    public CombinedSerializer getInnermostSerializer() {
        return this;
    }

    public Object deserialize(DataHandler dataHandler, SOAPDeserializationContext context) {
        throw new UnsupportedOperationException();
    }

    protected QName getName(XMLReader reader) throws Exception {
        return reader.getName();
    }

    public static QName getType(XMLReader reader) throws Exception {
        QName type = null;
        Attributes attrs = reader.getAttributes();
        String typeVal = attrs.getValue("http://www.w3.org/2001/XMLSchema-instance", "type");
        if(typeVal != null)
            type = XMLReaderUtil.decodeQName(reader, typeVal);
        return type;
    }

    public static boolean getNullStatus(XMLReader reader) throws Exception {
        boolean isNull = false;
        Attributes attrs = reader.getAttributes();
        String nullVal = attrs.getValue("http://www.w3.org/2001/XMLSchema-instance", "nil");
        isNull = nullVal != null && decodeBoolean(nullVal);
        return isNull;
    }

    public static boolean decodeBoolean(String str) throws Exception {
        return str.equals("true") || str.equals("1");
    }

    protected String getID(XMLReader reader) throws Exception {
        Attributes attrs = reader.getAttributes();
        return attrs.getValue("", "id");
    }

    protected void verifyName(XMLReader reader, QName expectedName) throws Exception {
        QName actualName = getName(reader);
        if(!actualName.equals(expectedName))
            throw new DeserializationException("soap.unexpectedElementName", new Object[] {
                expectedName.toString(), actualName.toString()
            });
        else
            return;
    }

    protected void verifyType(XMLReader reader) throws Exception {
        if(typeIsEmpty())
            return;
        QName actualType = getType(reader);
        if(actualType != null && !actualType.equals(type) && !isAcceptableType(actualType))
            throw new DeserializationException("soap.unexpectedElementType", new Object[] {
                type.toString(), actualType.toString()
            });
        else
            return;
    }

    protected boolean isAcceptableType(QName actualType) {
        return false;
    }

    protected void skipEmptyContent(XMLReader reader) throws Exception {
        reader.skipElement();
        XMLReaderUtil.verifyReaderState(reader, 2);
    }

    public String getMechanismType() {
        return "http://java.sun.com/jax-rpc-ri/1.0/streaming/";
    }

    protected boolean typeIsEmpty() {
        return type.getNamespaceURI().equals("") && type.getLocalPart().equals("");
    }

    public abstract void serialize(Object obj, QName qname, SerializerCallback serializercallback, XMLWriter xmlwriter, SOAPSerializationContext soapserializationcontext);

    public abstract Object deserialize(QName qname, XMLReader xmlreader, SOAPDeserializationContext soapdeserializationcontext);
}

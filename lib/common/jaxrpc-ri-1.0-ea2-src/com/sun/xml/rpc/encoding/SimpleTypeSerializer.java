// NMI's Java Code Viewer 5.1 © 1997-2001 B. Lemaire
// http://njcv.htmlplanet.com - info@njcv.htmlplanet.com

// Copy registered to Evaluation Copy                                   

// Source File Name:   SimpleTypeSerializer.java

package com.sun.xml.rpc.encoding;

import com.sun.xml.rpc.encoding.simpletype.SimpleTypeConstants;
import com.sun.xml.rpc.encoding.simpletype.SimpleTypeEncoder;
import com.sun.xml.rpc.streaming.*;
import com.sun.xml.rpc.util.exception.JAXRPCExceptionBase;
import com.sun.xml.rpc.util.exception.LocalizableExceptionAdapter;
import javax.xml.rpc.namespace.QName;

// Referenced classes of package com.sun.xml.rpc.encoding:
//            SerializerBase, SerializationException, DeserializationException, SerializerCallback, 
//            SOAPSerializationContext, SOAPDeserializationContext

public class SimpleTypeSerializer extends SerializerBase
    implements SimpleTypeConstants {

    protected SimpleTypeEncoder encoder;

    public SimpleTypeSerializer(QName type, boolean encodeType, boolean isNullable, String encodingStyle, SimpleTypeEncoder encoder) {
        super(type, encodeType, isNullable, encodingStyle);
        this.encoder = encoder;
    }

    public void serialize(Object obj, QName name, SerializerCallback callback, XMLWriter writer, SOAPSerializationContext context) {
        boolean pushedEncodingStyle = false;
        try {
            writer.startElement(name == null ? super.type : name);
            if(callback != null)
                callback.onStartTag(obj, name, writer, context);
            if(super.encodingStyle != null)
                pushedEncodingStyle = context.pushEncodingStyle(super.encodingStyle, writer);
            if(super.encodeType) {
                String attrVal = XMLWriterUtil.encodeQName(writer, super.type);
                writer.writeAttribute(SimpleTypeConstants.QNAME_XSI_TYPE, attrVal);
            }
            if(obj == null) {
                if(!super.isNullable)
                    throw new SerializationException("xsd.unexpectedNull");
                writer.writeAttribute(SimpleTypeConstants.QNAME_XSI_NIL, "1");
            } else {
                encoder.writeAdditionalNamespaceDeclarations(obj, writer);
                writer.writeChars(encoder.objectToString(obj, writer));
            }
            writer.endElement();
        }
        catch(JAXRPCExceptionBase e) {
            throw new SerializationException(e);
        }
        catch(Exception e) {
            throw new SerializationException(new LocalizableExceptionAdapter(e));
        }
        finally {
            if(pushedEncodingStyle)
                context.popEncodingStyle();
        }
    }

    public Object deserialize(QName name, XMLReader reader, SOAPDeserializationContext context) {
        boolean pushedEncodingStyle = false;
        try {
            pushedEncodingStyle = context.processEncodingStyle(reader);
            if(super.encodingStyle != null)
                context.verifyEncodingStyle(super.encodingStyle);
            if(name != null) {
                QName actualName = reader.getName();
                if(!actualName.equals(name))
                    throw new DeserializationException("xsd.unexpectedElementName", new Object[] {
                        name.toString(), actualName.toString()
                    });
            }
            verifyType(reader);
            Attributes attrs = reader.getAttributes();
            String nullVal = attrs.getValue("http://www.w3.org/2001/XMLSchema-instance", "nil");
            boolean isNull = nullVal != null && (nullVal.equals("true") || nullVal.equals("1"));
            reader.next();
            Object obj = null;
            if(isNull) {
                if(!super.isNullable)
                    throw new DeserializationException("xsd.unexpectedNull");
            } else {
                String val = null;
                switch(reader.getState()) {
                case 3: // '\003'
                    val = reader.getValue();
                    reader.next();
                    break;

                case 2: // '\002'
                    val = "";
                    break;
                }
                obj = encoder.stringToObject(collapseWhitespace(val), reader);
            }
            XMLReaderUtil.verifyReaderState(reader, 2);
            Object obj1 = obj;
            return obj1;
        }
        catch(JAXRPCExceptionBase e) {
            throw new DeserializationException(e);
        }
        catch(Exception e) {
            throw new DeserializationException(new LocalizableExceptionAdapter(e));
        }
        finally {
            if(pushedEncodingStyle)
                context.popEncodingStyle();
        }
    }

    public static boolean needsCollapsing(String str) {
        int len = str.length();
        int spanLen = 0;
        for(int idx = 0; idx < len; idx++)
            if(Character.isWhitespace(str.charAt(idx)))
                spanLen++;
            else
            if(spanLen > 0) {
                if(spanLen == idx)
                    return true;
                if(str.charAt(idx - spanLen) != ' ')
                    return true;
                if(spanLen > 1)
                    return true;
                spanLen = 0;
            }

        return spanLen > 0;
    }

    public static String collapseWhitespace(String str) {
        if(!needsCollapsing(str))
            return str;
        int len = str.length();
        char buf[] = new char[len];
        str.getChars(0, len, buf, 0);
        int leadingWSLen = 0;
        int trailingWSLen = 0;
        int spanLen = 0;
        for(int idx = 0; idx < len; idx++)
            if(Character.isWhitespace(buf[idx]))
                spanLen++;
            else
            if(spanLen > 0) {
                if(spanLen == idx) {
                    leadingWSLen = spanLen;
                } else {
                    int firstWSIdx = idx - spanLen;
                    buf[firstWSIdx] = ' ';
                    if(spanLen > 1) {
                        System.arraycopy(buf, idx, buf, firstWSIdx + 1, len - idx);
                        len -= spanLen - 1;
                        idx = firstWSIdx + 1;
                    }
                }
                spanLen = 0;
            }

        if(spanLen > 0)
            trailingWSLen = spanLen;
        return new String(buf, leadingWSLen, len - leadingWSLen - trailingWSLen);
    }
}

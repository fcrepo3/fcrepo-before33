// NMI's Java Code Viewer 5.1 © 1997-2001 B. Lemaire
// http://njcv.htmlplanet.com - info@njcv.htmlplanet.com

// Copy registered to Evaluation Copy                                   

// Source File Name:   ObjectSerializerBase.java

package com.sun.xml.rpc.encoding;

import com.sun.xml.rpc.encoding.xsd.XSDConstants;
import com.sun.xml.rpc.streaming.*;
import com.sun.xml.rpc.util.exception.JAXRPCExceptionBase;
import com.sun.xml.rpc.util.exception.LocalizableExceptionAdapter;
import javax.xml.rpc.namespace.QName;

// Referenced classes of package com.sun.xml.rpc.encoding:
//            SerializerBase, SerializationException, SOAPDeserializationState, DeserializationException, 
//            SerializerCallback, SOAPSerializationContext, SOAPDeserializationContext, SOAPInstanceBuilder

public abstract class ObjectSerializerBase extends SerializerBase {

    protected ObjectSerializerBase(QName type, boolean encodeType, boolean isNullable, String encodingStyle) {
        super(type, encodeType, isNullable, encodingStyle);
    }

    protected abstract Object doDeserialize(SOAPDeserializationState soapdeserializationstate, XMLReader xmlreader, SOAPDeserializationContext soapdeserializationcontext) throws Exception;

    protected abstract void doSerializeInstance(Object obj, XMLWriter xmlwriter, SOAPSerializationContext soapserializationcontext) throws Exception;

    public void serialize(Object obj, QName name, SerializerCallback callback, XMLWriter writer, SOAPSerializationContext context) {
        boolean pushedEncodingStyle = false;
        try {
            if(obj == null) {
                if(!super.isNullable)
                    throw new SerializationException("soap.unexpectedNull");
                serializeNull(name, writer, context);
            } else {
                writer.startElement(name == null ? super.type : name);
                if(callback != null)
                    callback.onStartTag(obj, name, writer, context);
                if(super.encodingStyle != null)
                    pushedEncodingStyle = context.pushEncodingStyle(super.encodingStyle, writer);
                if(super.encodeType) {
                    String attrVal = XMLWriterUtil.encodeQName(writer, super.type);
                    writer.writeAttribute(XSDConstants.QNAME_XSI_TYPE, attrVal);
                }
                doSerializeInstance(obj, writer, context);
                writer.endElement();
            }
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

    protected void serializeNull(QName name, XMLWriter writer, SOAPSerializationContext context) throws Exception {
        writer.startElement(name == null ? super.type : name);
        boolean pushedEncodingStyle = false;
        if(super.encodingStyle != null)
            pushedEncodingStyle = context.pushEncodingStyle(super.encodingStyle, writer);
        if(super.encodeType) {
            String attrVal = XMLWriterUtil.encodeQName(writer, super.type);
            writer.writeAttribute(XSDConstants.QNAME_XSI_TYPE, attrVal);
        }
        writer.writeAttribute(XSDConstants.QNAME_XSI_NIL, "1");
        writer.endElement();
        if(pushedEncodingStyle)
            context.popEncodingStyle();
    }

    public Object deserialize(QName name, XMLReader reader, SOAPDeserializationContext context) {
        boolean pushedEncodingStyle = false;
        try {
            pushedEncodingStyle = context.processEncodingStyle(reader);
            if(super.encodingStyle != null)
                context.verifyEncodingStyle(super.encodingStyle);
            if(name != null)
                verifyName(reader, name);
            String id = getID(reader);
            boolean isNull = SerializerBase.getNullStatus(reader);
            if(!isNull) {
                verifyType(reader);
                SOAPDeserializationState state = null;
                if(id != null) {
                    state = context.getStateFor(id);
                    state.setDeserializer(this);
                }
                Object instance = doDeserialize(state, reader, context);
                XMLReaderUtil.verifyReaderState(reader, 2);
                if(instance instanceof SOAPDeserializationState)
                    state = (SOAPDeserializationState)instance;
                else
                if(state != null)
                    state.setInstance(instance);
                if(state != null) {
                    state.doneReading();
                    SOAPDeserializationState soapdeserializationstate = state;
                    return soapdeserializationstate;
                }
                Object obj1 = instance;
                return obj1;
            }
            if(!super.isNullable)
                throw new DeserializationException("soap.unexpectedNull");
            skipEmptyContent(reader);
            if(id != null) {
                SOAPDeserializationState state = context.getStateFor(id);
                state.setDeserializer(this);
                state.setInstance(null);
                state.doneReading();
            }
            Object obj = null;
            return obj;
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

    public static SOAPDeserializationState registerWithMemberState(Object instance, SOAPDeserializationState state, Object member, int memberIndex, SOAPInstanceBuilder builder) {
        try {
            SOAPDeserializationState deserializationState;
            if(state == null)
                deserializationState = new SOAPDeserializationState();
            else
                deserializationState = state;
            deserializationState.setInstance(instance);
            if(deserializationState.getBuilder() == null) {
                if(builder == null)
                    throw new IllegalArgumentException();
                deserializationState.setBuilder(builder);
            }
            SOAPDeserializationState memberState = (SOAPDeserializationState)member;
            memberState.registerListener(deserializationState, memberIndex);
            return deserializationState;
        }
        catch(JAXRPCExceptionBase e) {
            throw new DeserializationException(e);
        }
        catch(Exception e) {
            throw new DeserializationException(new LocalizableExceptionAdapter(e));
        }
    }
}

// NMI's Java Code Viewer 5.1 © 1997-2001 B. Lemaire
// http://njcv.htmlplanet.com - info@njcv.htmlplanet.com

// Copy registered to Evaluation Copy                                   

// Source File Name:   ReferenceableSerializerImpl.java

package com.sun.xml.rpc.encoding;

import com.sun.xml.rpc.streaming.*;
import com.sun.xml.rpc.util.exception.JAXRPCExceptionBase;
import com.sun.xml.rpc.util.exception.LocalizableExceptionAdapter;
import com.sun.xml.rpc.wsdl.document.soap.SOAPConstants;
import javax.activation.DataHandler;
import javax.xml.rpc.namespace.QName;

// Referenced classes of package com.sun.xml.rpc.encoding:
//            SerializerBase, Initializable, SerializationException, SOAPDeserializationState, 
//            DeserializationException, ReferenceableSerializer, SerializerCallback, CombinedSerializer, 
//            JAXRPCSerializer, SOAPSerializationContext, SOAPSerializationState, JAXRPCDeserializer, 
//            SOAPDeserializationContext, InternalTypeMappingRegistry

public class ReferenceableSerializerImpl extends SerializerBase
    implements Initializable, ReferenceableSerializer, SerializerCallback {

    private CombinedSerializer serializer;
    private boolean serializeAsRef;

    public ReferenceableSerializerImpl(boolean serializeAsRef, CombinedSerializer serializer) {
        super(serializer.getXmlType(), serializer.getEncodeType(), serializer.isNullable(), serializer.getEncodingStyle());
        this.serializer = serializer;
        this.serializeAsRef = serializeAsRef;
    }

    public void initialize(InternalTypeMappingRegistry registry) throws Exception {
        if(serializer instanceof Initializable)
            ((Initializable)serializer).initialize(registry);
    }

    public CombinedSerializer getInnermostSerializer() {
        return serializer.getInnermostSerializer();
    }

    public void serialize(Object obj, QName name, SerializerCallback callback, XMLWriter writer, SOAPSerializationContext context) {
        boolean pushedEncodingStyle = false;
        try {
            if(!serializeAsRef || obj == null) {
                serializer.serialize(obj, name, null, writer, context);
            } else {
                SOAPSerializationState state = context.registerObject(obj, this);
                writer.startElement(name == null ? super.type : name);
                if(typeIsEmpty())
                    throw new SerializationException("soap.unspecifiedType");
                if(super.encodingStyle != null)
                    pushedEncodingStyle = context.pushEncodingStyle(super.encodingStyle, writer);
                writer.writeAttribute(SOAPConstants.QNAME_ATTR_HREF, "#" + state.getID());
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

    public Object deserialize(QName name, XMLReader reader, SOAPDeserializationContext context) {
        try {
            String href = getHRef(reader);
            if(href != null) {
                if(href.startsWith("cid:"))
                    return serializer.deserialize(name, reader, context);
                skipEmptyContent(reader);
                SOAPDeserializationState state = context.getStateFor(href);
                state.setDeserializer(this);
                if(state.isComplete())
                    return state.getInstance();
                else
                    return state;
            }
            String id = getID(reader);
            boolean isNull = SerializerBase.getNullStatus(reader);
            if(!isNull) {
                SOAPDeserializationState state = null;
                Object instance = serializer.deserialize(name, reader, context);
                if(id != null)
                    state = context.getStateFor(id);
                XMLReaderUtil.verifyReaderState(reader, 2);
                if(instance instanceof SOAPDeserializationState) {
                    state = (SOAPDeserializationState)instance;
                    state.setDeserializer(this);
                } else
                if(state != null) {
                    state.setInstance(instance);
                    state.setDeserializer(this);
                }
                if(state != null) {
                    state.doneReading();
                    return state;
                } else {
                    return instance;
                }
            }
            serializer.deserialize(name, reader, context);
            if(id != null) {
                SOAPDeserializationState state = context.getStateFor(id);
                state.setDeserializer(this);
                state.setInstance(null);
                state.doneReading();
            }
            return null;
        }
        catch(JAXRPCExceptionBase e) {
            throw new DeserializationException(e);
        }
        catch(Exception e) {
            throw new DeserializationException(new LocalizableExceptionAdapter(e));
        }
    }

    public Object deserialize(DataHandler dataHandler, SOAPDeserializationContext context) {
        return serializer.deserialize(dataHandler, context);
    }

    public void serializeInstance(Object obj, QName name, boolean isMultiRef, XMLWriter writer, SOAPSerializationContext context) {
        SerializerCallback callback = isMultiRef ? ((SerializerCallback) (this)) : null;
        serializer.serialize(obj, name, callback, writer, context);
    }

    public void onStartTag(Object obj, QName name, XMLWriter writer, SOAPSerializationContext context) {
        if(!serializeAsRef)
            return;
        try {
            SOAPSerializationState state = context.registerObject(obj, this);
            writer.writeAttribute(SOAPConstants.QNAME_ATTR_ID, state.getID());
        }
        catch(JAXRPCExceptionBase e) {
            throw new SerializationException(e);
        }
        catch(Exception e) {
            throw new SerializationException(new LocalizableExceptionAdapter(e));
        }
    }

    protected String getHRef(XMLReader reader) throws Exception {
        String href = null;
        Attributes attrs = reader.getAttributes();
        href = attrs.getValue("", "href");
        if(href != null)
            if(href.charAt(0) == '#')
                href = href.substring(1);
            else
            if(!href.startsWith("cid:"))
                throw new DeserializationException("soap.nonLocalReference", href);
        return href;
    }
}

// NMI's Java Code Viewer 5.1 © 1997-2001 B. Lemaire
// http://njcv.htmlplanet.com - info@njcv.htmlplanet.com

// Copy registered to Evaluation Copy                                   

// Source File Name:   PolymorphicArraySerializer.java

package com.sun.xml.rpc.encoding;

import com.sun.xml.rpc.encoding.soap.SOAPConstants;
import com.sun.xml.rpc.encoding.xsd.XSDConstants;
import com.sun.xml.rpc.streaming.*;
import com.sun.xml.rpc.util.exception.JAXRPCExceptionBase;
import com.sun.xml.rpc.util.exception.LocalizableExceptionAdapter;
import java.lang.reflect.Array;
import javax.xml.rpc.namespace.QName;

// Referenced classes of package com.sun.xml.rpc.encoding:
//            SerializerBase, SerializationException, JAXRPCSerializer, DeserializationException, 
//            JAXRPCDeserializer, SOAPDeserializationState, Initializable, SerializerCallback, 
//            SOAPSerializationContext, ArraySerializerBase, InternalTypeMappingRegistry, SOAPDeserializationContext, 
//            ObjectArraySerializer

public class PolymorphicArraySerializer extends SerializerBase
    implements Initializable {

    protected QName elemName;
    protected InternalTypeMappingRegistry registry;

    public PolymorphicArraySerializer(QName type, boolean encodeType, boolean isNullable, String encodingStyle, QName elemName) {
        super(type, encodeType, isNullable, encodingStyle);
        this.elemName = elemName;
    }

    public void initialize(InternalTypeMappingRegistry registry) throws Exception {
        this.registry = registry;
    }

    public void serialize(Object obj, QName name, SerializerCallback callback, XMLWriter writer, SOAPSerializationContext context) throws SerializationException {
        boolean pushedEncodingStyle = false;
        try {
            if(obj == null) {
                if(!super.isNullable)
                    throw new SerializationException("soap.unexpectedNull");
                serializeNull(name, writer, context);
            } else {
                if(!obj.getClass().isArray())
                    throw new SerializationException("type.is.not.array", new Object[] {
                        obj.getClass().getName()
                    });
                writer.startElement(name == null ? super.type : name);
                if(callback != null)
                    callback.onStartTag(obj, name, writer, context);
                pushedEncodingStyle = context.pushEncodingStyle(super.encodingStyle, writer);
                String attrVal;
                if(super.encodeType) {
                    attrVal = XMLWriterUtil.encodeQName(writer, SOAPConstants.QNAME_ENCODING_ARRAY);
                    writer.writeAttribute(XSDConstants.QNAME_XSI_TYPE, attrVal);
                }
                int dims[] = ArraySerializerBase.getArrayDimensions(obj, getArrayRank(obj));
                String encodedDims = ArraySerializerBase.encodeArrayDimensions(dims);
                QName xmlType = registry.getXmlType(super.encodingStyle, obj.getClass());
                if(xmlType == null)
                    throw new SerializationException("typemapping.serializerNotRegistered", new Object[] {
                        obj.getClass().getName()
                    });
                attrVal = XMLWriterUtil.encodeQName(writer, xmlType) + encodedDims;
                writer.writeAttribute(SOAPConstants.QNAME_ENCODING_ARRAYTYPE, attrVal);
                serializeArrayInstance(obj, dims, writer, context);
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

    protected void serializeArrayInstance(Object obj, int dims[], XMLWriter writer, SOAPSerializationContext context) throws Exception {
        serializeArrayElements((Object[])obj, 0, dims, writer, context);
    }

    protected void serializeArrayElements(Object arr[], int level, int dims[], XMLWriter writer, SOAPSerializationContext context) throws Exception {
        if(arr == null || arr.length != dims[level])
            throw new SerializationException("soap.irregularMultiDimensionalArray");
        boolean serializeLeaves = false;
        JAXRPCSerializer elemSer = null;
        if(level == dims.length - 1) {
            serializeLeaves = true;
            elemSer = (JAXRPCSerializer)registry.getSerializer(super.encodingStyle, ((Object) (arr)).getClass().getComponentType());
        }
        for(int i = 0; i < dims[level]; i++) {
            Object elem = arr[i];
            if(serializeLeaves)
                elemSer.serialize(elem, elemName, null, writer, context);
            else
                serializeArrayElements((Object[])elem, level + 1, dims, writer, context);
        }

    }

    protected void serializeNull(QName name, XMLWriter writer, SOAPSerializationContext context) throws Exception {
        boolean pushedEncodingStyle = false;
        writer.startElement(name == null ? super.type : name);
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
            context.verifyEncodingStyle(super.encodingStyle);
            if(name != null)
                verifyName(reader, name);
            boolean isNull = SerializerBase.getNullStatus(reader);
            if(!isNull) {
                QName actualType = SerializerBase.getType(reader);
                if(actualType != null && !actualType.equals(super.type) && !actualType.equals(SOAPConstants.QNAME_ENCODING_ARRAY))
                    throw new DeserializationException("soap.unexpectedElementType", new Object[] {
                        super.type.toString(), actualType.toString()
                    });
                String arrayType = null;
                Attributes attrs = reader.getAttributes();
                arrayType = attrs.getValue("http://schemas.xmlsoap.org/soap/encoding/", "arrayType");
                if(arrayType == null)
                    throw new DeserializationException("soap.malformedArrayType", "<arrayType attribute missing>");
                int dims[] = ArraySerializerBase.getArrayDimensions(arrayType, reader);
                QName elemXmlType = ArraySerializerBase.getArrayElementType(arrayType, reader);
                Class elemJavaType = registry.getJavaType(super.encodingStyle, elemXmlType);
                if(elemJavaType == null)
                    throw new SerializationException("typemapping.deserializerNotRegistered", new Object[] {
                        elemXmlType
                    });
                JAXRPCDeserializer elemDeser = (JAXRPCDeserializer)registry.getDeserializer(super.encodingStyle, elemXmlType);
                Object rslt = deserializeArrayInstance(reader, context, dims, elemJavaType, elemDeser);
                XMLReaderUtil.verifyReaderState(reader, 2);
                Object obj1 = rslt;
                return obj1;
            }
            if(!super.isNullable)
                throw new DeserializationException("soap.unexpectedNull");
            skipEmptyContent(reader);
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

    protected Object deserializeArrayInstance(XMLReader reader, SOAPDeserializationContext context, int dims[], Class elemClass, JAXRPCDeserializer elemDeser) throws Exception {
        String id = getID(reader);
        SOAPDeserializationState state = id == null ? null : context.getStateFor(id);
        boolean isComplete = true;
        boolean emptyDims = ArraySerializerBase.isEmptyDimensions(dims);
        int dimOffsets[] = ArraySerializerBase.getDimensionOffsets(dims);
        int offset[] = ArraySerializerBase.getArrayOffset(reader, dims);
        if(offset == null)
            offset = new int[emptyDims ? 1 : dims.length];
        Object value[] = null;
        int maxPosition = 0;
        int length = 0;
        if(reader.nextElementContent() != 2) {
            int position[] = ArraySerializerBase.getArrayElementPosition(reader, dims);
            boolean isSparseArray = position != null;
            if(!isSparseArray)
                position = offset;
            if(emptyDims) {
                maxPosition = position[0];
                length = Math.max(maxPosition * 2, 1024);
                value = (Object[])Array.newInstance(elemClass, length);
            } else {
                value = (Object[])Array.newInstance(elemClass, dims);
            }
            do {
                if(!emptyDims && !ArraySerializerBase.isPositionWithinBounds(position, dims))
                    if(isSparseArray)
                        throw new DeserializationException("soap.outOfBoundsArrayElementPosition", ArraySerializerBase.encodeArrayDimensions(position));
                    else
                        throw new DeserializationException("soap.tooManyArrayElements");
                if(emptyDims && position[0] >= length) {
                    int newLength;
                    for(newLength = length * 2; position[0] >= newLength; newLength *= 2);
                    Object newValue[] = (Object[])Array.newInstance(elemClass, newLength);
                    System.arraycopy(((Object) (value)), 0, ((Object) (newValue)), 0, length);
                    value = newValue;
                    length = newLength;
                }
                Object elem = null;
                elem = elemDeser.deserialize(elemName, reader, context);
                if(elem instanceof SOAPDeserializationState) {
                    SOAPDeserializationState elemState = (SOAPDeserializationState)elem;
                    isComplete = false;
                    if(state == null)
                        state = new SOAPDeserializationState();
                    state.setInstance(((Object) (value)));
                    if(state.getBuilder() == null)
                        state.setBuilder(new PolymorphicArraySerializer$ObjectArrayInstanceBuilder(this, dimOffsets));
                    elemState.registerListener(state, ArraySerializerBase.indexFromPosition(position, dimOffsets));
                } else {
                    ObjectArraySerializer.setElement(value, position, elem);
                }
                if(reader.nextElementContent() == 2)
                    break;
                if(isSparseArray) {
                    position = ArraySerializerBase.getArrayElementPosition(reader, dims);
                    if(position == null)
                        throw new DeserializationException("soap.missingArrayElementPosition");
                } else
                if(emptyDims)
                    position[0]++;
                else
                    ArraySerializerBase.incrementPosition(position, dims);
                if(emptyDims)
                    maxPosition = Math.max(position[0], maxPosition);
            } while(true);
            if(emptyDims && length != maxPosition + 1) {
                int newLength = maxPosition + 1;
                Object newValue[] = (Object[])Array.newInstance(elemClass, newLength);
                System.arraycopy(((Object) (value)), 0, ((Object) (newValue)), 0, newLength);
                value = newValue;
                length = newLength;
            }
        } else
        if(emptyDims)
            value = (Object[])Array.newInstance(elemClass, 0);
        else
            value = (Object[])Array.newInstance(elemClass, dims);
        if(state != null) {
            state.setDeserializer(this);
            state.setInstance(((Object) (value)));
            state.doneReading();
        }
        if(isComplete)
            return ((Object) (value));
        else
            return state;
    }

    protected int getArrayRank(Object obj) {
        int rank = 0;
        for(Class type = obj.getClass(); type.isArray(); type = type.getComponentType())
            rank++;

        return rank;
    }
}

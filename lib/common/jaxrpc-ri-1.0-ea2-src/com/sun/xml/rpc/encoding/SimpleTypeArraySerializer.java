// NMI's Java Code Viewer 5.1 © 1997-2001 B. Lemaire
// http://njcv.htmlplanet.com - info@njcv.htmlplanet.com

// Copy registered to Evaluation Copy                                   

// Source File Name:   SimpleTypeArraySerializer.java

package com.sun.xml.rpc.encoding;

import com.sun.xml.rpc.streaming.XMLReader;
import com.sun.xml.rpc.streaming.XMLWriter;
import java.lang.reflect.Array;
import javax.xml.rpc.namespace.QName;

// Referenced classes of package com.sun.xml.rpc.encoding:
//            ArraySerializerBase, SerializationException, DeserializationException, SimpleTypeSerializer, 
//            SOAPSerializationContext, SOAPDeserializationContext

public class SimpleTypeArraySerializer extends ArraySerializerBase {

    protected SimpleTypeSerializer elemSer;
    protected Class encoderElemClass;

    public SimpleTypeArraySerializer(QName type, boolean encodeType, boolean isNullable, String encodingStyle, QName elemName, QName elemType, Class elemClass, 
            int rank, int dims[], SimpleTypeSerializer elemSer) {
        super(type, encodeType, isNullable, encodingStyle, elemName, elemType, elemClass, rank, dims);
        this.elemSer = elemSer;
    }

    protected void serializeArrayInstance(Object obj, int dims[], XMLWriter writer, SOAPSerializationContext context) throws Exception {
        serializeArrayElements(obj, 0, dims, writer, context);
    }

    protected void serializeArrayElements(Object obj, int level, int dims[], XMLWriter writer, SOAPSerializationContext context) throws Exception {
        if(obj == null || Array.getLength(obj) != dims[level])
            throw new SerializationException("soap.irregularMultiDimensionalArray");
        boolean serializeLeaves = level == dims.length - 1;
        for(int i = 0; i < dims[level]; i++)
            if(serializeLeaves)
                serializeElement(obj, i, writer, context);
            else
                serializeArrayElements(Array.get(obj, i), level + 1, dims, writer, context);

    }

    protected void serializeElement(Object obj, int index, XMLWriter writer, SOAPSerializationContext context) throws Exception {
        Object elem = Array.get(obj, index);
        elemSer.serialize(elem, super.elemName, null, writer, context);
    }

    protected Object deserializeArrayInstance(XMLReader reader, SOAPDeserializationContext context, int dims[]) throws Exception {
        boolean emptyDims = ArraySerializerBase.isEmptyDimensions(dims);
        int offset[] = ArraySerializerBase.getArrayOffset(reader, dims);
        if(offset == null)
            offset = new int[emptyDims ? 1 : dims.length];
        Object value = null;
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
                value = Array.newInstance(super.elemClass, length);
            } else {
                value = Array.newInstance(super.elemClass, dims);
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
                    Object newValue = Array.newInstance(super.elemClass, newLength);
                    System.arraycopy(value, 0, newValue, 0, length);
                    value = newValue;
                    length = newLength;
                }
                deserializeElement(value, position, reader, context);
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
                Object newValue = Array.newInstance(super.elemClass, newLength);
                System.arraycopy(value, 0, newValue, 0, newLength);
                value = newValue;
                length = newLength;
            }
        } else
        if(emptyDims)
            value = Array.newInstance(super.elemClass, 0);
        else
            value = Array.newInstance(super.elemClass, dims);
        return value;
    }

    protected void deserializeElement(Object value, int position[], XMLReader reader, SOAPDeserializationContext context) throws Exception {
        Object arr = value;
        for(int i = 0; i < position.length - 1; i++)
            arr = Array.get(arr, position[i]);

        deserializeElement(arr, position[position.length - 1], reader, context);
    }

    protected void deserializeElement(Object value, int position, XMLReader reader, SOAPDeserializationContext context) throws Exception {
        Array.set(value, position, elemSer.deserialize(super.elemName, reader, context));
    }
}

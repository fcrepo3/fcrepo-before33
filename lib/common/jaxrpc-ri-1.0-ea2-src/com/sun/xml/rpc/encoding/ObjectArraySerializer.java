// NMI's Java Code Viewer 5.1 © 1997-2001 B. Lemaire
// http://njcv.htmlplanet.com - info@njcv.htmlplanet.com

// Copy registered to Evaluation Copy                                   

// Source File Name:   ObjectArraySerializer.java

package com.sun.xml.rpc.encoding;

import com.sun.xml.rpc.streaming.XMLReader;
import com.sun.xml.rpc.streaming.XMLWriter;
import java.lang.reflect.Array;
import javax.xml.rpc.namespace.QName;

// Referenced classes of package com.sun.xml.rpc.encoding:
//            ArraySerializerBase, JAXRPCSerializer, JAXRPCDeserializer, SerializationException, 
//            DeserializationException, SOAPDeserializationState, Initializable, SerializerBase, 
//            InternalTypeMappingRegistry, SOAPDeserializationContext, SOAPSerializationContext

public class ObjectArraySerializer extends ArraySerializerBase
    implements Initializable {

    protected JAXRPCSerializer elemSer;
    protected JAXRPCDeserializer elemDeser;

    public ObjectArraySerializer(QName type, boolean encodeType, boolean isNullable, String encodingStyle, QName elemName, QName elemType, Class elemClass, 
            int rank, int dims[]) {
        super(type, encodeType, isNullable, encodingStyle, elemName, elemType, elemClass, rank, dims);
    }

    public void initialize(InternalTypeMappingRegistry registry) throws Exception {
        elemSer = (JAXRPCSerializer)registry.getSerializer(super.encodingStyle, super.elemClass, super.elemType);
        elemDeser = (JAXRPCDeserializer)registry.getDeserializer(super.encodingStyle, super.elemClass, super.elemType);
    }

    protected void serializeArrayInstance(Object obj, int dims[], XMLWriter writer, SOAPSerializationContext context) throws Exception {
        serializeArrayElements((Object[])obj, 0, dims, writer, context);
    }

    protected void serializeArrayElements(Object arr[], int level, int dims[], XMLWriter writer, SOAPSerializationContext context) throws Exception {
        if(arr == null || arr.length != dims[level])
            throw new SerializationException("soap.irregularMultiDimensionalArray");
        boolean serializeLeaves = level == dims.length - 1;
        for(int i = 0; i < dims[level]; i++) {
            Object elem = arr[i];
            if(serializeLeaves)
                elemSer.serialize(elem, super.elemName, null, writer, context);
            else
                serializeArrayElements((Object[])elem, level + 1, dims, writer, context);
        }

    }

    protected Object deserializeArrayInstance(XMLReader reader, SOAPDeserializationContext context, int dims[]) throws Exception {
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
                value = (Object[])Array.newInstance(super.elemClass, length);
            } else {
                value = (Object[])Array.newInstance(super.elemClass, dims);
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
                    Object newValue[] = (Object[])Array.newInstance(super.elemClass, newLength);
                    System.arraycopy(((Object) (value)), 0, ((Object) (newValue)), 0, length);
                    value = newValue;
                    length = newLength;
                }
                Object elem = null;
                elem = elemDeser.deserialize(super.elemName, reader, context);
                if(elem instanceof SOAPDeserializationState) {
                    SOAPDeserializationState elemState = (SOAPDeserializationState)elem;
                    isComplete = false;
                    if(state == null)
                        state = new SOAPDeserializationState();
                    state.setInstance(((Object) (value)));
                    if(state.getBuilder() == null)
                        state.setBuilder(new ObjectArraySerializer$ObjectArrayInstanceBuilder(this, dimOffsets));
                    elemState.registerListener(state, ArraySerializerBase.indexFromPosition(position, dimOffsets));
                } else {
                    setElement(value, position, elem);
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
                Object newValue[] = (Object[])Array.newInstance(super.elemClass, newLength);
                System.arraycopy(((Object) (value)), 0, ((Object) (newValue)), 0, newLength);
                value = newValue;
                length = newLength;
            }
        } else
        if(emptyDims)
            value = (Object[])Array.newInstance(super.elemClass, 0);
        else
            value = (Object[])Array.newInstance(super.elemClass, dims);
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

    public static void setElement(Object value[], int position[], Object elem) {
        Object arr[] = value;
        for(int i = 0; i < position.length - 1; i++)
            arr = (Object[])arr[position[i]];

        arr[position[position.length - 1]] = elem;
    }
}
